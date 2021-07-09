package com.example.instagram.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagram.R;
import com.example.instagram.activities.PostDetailActivity;
import com.example.instagram.databinding.ItemPostGridBinding;
import com.example.instagram.databinding.ItemPostFeedBinding;
import com.example.instagram.fragments.ProfileFragment;
import com.example.instagram.models.Post;
import com.example.instagram.utilities.Utils;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.PostViewHolder> {

    private static final int POST_FEED = 123;
    private static final int POST_GRID = 321;
    private static final String TAG = "PostsAdapter";

    Fragment fragment;
    List<Post> posts;
    Boolean isFeed;

    public PostsAdapter(Fragment fragment, List<Post> posts, Boolean isFeed) {
        this.fragment = fragment;
        this.posts = posts;
        this.isFeed = isFeed;
    }

    @NonNull
    @Override
    public PostsAdapter.PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == POST_FEED) {
            ItemPostFeedBinding binding = ItemPostFeedBinding.inflate(LayoutInflater.from(fragment.getActivity()), parent, false);
            return new PostFeedViewHolder(binding);
        } else if (viewType == POST_GRID) {
            ItemPostGridBinding binding = ItemPostGridBinding.inflate(LayoutInflater.from(fragment.getActivity()), parent, false);
            return new PostGridViewHolder(binding);
        } else {
            throw new IllegalArgumentException("Unknown view type");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull PostsAdapter.PostViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (isFeed) {
            return POST_FEED;
        } else {
            return POST_GRID;
        }
    }

    public void clear() {
        posts.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Post> list) {
        posts.addAll(list);
        notifyDataSetChanged();
    }

    public void set(int position, Post post) {
        posts.set(position, post);
        notifyItemChanged(position);
    }

    public abstract class PostViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        abstract void bind(Post post);

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Post post = posts.get(position);
                Intent i = new Intent(fragment.getActivity(), PostDetailActivity.class);
                i.putExtra("post", post);
                i.putExtra("position", position);
                fragment.startActivityForResult(i, 10);
            }
        }
    }

    public class PostFeedViewHolder extends PostViewHolder {

        ItemPostFeedBinding binding;

        public PostFeedViewHolder(@NonNull ItemPostFeedBinding itemPostBinding) {
            super(itemPostBinding.getRoot());
            this.binding = itemPostBinding;
            itemPostBinding.getRoot().setOnClickListener(this);
        }

        public void bind(Post post) {
            binding.tvUsernameTop.setText(post.getUser().getUsername());
            binding.tvUsernameBottom.setText(post.getUser().getUsername());
            binding.tvTimestamp.setText(Utils.calculateTimeAgo(post.getCreatedAt()));

            binding.tvUsernameTop.setOnClickListener(v -> goToUserProfile());
            binding.tvUsernameBottom.setOnClickListener(v -> goToUserProfile());
            binding.ivProfilePhoto.setOnClickListener(v -> goToUserProfile());

            binding.tvDescription.setText(post.getDescription());
            ParseFile imageFile = post.getImage();
            if (imageFile != null) {
                Glide.with(fragment.getActivity())
                        .load(imageFile.getUrl())
                        .fitCenter()
                        .placeholder(R.drawable.placeholder)
                        .into(binding.ivPhoto);
            }

            ParseFile profilePhoto = post.getUser().getParseFile("profilePhoto");
            if (profilePhoto != null) {
                Glide.with(fragment.getActivity())
                        .load(profilePhoto.getUrl())
                        .circleCrop()
                        .into(binding.ivProfilePhoto);
            }
        }

        private void goToUserProfile() {
            ParseQuery<ParseUser> query = ParseUser.getQuery();
            String username = binding.tvUsernameTop.getText().toString();
            query.whereEqualTo("username", username); // find adults
            query.findInBackground(new FindCallback<ParseUser>() {
                public void done(List<ParseUser> objects, ParseException e) {
                    if (e != null) {
                        Log.e(TAG, "Error querying user", e);
                        return;
                    }
                    final FragmentManager fragmentManager = fragment.getActivity().getSupportFragmentManager();
                    final Fragment profileFragment = ProfileFragment.newInstance(objects.get(0));

                    fragmentManager.beginTransaction()
                            .replace(R.id.fragmentContainer, profileFragment)
                            .addToBackStack("")
                            .commit();
                }
            });
        }
    }

    public class PostGridViewHolder extends PostViewHolder {

        ItemPostGridBinding binding;

        public PostGridViewHolder(@NonNull ItemPostGridBinding itemPostBinding) {
            super(itemPostBinding.getRoot());
            this.binding = itemPostBinding;
            itemPostBinding.getRoot().setOnClickListener(this);
        }

        public void bind(Post post) {
            ParseFile imageFile = post.getImage();
            if (imageFile != null) {
                Glide.with(fragment.getActivity())
                        .load(imageFile.getUrl())
                        .fitCenter()
                        .into(binding.ivPhoto);
            }
        }
    }
}
