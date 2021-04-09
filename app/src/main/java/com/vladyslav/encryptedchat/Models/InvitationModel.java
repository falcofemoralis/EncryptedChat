package com.vladyslav.encryptedchat.Models;

import android.util.ArrayMap;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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

    public void getInvitations(ExCallable exCallable) {
        invitationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String, List<String>> invites = (Map) dataSnapshot.getValue();
                if (invites == null) invites = new ArrayMap<>();
                Log.d(DEBUG_TAG, "downloaded invites" + invites);
                exCallable.call(invites);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(DEBUG_TAG, "onCancelled: " + error.getDetails());
            }
        });
    }

    public void addInvite(String emailFrom, String emailTo) {
        invitationRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                Map<String, List<String>> serverInvites = (Map) currentData.getValue();
                if (serverInvites == null)
                    serverInvites = new ArrayMap<>();

                List<String> emails = serverInvites.get(InvitationManager.normalizeKey(emailTo));
                if (emails == null)
                    emails = new ArrayList<>();

                emails.add(emailFrom);
                serverInvites.put(InvitationManager.normalizeKey(emailTo), emails);

                currentData.setValue(serverInvites);
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                // Completed
            }
        });
    }
}
