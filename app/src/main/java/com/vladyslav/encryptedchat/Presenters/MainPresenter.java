package com.vladyslav.encryptedchat.Presenters;

import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.vladyslav.encryptedchat.Constants.InvitationUpdateType;
import com.vladyslav.encryptedchat.Managers.InvitationManager;
import com.vladyslav.encryptedchat.Models.UserModel;
import com.vladyslav.encryptedchat.Models.UserModel.User;
import com.vladyslav.encryptedchat.ViewsInterfaces.MainView;

import java.util.List;
import java.util.Map;

import static com.vladyslav.encryptedchat.Constants.InviteMapKeys.CHAT_ID_KEY;
import static com.vladyslav.encryptedchat.Constants.InviteMapKeys.EMAIL_FROM_KEY;
import static com.vladyslav.encryptedchat.Views.MainActivity.DEBUG_TAG;

public class MainPresenter {
    private FirebaseListAdapter<User> usersAdapter;
    private UserModel userModel;
    private MainView mainView;
    private int adapterPosition;
    private boolean isLoaded;
    private List<Map<String, String>> invites;
    private FirebaseUser currentUser;

    public MainPresenter(MainView mainView) {
        this.mainView = mainView;
        this.userModel = new UserModel();
        init();
    }

    public void addUser() {
        userModel.registerUser(data -> getActiveUsers());
    }

    public void deleteUser() {
        userModel.unregisterUser(usersAdapter, adapterPosition);
    }

    public void init() {
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        mainView.updateDeviceUserInfo(currentUser.getEmail());
    }

    public void getActiveUsers() {
        usersAdapter = new FirebaseListAdapter<User>(userModel.getUsersAdapterOptions()) {
            @Override
            protected void populateView(@NonNull View v, @NonNull User model, int position) {
                String currentEmail = currentUser.getEmail();

                // Обновляем информацию в обьекте
                mainView.updateInvitation(v, InvitationUpdateType.CLEAR_INVITE, null, null);
                mainView.updateUserInfo(v, model, currentUser.getEmail());

                // Запоминаем позицию текущего юзера
                if (model.email.equals(currentEmail))
                    adapterPosition = position;

                if (invites != null) {
                    for (Map<String, String> invite : invites) {
                        if (model.email.equals(invite.get(EMAIL_FROM_KEY))) {
                            mainView.updateInvitation(v, InvitationUpdateType.GET_INVITE, invite.get(EMAIL_FROM_KEY), invite.get(CHAT_ID_KEY));
                        }
                    }
                }

                // Когда все юзеры будут проставленны - загружаем инвайты
                if (!isLoaded && position == usersAdapter.getCount() - 1) {
                    isLoaded = true;
                    getCurrentInvitations();
                }
            }
        };

        usersAdapter.startListening();
        mainView.setActiveUsers(usersAdapter);
    }

    public void getCurrentInvitations() {
        InvitationManager.getInvitationList(data -> {
            Log.d(DEBUG_TAG, "downloaded CurrentInvitations");
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            invites = null;

            if (data != null) {
                for (Map.Entry<String, List<Map<String, String>>> entry : data.entrySet()) {
                    if (InvitationManager.denormalizeKey(entry.getKey()).equals(currentUser.getEmail())) {
                        invites = entry.getValue();
                        usersAdapter.notifyDataSetChanged();
                        return;
                    }
                }
            }
        });
    }

    public void reshowMessages() {
        Log.d(DEBUG_TAG, "reshowMessages: ");
        if (usersAdapter != null)
            usersAdapter.notifyDataSetChanged();
    }
}
