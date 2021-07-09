package com.example.instagram.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.instagram.adapters.CommentsAdapter;
import com.example.instagram.databinding.ActivityCommentsBinding;
import com.example.instagram.models.Comment;
import com.example.instagram.models.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class CommentsActivity extends AppCompatActivity {

    private static final String TAG = "CommentsActivity";
    ActivityCommentsBinding binding;
    Post post;
    List<Comment> comments;
    CommentsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCommentsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        post = Parcels.unwrap(getIntent().getParcelableExtra("post"));

        comments = new ArrayList<>();
        adapter = new CommentsAdapter(this, comments);

        binding.rvComments.setAdapter(adapter);
        binding.rvComments.setLayoutManager(new LinearLayoutManager(this));

        binding.btnPostComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = binding.etAddComment.getText().toString();
                if (content.isEmpty()) {
                    Toast.makeText(CommentsActivity.this, "Comment cannot be empty!", Toast.LENGTH_SHORT).show();
                    return;
                }

                binding.etAddComment.setText("");
                binding.etAddComment.clearFocus();

                Comment comment = new Comment();
                comment.setContent(content);
                comment.setUser(ParseUser.getCurrentUser());
                comment.setPost(post);
                post.addComment(comment);

                queryComments();
            }
        });

        queryComments();
    }

    private void queryComments() {
        ParseQuery<Comment> query = ParseQuery.getQuery(Comment.class);
        // include data referred by user key
        query.include(Comment.KEY_USER);
        // limit query to latest 20 items
        query.setLimit(20);
        query.whereEqualTo(Comment.KEY_POST, post);
        // start an asynchronous call for posts
        query.findInBackground(new FindCallback<Comment>() {
            @Override
            public void done(List<Comment> comments, ParseException e) {
                // check for errors
                if (e != null) {
                    Log.e(TAG, "Issue with getting comments", e);
                    return;
                }

                // save received posts to list and notify adapter of new data
                adapter.clear();
                adapter.addAll(comments);
            }
        });
    }
}