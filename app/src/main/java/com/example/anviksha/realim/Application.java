package com.example.anviksha.realim;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.SaveCallback;

/**
 * Created by anviksha on 11/05/15.
 */
public class Application extends android.app.Application {

    private static String APPLICATION_ID = "3qrMMd1qYrB51uyYKV8OtQFq2KJ96diWVpivuNUk";
    private static String KEY ="YLMwbGcVst7r0bKvGdxhFEfZO8nvNfVKQQ5vMPXJ";

    public void onCreate(){
        Parse.enableLocalDatastore(this);
        ParseObject.registerSubclass(Message.class);
        Parse.initialize(this, APPLICATION_ID, KEY);
    }
}
