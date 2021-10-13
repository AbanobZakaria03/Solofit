package com.example.mycaloriecalc.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mycaloriecalc.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;


public class BMRCalculator extends AppCompatActivity {
    private final String SEDENTARY = "Sedentary (little or no exercise, desk job)";
    private final String LIGHTLY_ACTIVE = "Lightly active (light exercise 1:3 days/week)";
    private final String MODERATELY_ACTIVE = "Moderately active (moderate exercise 3:5 days/week)";
    private final String VERY_ACTIVE = "Very active (hard exercise 6:7 days/week)";
    private final String EXTRA_ACTIVE = "Extra active (very hard exercise every day)";

    private final float NUM_SEDENTARY = 1.2f;
    private final float NUM_LIGHTLY_ACTIVE = 1.375f;
    private final float NUM_MODERATELY_ACTIVE = 1.55f;
    private final float NUM_VERY_ACTIVE = 1.725f;
    private final float NUM_EXTRA_ACTIVE = 1.9f;


    AutoCompleteTextView autoCompleteActivityLevel;
    AutoCompleteTextView autoCompleteGoal;
    TextInputLayout textInputAge;
    TextInputLayout textInputHeight;
    TextInputLayout textInputWeight;
    TextInputLayout textInputActivity;
    TextInputLayout textInputGoal;
    Button calc;
    View layoutResult;
    ScrollView scrollView;


    int TDEE, protein, carbs, fat;
    TextView calText, proText, carbsText, fatText;

