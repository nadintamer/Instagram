package com.example.instagram.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.example.instagram.databinding.ActivityPostDetailBinding;
import com.example.instagram.models.Post;
import com.example.instagram.utilities.Utils;
import com.parse.Parse;
import com.parse.ParseFile;

public class PostDetailActivity extends AppCompatActivity {

    ActivityPostDetailBinding binding;
    Post post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPostDetailBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        post = getIntent().getParcelableExtra("post");

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
    }
}