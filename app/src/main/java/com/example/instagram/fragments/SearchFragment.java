package com.example.instagram.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.example.instagram.adapters.PostsAdapter;
import com.example.instagram.adapters.UsersAdapter;
import com.example.instagram.databinding.FragmentSearchBinding;
import com.example.instagram.models.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private static final String TAG = "SearchFragment";
    FragmentSearchBinding binding;
    private List<ParseUser> users;
    private UsersAdapter adapter;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        users = new ArrayList<>();
        adapter = new UsersAdapter(this, users);

        binding.rvUsers.setAdapter(adapter);
        binding.rvUsers.setLayoutManager(new LinearLayoutManager(getActivity()));

        binding.searchBar.setOnClickListener(v -> binding.searchBar.onActionViewExpanded());
        binding.searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // fetch users
                searchUsers(query);
                // reset searchView
                binding.searchBar.clearFocus();
                binding.searchBar.setQuery("", false);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
    }

    private void searchUsers(String searchQuery) {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        // only get users whose username starts with the search query
        query.whereStartsWith("username", searchQuery);
        query.addDescendingOrder("createdAt");
        query.findInBackground((users, e) -> {
            // check for errors
            if (e != null) {
                Log.e(TAG, "Issue with getting posts", e);
                return;
            }

            adapter.clear();
            adapter.addAll(users);
        });
    }
}