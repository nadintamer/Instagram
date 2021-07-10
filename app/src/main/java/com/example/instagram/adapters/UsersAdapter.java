package com.example.instagram.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagram.R;
import com.example.instagram.databinding.ItemCommentBinding;
import com.example.instagram.databinding.ItemUserBinding;
import com.example.instagram.fragments.ProfileFragment;
import com.example.instagram.models.Comment;
import com.example.instagram.utilities.Utils;
import com.parse.ParseUser;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder> {
    List<ParseUser> users;
    Fragment fragment;

    public UsersAdapter(Fragment fragment, List<ParseUser> users) {
        this.fragment = fragment;
        this.users = users;
    }

    @NonNull
    @Override
    public UsersAdapter.UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemUserBinding binding = ItemUserBinding.inflate(
                LayoutInflater.from(fragment.getActivity()), parent, false);
        return new UsersAdapter.UserViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersAdapter.UserViewHolder holder, int position) {
        ParseUser user = users.get(position);
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public void clear() {
        users.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<ParseUser> list) {
        users.addAll(list);
        notifyDataSetChanged();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ItemUserBinding binding;

        public UserViewHolder(@NonNull ItemUserBinding itemUserBinding) {
            super(itemUserBinding.getRoot());
            this.binding = itemUserBinding;
            itemUserBinding.getRoot().setOnClickListener(this);
        }

        public void bind(ParseUser user) {
            binding.tvUsername.setText(user.getUsername());
            Glide.with(fragment.getActivity())
                    .load(user.getParseFile("profilePhoto").getUrl())
                    .circleCrop()
                    .into(binding.ivProfilePhoto);
        }

        @Override
        public void onClick(View v) {
            // clicking on a username leads to user profile
            int position = getAdapterPosition();
            final FragmentManager fragmentManager = fragment.getActivity().getSupportFragmentManager();
            final Fragment profileFragment = ProfileFragment.newInstance(users.get(position));

            fragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, profileFragment)
                    .addToBackStack("")
                    .commit();
        }
    }
}
