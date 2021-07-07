package com.example.instagram.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagram.activities.PostDetailActivity;
import com.example.instagram.databinding.ItemPostGridBinding;
import com.example.instagram.databinding.ItemPostFeedBinding;
import com.example.instagram.models.Post;
import com.parse.ParseFile;

import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.PostViewHolder> {

    private static final int POST_FEED = 123;
    private static final int POST_GRID = 321;

    Context context;
    List<Post> posts;
    Boolean isFeed;

    public PostsAdapter(Context context, List<Post> posts, Boolean isFeed) {
        this.context = context;
        this.posts = posts;
        this.isFeed = isFeed;
    }

    @NonNull
    @Override
    public PostsAdapter.PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == POST_FEED) {
            ItemPostFeedBinding binding = ItemPostFeedBinding.inflate(LayoutInflater.from(context), parent, false);
            return new PostFeedViewHolder(binding);
        } else if (viewType == POST_GRID) {
            ItemPostGridBinding binding = ItemPostGridBinding.inflate(LayoutInflater.from(context), parent, false);
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
                Intent i = new Intent(context, PostDetailActivity.class);
                i.putExtra("post", post);
                context.startActivity(i);
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
            binding.tvUsername.setText(post.getUser().getUsername());
            binding.tvDescription.setText(post.getDescription());
            ParseFile imageFile = post.getImage();
            if (imageFile != null) {
                Glide.with(context)
                        .load(imageFile.getUrl())
                        .fitCenter()
                        .into(binding.ivPhoto);
            }
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
                Glide.with(context)
                        .load(imageFile.getUrl())
                        .fitCenter()
                        .into(binding.ivPhoto);
            }
        }
    }
}
