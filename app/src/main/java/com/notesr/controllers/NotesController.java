package com.notesr.controllers;

import android.content.Context;
import android.util.Base64;
import com.notesr.models.ActivityTools;
import com.notesr.models.Config;

public class NotesController {

    public static String[][] getNotes(Context context) throws Exception {
        DatabaseController db = new DatabaseController(context);
        String[][] notes = db.getAllNotes();

        if(!notes.equals(new String[0][0]) && Config.cryptoKey != null) {
            byte[] key = Base64.decode(Config.cryptoKey, Base64.DEFAULT);

            for(int i = 0; i < notes.length; i++) {
                notes[i][0] = CryptoController.decrypt(notes[i][0], ActivityTools.sha256(Config.cryptoKey), key);
                notes[i][1] = CryptoController.decrypt(notes[i][1], ActivityTools.sha256(Config.cryptoKey), key);
            }

            return notes;
        } else {
            return new String[0][0];
        }
    }

    public static void setNotes(Context context, String[][] notes) throws Exception {
        DatabaseController db = new DatabaseController(context);
        byte[] key = Base64.decode(Config.cryptoKey, Base64.DEFAULT);

        for(int i = 0; i < notes.length; i++) {
            notes[i][0] = CryptoController.encrypt(notes[i][0], ActivityTools.sha256(Config.cryptoKey), key);
            notes[i][1] = CryptoController.encrypt(notes[i][1], ActivityTools.sha256(Config.cryptoKey), key);
        }

        db.setAllNotes(notes);
    }
}