    SharedPreferences pref;
    SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmr_calculator);

        autoCompleteActivityLevel = findViewById(R.id.dropdown_menu_text);
        autoCompleteGoal = findViewById(R.id.dropdown_goal);
        textInputAge = findViewById(R.id.age);
        textInputHeight = findViewById(R.id.height);
        textInputWeight = findViewById(R.id.weight);
        textInputActivity = findViewById(R.id.activity);
        textInputGoal = findViewById(R.id.goal);
        calc = findViewById(R.id.calc_button);
        layoutResult = findViewById(R.id.layout_result);

        scrollView = findViewById(R.id.scroll_parent);

        calText = findViewById(R.id.tdee);
        fatText = findViewById(R.id.fat);
        carbsText = findViewById(R.id.carbs);
        proText = findViewById(R.id.protein);


        pref = getSharedPreferences("result", MODE_PRIVATE);
        editor = pref.edit();

        checkPref();

        String[] arr = new String[]{
                SEDENTARY, LIGHTLY_ACTIVE, MODERATELY_ACTIVE, VERY_ACTIVE, EXTRA_ACTIVE
        };

        String[] goals = new String[]{
                "Loss Weight",
                "Maintain Weight",
                "Gain Weight"
        };

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.activity_level_dropdown_item, arr);
        autoCompleteActivityLevel.setAdapter(arrayAdapter);
        autoCompleteActivityLevel.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {

                if (b) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            autoCompleteActivityLevel.showDropDown();
                        }
                    }, 200);
                }
            }
        });
        autoCompleteActivityLevel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                autoCompleteActivityLevel.showDropDown();
            }
        });


        arrayAdapter = new ArrayAdapter<>(this, R.layout.activity_level_dropdown_item, goals);
        autoCompleteGoal.setAdapter(arrayAdapter);
        autoCompleteGoal.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {

                if (b) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            autoCompleteGoal.showDropDown();
                        }
                    }, 200);
                }
            }
        });
        autoCompleteGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                autoCompleteGoal.showDropDown();
            }
        });


        calc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calculate();
            }
        });

    }

    public void calculate() {
        if (validateAge() & validateHeight() & validateWeight() & validateActivityLevel() & validateGoal()) {
            int age = Integer.parseInt(textInputAge.getEditText().getText().toString());
            int height = Integer.parseInt(textInputHeight.getEditText().getText().toString());
            int weight = Integer.parseInt(textInputWeight.getEditText().getText().toString());
            //Toast.makeText(this, "" + calculate_BMR(age, height, weight), Toast.LENGTH_SHORT).show();

            int BMR = calculate_BMR(age, height, weight);
            TextView textView = findViewById(R.id.bmr);
            textView.setText("" + BMR);

            String activity = autoCompleteActivityLevel.getText().toString();
            String goal = autoCompleteGoal.getText().toString();

            switch (activity) {
                case SEDENTARY:
                    BMR = (int) (BMR * NUM_SEDENTARY);
                    break;
                case LIGHTLY_ACTIVE:
                    BMR = (int) (BMR * NUM_LIGHTLY_ACTIVE);
                    break;
                case MODERATELY_ACTIVE:
                    BMR = (int) (BMR * NUM_MODERATELY_ACTIVE);
                    break;
                case VERY_ACTIVE:
                    BMR = (int) (BMR * NUM_VERY_ACTIVE);
                    break;
                case EXTRA_ACTIVE:
                    BMR = (int) (BMR * NUM_EXTRA_ACTIVE);
                    break;
            }

            switch (goal) {
                case "Loss Weight":
                    BMR = (int) (BMR - BMR * 0.2);
                    protein = (int) (BMR * 0.45) / 4;
                    carbs = (int) (BMR * 0.25) / 4;
                    fat = (int) (BMR * 0.35) / 9;
                    break;
                case "Maintain Weight":
                    BMR = BMR;
                    protein = (int) (BMR * 0.30) / 4;
                    carbs = (int) (BMR * 0.40) / 4;
                    fat = (int) (BMR * 0.30) / 9;
                    break;
                case "Gain Weight":
                    BMR = (int) Math.round((BMR + BMR * 0.2));
                    protein = (int) (BMR * 0.30) / 4;
                    carbs = (int) (BMR * 0.50) / 4;
                    fat = (int) (BMR * 0.20) / 9;
                    break;
            }
            textView = findViewById(R.id.tdee);
            textView.setText("" + BMR);


            calText.setText(BMR + "Kcal");
            fatText.setText(fat + "g");
            carbsText.setText(carbs + "g");
            proText.setText(protein + "g");

            TDEE = BMR;

            layoutResult.setVisibility(View.VISIBLE);
            calc.setVisibility(View.GONE);

            scrollView.post(new Runnable() {
                @Override
                public void run() {
                    scrollView.fullScroll(View.FOCUS_DOWN);
                }
            });
        }


    }

    private boolean validateAge() {
        if (textInputAge.getEditText().getText().toString().length() == 0) {
            textInputAge.setError("Field can't be empty!");
            return false;
        } else if (Integer.parseInt(textInputAge.getEditText().getText().toString()) < 13) {
            textInputAge.setError("Your Age Should Be More Than 13");
            return false;
        } else if (Integer.parseInt(textInputAge.getEditText().getText().toString()) > 100) {
            textInputAge.setError("Too High Age!");
            return false;
        } else {
            textInputAge.setError(null);
            return true;
        }
    }

    private boolean validateHeight() {
        if (textInputHeight.getEditText().getText().toString().length() == 0) {
            textInputHeight.setError("Field can't be empty!");
            return false;
        } else if (Integer.parseInt(textInputHeight.getEditText().getText().toString()) < 100) {
            textInputHeight.setError("Too Low Height!");
            return false;
        } else if (Integer.parseInt(textInputHeight.getEditText().getText().toString()) > 300) {
            textInputHeight.setError("Too High Height!");
            return false;
        } else {
            textInputHeight.setError(null);
            return true;
        }
    }

    private boolean validateWeight() {
        if (textInputWeight.getEditText().getText().toString().length() == 0) {
            textInputWeight.setError("Field can't be empty!");
            return false;
        } else if (Integer.parseInt(textInputWeight.getEditText().getText().toString()) < 30) {
            textInputWeight.setError("Too Low Weight!");
            return false;
        } else if (Integer.parseInt(textInputWeight.getEditText().getText().toString()) > 300) {
            textInputWeight.setError("Too High Weight!");
            return false;
        } else {
            textInputWeight.setError(null);
            return true;
        }
    }

    private boolean validateActivityLevel() {
        if (autoCompleteActivityLevel.getText().toString().length() == 0) {
            textInputActivity.setError("Please Pick Your Activity Level!");
            return false;
        } else {
            textInputActivity.setError(null);
            return true;
        }
    }

    private boolean validateGoal() {
        if (autoCompleteGoal.getText().toString().length() == 0) {
            textInputGoal.setError("Please Pick Your Activity Level!");
            return false;
        } else {
            textInputGoal.setError(null);
            return true;
        }
    }

    private int calculate_BMR(int age, int height, int weight) {
        int result;
        RadioGroup radioGroup = findViewById(R.id.gender);
        if (radioGroup.getCheckedRadioButtonId() == R.id.male)
            result = (int) (10 * weight + 6.25 * height - 5 * age + 5);
        else
            result = (int) (10 * weight + 6.25 * height - 5 * age - 161);

        return result;

    }

    public void use(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("TDEE", "" + TDEE);
        intent.putExtra("carbs", "" + carbs);
        intent.putExtra("protein", "" + protein);
        intent.putExtra("fat", "" + fat);


        editor.putString("TDEE", "" + TDEE);
        editor.putString("carbs", "" + carbs);
        editor.putString("protein", "" + protein);
        editor.putString("fat", "" + fat);
        //editor.putBoolean("diabetic", true);
        editor.apply();


        FirebaseAuth auth = FirebaseAuth.getInstance();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        FirebaseUser firebaseUser = auth.getCurrentUser();
        assert firebaseUser != null;
        String userId = firebaseUser.getUid();
        DatabaseReference ref = reference.child(userId);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("TDEE",TDEE);
        hashMap.put("carbs",carbs);
        hashMap.put("protein",protein);
        hashMap.put("fat",fat);
        hashMap.put("isDiabetic",pref.getBoolean("diabetic",false));
        ref.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    startActivity(intent);
                    finish();
                }
            }
        });
        /*
        startActivity(intent);
        finish();

         */
    }

    public void newCalc(View view) {
        calc.setVisibility(View.VISIBLE);
        layoutResult.setVisibility(View.GONE);
        textInputAge.getEditText().setText("");
        textInputHeight.getEditText().setText("");
        textInputWeight.getEditText().setText("");
        autoCompleteActivityLevel.setText("");
        autoCompleteGoal.setText("");

        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(View.FOCUS_UP);
            }
        });
    }
        private void checkPref() {
/*
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            String userId = firebaseUser.getUid();
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userId);
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    boolean isDiabetic;
                    int TDEE;

                    if (snapshot.child("TDEE").getValue(Integer.class) != null)
                        TDEE = snapshot.child("TDEE").getValue(Integer.class);
                    else
                        TDEE = 0;
            */
            if (pref.getString("TDEE", null) != null) {
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("TDEE", pref.getString("TDEE", null));
                intent.putExtra("carbs", pref.getString("carbs", null));
                intent.putExtra("protein", pref.getString("protein", null));
                intent.putExtra("fat", pref.getString("fat", null));
                startActivity(intent);
                finish();
            }
        }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View v = getCurrentFocus();

        if (v != null &&
                (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) &&
                v instanceof EditText &&
                !v.getClass().getName().startsWith("android.webkit.")) {
            int scrcoords[] = new int[2];
            v.getLocationOnScreen(scrcoords);
            float x = ev.getRawX() + v.getLeft() - scrcoords[0];
            float y = ev.getRawY() + v.getTop() - scrcoords[1];

            if (x < v.getLeft() || x > v.getRight() || y < v.getTop() || y > v.getBottom())
                hideKeyboard(this);
        }
        return super.dispatchTouchEvent(ev);
    }

    public static void hideKeyboard(Activity activity) {
        if (activity != null && activity.getWindow() != null && activity.getWindow().getDecorView() != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
        }
    }

}
