/*
 * Created By Jong Ho, Lee on  2020.
 * Copyright 테크하임(주). All rights reserved.
 */

package x.com.nubextalk.Manager.FireBase;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.realm.Realm;
import x.com.nubextalk.ChatRoomActivity;
import x.com.nubextalk.Manager.DateManager;
import x.com.nubextalk.Manager.UtilityManager;
import x.com.nubextalk.Model.ChatContent;
import x.com.nubextalk.Model.ChatRoom;
import x.com.nubextalk.Model.Config;
import x.com.nubextalk.Model.User;
import x.com.nubextalk.R;

/**
 * Firebase Message Service
 * - onNewToken : Token 이 갱신될때 호출
 * - onMessageReceived : FCM이 수시신될떄 호출
 * - 참고 : https://firebase.google.com/docs/cloud-messaging/android/client?authuser=0
 */
public class FirebaseMsgService extends FirebaseMessagingService {
    String CHANNEL_ID = "0608";
    Realm realm;
    NotificationManager notificationManager;

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.d("FCM_TOKEN_OnNew : ", s);

        Realm realm = Realm.getInstance(UtilityManager.getRealmConfig());
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Config userMe = realm.where(Config.class).equalTo("CODE", "MyAccount").findFirst();
                if (userMe == null) {
                    userMe = new Config();
                    userMe.setCODENAME("MyAccount");
                    userMe.setCODE("MyAccount");
                }
                userMe.setExt4(s);
                realm.copyToRealmOrUpdate(userMe);
            }
        });


        /**
         * FCM Token 확인 함수
         */
        /*FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("TOKEN", "getInstanceId failed", task.getException());
                            return;
                        }
                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        Log.d("TOKEN", token);
                    }
                });*/
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        realm = Realm.getInstance(UtilityManager.getRealmConfig());
        notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        String cid;
        String uid;
        String rid;
        int type;
        String content;
        Date date;
        Boolean isFirst;

        Map<String, String> data = remoteMessage.getData();
        Log.d("TOKEN", "RECEIVE_TOKEN\nCODE : " + data.get("CODE") + "\nDATE : " + data.get("date"));
        switch (data.get("CODE")) {
            case "CHAT_CONTENT_CREATED": //chat 받았을 때
                Map<String, Object> payload = new HashMap<>();
                payload.put("uid", data.get("senderId"));
                payload.put("cid", data.get("chatContentId"));
                payload.put("rid", data.get("chatRoomId"));
                payload.put("content", data.get("content"));
                payload.put("type", data.get("contentType"));
                payload.put("sendDate", data.get("sendDate"));
                payload.put("isFirst", data.get("isFirst"));

                uid = data.get("senderId");
                rid = data.get("chatRoomId");
                type = Integer.parseInt(data.get("contentType"));
                content = data.get("content");

                // 채팅방이 없으면 먼저 FireStore에서 채팅방 불러오고 realm 에 채팅방 생성
                if (realm.where(ChatRoom.class).equalTo("rid", rid).findAll().isEmpty()) {
                    //Firebase Functions 함수의 getChatRoom 함수 호출을 통해 FireStore 에 있는 채팅방 데이터 불러옴
                    FirebaseFunctionsManager.getChatRoom("w34qjptO0cYSJdAwScFQ", rid)
                            .addOnSuccessListener(new OnSuccessListener<HttpsCallableResult>() {
                                @Override
                                public void onSuccess(HttpsCallableResult httpsCallableResult) {
                                    Gson gson = new Gson();
                                    Map<String, Object> value = new HashMap<>();
                                    try {
                                        // realm ChatRoom 데이터 초기화
                                        Realm realm1 = Realm.getInstance(UtilityManager.getRealmConfig());
                                        JSONObject result = new JSONObject(gson.toJson(httpsCallableResult.getData()));
                                        value.put("rid", rid);
                                        value.put("title", result.getJSONObject("chatRoom").getString("roomName"));
                                        value.put("roomImgUrl", result.getJSONObject("chatRoom").getString("roomImg"));
                                        value.put("updatedDate", result.getJSONObject("chatRoom").getString("updatedDate"));
                                        value.put("notificationId", result.getJSONObject("chatRoom").getString("notificationId"));
                                        ArrayList<String> userIdList = new ArrayList<>();
                                        for (int i = 0; i < result.getJSONArray("chatRoomMember").length(); i++) {
                                            userIdList.add(result.getJSONArray("chatRoomMember").getString(i));
                                        }

                                        // realm ChatRoom, ChatContent 생성
                                        ChatRoom.createChatRoom(realm1, value, userIdList);
                                        ChatContent.createChat(realm1, payload);

                                        // 알림 설정
                                        if (!Config.getMyUID(realm1).equals(uid)) {
                                            int channelId = Integer.parseInt(realm1.where(ChatRoom.class).equalTo("rid", data.get("chatRoomId")).findFirst().getNotificationId());
                                            makeChannel(CHANNEL_ID);
                                            notificationManager.notify(channelId, makeBuilder(rid, uid, type, content).build());
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                } else { // 기존 realm 에 채팅방이 있는 경우에는 ChatContent 만 생성
                    ChatContent.createChat(realm, payload);

                    // 알림 설정
                    if (!Config.getMyUID(realm).equals(uid)) {
                        int channelId = Integer.parseInt(realm.where(ChatRoom.class).equalTo("rid", data.get("chatRoomId")).findFirst().getNotificationId());
                        makeChannel(CHANNEL_ID);
                        notificationManager.notify(channelId, makeBuilder(rid, uid, type, content).build());
                    }
                }
                break;
            case "CHAT_ROOM_INVITED":
                Log.d("TOKEN", "room invited!!");

                // Chatting Message(Notification Message)
                // System Message

        }
    }

    public void makeChannel(String id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager.getNotificationChannel(id) == null) {
                String channelName = "chat_notify";
                String description = "chatting";
                int importance = NotificationManager.IMPORTANCE_DEFAULT;

                NotificationChannel channel = new NotificationChannel(id, channelName, importance);
                channel.setDescription(description);
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    public NotificationCompat.Builder makeBuilder(String rid, String uid, int type, String content) {
        Realm realm = Realm.getInstance(UtilityManager.getRealmConfig());
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        ChatRoom roomInfo = realm.where(ChatRoom.class).equalTo("rid", rid).findFirst();
        User userInfo = realm.where(User.class).equalTo("userId", uid).findFirst();

        Bitmap largeIcon;
        if(userInfo != null) {
             largeIcon = getImageFromURL(userInfo.getAppImagePath());
        }
        else{
            largeIcon = getImageFromURL(roomInfo.getRoomImg());
        }
        Intent intent = new Intent(this, ChatRoomActivity.class);
        intent.putExtra("rid", rid);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setSmallIcon(R.drawable.nube_x_logo)
                .setContentTitle(roomInfo.getRoomName())
                .setLargeIcon(largeIcon)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setChannelId(CHANNEL_ID)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);
        if (type == 1) {
            builder.setContentText("사진");
        } else {
            builder.setContentText(content);
        }
        return builder;
    }

    public static Bitmap getImageFromURL(String imageURL) {
        Bitmap imgBitmap = null;
        HttpURLConnection conn = null;
        BufferedInputStream bis = null;

        try {
            URL url = new URL(imageURL);
            conn = (HttpURLConnection) url.openConnection();
            conn.connect();

            int nSize = conn.getContentLength();
            bis = new BufferedInputStream(conn.getInputStream(), nSize);
            imgBitmap = BitmapFactory.decodeStream(bis);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                }
            }
            if (conn != null) {
                conn.disconnect();
            }
        }

        return imgBitmap;
    }
}
