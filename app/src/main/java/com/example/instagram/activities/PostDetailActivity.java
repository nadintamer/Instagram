package com.example.instagram.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.example.instagram.R;
import com.example.instagram.adapters.CommentsAdapter;
import com.example.instagram.databinding.ActivityPostDetailBinding;
import com.example.instagram.fragments.FeedFragment;
import com.example.instagram.models.Comment;
import com.example.instagram.models.Post;
import com.example.instagram.utilities.Utils;
import com.parse.Parse;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class PostDetailActivity extends AppCompatActivity {

    ActivityPostDetailBinding binding;
    Post post;
    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPostDetailBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        post = getIntent().getParcelableExtra("post");
        position = getIntent().getExtras().getInt("position");

        binding.tvUsername.setText(post.getUser().getUsername());
        binding.tvDescription.setText(post.getDescription());
        binding.tvTimestamp.setText(Utils.calculateTimeAgo(post.getCreatedAt()));

        ParseFile imageFile = post.getImage();
        if (imageFile != null) {
            Glide.with(this)
                    .load(imageFile.getUrl())
                    .fitCenter()
                    .into(binding.ivPhoto);
        }

        setLikesLabel();

        Boolean isLiked = post.isLikedBy(ParseUser.getCurrentUser());
        binding.ibLike.setSelected(isLiked);

        binding.ibLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.ibLike.setSelected(!binding.ibLike.isSelected());

                if (binding.ibLike.isSelected()) {
                    post.addLike(ParseUser.getCurrentUser());
                } else {
                    post.removeLike(ParseUser.getCurrentUser());
                }

                setLikesLabel();
            }
        });

        binding.ibComment.setOnClickListener(v -> goCommentsActivity());
        binding.tvViewComments.setOnClickListener(v -> goCommentsActivity());
    }

    private void goCommentsActivity() {
        Intent i = new Intent(this, CommentsActivity.class);
        i.putExtra("post", Parcels.wrap(post));
        startActivity(i);
    }

    private void setLikesLabel() {
        int numLikes = post.getNumLikes();
        String strToFormat =  numLikes != 1 ? "%d likes" : "%d like";
        binding.tvLikes.setText(String.format(strToFormat, numLikes));
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent();
        i.putExtra("postId", post.getObjectId());
        i.putExtra("position", position);
        setResult(RESULT_OK, i);
        finish();
        super.onBackPressed();
    }
}