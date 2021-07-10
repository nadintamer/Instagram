package com.example.instagram.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.instagram.adapters.PostsAdapter;
import com.example.instagram.databinding.FragmentProfileBinding;
import com.example.instagram.models.Post;
import com.example.instagram.utilities.Utils;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    private static final String TAG = "FeedFragment";
    private static final int PICK_PHOTO_REQUEST_CODE = 1034;
    private FragmentProfileBinding binding;
    private List<Post> posts;
    private PostsAdapter adapter;
    private ParseUser user;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance(ParseUser user) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putParcelable("user", Parcels.wrap(user));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = Parcels.unwrap(getArguments().getParcelable("user"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        posts = new ArrayList<>();
        adapter = new PostsAdapter(this, posts, false);

        binding.rvPosts.setAdapter(adapter);
        binding.rvPosts.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        binding.tvUsername.setText(user.getUsername());
        fetchProfilePhoto();

        // user can edit profile photo if this is their own profile
        if (user == ParseUser.getCurrentUser()) {
            binding.ivProfilePhoto.setOnClickListener(v ->
                    Utils.launchGallery(ProfileFragment.this, PICK_PHOTO_REQUEST_CODE));
        }

        binding.swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                queryPosts();
            }
        });
        binding.swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        queryPosts();
    }

    private void queryPosts() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.whereEqualTo("user", user);
        query.include(Post.KEY_USER);
        query.setLimit(20);
        query.addDescendingOrder("createdAt");
        query.findInBackground((posts, e) -> {
            if (e != null) {
                Log.e(TAG, "Issue with getting posts", e);
                return;
            }

            adapter.clear();
            adapter.addAll(posts);
            binding.swipeContainer.setRefreshing(false);
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ((data != null) && requestCode == PICK_PHOTO_REQUEST_CODE) {
            Uri photoUri = data.getData();

            Bitmap selectedImage = Utils.loadFromUri(this, photoUri);
            saveProfilePhoto(selectedImage);
        }
    }

    // Code adapted from:
    // https://stackoverflow.com/questions/27146665/putting-image-from-gallery-in-parsefile-in-android
    public void saveProfilePhoto(Bitmap map) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        // Compress image to lower quality scale 1 - 100
        map.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] image = stream.toByteArray();

        ParseFile file  = new ParseFile("profilePhoto.jpeg", image);
        ParseUser user = ParseUser.getCurrentUser();
        user.put("profilePhoto", file);
        user.saveInBackground(e -> {
            if (e != null) {
                Toast.makeText(getActivity(), "Error uploading profile photo!", Toast.LENGTH_SHORT).show();
                return;
            }

            fetchProfilePhoto();
        });
    }

    private void fetchProfilePhoto() {
        ParseFile imageFile = user.getParseFile("profilePhoto");
        if (imageFile != null) {
            Glide.with(getActivity())
                    .load(imageFile.getUrl())
                    .circleCrop()
                    .into(binding.ivProfilePhoto);
        }
    }
}