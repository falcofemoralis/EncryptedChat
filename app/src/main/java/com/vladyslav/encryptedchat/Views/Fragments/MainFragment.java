package com.vladyslav.encryptedchat.Views.Fragments;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.firebase.ui.database.FirebaseListAdapter;
import com.vladyslav.encryptedchat.Constants.InvitationUpdateType;
import com.vladyslav.encryptedchat.Managers.InvitationManager;
import com.vladyslav.encryptedchat.Models.UserModel.User;
import com.vladyslav.encryptedchat.Presenters.MainPresenter;
import com.vladyslav.encryptedchat.R;
import com.vladyslav.encryptedchat.Views.OnFragmentInteractionListener;
import com.vladyslav.encryptedchat.ViewsInterfaces.MainView;

public class MainFragment extends Fragment implements MainView {
    private View currentFragment;
    private MainPresenter mainPresenter;
    private ListView usersList;
    private OnFragmentInteractionListener fragmentListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        fragmentListener = (OnFragmentInteractionListener) context;
    }


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
        usersList.setAdapter(usersAdapter);
    }



    @Override
    public void updateUserInfo(View v, User model, String currentEmail) {
        ((TextView) v.findViewById(R.id.user_username)).setText(model.username);
        ((TextView) v.findViewById(R.id.user_email)).setText(model.email);

        if (model.email.equals(currentEmail)) {
            // TODO берется лаяут у всех, соотвественно сообщений нету. getParent берет одного и того же отца. Проблему решить не смог
            //((RelativeLayout) v.findViewById(R.id.user_container)).setVisibility(View.GONE);
        } else {
            v.setOnClickListener(v1 -> {
                String chatId = InvitationManager.sendInvite(currentEmail, model.email);
                openChat(chatId);
            });
        }
    }

    @Override
    public void updateInvitation(View v, InvitationUpdateType type, String email, String chatId) {
        if (type == InvitationUpdateType.GET_INVITE) {
            v.setOnClickListener(v1 -> {
                InvitationManager.deleteInvite(email, data -> {
                    openChat(chatId);
                });
            });
            ((TextView) v.findViewById(R.id.user_invite)).setVisibility(View.VISIBLE);
            ((TextView) v.findViewById(R.id.user_username)).setTypeface(null, Typeface.BOLD);
            ((TextView) v.findViewById(R.id.new_invite)).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void updateDeviceUserInfo(@Nullable String email) {
        ((TextView) currentFragment.findViewById(R.id.deviceUser_email)).setText(email);
    }

    private void openChat(@Nullable String chatId) {
        Bundle bundle = null;
        if (chatId != null) {
            bundle = new Bundle();
            bundle.putSerializable("chatId", chatId);
        }
        fragmentListener.onFragmentInteraction(this, new ChatFragment(),
                OnFragmentInteractionListener.Action.NEXT_FRAGMENT_HIDE, bundle, "OPENED_CHAT");
    }

    @Override
    public void onResume() {
        mainPresenter.addUser();
        super.onResume();
    }

    @Override
    public void onStop() {
        mainPresenter.deleteUser();
        super.onStop();
    }

    public void performBackPressed() {
        mainPresenter.reshowMessages();
    }
}