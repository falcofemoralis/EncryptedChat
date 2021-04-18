package com.vladyslav.encryptedchat.Views.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
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

import static com.vladyslav.encryptedchat.Views.MainActivity.DEBUG_TAG;

public class MainFragment extends Fragment implements MainView{
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
            //  v.findViewById(R.id.userContainer).setVisibility(View.GONE);
            ((TextView) v.findViewById(R.id.user_username)).setText("YOU");
            ((TextView) v.findViewById(R.id.user_email)).setText(model.email);
        } else {
            v.setOnClickListener(v1 -> {
                String chatId = InvitationManager.sendInvite(currentEmail, model.email);
                openChat(chatId);
            });
        }
    }

    @Override
    public void updateInvitation(View v, InvitationUpdateType type, String email, String chatId) {
        String text = "";
        if (type == InvitationUpdateType.GET_INVITE) {
            text = "WANT TO START CHAT WITH YOU";

            v.setOnClickListener(v1 -> {
                Log.d(DEBUG_TAG, "onClick: open chat on acceptor!");
                InvitationManager.deleteInvite(email, data -> {
                    openChat(chatId);
                });
            });
        }

        ((TextView) v.findViewById(R.id.user_invite)).setText(text);
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

    public void performBackPressed(){
        Log.d(DEBUG_TAG, "OnBackPressed: ");
        mainPresenter.reshowMessages();
    }
}