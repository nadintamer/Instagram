package com.example.instagram.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.instagram.R;
import com.example.instagram.databinding.FragmentNewPostBinding;
import com.example.instagram.models.Comment;
import com.example.instagram.models.Post;
import com.example.instagram.utilities.BitmapScaler;
import com.example.instagram.utilities.Utils;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public class NewPostFragment extends Fragment {

    private static final String TAG = "NewPostFragment";
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 42;
    public static final int CHOOSE_IMAGE_ACTIVITY_REQUEST_CODE = 43;
    public static final int MAX_IMAGE_WIDTH = 300;

    private FragmentNewPostBinding binding;
    private File photoFile;
    public String photoFileName = "photo.jpg";

    public NewPostFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentNewPostBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // set up buttons
        binding.btnSubmit.setOnClickListener(v -> createPost());
        binding.btnTakePhoto.setOnClickListener(v -> launchCamera());
        binding.btnChoosePhoto.setOnClickListener(v ->
                Utils.launchGallery(this, CHOOSE_IMAGE_ACTIVITY_REQUEST_CODE));
    }

    private void createPost() {
        String description = binding.etDescription.getText().toString();
        if (description.isEmpty()) {
            Toast.makeText(getActivity(), "Description cannot be empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (photoFile == null || binding.ivPhoto.getDrawable() == null) {
            Toast.makeText(getActivity(), "There is no image!", Toast.LENGTH_SHORT).show();
            return;
        }

        ParseUser currentUser = ParseUser.getCurrentUser();
        savePost(description, currentUser, photoFile);
    }

    private void savePost(String description, ParseUser currentUser, File photoFile) {
        binding.progressBar.setVisibility(View.VISIBLE);
        Post post = new Post();
        post.setDescription(description);
        post.setImage(new ParseFile(photoFile));
        post.setUser(currentUser);
        post.setComments(new ArrayList<>());
        post.setLikers(new ArrayList<>());
        post.saveInBackground(e -> {
            if (e != null) {
                Log.e(TAG, "Error while saving post", e);
                Toast.makeText(getActivity(), "Error while saving post!", Toast.LENGTH_SHORT).show();
            }
            Log.i(TAG, "Post save successful!");

            // add caption as first comment
            Comment comment = new Comment();
            comment.setContent(post.getDescription());
            comment.setUser(post.getUser());
            comment.setPost(post);
            post.addComment(comment, null);

            // clear inputs and navigate back to feed
            binding.etDescription.setText("");
            binding.etDescription.clearFocus();

            binding.ivPhoto.setImageResource(0);
            binding.progressBar.setVisibility(View.INVISIBLE);

            BottomNavigationView bottomNavigation = getActivity().findViewById(R.id.bottom_navigation);
            bottomNavigation.setSelectedItemId(R.id.action_home);
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
                // resize file before uploading to parse
                Bitmap rawTakenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                Bitmap resizedBitmap = BitmapScaler.scaleToFitWidth(rawTakenImage, MAX_IMAGE_WIDTH);

                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 40, bytes);
                File resizedFile = getPhotoFileUri(photoFileName);
                try {
                    FileOutputStream fos = new FileOutputStream(resizedFile);
                    fos.write(bytes.toByteArray());
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                binding.ivPhoto.setImageBitmap(resizedBitmap);
            } else if (requestCode == CHOOSE_IMAGE_ACTIVITY_REQUEST_CODE) {
                Uri photoUri = data.getData();

                Bitmap selectedImage = Utils.loadFromUri(this, photoUri);
                photoFile = getPhotoFileFromBitmap(selectedImage);
                binding.ivPhoto.setImageBitmap(selectedImage);
            }
        } else {
            Toast.makeText(getActivity(), "Error with photo!", Toast.LENGTH_SHORT).show();
        }
    }

    // helper functions for camera and photo gallery intents
    private void launchCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // create a File reference for future access
        photoFile = getPhotoFileUri(photoFileName);

        // wrap File object into a content provider
        Uri fileProvider = FileProvider.getUriForFile(
                getActivity(), "com.codepath.fileprovider.nadinstagram", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            // start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    private File getPhotoFileUri(String photoFileName) {
        File mediaStorageDir = new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, "Failed to create directory");
        }

        // return the file target for the photo based on filename
        return new File(mediaStorageDir.getPath() + File.separator + photoFileName);
    }

    private File getPhotoFileFromBitmap(Bitmap map) {
        File file = getPhotoFileUri(photoFileName);
        try {
            OutputStream os = new BufferedOutputStream(new FileOutputStream(file));
            map.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.close();
            return file;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }
}