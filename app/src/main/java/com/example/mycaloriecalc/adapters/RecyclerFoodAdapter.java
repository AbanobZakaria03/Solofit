/**
 * this is custom adapter created for recycler view to show foods items
 * RecyclerFoodAdapter extends RecyclerView.Adapter<RecyclerFoodAdapter.ViewHolder>
 * RecyclerFoodAdapter.ViewHolder is custom class we created in RecyclerFoodAdapter class to hold data
 * setOnItemClickListener Method and OnItemClickListener Interface to handle RecyclerView item clicked
 * <p>
 * when you click on item in recyclerview; the itemView.setOnClickListener in ViewHolder class is triggered
 * and call the on onItemClick Method in the listener Interface which you made instance of it
 */


package com.example.mycaloriecalc.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mycaloriecalc.models.Food;
import com.example.mycaloriecalc.R;

import java.util.ArrayList;
import java.util.List;

public class RecyclerFoodAdapter extends RecyclerView.Adapter<RecyclerFoodAdapter.ViewHolder> {
    private List<Food> foods = new ArrayList<>();
    private OnItemClickListener listener;
    private OnLongItemClickListener listener2;


    //Constructor
    public RecyclerFoodAdapter(List<Food> foods) {
        this.foods = foods;
    }


    //create instance of ViewHolder class to hold the views and widgets we inflated from food_item.xml layout
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    //onBindViewHolder used to set data on item which we inflate in onCreateViewHolder method in hold its views in the ViewHolder Class
    //when you scroll, new item appear, this new item is and old item leaved the screen and get recycled
    //when the old item recycled, we just set a new data for it
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.foodName.setText(foods.get(position).getDesc());
        holder.foodServing.setText(foods.get(position).getPortion());
        holder.foodCalories.setText((int) foods.get(position).getEnergy() + " Kcal");
    }

    //method to calculate the number of items should appear in the RecyclerView
    @Override
    public int getItemCount() {
        return foods.size();
    }

    //Class to hold the views and widgets in the food_item.xml layout which we inflated in onCreateViewHolder
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView foodName, foodServing, foodCalories;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            foodName = itemView.findViewById(R.id.food_name);
            foodServing = itemView.findViewById(R.id.food_serving);
            foodCalories = itemView.findViewById(R.id.food_calories);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (listener2 != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener2.onLongItemClick(position);
                        }
                    }
                    return true;
                }
            });


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }

                    }
                }
            });


        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public interface OnLongItemClickListener {
        void onLongItemClick(int position);
    }

    /**
     * this method not trigger the item click
     * but instead this prepare the listener for your RecyclerView
     *
     * @param listener An interface object you create for your RecyclerView to trigger the onItemClick
     *                 for each instance of recyclerview you make in project, you prepare the listener
     *                 to trigger item click
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setOnLongItemClickListener(OnLongItemClickListener listener2) {
        this.listener2 = listener2;
    }

}

