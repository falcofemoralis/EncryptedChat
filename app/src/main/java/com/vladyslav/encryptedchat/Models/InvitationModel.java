package com.vladyslav.encryptedchat.Models;

import android.util.ArrayMap;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.vladyslav.encryptedchat.Managers.InvitationManager;
import com.vladyslav.encryptedchat.lib.ExCallable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.vladyslav.encryptedchat.Constants.InviteMapKeys.CHAT_ID_KEY;
import static com.vladyslav.encryptedchat.Constants.InviteMapKeys.EMAIL_FROM_KEY;
import static com.vladyslav.encryptedchat.Views.MainActivity.DEBUG_TAG;

public class InvitationModel {
    private static InvitationModel instance;
    private static final String INVITATIONS_STORAGE = "invitations";
    private DatabaseReference invitationRef;

    public static InvitationModel getInstance() {
        if (instance == null) {
            instance = new InvitationModel();
            instance.init();
        }
        return instance;
    }

    public void init() {
        invitationRef = FirebaseDatabase.getInstance().getReference(INVITATIONS_STORAGE);
    }

    public void getInvitations(ExCallable<Map<String, List<Map<String, String>>>> exCallable) {
        invitationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String, List<Map<String, String>>> serverInvitations = (Map<String, List<Map<String, String>>>) dataSnapshot.getValue();
                if (serverInvitations == null) serverInvitations = new ArrayMap<>();
                Log.d(DEBUG_TAG, "downloaded invites" + serverInvitations);
                exCallable.call(serverInvitations);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(DEBUG_TAG, "onCancelled: " + error.getDetails());
            }
        });
    }

    public void addInvite(String emailFrom, String emailTo, String chatId) {
        invitationRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                Map<String, List<Map<String, String>>> serverInvitations = (Map<String, List<Map<String, String>>>) currentData.getValue();
                if (serverInvitations == null)
                    serverInvitations = new ArrayMap<>();

                List<Map<String, String>> invites = serverInvitations.get(InvitationManager.normalizeKey(emailTo));
                if (invites == null)
                    invites = new ArrayList<>();

                Map<String, String> invite = new ArrayMap<>();
                invite.put(EMAIL_FROM_KEY, emailFrom);
                invite.put(CHAT_ID_KEY, chatId);

                invites.add(invite);
                serverInvitations.put(InvitationManager.normalizeKey(emailTo), invites);

                currentData.setValue(serverInvitations);
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                // Completed
            }
        });
    }

    public void removeInvite(String email) {
        invitationRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                String clientEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

                Map<String, List<Map<String, String>>> serverInvitations = (Map<String, List<Map<String, String>>>) currentData.getValue();
                List<Map<String, String>> invites = serverInvitations.get(InvitationManager.normalizeKey(clientEmail));

                for (int i = 0; i < invites.size(); ++i) {
                    Map<String, String> invite = invites.get(i);
                    if (invite.get(EMAIL_FROM_KEY).equals(email)) {
                        invites.remove(i);
                        break;
                    }
                }

                serverInvitations.put(InvitationManager.normalizeKey(clientEmail), invites);
                currentData.setValue(serverInvitations);
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                // Completed
            }
        });
    }
}
