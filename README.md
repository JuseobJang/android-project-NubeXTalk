# NubeXTalk for Android



<p align="center">
  <img src="https://user-images.githubusercontent.com/22047374/128466889-e7dbf606-e3fb-40bf-827d-a4654d02b3bd.png" width="200px;" alt="app_icon" style="border-radius:50%"/></p>



## Mobile messanger on Health-care system / PACS

This project is 'Industry-University Cooperation Project' between TechHeim Co.Ltd and Hanyang University.  
This project is prototype product and not commerial health-care product.  
This project is not stable health-care system.  
This project is not authorizied health-care authority.(like CE, FDA..)  

**Prohibit redistribution, modification, commerial use.**

**TechHeim Co.Ltd has not responsibility on this software on illegal use.**

**Copyright 테크하임(주). All rights reserved.**

## Contact
Contact on call '(+82)1588-6620' or email 'jongholee@techheim.com'. 

## Contributors 

Tanks goes to Hanyang Univiersity students team:
<!-- ALL-CONTRIBUTORS-LIST:START - Do not remove or modify this section -->
<!-- prettier-ignore-start -->
<!-- markdownlint-disable -->
<table>
  <tr>
    <td align="center">
     <a href="https://github.com/wntjq68">
     <img src="https://avatars.githubusercontent.com/u/22047374?v=4" width="100px;" alt=""/>
      <br />
      <sub><b>JuseobJang</b></sub>
     </a>
    </td>
    <td align="center">
     <a href="https://github.com/chlwodud77">
     <img src="https://avatars.githubusercontent.com/u/22047622?v=4" width="100px;" alt=""/>
      <br />
      <sub><b>Jae Young Choi (Jay)</b></sub>
     </a>
    </td>
    <td align="center">
     <a href="https://github.com/JeongGod">
     <img src="https://avatars.githubusercontent.com/u/22341452?v=4" width="100px;" alt=""/>
      <br />
      <sub><b>JeongGod</b></sub>
     </a>
    </td>
 </tr>
 </table>
## Contents

