package com.vladyslav.encryptedchat.Managers;

import com.vladyslav.encryptedchat.Models.ChatModel;
import com.vladyslav.encryptedchat.Models.InvitationModel;
import com.vladyslav.encryptedchat.lib.ExCallable;

import java.util.List;
import java.util.Map;

public class InvitationManager {
    public static String sendInvite(String emailFrom, String emailTo) {
        String chatId = ChatModel.getInstance(null).getChatId();
        InvitationModel.getInstance().addInvite(emailFrom, emailTo, chatId);
        return chatId;
    }

    public static void getInvitationList(ExCallable<Map<String, List<Map<String, String>>>> exCallable) {
        InvitationModel.getInstance().getInvitations(exCallable);
    }

    public static void deleteInvite(String email, ExCallable<Void> exCallable) {
        InvitationModel.getInstance().removeInvite(email, exCallable);
    }

    public static String normalizeKey(String key) {
        return key.replace(".", "*");
    }

    public static String denormalizeKey(String key) {
        return key.replace("*", ".");
    }
}
