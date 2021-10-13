package com.example.mycaloriecalc.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mycaloriecalc.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    EditText email, password;
    Button btnLogin;

    FirebaseAuth auth;
    TextView register_text;

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        pref = getSharedPreferences("result", MODE_PRIVATE);
        editor = pref.edit();

        auth = FirebaseAuth.getInstance();

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        btnLogin = findViewById(R.id.btn_login);
        register_text = findViewById(R.id.register_text);

        register_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txt_email = email.getText().toString();
                String txt_password = password.getText().toString();
                if (TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password)) {
                    Toast.makeText(LoginActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                } else {

                    auth.signInWithEmailAndPassword(txt_email, txt_password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {

                                        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                        if (firebaseUser != null) {
                                            String userId = firebaseUser.getUid();
                                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userId);
                                            reference.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    boolean isDiabetic;
                                                    int TDEE, carbs, fat, protein;
                                                    if (snapshot.child("isDiabetic").getValue(Boolean.class) != null)
                                                        isDiabetic = snapshot.child("isDiabetic").getValue(Boolean.class);
                                                    else
                                                        isDiabetic = false;

                                                    if (snapshot.child("TDEE").getValue(Integer.class) != null)
                                                        TDEE = snapshot.child("TDEE").getValue(Integer.class);
                                                    else
                                                        TDEE = 0;

                                                    if (TDEE != 0) {
                                                        protein = snapshot.child("TDEE").getValue(Integer.class);
                                                        fat = snapshot.child("fat").getValue(Integer.class);
                                                        carbs = snapshot.child("carbs").getValue(Integer.class);


                                                        editor.putString("TDEE", "" + TDEE);
                                                        editor.putString("carbs", "" + carbs);
                                                        editor.putString("protein", "" + protein);
                                                        editor.putString("fat", "" + fat);
                                                        editor.apply();
                                                    }
                                                    editor.putBoolean("diabetic", isDiabetic);
                                                    editor.apply();
                                                    startActivity(new Intent(LoginActivity.this, BMRCalculator.class));
                                                    finish();
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                        }

                                    } else {
                                        Toast.makeText(LoginActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences pref = getSharedPreferences("result", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        editor.putString("TDEE", null);
        editor.putString("carbs", null);
        editor.putString("protein", null);
        editor.putString("fat", null);
        editor.putBoolean("diabetic", false);
        editor.apply();


    }
}