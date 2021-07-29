package com.ware.compatibility.notch;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Properties;

public class RomUtils {

    private static final String TAG = "RomUtils";
    public static final String HUAWEI = "huawei";
    public static final String VIVO = "vivo";
    public static final String XIAOMI = "xiaomi";
    public static final String OPPO = "oppo";
    public static final String SAMSUNG = "samsung";
    public static final String SONY = "sony";

    private static final String VERSION_PROPERTY_HUAWEI = "ro.build.version.emui";
    private static final String VERSION_PROPERTY_VIVO = "ro.vivo.os.build.display.id";
    private static final String VERSION_PROPERTY_XIAOMI = "ro.build.version.incremental";
    private static final String VERSION_PROPERTY_OPPO = "ro.build.version.opporom";
    private static final String VERSION_MIUI = "ro.miui.ui.version.name";
    public static final String VERSION_MIUI_VALUE_11 = "V11";
    private static final String UNKNOWN = "unknown";

    private static RomInfo bean = null;

    private RomUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * Return whether the rom is made by huawei.
     *
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isHuawei() {
        return HUAWEI.equals(getRomInfo().name);
    }

    /**
     * Return whether the rom is made by vivo.
     *
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isVivo() {
        return VIVO.equals(getRomInfo().name);
    }

    /**
     * Return whether the rom is made by xiaomi.
     *
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isXiaomi() {
        return XIAOMI.equals(getRomInfo().name);
    }

    public static boolean isSamSung() {
        return SAMSUNG.equals(getRomInfo().name);
    }

    public static boolean isSony() {
        return SONY.equals(getRomInfo().name);
    }

    public static boolean isMIUIRom(final Context context) {
        try {
            return context.getPackageManager().getPackageInfo(
                    "com.miui.cloudservice", PackageManager.GET_CONFIGURATIONS) != null;
        } catch (final PackageManager.NameNotFoundException e) {
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isMiui() {
        return isXiaomi() && getRomInfo().isMiui();
    }

    public static boolean isMiuiDev() {
        return isXiaomi() && getRomInfo().isMiuiDev();
    }

    /**
     * Return whether the rom is made by oppo.
     *
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isOppo() {
        return OPPO.equals(getRomInfo().name);
    }

    /**
     * Return the rom's information.
     *
     * @return the rom's information
     */
    public static RomInfo getRomInfo() {
        if (bean != null) return bean;
        bean = new RomInfo();
        final String brand = getBrand();
        final String manufacturer = getManufacturer();
        if (isRightRom(brand, manufacturer, HUAWEI)) {
            bean.name = HUAWEI;
            String version = getRomVersion(VERSION_PROPERTY_HUAWEI);
            String[] temp = version.split("_");
            if (temp.length > 1) {
                bean.version = temp[1];
            } else {
                bean.version = version;
            }
            return bean;
        }
        if (isRightRom(brand, manufacturer, VIVO)) {
            bean.name = VIVO;
            bean.version = getRomVersion(VERSION_PROPERTY_VIVO);
            return bean;
        }
        if (isRightRom(brand, manufacturer, XIAOMI)) {
            bean.name = XIAOMI;
            bean.version = getRomVersion(VERSION_PROPERTY_XIAOMI);
            bean.isMiui = checkIsMiui(false);
            bean.isMiuiDev = checkIsMiui(true);
            return bean;
        }
        if (isRightRom(brand, manufacturer, SAMSUNG)) {
            bean.name = SAMSUNG;
            bean.version = UNKNOWN;
            return bean;
        }
        if (isRightRom(brand, manufacturer, SONY)) {
            bean.name = SONY;
            bean.version = UNKNOWN;
            return bean;
        }
        if (isRightRom(brand, manufacturer, OPPO)) {
            bean.name = OPPO;
            bean.version = getRomVersion(VERSION_PROPERTY_OPPO);
            return bean;
        } else {
            bean.name = manufacturer;
        }
        bean.version = getRomVersion("");
        return bean;
    }

    private static boolean isRightRom(final String brand, final String manufacturer, final String... names) {
        for (String name : names) {
            if (brand.contains(name) || manufacturer.contains(name)) {
                return true;
            }
        }
        return false;
    }

    private static String getManufacturer() {
        try {
            String manufacturer = Build.MANUFACTURER;
            if (!TextUtils.isEmpty(manufacturer)) {
                return manufacturer.toLowerCase();
            }
        } catch (Throwable ignore) { /**/ }
        return UNKNOWN;
    }

