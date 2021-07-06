package com.example.instagram.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagram.databinding.ItemPostBinding;
import com.example.instagram.models.Post;
import com.parse.ParseFile;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    Context context;
    List<Post> posts;

    public PostAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.posts = posts;
    }

    @NonNull
    @Override
    public PostAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPostBinding binding = ItemPostBinding.inflate(LayoutInflater.from(context), parent, false);
        return new PostAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PostAdapter.ViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public void clear() {
        posts.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Post> list) {
        posts.addAll(list);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ItemPostBinding binding;

        public ViewHolder(@NonNull ItemPostBinding itemPostBinding) {
            super(itemPostBinding.getRoot());
            this.binding = itemPostBinding;
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
}
