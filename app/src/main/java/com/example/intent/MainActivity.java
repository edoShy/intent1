package com.example.intent;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.Objects;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth auth;
    GoogleSignInClient googleSignInClient;
    ShapeableImageView imageView;
    TextView name, mail;

    private final ActivityResultLauncher<Intent> activityResultLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult result) {
                            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                                Task<GoogleSignInAccount> accountTask = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                                try {
                                    GoogleSignInAccount signInAccount = accountTask.getResult(ApiException.class);
                                    if (signInAccount != null) {
                                        AuthCredential authCredential = GoogleAuthProvider.getCredential(signInAccount.getIdToken(), null);
                                        auth.signInWithCredential(authCredential)
                                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                                        if (task.isSuccessful()) {
                                                            FirebaseUser user = auth.getCurrentUser();
                                                            if (user != null) {
                                                                Glide.with(MainActivity.this)
                                                                        .load(user.getPhotoUrl())
                                                                        .into(imageView);
                                                                name.setText(user.getDisplayName());
                                                                mail.setText(user.getEmail());
                                                            }
                                                            Toast.makeText(MainActivity.this, "Signed in successfully!", Toast.LENGTH_SHORT).show();
                                                        } else {
                                                            Toast.makeText(MainActivity.this, "Sign-in failed: " + task.getException(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    }
                                } catch (ApiException e) {
                                    Log.e("SignInError", "Google Sign-In failed: " + e.getMessage());
                                }
                            }
                        }
                    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);

        imageView = findViewById(R.id.profileImage);
        name = findViewById(R.id.nameTV);
        mail = findViewById(R.id.mailTV);

        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(MainActivity.this, options);
        auth = FirebaseAuth.getInstance();

        Button openBrowserButton = findViewById(R.id.openBrowserButton);
        Button makeCallButton = findViewById(R.id.makeCallButton);
        Button shareTextButton = findViewById(R.id.shareTextButton);

        // ✅ FIX: Use the correct type for the Google Sign-In button
        SignInButton signInButton = findViewById(R.id.signIn);

        openBrowserButton.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com"));
            startActivity(browserIntent);
        });

        makeCallButton.setOnClickListener(v -> {
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:1234567890"));
            startActivity(callIntent);
        });

        shareTextButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ShareActivity.class);
            startActivity(intent);
        });

        signInButton.setOnClickListener(view -> {
            Intent intent = googleSignInClient.getSignInIntent();
            activityResultLauncher.launch(intent);
        });
    }
}
