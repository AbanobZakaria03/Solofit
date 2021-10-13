package com.example.mycaloriecalc.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mycaloriecalc.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {


    EditText email, password;
    Button btnRegister;
    CheckBox checkBox;
    TextView login_text;

    SharedPreferences pref;
    SharedPreferences.Editor editor;


    FirebaseAuth auth = FirebaseAuth.getInstance();
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        pref = getSharedPreferences("result", MODE_PRIVATE);
        editor = pref.edit();

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        btnRegister = findViewById(R.id.btn_register);
        checkBox = findViewById(R.id.checkBox);
        login_text = findViewById(R.id.login_text);

        login_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txt_email = email.getText().toString();
                String txt_password = password.getText().toString();

                if (TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password)) {
                    Toast.makeText(RegisterActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                } else if (txt_password.length() < 6) {
                    password.setError("password must be at least 6 characters");
                } else {
                    register(txt_email, txt_password);
                }
            }
        });
    }

    private void register(String email, String password) {

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            assert firebaseUser != null;
                            String userId = firebaseUser.getUid();
                            DatabaseReference ref = reference.child(userId);
                            HashMap<String, Boolean> hashMap = new HashMap<>();
                            hashMap.put("isDiabetic", checkBox.isChecked());
                            ref.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        editor.putBoolean("diabetic", checkBox.isChecked());
                                        editor.apply();
                                        Intent intent = new Intent(RegisterActivity.this, BMRCalculator.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });

/*
                            if(checkBox.isChecked()){
                               SharedPreferences pref = getSharedPreferences("result", MODE_PRIVATE);
                               SharedPreferences.Editor editor = pref.edit();
                                editor.putBoolean("diabetic", true);
                                editor.apply();
                            }

 */

                        } else {
                            Toast.makeText(RegisterActivity.this, "You Can't register with this email", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}