package com.vladyslav.encryptedchat.Views.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.firebase.ui.database.FirebaseListAdapter;
import com.vladyslav.encryptedchat.Constants.InvitationUpdateType;
import com.vladyslav.encryptedchat.Managers.InvitationManager;
import com.vladyslav.encryptedchat.Models.UserModel.User;
import com.vladyslav.encryptedchat.Presenters.MainPresenter;
import com.vladyslav.encryptedchat.R;
import com.vladyslav.encryptedchat.ViewsInterfaces.MainView;

import static com.vladyslav.encryptedchat.Views.MainActivity.DEBUG_TAG;

public class MainFragment extends Fragment implements MainView {
    private View currentFragment;
    private MainPresenter mainPresenter;
    private ListView usersList;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        currentFragment = inflater.inflate(R.layout.fragment_main, container, false);
        usersList = currentFragment.findViewById(R.id.usersList);
        mainPresenter = new MainPresenter(this);
        return currentFragment;
    }

    @Override
    public void setActiveUsers(FirebaseListAdapter<User> usersAdapter) {
        ListView UsersList = currentFragment.findViewById(R.id.usersList);
        UsersList.setAdapter(usersAdapter);
    }

    @Override
    public void updateUserInfo(View v, User model, String currentEmail) {
        ((TextView) v.findViewById(R.id.user_username)).setText(model.username);
        ((TextView) v.findViewById(R.id.user_email)).setText(model.email);

        if (model.email.equals(currentEmail)) {
            //  v.findViewById(R.id.userContainer).setVisibility(View.GONE);
            ((TextView) v.findViewById(R.id.user_username)).setText("YOU");
            ((TextView) v.findViewById(R.id.user_email)).setText(model.email);
        } else {
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    InvitationManager.sendInvite(currentEmail, model.email);
                    updateInvitation(v, InvitationUpdateType.SENT_INVITE);
                }
            });
        }
    }

    @Override
    public void updateInvitation(View v, InvitationUpdateType type) {
        String text = "";
        if (type == InvitationUpdateType.GET_INVITE) {
            text = "WANT TO START CHAT WITH YOU";
        } else if (type == InvitationUpdateType.SENT_INVITE) {
            text = "INVITE WAS SENT";
        }

        ((TextView) v.findViewById(R.id.user_invite)).setText(text);

        if (type == InvitationUpdateType.GET_INVITE ) {
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(DEBUG_TAG, "onClick: open chat!");
                }
            });
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        mainPresenter.addUser();
    }

    @Override
    public void onPause() {
        mainPresenter.deleteUser();
        super.onPause();
    }
}