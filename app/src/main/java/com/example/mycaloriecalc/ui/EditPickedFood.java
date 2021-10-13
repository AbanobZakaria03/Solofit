package com.example.mycaloriecalc.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.mycaloriecalc.R;
import com.example.mycaloriecalc.adapters.SpinnerAdabter;
import com.example.mycaloriecalc.data.DatabaseAccess;
import com.example.mycaloriecalc.models.Food;
import com.example.mycaloriecalc.models.Serving;

import java.util.ArrayList;

public class EditPickedFood extends AppCompatActivity {
    DatabaseAccess databaseAccess;

    Food food, result;
    Serving serving;
    Spinner spinner;
    TextView name, calories_value, protein_value, fat_value, carbs_value, sugar_value;
    EditText foodQuantity;
    int food_quantity, food_cal;
    ArrayList<Serving> servings;
    String currentFoodQuantity, currentFoodPortion;
    Serving currentFoodServing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picked_food);

        Button button = findViewById(R.id.button);
        button.setText("Update");

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

        String pr = food.getPortion();
        String str = pr.substring(1, pr.length());
        currentFoodQuantity = str.substring(0, str.indexOf(" "));
        currentFoodPortion = str.substring(str.indexOf(" ") + 1);

        foodQuantity.setText(currentFoodQuantity);


        name.setText(food.getDesc());
        servings = new ArrayList<>();
        servings.add(new Serving("100g", 100));
        servings.addAll(databaseAccess.servings(food.getId()));
        SpinnerAdabter adapter = new SpinnerAdabter(this, servings);
        spinner.setAdapter(adapter);


        spinner.setSelection(adapter.getPosition(getServingPosition(currentFoodPortion)));
        currentFoodServing = getServingPosition(currentFoodPortion);

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


        int current_food_quantity = Integer.parseInt(currentFoodQuantity);
        double currentWeight = currentFoodServing.getWeight();
        double newWeight = serving.getWeight();
        double energy, protein, fat, carbs, sugar;
        energy = food.getEnergy();
        protein = food.getProtein();
        fat = food.getFat();
        carbs = food.getCarb();
        sugar = food.getSugar();


        energy = (energy / currentWeight / current_food_quantity) * newWeight * food_quantity;
        protein = (protein / currentWeight / current_food_quantity) * newWeight * food_quantity;
        fat = (fat / currentWeight / current_food_quantity) * newWeight * food_quantity;
        carbs = (carbs / currentWeight / current_food_quantity) * newWeight * food_quantity;
        sugar = (sugar / currentWeight / current_food_quantity) * newWeight * food_quantity;

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
        String portion = "x" + foodQuantity.getText().toString() + " " + serving.getDesc();
        result.setPortion(portion);

        Intent intent = new Intent();
        intent.putExtra("food", result);

        if (getIntent().getBooleanExtra("edit", false) != false) {
            intent.putExtra("position", getIntent().getIntExtra("position", -1));
            setResult(RESULT_OK, intent);
        } else {
            setResult(RESULT_OK, intent);
        }
        finish();

    }

    Serving getServingPosition(String servingName) {
        for (Serving serving : servings) {
            if (serving.getDesc().equals(servingName))
                return serving;
        }
        return null;
    }


}