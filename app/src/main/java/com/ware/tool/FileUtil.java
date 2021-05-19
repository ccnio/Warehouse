package com.ware.tool;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.WorkerThread;

import com.ware.WareApp;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

public class FileUtil {

    private static final String TAG = "FileUtil";

    public static final String EXTERNAL_STORAGE_ROOT_DIRECTORY = "MiWatch";
    private static final String TEMP_DIR = "temp";

    public static String getTempDirPath() {
        return getExternalStorageDirPath(TEMP_DIR);
    }

    public static String getExternalStorageDirPath(String dir) {
        return ensureDirectoryExist(getExternalStorageRootPath() + File.separator + dir);
    }

    public static String getExternalStorageRootPath() {
        return ensureDirectoryExist(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath()
                + File.separator + EXTERNAL_STORAGE_ROOT_DIRECTORY);
    }

    private static String ensureDirectoryExist(String path) {
        File file = new File(path);
        if (file.exists()) {
            if (!file.isDirectory()) {
                file.delete();
                file.mkdirs();
            }
        } else {
            file.mkdirs();
        }
        return path;
    }

    @WorkerThread
    public static String getPath(final Uri uri) {
        Context context = WareApp.sContext;
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {

            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();
            Log.d(TAG, "getPath: " + uri.toString());
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @return The value of the _data column, which is typically a file path.
     */
    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        final String[] projection = {MediaStore.Images.Media.DATA};
        ContentResolver resolver = context.getContentResolver();
        try (Cursor cursor = resolver.query(uri, projection, selection, selectionArgs, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                return cursor.getString(column_index);
            }
        } catch (Exception e) {
            return createTempFile(uri);
        }
        return null;
    }

    private static String createTempFile(Uri uri) {
        Log.d(TAG, "createTempFile uri = " + uri.toString());
        ContentResolver resolver = WareApp.sContext.getContentResolver();
        try (InputStream inputStream = resolver.openInputStream(uri)) {
            String path = getTempDirPath() + File.separator + uri.getLastPathSegment();
            boolean ok = copyFile(inputStream, path);
            Log.d(TAG, "ok = " + ok + "; path = " + path);
            return ok ? path : null;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return null;
    }

    public static boolean copyFile(InputStream input, String path) {
        if (input == null || TextUtils.isEmpty(path)) return false;

        File file = new File(path);

        File parentFile = file.getParentFile();
        if (!parentFile.exists() && !parentFile.mkdirs()) return false;

        Log.d(TAG, "copyFile parent " + parentFile.exists());
        try (BufferedSource bufferedSource = Okio.buffer(Okio.source(input));
             BufferedSink bufferedSink = Okio.buffer(Okio.sink(new File(path)))) {
            bufferedSink.writeAll(bufferedSource);
            bufferedSink.flush();
            return true;
        } catch (Exception e) {
            Log.e(TAG, "copyFile " + e.getMessage());
        }
        return false;
    }

    /**
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
