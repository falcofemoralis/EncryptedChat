package com.vladyslav.encryptedchat.Models;

import android.util.Log;

import androidx.annotation.NonNull;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.vladyslav.encryptedchat.R;
import com.vladyslav.encryptedchat.lib.ExCallable;

import static com.vladyslav.encryptedchat.Views.MainActivity.DEBUG_TAG;

public class UserModel {
    private final String USERS_STORAGE = "users"; // Путь к расположению пользователей в базе

    public static class User {
        public String username;
        public String email;

        public User() {
        }

        public User(String username, String email) {
            this.username = username;
            this.email = email;
        }
    }

    public FirebaseListOptions<User> getUsersAdapterOptions() {
        return new FirebaseListOptions.Builder<User>()
                .setQuery(FirebaseDatabase.getInstance().getReference(USERS_STORAGE), User.class)
                .setLayout(R.layout.user_item)
                .build();
    }

    public void registerUser(ExCallable<Void> exCallable) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase.getInstance().getReference(USERS_STORAGE).push().setValue(new User(currentUser.getDisplayName(), currentUser.getEmail())).addOnCompleteListener(task -> {
            exCallable.call(null);
        });
    }

    public void unregisterUser(FirebaseListAdapter<User> adapter, int position) {
        Log.d(DEBUG_TAG, "unregisterUser: " + adapter.getRef(position));
        adapter.getRef(position).removeValue();
    }
}
