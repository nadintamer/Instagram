package com.example.instagram.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.instagram.R;
import com.example.instagram.databinding.ActivityMainBinding;
import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        // set up toolbar
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setTitle("");

        final FragmentManager fragmentManager = getSupportFragmentManager();
        final Fragment feedFragment = new FeedFragment();
        final Fragment newPostFragment = new NewPostFragment();

        binding.bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.action_home:
                    fragment = feedFragment;
                    break;
                case R.id.action_capture:
                    fragment = newPostFragment;
                    break;
                case R.id.action_profile:
                default:
                    fragment = feedFragment;
                    break;
            }
            fragmentManager.beginTransaction().replace(R.id.fragmentContainer, fragment).commit();
            return true;
        });
        binding.bottomNavigation.setSelectedItemId(R.id.action_home);
    }

    private void goLoginActivity() {
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_feed, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            ParseUser.logOut();
            goLoginActivity();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}