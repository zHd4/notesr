package com.git.notesr;

import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import com.git.notesr.controllers.Crypto;
import com.git.notesr.controllers.Database;
import com.git.notesr.models.ActivityTools;
import com.git.notesr.models.Config;

public class RecoveryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recovery_activity);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);

        final EditText keyText = findViewById(R.id.keyText);
        final Button decryptButton =  findViewById(R.id.decryptButton);

        decryptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String keyString = ActivityTools.hexToKey(keyText.getText().toString());
                    byte[] key = Base64.decode(keyString, Base64.DEFAULT);
                    Database db = new Database(getApplicationContext());

                    Crypto.decrypt(db.getAllNotes()[0][0], ActivityTools.sha256(keyString), key);

                    Config.cryptoKey = Base64.encodeToString(key, Base64.DEFAULT);
                    AccessActivity.operation = AccessActivity.CREATE_PIN;

                    startActivity(ActivityTools.getIntent(
                            getApplicationContext(),
                            AccessActivity.class
                    ));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
