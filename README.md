# Real-IM-

Real IM has the following features:
* User can join a chat room using any name he/she wants
* User starts seeing the messages in the chat room which have been sent after he/she joined the room
* User can send a message to all the other users in the room
* User can also click a picture (using the camera button) and send it to other users
* Once the user goes back or kills the app, the user is destroyed and on entering the room again, he will be trated as a new user.
 This feature is required to maintain anonymity in the chat room.
* If multiple users have the same UserName, even then they will be trated as different users because userName is just an alis
 for the client side. Internally everything is done using Parse.Anonymous user. This is the expected behaviour of any chat room
 
Ream IM is real time based:
* All the messages are sent to a single database in Parse
* Whenever a user enters the room, he/she can get the chats only after the time he joined
* The app notes the time stamp of the last chat in the listAdapter and gets only those chats from the database which
  were created after the last one. This ensures we dont get any duplicate chats.
* The app recieves message after every 400ms to get the latest chats

Note: The logic could have been improved by using push mechanism instead of pull(Push is always more efficient). But due to time contraints and the limitation
      to only use Parse, I have implemented it using pull mechanism.
