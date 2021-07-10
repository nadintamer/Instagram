package com.example.instagram.utilities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;

import androidx.fragment.app.Fragment;

import com.parse.ParseUser;

import java.io.IOException;
import java.util.Date;

public class Utils {
    public static String calculateTimeAgo(Date createdAt, Boolean shortened) {

        int SECOND_MILLIS = 1000;
        int MINUTE_MILLIS = 60 * SECOND_MILLIS;
        int HOUR_MILLIS = 60 * MINUTE_MILLIS;
        int DAY_MILLIS = 24 * HOUR_MILLIS;

        try {
            createdAt.getTime();
            long time = createdAt.getTime();
            long now = System.currentTimeMillis();

            final long diff = now - time;
            if (diff < MINUTE_MILLIS) {
                return "just now";
            } else if (diff < 2 * MINUTE_MILLIS) {
                return "a minute ago";
            } else if (diff < 50 * MINUTE_MILLIS) {
                return diff / MINUTE_MILLIS + (shortened ?  "m" : " minutes ago");
            } else if (diff < 90 * MINUTE_MILLIS) {
                return "an hour ago";
            } else if (diff < 24 * HOUR_MILLIS) {
                return diff / HOUR_MILLIS + (shortened ?  "h" : " hours ago");
            } else if (diff < 48 * HOUR_MILLIS) {
                return "yesterday";
            } else {
                return diff / DAY_MILLIS + (shortened ?  "d" : " days ago");
            }
        } catch (Exception e) {
            Log.e("Error:", "calculateTimeAgo failed", e);
        }

        return "";
    }

    public static String getProfilePhotoUrl(ParseUser user) {
        return user.getParseFile("profilePhoto").getUrl();
    }

    public static void launchGallery(Fragment fragment, int requestCode) {
        // create intent for picking a photo from the gallery
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        if (intent.resolveActivity(fragment.getActivity().getPackageManager()) != null) {
            // bring up gallery to select a photo
            fragment.startActivityForResult(intent, requestCode);
        }
    }

    public static Bitmap loadFromUri(Fragment fragment, Uri photoUri) {
        Bitmap image = null;
        try {
            // check version of Android on device
            if (Build.VERSION.SDK_INT > 27){
                ImageDecoder.Source source = ImageDecoder.createSource(fragment.getActivity().getContentResolver(), photoUri);
                image = ImageDecoder.decodeBitmap(source);
            } else {
                image = MediaStore.Images.Media.getBitmap(fragment.getActivity().getContentResolver(), photoUri);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }
}
