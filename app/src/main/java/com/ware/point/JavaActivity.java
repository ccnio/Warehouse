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
        findViewById(R.id.mTv1).setOnClickListener(this);
        findViewById(R.id.mTv2).setOnClickListener(this);
        findViewById(R.id.mTv3).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mTv:
                tryWithResource();
                break;
            case R.id.mTv1:
                try {
                    exceptionSuppressed();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.mTv2:
                try {
                    exceptionNotSuppressed();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.mTv3:
                multipleException();
                break;
        }
    }

    public void multipleException() {
        try {
            int a = Integer.parseInt("0");
            System.out.println(5 / a);
        } catch (NumberFormatException | ArithmeticException e) {
            e.printStackTrace();
        }
    }

    public void exceptionNotSuppressed() throws Exception {
        try {
            Integer.parseInt("Hello");
        } catch (NumberFormatException e1) {
            throw new Exception("NumberFormatException");
        } finally {
            try {
                int result = 2 / 0;
            } catch (ArithmeticException e2) {
                throw new Exception("ArithmeticException");
            }
        }
    }

    public void exceptionSuppressed() throws Exception {
        Exception numFormatEx = null;
        try {
            Integer.parseInt("Hello");
        } catch (NumberFormatException e1) {
            numFormatEx = new Exception("NumberFormatException");
        } finally {
            try {
                int result = 2 / 0;
            } catch (Exception e2) {
                if (numFormatEx != null) {
                    numFormatEx.addSuppressed(e2);
                } else {
                    numFormatEx = e2;
                }
            }
            if (numFormatEx != null) throw numFormatEx;
        }
    }

    void tryWithResource() {
        /**
         * try catch finally
         */
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


        /**
         * try with resource
         */
        try (BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(new File("in.txt")));
             BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(new File("out.txt")))) {
            int b;
            while ((b = inputStream.read()) != -1) {
                outputStream.write(b);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        /**
         * custom AutoCloseable
         */
        try (MResource resources = new MResource()) {
            resources.doSomeThing();
        }
    }
}

class MResource implements AutoCloseable {
    @Override
    public void close() {
        System.out.println("close");
        //destroy some instance
    }

    public void doSomeThing() {

    }
}
