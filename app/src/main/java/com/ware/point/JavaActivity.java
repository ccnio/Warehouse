package com.ware.point;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.ware.R;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class JavaActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_java);
        findViewById(R.id.mTv).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.mTv) {
            tryWithResource();
        }
    }

    void tryWithResource() {
//        BufferedInputStream inputStream = null;
//        BufferedOutputStream outputStream = null;
//        try {
//            inputStream = new BufferedInputStream(new FileInputStream(new File("in.txt")));
//            outputStream = new BufferedOutputStream(new FileOutputStream(new File("out.txt")));
//            int b;
//            while ((b = inputStream.read()) != -1) {
//                outputStream.write(b);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            if (inputStream != null) {
//                try {
//                    inputStream.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//            if (outputStream != null) {
//                try {
//                    outputStream.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }


        try (BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(new File("in.txt")));
             BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(new File("out.txt")))) {
            int b;
            while ((b = inputStream.read()) != -1) {
                outputStream.write(b);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
