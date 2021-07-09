package com.example.instagram.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagram.databinding.ItemCommentBinding;
import com.example.instagram.models.Comment;
import com.example.instagram.models.Post;
import com.example.instagram.utilities.Utils;
import com.parse.ParseException;

import java.util.List;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentViewHolder> {

    List<Comment> comments; // each comment has user and string
    Context context;

    public CommentsAdapter(Context context, List<Comment> comments) {
        this.context = context;
        this.comments = comments;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCommentBinding binding = ItemCommentBinding.inflate(LayoutInflater.from(context), parent, false);
        return new CommentsAdapter.CommentViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentsAdapter.CommentViewHolder holder, int position) {
        Comment comment = comments.get(position);
        holder.bind(comment);
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public void clear() {
        comments.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Comment> list) {
        comments.addAll(list);
        notifyDataSetChanged();
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder {

        ItemCommentBinding binding;

        public CommentViewHolder(@NonNull ItemCommentBinding itemCommentBinding) {
            super(itemCommentBinding.getRoot());
            this.binding = itemCommentBinding;
        }

        public void bind(Comment comment) {
            binding.tvUsername.setText(comment.getUser().getUsername());
            binding.tvComment.setText(comment.getContent());
            binding.tvTimestamp.setText(Utils.calculateTimeAgo(comment.getCreatedAt(), true));
            Glide.with(context)
                    .load(comment.getUser().getParseFile("profilePhoto").getUrl())
                    .circleCrop()
                    .into(binding.ivProfilePhoto);
        }
    }
}
