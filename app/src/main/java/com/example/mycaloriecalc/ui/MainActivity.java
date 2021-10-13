package com.example.mycaloriecalc.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.mycaloriecalc.R;
import com.example.mycaloriecalc.adapters.RecyclerFoodAdapter;
import com.example.mycaloriecalc.models.Food;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, BmrCalculatorFragment.OnDataPass {
    ArrayList<Food> foodsBreakfast = new ArrayList<>();
    ArrayList<Food> foodsLunches = new ArrayList<>();
    ArrayList<Food> foodsDinner = new ArrayList<>();
    RecyclerFoodAdapter breakfastAdapter;
    RecyclerFoodAdapter lunchAdapter;
    RecyclerFoodAdapter dinnerAdapter;
    RecyclerView recyclerViewBreakfast;
    RecyclerView recyclerViewLunch;
    RecyclerView recyclerViewDinner;
    Button addBreakfast, camBreakfast, addLunch, camLunch, addDinner, camDinner;
    CircularProgressBar circularProgressBar;
    ProgressBar progress_carbs, progress_protein, progress_fat;
    NestedScrollView nestedScrollView;

    BottomNavigationView nav;
    BmrCalculatorFragment bmrFragment;
    BmiCalculatorFragment bmiFragment;
    FragmentTransaction ft;

    TextView textViewMyCal, textViewAteCal, textViewResult, textViewMyCarbs, textViewAteCarbs,
            textViewMyProtein, textViewAteProtein, textViewMyFat, textViewAteFat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewMyCal = findViewById(R.id.my_cal);
        textViewAteCal = findViewById(R.id.ate_cal);
        textViewResult = findViewById(R.id.result);

        textViewMyCarbs = findViewById(R.id.my_carbs);
        textViewAteCarbs = findViewById(R.id.ate_carbs);

        textViewMyProtein = findViewById(R.id.my_protein);
        textViewAteProtein = findViewById(R.id.ate_protein);

        textViewMyFat = findViewById(R.id.my_fat);
        textViewAteFat = findViewById(R.id.ate_fat);

        reloadCal();

        circularProgressBar = findViewById(R.id.progress_circular);
        progress_protein = findViewById(R.id.progress_protein);
        progress_fat = findViewById(R.id.progress_fat);
        progress_carbs = findViewById(R.id.progress_carbs);
        recyclerViewBreakfast = findViewById(R.id.recyclerView_Breakfast);
        recyclerViewLunch = findViewById(R.id.recyclerView_Lunch);
        recyclerViewDinner = findViewById(R.id.recyclerView_Dinner);
        addBreakfast = findViewById(R.id.add_breakfast);
        camBreakfast = findViewById(R.id.cam_breakfast);
        addLunch = findViewById(R.id.add_lunch);
        camLunch = findViewById(R.id.cam_lunch);
        addDinner = findViewById(R.id.add_dinner);
        camDinner = findViewById(R.id.cam_dinner);

        nestedScrollView = findViewById(R.id.nested_scroll);

        nav = findViewById(R.id.bottom_nav);
        bmrFragment = new BmrCalculatorFragment();
        bmiFragment = new BmiCalculatorFragment();

        ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.fragment_container, bmrFragment);
        ft.add(R.id.fragment_container, bmiFragment);
        ft.hide(bmrFragment);
        ft.hide(bmiFragment);
        ft.commit();


        addBreakfast.setOnClickListener(this);
        camBreakfast.setOnClickListener(this);
        addLunch.setOnClickListener(this);
        camLunch.setOnClickListener(this);
        addDinner.setOnClickListener(this);
        camDinner.setOnClickListener(this);


        setAdapters();

        breakfastAdapter.setOnItemClickListener(new RecyclerFoodAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(MainActivity.this, EditPickedFood.class);
                intent.putExtra("food", foodsBreakfast.get(position));
                intent.putExtra("edit", true);
                intent.putExtra("position", position);
                startActivityForResult(intent, 4);
            }
        });
        breakfastAdapter.setOnLongItemClickListener(new RecyclerFoodAdapter.OnLongItemClickListener() {
            @Override
            public void onLongItemClick(int position) {
                alertDeleteFood(position, 1);
            }
        });


        lunchAdapter.setOnItemClickListener(new RecyclerFoodAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(MainActivity.this, EditPickedFood.class);
                intent.putExtra("food", foodsLunches.get(position));
                intent.putExtra("edit", true);
                intent.putExtra("position", position);
                startActivityForResult(intent, 5);
            }
        });
        lunchAdapter.setOnLongItemClickListener(new RecyclerFoodAdapter.OnLongItemClickListener() {
            @Override
            public void onLongItemClick(int position) {
                alertDeleteFood(position, 2);

            }
        });


        dinnerAdapter.setOnItemClickListener(new RecyclerFoodAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(MainActivity.this, EditPickedFood.class);
                intent.putExtra("food", foodsDinner.get(position));
                intent.putExtra("edit", true);
                intent.putExtra("position", position);
                startActivityForResult(intent, 6);
            }
        });
        dinnerAdapter.setOnLongItemClickListener(new RecyclerFoodAdapter.OnLongItemClickListener() {
            @Override
            public void onLongItemClick(int position) {
                alertDeleteFood(position, 3);
            }
        });

        nav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        ft.hide(bmrFragment);
                        ft.hide(bmiFragment);
                        break;
                    case R.id.nav_bmr:
                        ft.show(bmrFragment);
                        ft.hide(bmiFragment);
                        break;
                    case R.id.nav_bmi:
                        ft.hide(bmrFragment);
                        ft.show(bmiFragment);
                        break;
                    default:
                        break;
                }
                ft.commit();
                return true;
            }
        });


        //RecyclerView.ItemDecoration divider = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        //recyclerView.addItemDecoration(divider);
        //recyclerView.setHasFixedSize(true);


    }

    private void editFood(Food food, int position, int requestCode) {

        int newFoodCal = (int) food.getEnergy();
        int newFoodProtein = (int) food.getProtein();
        int newFoodFat = (int) food.getFat();
        int newFoodCarbs = (int) food.getCarb();

        int myCal = Integer.parseInt(textViewMyCal.getText().toString());
        int myProtein = Integer.parseInt(textViewMyProtein.getText().toString());
        int myFat = Integer.parseInt(textViewMyFat.getText().toString());
        int myCarbs = Integer.parseInt(textViewMyCarbs.getText().toString());

        int ateCal = Integer.parseInt(textViewAteCal.getText().toString());
        int ateProtein = Integer.parseInt(textViewAteProtein.getText().toString());
        int ateFat = Integer.parseInt(textViewAteFat.getText().toString());
        int ateCarbs = Integer.parseInt(textViewAteCarbs.getText().toString());

        int currentFoodCal=0,currentFoodProtein=0,currentFoodFat=0,currentFoodCarbs=0;

        switch (requestCode) {
            case 4:
                currentFoodCal = (int) foodsBreakfast.get(position).getEnergy();
                currentFoodProtein = (int) foodsBreakfast.get(position).getProtein();
                currentFoodFat = (int) foodsBreakfast.get(position).getFat();
                currentFoodCarbs = (int) foodsBreakfast.get(position).getCarb();

                foodsBreakfast.set(position, food);
                breakfastAdapter.notifyItemChanged(position);
                break;
            case 5:
                currentFoodCal = (int) foodsLunches.get(position).getEnergy();
                currentFoodProtein = (int) foodsLunches.get(position).getProtein();
                currentFoodFat = (int) foodsLunches.get(position).getFat();
                currentFoodCarbs = (int) foodsLunches.get(position).getCarb();

                foodsLunches.set(position, food);
                lunchAdapter.notifyItemChanged(position);
                break;
            case 6:
                currentFoodCal = (int) foodsDinner.get(position).getEnergy();
                currentFoodProtein = (int) foodsDinner.get(position).getProtein();
                currentFoodFat = (int) foodsDinner.get(position).getFat();
                currentFoodCarbs = (int) foodsDinner.get(position).getCarb();

                foodsDinner.set(position, food);
                dinnerAdapter.notifyItemChanged(position);
                break;

        }


        ateCal = ateCal - currentFoodCal + newFoodCal;
        ateProtein = ateProtein - currentFoodProtein + newFoodProtein;
        ateFat = ateFat - currentFoodFat + newFoodFat;
        ateCarbs = ateCarbs - currentFoodCarbs + newFoodCarbs;


        int result = myCal - ateCal;
        textViewAteCal.setText("" + ateCal);
        textViewResult.setText("" + result);

        float progress = ((float) ateCal / (float) myCal) * 100;
        if (progress >= 100)
            circularProgressBar.setProgressBarColor(Color.RED);
        else
            circularProgressBar.setProgressBarColor(Color.WHITE);

        circularProgressBar.setProgressWithAnimation(progress, 1000l); // =1s
        TextView textView = findViewById(R.id.cal_progress);
        textView.setText((int) progress + "%");

        //protein
        textViewAteProtein.setText("" + ateProtein);
        float progressProtein = ((float) ateProtein / (float) myProtein) * 100;
        ObjectAnimator.ofInt(progress_protein, "progress", (int) progressProtein).setDuration(1000).start();

        //Fat
        textViewAteFat.setText("" + ateFat);
        float progressFat = ((float) ateFat / (float) myFat) * 100;
        ObjectAnimator.ofInt(progress_fat, "progress", (int) progressFat).setDuration(1000).start();


        //Carbs
        textViewAteCarbs.setText("" + ateCarbs);
        float progressCarbs = ((float) ateCarbs / (float) myCarbs) * 100;
        ObjectAnimator.ofInt(progress_carbs, "progress", (int) progressCarbs).setDuration(1000).start();





    }

    private void alertDeleteFood(final int position, final int requestCode) {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Delete entry")
                .setMessage("Are you sure you want to delete this entry?")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (requestCode) {
                            case 1:
                                deleteFood(foodsBreakfast.get(position));
                                foodsBreakfast.remove(position);
                                breakfastAdapter.notifyItemRemoved(position);
                                break;
                            case 2:
                                deleteFood(foodsLunches.get(position));
                                foodsLunches.remove(position);
                                lunchAdapter.notifyItemRemoved(position);
                                break;
                            case 3:
                                deleteFood(foodsDinner.get(position));
                                foodsDinner.remove(position);
                                dinnerAdapter.notifyItemRemoved(position);
                                break;
                        }

                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            Food food = data.getParcelableExtra("food");
            if (requestCode == 1 && resultCode == RESULT_OK)
                addFood(food, requestCode);

            if (requestCode == 2 && resultCode == RESULT_OK)
                addFood(food, requestCode);

            if (requestCode == 3 && resultCode == RESULT_OK)
                addFood(food, requestCode);

            if (requestCode == 4 && resultCode == RESULT_OK) {
                int pos = data.getExtras().getInt("position");
                editFood(food, pos, requestCode);
            }
            if (requestCode == 5 && resultCode == RESULT_OK) {
                int pos = data.getExtras().getInt("position");
                editFood(food, pos, requestCode);
            }
            if (requestCode == 6 && resultCode == RESULT_OK) {
                int pos = data.getExtras().getInt("position");
                editFood(food, pos, requestCode);
            }
        }
    }

    private void setAdapters() {

        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(this);
        linearLayoutManager1.setItemPrefetchEnabled(true);
        breakfastAdapter = new RecyclerFoodAdapter(foodsBreakfast);
        recyclerViewBreakfast.setAdapter(breakfastAdapter);
        recyclerViewBreakfast.setLayoutManager(linearLayoutManager1);

        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(this);
        linearLayoutManager2.setItemPrefetchEnabled(true);
        lunchAdapter = new RecyclerFoodAdapter(foodsLunches);
        recyclerViewLunch.setAdapter(lunchAdapter);
        recyclerViewLunch.setLayoutManager(linearLayoutManager2);

        LinearLayoutManager linearLayoutManager3 = new LinearLayoutManager(this);
        linearLayoutManager3.setItemPrefetchEnabled(true);
        dinnerAdapter = new RecyclerFoodAdapter(foodsDinner);
        recyclerViewDinner.setAdapter(dinnerAdapter);
        recyclerViewDinner.setLayoutManager(linearLayoutManager3);


    }

    private void addFood(Food food, int requestCode) {

        TextView textViewMyCal = findViewById(R.id.my_cal);
        TextView textViewAteCal = findViewById(R.id.ate_cal);
        TextView textViewResult = findViewById(R.id.result);

        int currentAteCal = Integer.parseInt(textViewAteCal.getText().toString());
        int newAteCal = (int) food.getEnergy();
        int allAteCal = currentAteCal + newAteCal;
        String allAteCal2 = String.valueOf(allAteCal);
        textViewAteCal.setText(allAteCal2);


        int myCal = Integer.parseInt(textViewMyCal.getText().toString());
        String result = String.valueOf(myCal - allAteCal);
        textViewResult.setText(result);

        float progress = ((float) allAteCal / (float) myCal) * 100;
        if (progress >= 100)
            circularProgressBar.setProgressBarColor(Color.RED);
        circularProgressBar.setProgressWithAnimation(progress, 1000l); // =1s
        TextView textView = findViewById(R.id.cal_progress);
        textView.setText((int) progress + "%");


        //protein
        int myProtein = Integer.parseInt(textViewMyProtein.getText().toString());
        int currentAteProtein = Integer.parseInt(textViewAteProtein.getText().toString());
        int newAteProtein = (int) food.getProtein();
        int allAteProtein = currentAteProtein + newAteProtein;
        textViewAteProtein.setText("" + allAteProtein);
        float progressProtein = ((float) allAteProtein / (float) myProtein) * 100;
        ObjectAnimator.ofInt(progress_protein, "progress", (int) progressProtein).setDuration(1000).start();

        //fat
        int myFat = Integer.parseInt(textViewMyFat.getText().toString());
        int currentAteFat = Integer.parseInt(textViewAteFat.getText().toString());
        int newAteFat = (int) food.getFat();
        int allAteFat = currentAteFat + newAteFat;
        textViewAteFat.setText("" + allAteFat);
        float progressFat = ((float) allAteFat / (float) myFat) * 100;
        ObjectAnimator.ofInt(progress_fat, "progress", (int) progressFat).setDuration(1000).start();

        //carbs
        int myCarbs = Integer.parseInt(textViewMyCarbs.getText().toString());
        int currentAteCarbs = Integer.parseInt(textViewAteCarbs.getText().toString());
        int newAteCarbs = (int) food.getCarb();
        int allAteCarbs = currentAteCarbs + newAteCarbs;
        textViewAteCarbs.setText("" + allAteCarbs);
        float progressCarbs = ((float) allAteCarbs / (float) myCarbs) * 100;
        ObjectAnimator.ofInt(progress_carbs, "progress", (int) progressCarbs).setDuration(1000).start();


        switch (requestCode) {
            case 1:
                foodsBreakfast.add(0, food);
                breakfastAdapter.notifyItemInserted(0);
                break;
            case 2:
                foodsLunches.add(0, food);
                lunchAdapter.notifyItemInserted(0);
                break;
            case 3:
                foodsDinner.add(0, food);
                dinnerAdapter.notifyItemInserted(0);
                int pos = recyclerViewDinner.getTop() + 240;
                nestedScrollView.smoothScrollTo(0, pos);
        }


    }

    private void deleteFood(Food food) {
        TextView textViewMyCal = findViewById(R.id.my_cal);
        TextView textViewAteCal = findViewById(R.id.ate_cal);
        TextView textViewResult = findViewById(R.id.result);
        int foodCal = (int) food.getEnergy();
        int myCal = Integer.parseInt(textViewMyCal.getText().toString());
        int ateCal = Integer.parseInt(textViewAteCal.getText().toString());
        int result = Integer.parseInt(textViewResult.getText().toString());

        int newAteCal = ateCal - foodCal;
        int newResult = result + foodCal;

        textViewAteCal.setText("" + newAteCal);
        textViewResult.setText("" + newResult);

        float progress = ((float) newAteCal / (float) myCal) * 100;
        if (progress >= 100)
            circularProgressBar.setProgressBarColor(Color.RED);
        else
            circularProgressBar.setProgressBarColor(Color.WHITE);

        circularProgressBar.setProgressWithAnimation(progress, 1000l); // =1s
        TextView textView = findViewById(R.id.cal_progress);
        textView.setText((int) progress + "%");


        //protein
        int myProtein = Integer.parseInt(textViewMyProtein.getText().toString());
        int currentAteProtein = Integer.parseInt(textViewAteProtein.getText().toString());
        int newAteProtein = currentAteProtein - (int) food.getProtein();
        textViewAteProtein.setText("" + newAteProtein);
        float progressProtein = ((float) newAteProtein / (float) myProtein) * 100;
        ObjectAnimator.ofInt(progress_protein, "progress", (int) progressProtein).setDuration(1000).start();

        //fat
        int myFat = Integer.parseInt(textViewMyFat.getText().toString());
        int currentAteFat = Integer.parseInt(textViewAteFat.getText().toString());
        int newAteFat = currentAteFat - (int) food.getFat();
        textViewAteFat.setText("" + newAteFat);
        float progressFat = ((float) newAteFat / (float) myFat) * 100;
        ObjectAnimator.ofInt(progress_fat, "progress", (int) progressFat).setDuration(1000).start();

        //carbs
        int myCarbs = Integer.parseInt(textViewMyCarbs.getText().toString());
        int currentAteCarbs = Integer.parseInt(textViewAteCarbs.getText().toString());
        int newAteCarbs = currentAteCarbs - (int) food.getCarb();
        textViewAteCarbs.setText("" + newAteCarbs);
        float progressCarbs = ((float) newAteCarbs / (float) myCarbs) * 100;
        ObjectAnimator.ofInt(progress_carbs, "progress", (int) progressCarbs).setDuration(1000).start();

    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.add_breakfast:
                intent = new Intent(MainActivity.this, FoodSearch.class);
                startActivityForResult(intent, 1);
                break;
            case R.id.cam_breakfast:
                intent = new Intent(MainActivity.this, CamActivity.class);
                startActivityForResult(intent, 1);
                break;
            case R.id.add_lunch:
                intent = new Intent(MainActivity.this, FoodSearch.class);
                startActivityForResult(intent, 2);
                break;
            case R.id.cam_lunch:
                intent = new Intent(MainActivity.this, CamActivity.class);
                startActivityForResult(intent, 2);
                break;
            case R.id.add_dinner:
                intent = new Intent(MainActivity.this, FoodSearch.class);
                startActivityForResult(intent, 3);
                break;
            case R.id.cam_dinner:
                intent = new Intent(MainActivity.this, CamActivity.class);
                startActivityForResult(intent, 3);
                break;
        }
    }

    @Override
    public void onDataPass(String cal, String protein, String fat, String carbs) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (bmrFragment.isAdded()) {
            ft.hide(bmrFragment);
            ft.commit();
        }
        nav.setSelectedItemId(R.id.nav_home);

        updateCal(cal, protein, fat, carbs);
    }

    private void updateCal(String cal, String protein, String fat, String carbs) {

        textViewMyCal.setText(cal);
        textViewMyProtein.setText(protein);
        textViewMyFat.setText(fat);
        textViewMyCarbs.setText(carbs);

        int myCal = Integer.parseInt(textViewMyCal.getText().toString());
        int ateCal = Integer.parseInt(textViewAteCal.getText().toString());
        int result = myCal - ateCal;
        textViewResult.setText("" + result);

        float progress = ((float) ateCal / (float) myCal) * 100;
        if (progress >= 100)
            circularProgressBar.setProgressBarColor(Color.RED);
        else
            circularProgressBar.setProgressBarColor(Color.WHITE);

        circularProgressBar.setProgressWithAnimation(progress, 1000l); // =1s
        TextView textView = findViewById(R.id.cal_progress);
        textView.setText((int) progress + "%");

        SharedPreferences pref = getSharedPreferences("result", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("TDEE", cal);
        editor.putString("protein", "" + protein);
        editor.putString("fat", "" + fat);
        editor.putString("carbs", "" + carbs);
        editor.apply();
    }

    private void reloadCal() {
        String TDEE = getIntent().getStringExtra("TDEE");
        String protein = getIntent().getStringExtra("protein");
        String fat = getIntent().getStringExtra("fat");
        String carbs = getIntent().getStringExtra("carbs");

        textViewMyCal.setText(TDEE);
        textViewResult.setText(TDEE);

        textViewMyProtein.setText(protein);
        textViewMyFat.setText(fat);
        textViewMyCarbs.setText(carbs);
    }
}
