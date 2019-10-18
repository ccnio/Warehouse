package com.ware;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.ware.common.Utils;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends ListActivity {

    private static final String PKG_NAME = Utils.mContext.getPackageName();
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String path = intent.getStringExtra(PKG_NAME);

        Log.d(TAG, "onCreate: " + PKG_NAME + "  " + BuildConfig.APPLICATION_ID);
        if (path == null) {
            path = "";
        }

        /**
         * SimpleAdapter是扩展性最好的适配器，可以定义各种你想要的布局，而且使用很方便
         * SimpleAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to)
         */
        setListAdapter(new SimpleAdapter(this, getData(path),
                android.R.layout.simple_list_item_1, new String[]{"title"},
                new int[]{android.R.id.text1}));

        /**
         * ListView setTextFilterEnabled
         *
         * Enables or disables the type filter window. If enabled, typing when this view has focus will filter the children to match the users input.
         * Note that the Adapter used by this view must implement the Filterable interface.
         * 用来过滤列表中的数据:在ListView上输入字母，就会自动筛选出以此内容开头的Item,注意只能搜索出以其开头的条目
         */
        getListView().setTextFilterEnabled(false);
        getListView().setPadding(15, 0, 15, 0);
//        requestPermissions(new String[]{Manifest.permission.ACCESS_NOTIFICATION_POLICY, Manifest.permission.SYSTEM_ALERT_WINDOW, Manifest.permission.WRITE_EXTERNAL_STORAGE,
//        Manifest.permission.CAMERA}, 33);
//        Log.d(TAG, "onCreate: " + Settings.canDrawOverlays(this));
    }

    protected List getData(String prefix) {
        List<Map> myData = new ArrayList<Map>();

        /**
         * 匹配 (action == action.Warehouse && category == package) 的intent信息
         */
        Intent intent = new Intent("action.Warehouse", null);
        intent.addCategory(PKG_NAME);

        PackageManager pm = getPackageManager();
        List<ResolveInfo> list = pm.queryIntentActivities(intent, 0);

        if (null == list) {
            return myData;
        }

        String[] prefixPath;

        if (prefix.equals("")) {
            prefixPath = null;
        } else {
            prefixPath = prefix.split("/");
        }

        int len = list.size();

        Map<String, Boolean> entries = new HashMap<String, Boolean>();

        for (int i = 0; i < len; i++) {
            ResolveInfo info = list.get(i);
            CharSequence labelSeq = info.loadLabel(pm);
            String label = labelSeq != null
                    ? labelSeq.toString()
                    : info.activityInfo.name;

            if (prefix.length() == 0 || label.startsWith(prefix)) {

                String[] labelPath = label.split("/");

                String nextLabel = prefixPath == null ? labelPath[0] : labelPath[prefixPath.length];

                if ((prefixPath != null ? prefixPath.length : 0) == labelPath.length - 1) {
                    addItem(myData, nextLabel, activityIntent(
                            info.activityInfo.applicationInfo.packageName,
                            info.activityInfo.name));
                } else {
                    if (entries.get(nextLabel) == null) {
                        addItem(myData, nextLabel, browseIntent(prefix.equals("") ? nextLabel : prefix + "/" + nextLabel));
                        entries.put(nextLabel, true);
                    }
                }
            }
        }

        Collections.sort(myData, sDisplayNameComparator);

        return myData;
    }

    protected Intent activityIntent(String pkg, String componentName) {
        Intent result = new Intent();
        result.setClassName(pkg, componentName);
        return result;
    }

    protected Intent browseIntent(String path) {
        Intent result = new Intent();
        result.setClass(this, MainActivity.class);
        result.putExtra(PKG_NAME, path);
        return result;
    }

    protected void addItem(List<Map> data, String name, Intent intent) {
        Map<String, Object> temp = new HashMap<String, Object>();
        temp.put("title", name);
        temp.put("intent", intent);
        data.add(temp);
    }

    private final static Comparator<Map> sDisplayNameComparator = new Comparator<Map>() {
        private final Collator collator = Collator.getInstance();

        public int compare(Map map1, Map map2) {
            return collator.compare(map1.get("title"), map2.get("title"));
        }
    };


    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        /**
         * 获取Item关联的map数据
         */
        Map map = (Map) l.getItemAtPosition(position);

        Intent intent = (Intent) map.get("intent");
        startActivity(intent);
    }

    public static void start(Context context) {
        context.startActivity(new Intent(context, MainActivity.class));
    }
}