- [Mobile Phone](###Mobile Phone)
  - [Splash & Login](####Splash & Login)
  - [Tutorial](####Tutorial)
  - [Friends List](####Friends List)
  - [Chat Room List](####Chat Room List)
  - [Chat Room](####Chating Room)
  - [Image Processing](####Image Processing)
  - [PACS System](####PACS System)
  - [Dark Mode](####Dark Mode)
- [Tablet](###Tablet)
  - [Main & PACS System](####Main & PACS System)
  - [Chat Room & PACS System](####Chat Room & PACS System)

---

### Mobile Phone

**Minimun SDK version** 23

**Target SDK version** 29 

**Emulator** Pixel 4 XL API 26 (1440 x 3040 : 560dpi)

#### Splash & Login

 When running this application for the first time, the splash activity is expressed for a certain period of time before it is transferred to the login activity.

<p align="center">
  <img src="https://user-images.githubusercontent.com/22047374/128460597-dc336c21-949b-40ca-9899-e9c711a2ae57.png" width="200px;" alt="splash"/>
	<img src="https://user-images.githubusercontent.com/22047374/128459760-3609a351-2cb6-4ba8-80c7-f4b379ee5daa.png" width="200px;" alt="login"/>
</p>


#### Tutorial

 If a user is first connected to an application, the tutorial is shown. Users can learn how to use the application through the corresponding tutorial.

<p align="center">
	<img src="https://user-images.githubusercontent.com/22047374/128459842-a18b1789-25d2-4432-9a89-af59e74d3680.png" width="200px;" alt="tutorial1"/>
	<img src="https://user-images.githubusercontent.com/22047374/128459902-9486481d-a413-44d6-bc0a-617d49a52ff5.png" width="200px;" alt="tutorial2"/>
	<img src="https://user-images.githubusercontent.com/22047374/128459921-b8041a90-b94e-42b8-bb13-aca1a134b5d5.png" width="200px;" alt="tutorial3"/>
</p>


#### Friends List

 If you sign in normally and the user passes through the tutorial, the main activity's Friends List Fragment usually appears for the first time. The screen allows the user to change their profile. You can also view other users' profiles, create or enter a 1:1 chat room.

<p align="center">
	<img src="https://user-images.githubusercontent.com/22047374/128460034-560b5f55-fb66-42ca-ac4b-8be23735a7f8.png" width="200px;" alt="friends_list"/>
  <img src="https://user-images.githubusercontent.com/22047374/128461291-0500c27f-36e3-42f1-a68e-6049fd0aa09f.png" width="200px;" alt="friends_detail"/>
  <img src="https://user-images.githubusercontent.com/22047374/128464273-7f8d6306-a6d7-4464-91a2-f1072775784a.png" width="200px;" alt="friends_detail"/>
  <img src="https://user-images.githubusercontent.com/22047374/128464279-43727509-325c-4d73-9242-41c4a9efd0a5.png" width="200px;" alt="friends_detail"/>
</p>



#### Chat Room List

 Through the bottom navigation bar, it is possible to move to four different programs. Among them, if you go to the chat list fragment, you can see the chat room list. Here you can see the most recent message and the number of unread messages.

<p align="center">
	<img src="https://user-images.githubusercontent.com/22047374/128460975-267d33cc-d3f3-4623-8891-ce73e733f535.png" width="200px;" alt="chating_room_list">
</p>


#### Chat Room

 When you enter the chat room, there are EditText and Button at the bottom to send text messages, and there are chat room names and drawer navigation tabs at the top action bar. When you open the drawer navigation, you will see menus that can be sent using cameras, galleries, PACS systems, toggle buttons that can be fixed and set notifications, and members of the chat room concerned.

<p align="center">
	<img src="https://user-images.githubusercontent.com/22047374/128461563-9287356a-26ce-4627-a1b0-ec584533a0ce.png" width="200px;" alt="chating_room">
	<img src="https://user-images.githubusercontent.com/22047374/128461576-6ec416fb-d9b3-42b9-a20b-2ea0e077b74e.png" width="200px;" alt="chating_room_drawer">
</p>
#### Image Processing

 Images can be sent through a camera or gallery. This image stores the original image in Firebase storage and uses cached images in clients. If you download an image, you can download the original image to the server.

<p align="center">
	<img src="https://user-images.githubusercontent.com/22047374/128461711-d7d508d4-6c23-4313-a25d-b126d613f8a2.png" width="200px;" alt="image_send">
	<img src="https://user-images.githubusercontent.com/22047374/128461715-746d2de6-f34c-449f-b9a4-cba5b00e0434.png" width="200px;" alt="image_show">
</p>


#### PACS System 

 It can enter the PACS system through bottom navigation of main activity or drawer navigation of chat rooms. The Search tab allows medical images to be retrieved through periods or specific conditions. The top of the Worklist displays the results of the search. At the bottom of the Worklist, other medical images taken by patients of selected medical images are displayed at the top. Pressing the Eyes icon shows the corresponding medical images in the Viewer. This allows the user to zoom in, zoom out, move around, display, black-and-white inversion, and send to other users.

<p align="center">
	<img src="https://user-images.githubusercontent.com/22047374/128461869-2009d1e9-4cc5-43fc-9d7f-39b734b1a72e.png" width="200px;" alt="pacs_init">
	<img src="https://user-images.githubusercontent.com/22047374/128461878-1d4f9c6a-ce4a-416d-aa7f-ff7cc38f460d.png" width="200px;" alt="pacs_list">
	<img src="https://user-images.githubusercontent.com/22047374/128461887-38fcf090-4f13-402f-adf7-f14a83821007.png" width="200px;" alt="pacs_detail">
	<img src="https://user-images.githubusercontent.com/22047374/128461891-6b02751c-b01a-432d-b2bd-13f137597f18.png" width="200px;" alt="pacs_send">
</p>


#### Dark Mode

 Dark mode for user convenience is implemented using values.colors (night).

<p align="center">
	<img src="https://user-images.githubusercontent.com/22047374/128462269-aa8a2fcf-7877-4820-a34b-a09b3e716724.png" width="200px;" alt="pacs_init">
	<img src="https://user-images.githubusercontent.com/22047374/128462277-46a46bb6-ebed-4c4d-8f4f-0ab3c60d4164.png" width="200px;" alt="pacs_list">
	<img src="https://user-images.githubusercontent.com/22047374/128462286-12a4aa67-e741-4e75-b79f-923d69569fc7.png" width="200px;" alt="pacs_send">
</p>
---

### Tablet

**Emulator** Pixel C  (2560 x 1800 : xhdpi)

The biggest difference between mobile phones and tablets is the wider screen. Using this wide screen, the application can always display the PACS System, the killing function.

#### Main & PACS System

The PACS System is accessible no matter what the main activity on the left displays. In other words, PACS medical images always can be viewed, manipulated, and sent to other users from any tab.

<p align="center">
	<img src="https://user-images.githubusercontent.com/22047374/128467422-9e1b9dd9-f84b-418a-bf58-98175c01b02c.png" width="350px;" alt="pacs_init">
	<img src="https://user-images.githubusercontent.com/22047374/128467515-e423f4ed-d1a9-4d7a-8dbd-09a4d96e44ec.png" width="350px;" alt="pacs_list">
</p>



### Chat Room & PACS System

Similarly, PACS System can be used within chat room activities. The biggest difference between a mobile phone and a tablet is that the screen does not switch when clicking on a transmitting or receiving PACS message, and the corresponding medical image is shown in the right-hand fragment.

<p align="center">
	<img src="https://user-images.githubusercontent.com/22047374/128467756-679265c0-5e68-4ce6-b17d-3a04ea81fa71.png" width="350px;" alt="pacs_init">
	<img src="https://user-images.githubusercontent.com/22047374/128467766-ce6106cd-c84e-4163-8172-576e1c4af9d7.png" width="350px;" alt="pacs_list">
</p>



**Thank you for reading**
