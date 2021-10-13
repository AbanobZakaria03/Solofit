package com.example.mycaloriecalc.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.mycaloriecalc.R;
import com.google.android.material.textfield.TextInputLayout;

import java.text.DecimalFormat;

public class BmiCalculatorFragment extends Fragment {
    TextInputLayout textInputHeight;
    TextInputLayout textInputWeight;
    Button calc;
    Button calcNew;
    View layoutResult;
    ScrollView scrollView;

    public BmiCalculatorFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_bmi_calculator, container, false);

        textInputHeight = v.findViewById(R.id.height);
        textInputWeight = v.findViewById(R.id.weight);
        calc = v.findViewById(R.id.calc_button);
        calcNew = v.findViewById(R.id.calc_new);
        layoutResult = v.findViewById(R.id.layout_result);
        scrollView = v.findViewById(R.id.scroll_parent);

        calcNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newCalc();
            }
        });

        calc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculate();
            }
        });


        return v;
    }

    public void calculate() {
        if (validateHeight() & validateWeight()) {
            int height = Integer.parseInt(textInputHeight.getEditText().getText().toString());
            int weight = Integer.parseInt(textInputWeight.getEditText().getText().toString());
            double meter = (double) height / 100;

            double BMI = (double) weight / (meter * meter);
            DecimalFormat df = new DecimalFormat("#.#");
//            String result = String.format("%.2f", BMI);

            TextView textView = getView().findViewById(R.id.bmi);
            textView.setText(df.format(BMI));

            layoutResult.setVisibility(View.VISIBLE);
            calc.setVisibility(View.GONE);
            //scrollView.smoothScrollTo(0,scrollView.getBottom());
            //scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            scrollView.post(new Runnable() {
                @Override
                public void run() {
                    scrollView.fullScroll(View.FOCUS_DOWN);
                }
            });
        }
    }

    public void newCalc() {
        calc.setVisibility(View.VISIBLE);
        layoutResult.setVisibility(View.GONE);
        textInputHeight.getEditText().setText("");
        textInputWeight.getEditText().setText("");
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
}