    private static String getBrand() {
        try {
            String brand = Build.BRAND;
            if (!TextUtils.isEmpty(brand)) {
                return brand.toLowerCase();
            }
        } catch (Throwable ignore) { /**/ }
        return UNKNOWN;
    }

    public static String getBrandSpecial() {
        if (isXiaomi()) {
            return XIAOMI;
        } else if (isHuawei()) {
            return HUAWEI;
        } else if (isOppo()) {
            return OPPO;
        } else if (isVivo()) {
            return VIVO;
        } else {
            return getBrand();
        }
    }

    public static String getMiuiVersion() {
        String version = getSystemProperty(VERSION_MIUI);
        return version;
    }

    private static String getRomVersion(final String propertyName) {
        String ret = "";
        if (!TextUtils.isEmpty(propertyName)) {
            ret = getSystemProperty(propertyName);
        }
        if (TextUtils.isEmpty(ret) || ret.equals(UNKNOWN)) {
            try {
                String display = Build.DISPLAY;
                if (!TextUtils.isEmpty(display)) {
                    ret = display.toLowerCase();
                }
            } catch (Throwable ignore) { /**/ }
        }
        if (TextUtils.isEmpty(ret)) {
            return UNKNOWN;
        }
        return ret;
    }

    private static String getSystemProperty(final String name) {
        String prop = getSystemPropertyByShell(name);
        if (!TextUtils.isEmpty(prop)) return prop;
        prop = getSystemPropertyByStream(name);
        if (!TextUtils.isEmpty(prop)) return prop;
        if (Build.VERSION.SDK_INT < 28) {
            return getSystemPropertyByReflect(name);
        }
        return prop;
    }

    private static String getSystemPropertyByShell(final String propName) {
        String line;
        BufferedReader input = null;
        try {
            Process p = Runtime.getRuntime().exec("getprop " + propName);
            input = new BufferedReader(new InputStreamReader(p.getInputStream()), 1024);
            String ret = input.readLine();
            if (ret != null) {
                return ret;
            }
        } catch (IOException ignore) {
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException ignore) { /**/ }
            }
        }
        return "";
    }

    private static String getSystemPropertyByStream(final String key) {
        try (FileInputStream is = new FileInputStream(
                new File(Environment.getRootDirectory(), "build.prop"))) {
            Properties prop = new Properties();
            prop.load(is);
            return prop.getProperty(key, "");
        } catch (Exception ignore) { /**/ }
        return "";
    }

    private static String getSystemPropertyByReflect(String key) {
        try {
            @SuppressLint("PrivateApi")
            Class<?> clz = Class.forName("android.os.SystemProperties");
            Method getMethod = clz.getMethod("get", String.class, String.class);
            return (String) getMethod.invoke(clz, key, "");
        } catch (Exception e) { /**/ }
        return "";
    }

    private static boolean checkIsMiui(boolean onlyDev) {
        Class<?> buildClass = null;
        try {
            buildClass = Class.forName("miui.os.Build");
        } catch (Exception e) {
            e.printStackTrace();
        }

        boolean result = false;
        if (buildClass != null) {
            result = getStaticBooleanField(buildClass,
                    onlyDev ? "IS_DEVELOPMENT_VERSION" : "IS_MIUI", false);
        }
        Log.e(TAG, onlyDev ? "isMiuiDev: " : "isMiui: " + result);
        return result;
    }

    private static boolean getStaticBooleanField(Class<?> buildClass, String fieldName, boolean defaultValue) {
        try {
            Field field = buildClass.getField(fieldName);
            field.setAccessible(true);
            return field.getBoolean(null);
        } catch (Exception e) {
            Log.e(TAG, "getStaticBooleanField  fail");
        }
        return defaultValue;
    }

    public static boolean supportMIUINearByApiService() {
        Log.e(TAG, "supportMIUINearByApiService  product: " + Build.PRODUCT);
        return RomUtils.isMiuiDev() && ("cmi".equals(Build.PRODUCT) || "umi".equals(Build.PRODUCT));
    }

    public static class RomInfo {
        private String name;
        private String version;
        private boolean isMiui;
        private boolean isMiuiDev;

        public String getName() {
            return name;
        }

        public String getVersion() {
            return version;
        }

        public boolean isMiui() {
            return isMiui;
        }

        public boolean isMiuiDev() {
            return isMiuiDev;
        }

        @Override
        public String toString() {
            return "RomInfo{name=" + name
                    + ", version=" + version
                    + ", isMiui:" + isMiui
                    + ", isMiuiDev:" + isMiuiDev
                    + "}";
        }
    }

}
