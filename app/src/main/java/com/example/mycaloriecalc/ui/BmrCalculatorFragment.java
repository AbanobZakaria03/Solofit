package com.example.mycaloriecalc.ui;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.mycaloriecalc.R;
import com.google.android.material.textfield.TextInputLayout;


public class BmrCalculatorFragment extends Fragment {
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
    Button calcNew;
    Button use;
    View layoutResult;

    ScrollView scrollView;

    OnDataPass dataPasser;


    int TDEE, protein, carbs, fat;
    TextView calText, proText, carbsText, fatText;

    public BmrCalculatorFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        dataPasser = (OnDataPass) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_bmr_calculator, container, false);


        autoCompleteActivityLevel = v.findViewById(R.id.dropdown_menu_text);
        autoCompleteGoal = v.findViewById(R.id.dropdown_goal);
        textInputAge = v.findViewById(R.id.age);
        textInputHeight = v.findViewById(R.id.height);
        textInputWeight = v.findViewById(R.id.weight);
        textInputActivity = v.findViewById(R.id.activity);
        textInputGoal = v.findViewById(R.id.goal);
        calc = v.findViewById(R.id.calc_button);
        calcNew = v.findViewById(R.id.calc_new);
        use = v.findViewById(R.id.use);
        layoutResult = v.findViewById(R.id.layout_result);
        scrollView = v.findViewById(R.id.scroll_parent);

        calText = v.findViewById(R.id.tdee);
        fatText = v.findViewById(R.id.fat);
        carbsText = v.findViewById(R.id.carbs);
        proText = v.findViewById(R.id.protein);


        String[] arr = new String[]{
                SEDENTARY, LIGHTLY_ACTIVE, MODERATELY_ACTIVE, VERY_ACTIVE, EXTRA_ACTIVE
        };

        String[] goals = new String[]{
                "Loss Weight",
                "Maintain Weight",
                "Gain Weight"
        };

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.activity_level_dropdown_item, arr);
        autoCompleteActivityLevel.setAdapter(arrayAdapter);
        autoCompleteActivityLevel.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {

                if (b) {
                    closeKeyboard();
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


        arrayAdapter = new ArrayAdapter<>(getActivity(), R.layout.activity_level_dropdown_item, goals);
        autoCompleteGoal.setAdapter(arrayAdapter);
        autoCompleteGoal.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {

                if (b) {
                    closeKeyboard();
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

        calcNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newCalc();
            }
        });

        use.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                use();
            }
        });

        return v;
    }


    public void calculate() {
        if (validateAge() & validateHeight() & validateWeight() & validateActivityLevel() & validateGoal()) {
            int age = Integer.parseInt(textInputAge.getEditText().getText().toString());
            int height = Integer.parseInt(textInputHeight.getEditText().getText().toString());
            int weight = Integer.parseInt(textInputWeight.getEditText().getText().toString());

            int BMR = calculate_BMR(age, height, weight);

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

            calText.setText(BMR + "Kcal");
            fatText.setText(fat + "g");
            carbsText.setText(carbs + "g");
            proText.setText(protein + "g");

            TDEE = BMR;

            layoutResult.setVisibility(View.VISIBLE);
            calc.setVisibility(View.GONE);
//                    scrollView.smoothScrollTo(0, scrollView.getBottom());
            //scrollView.fullScroll(ScrollView.FOCUS_DOWN);
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
        RadioGroup radioGroup = getView().findViewById(R.id.gender);
        if (radioGroup.getCheckedRadioButtonId() == R.id.male)
            result = (int) (10 * weight + 6.25 * height - 5 * age + 5);
        else
            result = (int) (10 * weight + 6.25 * height - 5 * age - 161);

        return result;

    }

    public void use() {
        dataPasser.onDataPass("" + TDEE, "" + protein, "" + fat, "" + carbs);
        //getActivity().getSupportFragmentManager().beginTransaction().hide(this).commit();
        /*
        Intent intent = new Intent(this,MainActivity.class);
        intent.putExtra("TDEE",""+TDEE);


        editor.putString("TDEE",""+TDEE);
        editor.apply();


        startActivity(intent);
        finish();
         */
    }

    public void newCalc() {
        calc.setVisibility(View.VISIBLE);
        layoutResult.setVisibility(View.GONE);
        textInputAge.getEditText().setText("");
        textInputHeight.getEditText().setText("");
        textInputWeight.getEditText().setText("");
        autoCompleteActivityLevel.setText("");
        autoCompleteGoal.setText("");

    }

    private void closeKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public interface OnDataPass {
        public void onDataPass(String cal, String protein, String fat, String carbs);
    }
}
