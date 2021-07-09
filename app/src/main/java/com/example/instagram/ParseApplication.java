package com.example.instagram;

import com.example.instagram.models.Comment;
import com.example.instagram.models.Post;
import com.parse.Parse;
import com.parse.ParseObject;

import android.app.Application;

public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(Post.class);
        ParseObject.registerSubclass(Comment.class);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(getString(R.string.parse_application_id))
                .clientKey(getString(R.string.parse_client_key))
                .server(getString(R.string.parse_server))
                .build()
        );
    }
}
