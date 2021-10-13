package com.example.mycaloriecalc.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Food implements Parcelable {
    int id;
    String desc;
    String portion;
    double energy;
    double protein;
    double fat;
    double carb;
    double sugar;

    public Food() {
    }

    public Food(int id, String desc, double energy, double protein, double fat, double carb, double sugar) {
        this.id = id;
        this.desc = desc;
        this.energy = energy;
        this.protein = protein;
        this.fat = fat;
        this.carb = carb;
        this.sugar = sugar;
    }

    public Food(int id, String desc) {
        this.id = id;
        this.desc = desc;
        energy = 0.0;
        fat = 0.0;
        carb = 0.0;
        sugar = 0.0;
    }

    protected Food(Parcel in) {
        id = in.readInt();
        desc = in.readString();
        portion = in.readString();
        energy = in.readDouble();
        protein = in.readDouble();
        fat = in.readDouble();
        carb = in.readDouble();
        sugar = in.readDouble();
    }

    public static final Creator<Food> CREATOR = new Creator<Food>() {
        @Override
        public Food createFromParcel(Parcel in) {
            return new Food(in);
        }

        @Override
        public Food[] newArray(int size) {
            return new Food[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getPortion() {
        return portion;
    }

    public void setPortion(String portion) {
        this.portion = portion;
    }

    public double getEnergy() {
        return energy;
    }

    public void setEnergy(double energy) {
        this.energy = energy;
    }

    public double getProtein() {
        return protein;
    }

    public void setProtein(double protein) {
        this.protein = protein;
    }

    public double getFat() {
        return fat;
    }

    public void setFat(double fat) {
        this.fat = fat;
    }

    public double getCarb() {
        return carb;
    }

    public void setCarb(double carb) {
        this.carb = carb;
    }

    public double getSugar() {
        return sugar;
    }

    public void setSugar(double sugar) {
        this.sugar = sugar;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(desc);
        dest.writeString(portion);
        dest.writeDouble(energy);
        dest.writeDouble(protein);
        dest.writeDouble(fat);
        dest.writeDouble(carb);
        dest.writeDouble(sugar);
    }
}
