package com.example.instagram.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.instagram.R;
import com.example.instagram.databinding.ActivityMainBinding;
import com.example.instagram.databinding.FragmentNewPostBinding;
import com.example.instagram.models.Post;
import com.example.instagram.utilities.BitmapScaler;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class NewPostFragment extends Fragment {

    private static final String TAG = "NewPostFragment";
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 42;
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
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String description = binding.etDescription.getText().toString();
                if (description.isEmpty()) {
                    Toast.makeText(getActivity(), "Description cannot be empty!", Toast.LENGTH_SHORT).show();;
                    return;
                }

                if (photoFile == null || binding.ivPhoto.getDrawable() == null) {
                    Toast.makeText(getActivity(), "There is no image!", Toast.LENGTH_SHORT).show();;
                    return;
                }

                ParseUser currentUser = ParseUser.getCurrentUser();
                savePost(description, currentUser, photoFile);
            }
        });

        binding.btnTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchCamera();
            }
        });
    }

    private void launchCamera() {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference for future access
        photoFile = getPhotoFileUri(photoFileName);

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(getActivity(), "com.codepath.fileprovider.nadinstagram", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    private File getPhotoFileUri(String photoFileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, "Failed to create directory");
        }

        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + photoFileName);

        return file;
    }

    private void savePost(String description, ParseUser currentUser, File photoFile) {
        Post post = new Post();
        post.setDescription(description);
        post.setImage(new ParseFile(photoFile));
        post.setUser(currentUser);
        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error while saving post", e);
                    Toast.makeText(getActivity(), "Error while saving post!", Toast.LENGTH_SHORT).show();
                }
                Log.i(TAG, "Post save successful!");
                binding.etDescription.setText("");
                binding.ivPhoto.setImageResource(0);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                // by this point we have the camera photo on disk
                Bitmap rawTakenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                // RESIZE BITMAP, see section below
                Bitmap resizedBitmap = BitmapScaler.scaleToFitWidth(rawTakenImage, MAX_IMAGE_WIDTH);

                // Configure byte output stream
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                // Compress the image further
                resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 40, bytes);
                // Create a new file for the resized bitmap (`getPhotoFileUri` defined above)
                File resizedFile = getPhotoFileUri(photoFileName);
                try {
                    resizedFile.createNewFile();
                    FileOutputStream fos = new FileOutputStream(resizedFile);
                    // Write the bytes of the bitmap to file
                    fos.write(bytes.toByteArray());
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // Load the taken image into a preview
                binding.ivPhoto.setImageBitmap(resizedBitmap);
            } else { // Result was a failure
                Toast.makeText(getActivity(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}