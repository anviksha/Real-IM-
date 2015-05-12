package com.example.anviksha.realim;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseFile;

/**
 * Created by anviksha on 10/05/15.
 */
@ParseClassName("Message")
public class Message extends ParseObject {
    public String getUserId() {
        return getString("userId");
    }

    public String getBody() {
        return getString("body");
    }

    public String getUserName() {
        return getString("userName");
    }

    public ParseFile getImageFile() {return getParseFile("ImageFile");}

    public void setUserId(String userId) {
        put("userId", userId);
    }

    public void setImageFile(ParseFile file) { put("ImageFile",file);}

    public void setBody(String body) {
        put("body", body);
    }

    public void setUserName(String userName) {
        put("userName", userName);
    }


}