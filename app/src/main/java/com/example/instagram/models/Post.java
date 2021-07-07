package com.example.instagram.models;

import com.parse.Parse;
import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.List;

@ParseClassName("Post")
public class Post extends ParseObject {

    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_USER = "user";
    public static final String KEY_COMMENTS = "comments";
    private static final String KEY_LIKES = "numLikes";

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
        return getInt(KEY_LIKES);
    }

    public void setNumLikes(int likes) {
        put(KEY_LIKES, likes);
    }

    public void addLike() {
        int numLikes = getNumLikes();
        setNumLikes(numLikes + 1);
    }

    public void removeLike() {
        int numLikes = getNumLikes();
        setNumLikes(numLikes - 1);
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
