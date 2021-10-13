package com.example.mycaloriecalc.ui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mycaloriecalc.R;
import com.example.mycaloriecalc.ml.Model;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CamActivity extends AppCompatActivity {
    Button select, predict;
    ImageView imageView;
    Bitmap bitmap;
    TextView food1,food2,food3;
    List<String> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cam);


        String fileName = "labels.txt";
        list = new ArrayList<>();

        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(getApplication().getAssets().open(fileName)));
            String line = bufferedReader.readLine();
            while (line != null) {
                list.add(line);
                line = bufferedReader.readLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        select = findViewById(R.id.button);
        predict = findViewById(R.id.button2);
        imageView = findViewById(R.id.imageView);
        food1 = findViewById(R.id.food1);
        food2 = findViewById(R.id.food2);
        food3 = findViewById(R.id.food3);

        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 100);
            }
        });

        predict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Model model = Model.newInstance(CamActivity.this);

                    // Creates inputs for reference.
                    TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);

                    ByteBuffer byteBuffer = convertBitmapToByteBuffer(bitmap);
                    inputFeature0.loadBuffer(byteBuffer);

                    // Runs model inference and gets result.
                    Model.Outputs outputs = model.process(inputFeature0);
                    TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

                    float[] array = outputFeature0.getFloatArray();

                    List<String> max = getMax(array);
                    food1.setText(max.get(0));
                    food2.setText(max.get(1));
                    food3.setText(max.get(2));

                    // Releases model resources if no longer used.
                    model.close();
                } catch (IOException e) {
                    // TODO Handle the exception
                }
            }
        });


        food1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CamActivity.this, FoodSearch.class);
                intent.putExtra("foodName", food1.getText().toString());
                startActivityForResult(intent, 1);
            }
        });

        food2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CamActivity.this, FoodSearch.class);
                intent.putExtra("foodName", food2.getText().toString());
                startActivityForResult(intent, 1);
            }
        });

        food3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CamActivity.this, FoodSearch.class);
                intent.putExtra("foodName", food3.getText().toString());
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            setResult(RESULT_OK, data);
            finish();
        }

        if(resultCode==RESULT_OK && requestCode == 100){
            Uri uri = data.getData();
            imageView.setImageURI(uri);
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public List<String> getMax(float[] arr) {

        List<Float> floats = new ArrayList<Float>();
        for (float f : arr) {
            floats.add(f);
        }
        List<String> names = new ArrayList<>(list);

        float maxVal;
        int maxIdx;
        List<String> strings = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            maxVal = Collections.max(floats);
            maxIdx = floats.indexOf(maxVal);
            strings.add(names.get(maxIdx));
            floats.remove(maxIdx);
            names.remove(maxIdx);
        }
        return strings;

    }

    private ByteBuffer convertBitmapToByteBuffer(Bitmap bp) {
        ByteBuffer imgData = ByteBuffer.allocateDirect(Float.BYTES * 224 * 224 * 3);
        imgData.order(ByteOrder.nativeOrder());
        Bitmap bitmap = Bitmap.createScaledBitmap(bp, 224, 224, true);
        int[] intValues = new int[224 * 224];
        bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        // Convert the image to floating point.
        int pixel = 0;

        for (int i = 0; i < 224; ++i) {
            for (int j = 0; j < 224; ++j) {
                final int val = intValues[pixel++];

                imgData.putFloat(((val >> 16) & 0xFF) / 255.f);
                imgData.putFloat(((val >> 8) & 0xFF) / 255.f);
                imgData.putFloat((val & 0xFF) / 255.f);
            }
        }
        return imgData;
    }
}