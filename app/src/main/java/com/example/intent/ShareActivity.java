package com.example.intent;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class ShareActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        Log.e("SSSSSS","here");

        EditText shareEditText = findViewById(R.id.shareEditText);
        Button shareButton = findViewById(R.id.shareButton);

        shareButton.setOnClickListener(v -> {
            String textToShare = shareEditText.getText().toString();
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_TEXT, textToShare);
            shareIntent.setType("text/plain");
            startActivity(Intent.createChooser(shareIntent, "Share text via"));
        });
    }
}
