package com.example.instagram.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.instagram.R;
import com.example.instagram.adapters.CommentsAdapter;
import com.example.instagram.databinding.ActivityCommentsBinding;
import com.example.instagram.models.Comment;
import com.example.instagram.models.Post;
import com.example.instagram.utilities.Utils;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

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
        setUpToolbar();

        post = Parcels.unwrap(getIntent().getParcelableExtra("post"));

        // set up recyclerView
        comments = new ArrayList<>();
        adapter = new CommentsAdapter(this, comments);
        binding.rvComments.setAdapter(adapter);
        binding.rvComments.setLayoutManager(new LinearLayoutManager(this));

        // set up commenter profile photo & post button
        Glide.with(this)
                .load(Utils.getProfilePhotoUrl(ParseUser.getCurrentUser()))
                .circleCrop()
                .into(binding.ivProfilePhoto);

        binding.btnPostComment.setOnClickListener(v -> postNewComment());

        queryComments();
    }

    private void queryComments() {
        ParseQuery<Comment> query = ParseQuery.getQuery(Comment.class);
        query.include(Comment.KEY_USER);
        // only get comments that apply to this post
        query.whereEqualTo(Comment.KEY_POST, post);
        query.findInBackground((comments, e) -> {
            if (e != null) {
                Log.e(TAG, "Issue with getting comments", e);
                return;
            }

            // save received posts to list and notify adapter of new data
            adapter.clear();
            adapter.addAll(comments);
        });
    }

    private void postNewComment() {
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
        post.addComment(comment, e -> queryComments());
    }

    private void setUpToolbar() {
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setTitle("Comments");
        binding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        binding.toolbar.setNavigationOnClickListener(view1 -> onBackPressed());
    }

}