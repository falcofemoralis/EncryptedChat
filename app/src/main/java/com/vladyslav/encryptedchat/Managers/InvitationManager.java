package com.vladyslav.encryptedchat.Managers;

import com.vladyslav.encryptedchat.Models.InvitationModel;
import com.vladyslav.encryptedchat.lib.ExCallable;

import java.util.List;
import java.util.Map;

public class InvitationManager {
    public static void sendInvite(String emailFrom, String emailTo) {
        InvitationModel.getInstance().addInvite(emailFrom, emailTo);
    }

    public static void getInvitationList(ExCallable<Map<String, List<String>>> exCallable) {
        InvitationModel.getInstance().getInvitations(exCallable);
    }

    public static String normalizeKey(String key) {
        return key.replace(".", "*");
    }

    public static String denormalizeKey(String key){
        return key.replace("*", ".");
    }
}
