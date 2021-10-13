package com.example.mycaloriecalc.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.mycaloriecalc.models.Food;
import com.example.mycaloriecalc.models.Serving;

import java.util.ArrayList;
import java.util.List;

public class DatabaseAccess {
    private SQLiteOpenHelper openHelper;
    private SQLiteDatabase db;
    private static DatabaseAccess instance;
    Cursor c = null;
    Cursor c2 = null;

    private DatabaseAccess(Context context) {
        this.openHelper = new DatabaseOpenHelper(context);
    }

    public static DatabaseAccess getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseAccess(context);
        }
        return instance;
    }

    public void open() {
        this.db = openHelper.getWritableDatabase();
    }

    public void close() {
        if (db != null) {
            this.db.close();
        }
    }


    //query methods
    public String foodName(int id) {
        String selectQuery = "SELECT  * FROM food WHERE id = " + id;
        c = db.rawQuery(selectQuery, null);
        c.moveToFirst();
        String foodName = c.getString(c.getColumnIndex("long_desc"));
        return foodName;
    }


    //query methods
    public List<Food> foods(String name) {
        String selectQuery = "SELECT  * FROM food WHERE long_desc LIKE '%" + name + "%'";
        Cursor c = db.rawQuery(selectQuery, null);
        List<Food> foods = new ArrayList<>();

        if (c != null && c.moveToFirst()) {

            do {
                int id = c.getInt(c.getColumnIndex("id"));
                String foodName = c.getString(c.getColumnIndex("long_desc"));


                double energy = 0, protein = 0, fat = 0, carb = 0, sugar = 0;
                String selectQuery2 = "SELECT  amount FROM nutrition WHERE food_id = " + id + " AND nutrient_id IN (203,204,205,208,269) ";
                Cursor c2 = db.rawQuery(selectQuery2, null);
                if (c2.moveToFirst())
                    protein = c2.getDouble(0);
                if (c2.moveToNext())
                    fat = c2.getDouble(0);
                if (c2.moveToNext())
                    carb = c2.getDouble(0);
                if (c2.moveToNext())
                    energy = c2.getDouble(0);
                if (c2.moveToNext())
                    sugar = c2.getDouble(0);

                Food food = new Food(id, foodName, energy, protein, fat, carb, sugar);
                foods.add(food);

                c2.close();

            } while (c.moveToNext());

            c.close();
        }
        return foods;
    }

    public List<Serving> servings(int fid) {
        String selectQuery = "SELECT  * FROM weight WHERE food_id = " + fid;
        c = db.rawQuery(selectQuery, null);
        List<Serving> servings = new ArrayList<>();
        while (c.moveToNext()) {
            String desc = c.getString(c.getColumnIndex("description"));
            double weight = c.getDouble(c.getColumnIndex("gm_weight"));
            Serving serving = new Serving(desc, weight);
            servings.add(serving);
        }

        return servings;
    }

}
