package com.example.mycaloriecalc.ui;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.mycaloriecalc.R;
import com.example.mycaloriecalc.adapters.SpinnerAdabter;
import com.example.mycaloriecalc.data.DatabaseAccess;
import com.example.mycaloriecalc.models.Food;
import com.example.mycaloriecalc.models.Serving;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PickedFood extends AppCompatActivity {
    DatabaseAccess databaseAccess;

    Food food, result;
    Serving serving;
    Spinner spinner;
    TextView name, calories_value, protein_value, fat_value, carbs_value, sugar_value;
    EditText foodQuantity;
    int food_quantity, food_cal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picked_food);

        databaseAccess = DatabaseAccess.getInstance(getApplicationContext());
        databaseAccess.open();

        name = findViewById(R.id.food_name);
        calories_value = findViewById(R.id.calories_value);
        protein_value = findViewById(R.id.protein_value);
        fat_value = findViewById(R.id.fat_value);
        carbs_value = findViewById(R.id.carbs_value);
        sugar_value = findViewById(R.id.sugar_value);

        foodQuantity = findViewById(R.id.food_quantity);
        spinner = findViewById(R.id.spinner);


        food = getIntent().getParcelableExtra("food");
        result = new Food();
        result.setId(food.getId());
        result.setDesc(food.getDesc());


        name.setText(food.getDesc());
        ArrayList<Serving> servings = new ArrayList<>();
        servings.add(new Serving("100g", 100));
        servings.addAll(databaseAccess.servings(food.getId()));
        SpinnerAdabter adapter = new SpinnerAdabter(this, servings);
        spinner.setAdapter(adapter);


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                serving = (Serving) adapterView.getItemAtPosition(i);
                calc();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        /*
        servings = db.getAllFoodServings(food.getName());

        */

        foodQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                calc();

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        /*
        Intent intent = new Intent();
        intent.putExtra("food",food);
        setResult(RESULT_OK, intent);
        finish();
         */
    }


    public void calc() {


        if (foodQuantity.length() == 0)
            food_quantity = 0;
        else
            food_quantity = Integer.parseInt(foodQuantity.getText().toString());


        double quantity = serving.getWeight();
        double energy, protein, fat, carbs, sugar;
        energy = food.getEnergy();
        protein = food.getProtein();
        fat = food.getFat();
        carbs = food.getCarb();
        sugar = food.getSugar();


        if (quantity != 100) {
            energy = quantity * energy / 100;
            protein = quantity * protein / 100;
            fat = quantity * fat / 100;
            carbs = quantity * carbs / 100;
            sugar = quantity * sugar / 100;

        }

        energy *= food_quantity;
        protein *= food_quantity;
        fat *= food_quantity;
        carbs *= food_quantity;
        sugar *= food_quantity;

        calories_value.setText("" + String.format("%.2f", energy));
        protein_value.setText("" + String.format("%.2f", protein));
        fat_value.setText("" + String.format("%.2f", fat));
        carbs_value.setText("" + String.format("%.2f", carbs));
        sugar_value.setText("" + String.format("%.2f", sugar));

        result.setEnergy(energy);
        result.setProtein(protein);
        result.setFat(fat);
        result.setCarb(carbs);
        result.setSugar(sugar);

    }

    /*
    public void calc2() {
        if (foodQuantity.length() == 0)
            food_quantity = 0;
        else
            food_quantity = Integer.parseInt(foodQuantity.getText().toString());


        float quantity = serving2.getQuantity();
        if (quantity > 1) {
            food_cal = (int) (food_quantity * food.getCal() * quantity) / food.getWeight();

        } else if (quantity > 0 && quantity <= 1) {
            food_cal = (int) (food_quantity * food.getCal() * quantity * food.getWeight()) / food.getWeight();
        } else if (quantity == 0) {
            food_cal = (int) (food_quantity * food.getCal()) / food.getWeight();
        }

        cal.setText("" + food_cal);

    }
*/
    public void insert(View view) {

        SharedPreferences pref = getSharedPreferences("result", MODE_PRIVATE);
        Log.i("diabetic",pref.getBoolean("diabetic", false)+"");
        if (pref.getBoolean("diabetic", false) && Double.parseDouble(sugar_value.getText().toString())>0) {
            new AlertDialog.Builder(PickedFood.this)
                    .setTitle("Sugar Warning")
                    .setMessage("This food contains Sugar and you have diabetic.\nAre you sure you want to add this food?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String portion = "x" + foodQuantity.getText().toString() + " " + serving.getDesc();
                            result.setPortion(portion);
                            Intent intent = new Intent();
                            intent.putExtra("food", result);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        } else{
            String portion = "x" + foodQuantity.getText().toString() + " " + serving.getDesc();
            result.setPortion(portion);
            Intent intent = new Intent();
            intent.putExtra("food", result);
            setResult(RESULT_OK, intent);
            finish();
        }


/*
        if (getIntent().getBooleanExtra("edit", false) != false) {
            intent.putExtra("position", getIntent().getIntExtra("position", -1));
            setResult(RESULT_OK, intent);
        } else {
            setResult(RESULT_OK, intent);
        }
 */

    }


}
