package com.vladyslav.encryptedchat.ViewsInterfaces;

import android.view.View;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.firebase.ui.database.FirebaseListAdapter;
import com.vladyslav.encryptedchat.Constants.InvitationUpdateType;
import com.vladyslav.encryptedchat.Models.UserModel.User;

public interface MainView {
    void setActiveUsers(FirebaseListAdapter<User> adapter);

    void updateUserInfo(View v, User model, String currentEmail);

    void updateInvitation(View v, InvitationUpdateType type, @Nullable String email, @Nullable String chatId);

    void updateDeviceUserInfo(@Nullable String email);
}
