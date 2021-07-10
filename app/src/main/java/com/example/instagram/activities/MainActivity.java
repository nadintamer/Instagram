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
import com.example.instagram.fragments.FeedFragment;
import com.example.instagram.fragments.NewPostFragment;
import com.example.instagram.fragments.ProfileFragment;
import com.example.instagram.fragments.SearchFragment;
import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        // set up toolbar
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setTitle("");

        binding.bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            switchToSelectedFragment(item);
            return true;
        });
        binding.bottomNavigation.setSelectedItemId(R.id.action_home); // default tab is home
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

    private void switchToSelectedFragment(MenuItem item) {
        final FragmentManager fragmentManager = getSupportFragmentManager();
        final Fragment feedFragment = new FeedFragment();
        final Fragment searchFragment = new SearchFragment();
        final Fragment newPostFragment = new NewPostFragment();
        final Fragment profileFragment = ProfileFragment.newInstance(ParseUser.getCurrentUser());

        Fragment fragment;
        switch (item.getItemId()) {
            case R.id.action_home:
                fragment = feedFragment;
                break;
            case R.id.action_search:
                fragment = searchFragment;
                break;
            case R.id.action_capture:
                fragment = newPostFragment;
                break;
            case R.id.action_profile:
            default:
                fragment = profileFragment;
                break;
        }
        fragmentManager.beginTransaction().replace(R.id.fragmentContainer, fragment).commit();
    }
}