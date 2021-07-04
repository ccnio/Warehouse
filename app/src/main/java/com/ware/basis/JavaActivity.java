package com.ware.basis;

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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * # LinkedHashMap
 * 1. accessOrder为true时，每次访问一个元素（get或put），被访问的元素都被提到【最后】面去了
 * 2. 根据accessOrder很容易实现LRUCache
 */
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

    public void linkedHashMap() {
        LinkedHashMap<String, String> linkedHashMap = new LinkedHashMap<>(16, 0.75f, true);
        linkedHashMap.put("111", "111");
        linkedHashMap.put("222", "222");
        linkedHashMap.put("333", "333");
        linkedHashMap.put("444", "444");
        loopLinkedHashMap(linkedHashMap);

        linkedHashMap.get("111");
        loopLinkedHashMap(linkedHashMap);

        linkedHashMap.put("222", "2222");
        loopLinkedHashMap(linkedHashMap);
//        111=111    222=222    333=333    444=444
//        222=222    333=333    444=444    111=111
//        333=333    444=444    111=111    222=2222
    }

    public void loopLinkedHashMap(LinkedHashMap<String, String> linkedHashMap) {
        Set<Map.Entry<String, String>> set = linkedHashMap.entrySet();

        for (Map.Entry<String, String> stringStringEntry : set) {
            System.out.print(stringStringEntry + "\t");
        }
        System.out.println();
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
