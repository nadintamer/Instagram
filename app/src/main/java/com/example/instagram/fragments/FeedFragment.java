package com.example.instagram.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.instagram.adapters.PostsAdapter;
import com.example.instagram.databinding.FragmentFeedBinding;
import com.example.instagram.models.Post;
import com.example.instagram.utilities.EndlessRecyclerViewScrollListener;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class FeedFragment extends Fragment {

    private static final String TAG = "FeedFragment";
    private static final int NUM_POSTS_TO_LOAD = 20;
    private static final int POST_DETAIL_REQUEST_CODE = 10;

    private FragmentFeedBinding binding;
    private List<Post> posts;
    private PostsAdapter adapter;
    private EndlessRecyclerViewScrollListener scrollListener;

    public FeedFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFeedBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        posts = new ArrayList<>();
        adapter = new PostsAdapter(this, posts, true);

        // set up recyclerview, endlessScrollListener and swipeRefreshLayout
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        binding.rvPosts.setAdapter(adapter);
        binding.rvPosts.setLayoutManager(linearLayoutManager);
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                queryPosts(totalItemsCount);
            }
        };
        binding.rvPosts.addOnScrollListener(scrollListener);

        binding.swipeContainer.setOnRefreshListener(() -> {
            adapter.clear();
            queryPosts(0);
            binding.swipeContainer.setRefreshing(false);
        });
        binding.swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        queryPosts(0);
    }

    // queries 20 posts from Parse and updates adapter
    private void queryPosts(int numToSkip) {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.setLimit(NUM_POSTS_TO_LOAD);
        query.setSkip(numToSkip);
        query.addDescendingOrder("createdAt");
        query.findInBackground((posts, e) -> {
            if (e != null) {
                Log.e(TAG, "Issue with getting posts", e);
                return;
            }
            adapter.addAll(posts);
        });
    }

    // queries a single post with the given id and updates the adapter at given position
    private void querySinglePost(String id, int position) {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.whereEqualTo("objectId", id); // get post with this specific id
        query.setLimit(20);
        query.addDescendingOrder("createdAt");
        query.findInBackground((posts, e) -> {
            if (e != null) {
                Log.e(TAG, "Issue with getting post", e);
                return;
            }

            adapter.set(position, posts.get(0));
            binding.swipeContainer.setRefreshing(false);
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // update adapter with new information from post detail activity
        if (requestCode == POST_DETAIL_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            String id = data.getStringExtra("postId");
            int position = data.getExtras().getInt("position");
            querySinglePost(id, position);
        }
    }
}