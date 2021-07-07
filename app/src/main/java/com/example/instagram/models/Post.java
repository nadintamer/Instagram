package com.example.instagram.models;

import android.util.Log;

import com.parse.Parse;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

@ParseClassName("Post")
public class Post extends ParseObject {

    private static final String TAG = "Post";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_USER = "user";
    public static final String KEY_COMMENTS = "comments";
    private static final String KEY_LIKES = "likers";

    public String getDescription() {
        return getString(KEY_DESCRIPTION);
    }

    public void setDescription(String description) {
        put(KEY_DESCRIPTION, description);
    }

    public ParseFile getImage() {
        return getParseFile(KEY_IMAGE);
    }

    public void setImage(ParseFile image) {
        put(KEY_IMAGE, image);
    }

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

    public int getNumLikes() {
        return getList(KEY_LIKES).size();
    }

    public List<String> getLikers() {
        return getList(KEY_LIKES);
    }

    public void setLikers(List<String> users) {
        put(KEY_LIKES, users);
        saveInBackground();
    }

    public void addLike(ParseUser user) {
        List<String> likers = getLikers();
        likers.add(user.getObjectId());
        setLikers(likers);
    }

    public void removeLike(ParseUser user) {
        List<String> likers = getLikers();
        likers.remove(user.getObjectId());
        setLikers(likers);
    }

    public Boolean isLikedBy(ParseUser user) {
        List<String> likers = getLikers();
        return likers.contains(user.getObjectId());
    }

    public List<String> getComments() {
        return getList(KEY_COMMENTS);
    }

    public void setComments(List<String> comments) {
        put(KEY_COMMENTS, comments);
    }

    public void addComment(String comment) {
        List<String> comments = getComments();
        comments.add(comment);
        setComments(comments);
    }
}
