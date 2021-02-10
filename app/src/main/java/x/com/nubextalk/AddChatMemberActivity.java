/*
 * Created By Jong Ho, Lee on  2021.
 * Copyright 테크하임(주). All rights reserved.
 */

package x.com.nubextalk;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmResults;
import x.com.nubextalk.Manager.FireBase.FirebaseFunctionsManager;
import x.com.nubextalk.Manager.UtilityManager;
import x.com.nubextalk.Model.ChatContent;
import x.com.nubextalk.Model.ChatRoom;
import x.com.nubextalk.Model.ChatRoomMember;
import x.com.nubextalk.Model.Config;
import x.com.nubextalk.Model.User;
import x.com.nubextalk.Module.Adapter.ChatAddMemberAdapter;
import x.com.nubextalk.Module.Adapter.ChatAddSearchAdapter;

public class AddChatMemberActivity extends AppCompatActivity implements
        ChatAddSearchAdapter.OnItemSelectedListener, View.OnClickListener {
    private ArrayList<User> userList = new ArrayList<User>();
    private ArrayList<User> addedUserList = new ArrayList<User>();
    private Button chatAddConfirmButton;
    private Button chatAddCancelButton;
    private ChatAddMemberAdapter selectedMemberAdapter;
    private ChatAddSearchAdapter realmSearchAdapter;
    private EditText userNameInput;
    private InputMethodManager imm;
    private RecyclerView realmMemberSearchView;
    private RecyclerView selectedMemberView;
    private Realm realm;

    private String hospitalId;
    private String chatRoomId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_chat_member);

        Intent intent = getIntent();
        chatRoomId = intent.getExtras().getString("rid");
        hospitalId = "w34qjptO0cYSJdAwScFQ";

        realm = Realm.getInstance(UtilityManager.getRealmConfig());
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        userNameInput = findViewById(R.id.add_chat_member_input);
        userNameInput.setSingleLine(true);
        userNameInput.addTextChangedListener(textWatcher);
        chatAddConfirmButton = findViewById(R.id.add_chat_confirm_btn);
        chatAddCancelButton = findViewById(R.id.add_chat_cancel_btn);
        chatAddConfirmButton.setOnClickListener(this);
        chatAddCancelButton.setOnClickListener(this);

        initUserList(); //리싸이클러뷰에 사용될 사용자 데이터 리스트 초기화

        //사용자 검색 및 목록 리싸이클러뷰 설정
        realmMemberSearchView = findViewById(R.id.add_chat_member_search_view);
        realmSearchAdapter = new ChatAddSearchAdapter(this, userList);
        realmSearchAdapter.setItemSelectedListener(this);
        realmMemberSearchView.
                setLayoutManager(new LinearLayoutManager(
                        this, LinearLayoutManager.VERTICAL, false));
        realmMemberSearchView.setAdapter(realmSearchAdapter);

        //선택된 사용자 표시 리싸이클러뷰 설정
        selectedMemberView = findViewById(R.id.add_chat_member_added_view);
        selectedMemberAdapter = new ChatAddMemberAdapter(this, addedUserList);
        selectedMemberView.
                setLayoutManager(new LinearLayoutManager(
                        this, LinearLayoutManager.HORIZONTAL, false));
        selectedMemberView.setAdapter(selectedMemberAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (realm != null) {
            realm.close();
            realm = null;
        }
    }

    @Override
    public void onItemSelected(User user) {
        selectedMemberAdapter.addItem(user);

        userNameInput.setText("");
        imm.hideSoftInputFromWindow(realmMemberSearchView.getWindowToken(), 0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_chat_confirm_btn:
                ArrayList<User> selectedUser = selectedMemberAdapter.getUserList();
                if (inviteChatUser(realm, hospitalId, chatRoomId, selectedUser)) {
                    Toast.makeText(this, "대화 상대가 추가되었습니다.", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    Toast.makeText(this, "대화 상대 추가가 실패되었습니다.", Toast.LENGTH_SHORT)
                            .show();
                }
            case R.id.add_chat_cancel_btn:
                Log.d("ADD_CHAT_MEMBER", "back pressed!");
                onBackPressed();
        }
    }

    /**
     * 리싸이클러뷰에 표시될 사용자들 담을 데이터 초기화 함수 - 기존 채팅방에 있는 사용자는 보여주지 않는다
     */
    public void initUserList() {
        RealmResults<User> users = User.getUserlist(this.realm);
        RealmResults<ChatRoomMember> chatRoomMembers = ChatRoom.getChatRoomUsers(this.realm, chatRoomId);

        for (User user : users) {
            userList.add(user);
        }

        //기존 채팅방 사용자들이 있으면 userList 데이터에서 삭제
        for (ChatRoomMember chatRoomMember : chatRoomMembers) {
            Iterator<User> userIterator = userList.iterator();
            while (userIterator.hasNext()) {
                if (chatRoomMember.getUid().equals(userIterator.next().getUserId())) {
                    userIterator.remove();
                }
            }
        }
    }

    /**
     * 사용자 검색해서 해당 사용자가 있으면 보여주는 함수
     *
     * @param name 사용자 이름
     */
    private void searchUser(String name) {
        RealmResults<User> users = this.realm.where(User.class).contains("appName", name).
                or().contains("appNickName", name).findAll();
        if (!users.isEmpty()) {
            realmSearchAdapter.deleteAllItem();
            for (User user : users) {
                if (!isExistUser(realm, chatRoomId, user.getUserId())) {
                    realmSearchAdapter.addItem(user);
                }
            }
        } else {
            realmSearchAdapter.deleteAllItem();
            initUserList();
            realmSearchAdapter.setItem(userList);
        }
    }

    /**
     * 해당 채팅방에 사용자가 있는지 확인하는 함수
     *
     * @param realm
     * @param roomId
     * @param uid
     * @return
     */
    private boolean isExistUser(Realm realm, String roomId, String uid) {
        return !realm.where(ChatRoomMember.class).equalTo("rid", roomId).and()
                .equalTo("uid", uid).findAll().isEmpty();
    }


    /**
     * 기존 대화방 ChatRoom 에서 선택된 사용자들 추가
     *
     * @param realm
     * @param hid
     * @param rid
     * @param userList
     * @return
     */
    public boolean inviteChatUser(Realm realm, String hid, String rid, ArrayList<User> userList) {

        ChatRoom chatRoom = realm.where(ChatRoom.class).equalTo("rid", rid).findFirst();
        Boolean isGroupChat = false;
        if (chatRoom != null) {
            isGroupChat = chatRoom.getIsGroupChat();

            if (isGroupChat) {
                FirebaseFirestore fs = FirebaseFirestore.getInstance();
                for (User user : userList) {

                    //FireStore 에 해당 채팅방에 사용자 추가
                    Map<String, Object> data = new HashMap<>();
                    fs.collection("hospital").document(hid)
                            .collection("chatRoom").document(rid)
                            .collection("chatRoomMember")
                            .document(user.getUserId()).set(data);
                }

                String uid = Config.getMyUID(realm);
                Map<String, Object> chat = new HashMap<>();
                String cid = uid.concat(String.valueOf(new Date().getTime()));
                chat.put("cid", cid);
                chat.put("uid", uid);
                chat.put("rid", rid);
                chat.put("content", "상대방을 초대 중 입니다.");
                chat.put("type", "9");

                ChatContent.createChat(realm, chat);

                //Functions 통해 기존 사용자 및 초대된 새로운 사용자들에게 시스템 메세지 보냄
                Map<String, Object> data = new HashMap<>();
                JSONArray jsonArray = new JSONArray();
                for (User user : userList) {
                    jsonArray.put(user.getUserId());
                }
                data.put("chatContentId", cid);
                data.put("hospitalId", hid);
                data.put("senderId", Config.getMyAccount(realm).getExt1());
                data.put("membersId", jsonArray);
                data.put("chatRoomId", rid);
                FirebaseFunctionsManager.notifyToChatRoomAddedUser(data);
            } else {
                ArrayList<User> userArrayList = new ArrayList<>(userList);
                RealmResults<ChatRoomMember> realmResults = ChatRoom.getChatRoomUsers(realm, rid);
                for (ChatRoomMember chatRoomMember : realmResults) {
                    User user = realm.where(User.class).equalTo("userId", chatRoomMember.getUid()).findFirst();
                    if (user != null) {
                        userArrayList.add(user);
                    }
                }
                Intent intent = new Intent(this, ChatRoomActivity.class);
                ChatAddActivity.createNewChat(realm, this, userArrayList, getUserName(userArrayList), new ChatAddActivity.onNewChatCreatedListener() {
                    @Override
                    public void onCreate(String rid) {
                        intent.putExtra("rid", rid).setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        startActivity(intent);
                    }
                });

            }

        }
        return true;

    }

    public String getUserName(ArrayList<User> userArrayList) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < userArrayList.size(); i++) {
            String name = userArrayList.get(i).getAppName();
            stringBuilder.append(name);
            if (i != userArrayList.size() - 1) {
                stringBuilder.append(", ");
            }
        }
        return stringBuilder.toString();
    }

    /**
     * 사용자 이름 검색창에서 텍스트 입력 감지에 사용되는 TextWatcher
     */
    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            searchUser(s.toString());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
}