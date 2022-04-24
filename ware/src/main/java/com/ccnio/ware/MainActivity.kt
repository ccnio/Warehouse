package com.ccnio.ware

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.ccnio.ware.databinding.ActivityMainBinding
import com.ccnio.ware.databinding.CatalogItemBinding
import com.ccnio.ware.jetpack.viewbinding.viewBinding
import com.ccnio.ware.utils.inflater
import java.text.Collator
import java.util.*

private val PKG_NAME: String = app.packageName
private const val TAG = "MainActivity"

open class MainActivity : AppCompatActivity(R.layout.activity_main) {
    private val binding by viewBinding(ActivityMainBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inflater
        val intent = intent
        var path = intent.getStringExtra(PKG_NAME)
        if (path == null) path = ""
        binding.recyclerView.addItemDecoration(DividerItemDecoration(this, RecyclerView.VERTICAL))
        binding.recyclerView.adapter = CatalogAdapter(this, getData(path))
//        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), 33)
////        Log.d(TAG, "onCreate: " + Settings.canDrawOverlays(this));
        Log.d(TAG, "onCreate: ${ChannelImpl().doSomething()}")
    }

    private fun getData(prefix: String): List<Any> {
        val myData = mutableListOf<Map<String, Any>>()

        /**
         * 匹配 (action == action.Warehouse && category == package) 的intent信息
         */
        val intent = Intent("action.Warehouse", null)
        intent.addCategory(PKG_NAME)
        val pm = packageManager
        val list = pm.queryIntentActivities(intent, 0)
        val prefixPath = if (prefix == "") null else prefix.split("/").toTypedArray()
        val len = list.size
        val entries: MutableMap<String, Boolean?> = HashMap()
        for (i in 0 until len) {
            val info = list[i]
            val labelSeq = info.loadLabel(pm)
            val label = labelSeq?.toString() ?: info.activityInfo.name
            if (prefix.isEmpty() || label.startsWith(prefix)) {
                val labelPath = label.split("/").toTypedArray()
                val nextLabel = if (prefixPath == null) labelPath[0] else labelPath[prefixPath.size]
                if (prefixPath?.size ?: 0 == labelPath.size - 1) {
                    addItem(myData, nextLabel, activityIntent(info.activityInfo.applicationInfo.packageName, info.activityInfo.name)!!)
                } else {
                    if (entries[nextLabel] == null) {
                        addItem(myData, nextLabel, browseIntent(if (prefix == "") nextLabel else "$prefix/$nextLabel"))
                        entries[nextLabel] = true
                    }
                }
            }
        }
        Collections.sort(myData, sDisplayNameComparator)
        return myData
    }

    private fun activityIntent(pkg: String?, componentName: String?): Intent? {
        val result = Intent()
        result.setClassName(pkg!!, componentName!!)
        return result
    }

    private fun browseIntent(path: String?): Intent {
        val result = Intent()
        result.setClass(this, MainActivity::class.java)
        result.putExtra(PKG_NAME, path)
        return result
    }

    private fun addItem(data: MutableList<Map<String, Any>>, name: String, intent: Intent) {
        val temp: MutableMap<String, Any> = HashMap()
        temp["title"] = name
        temp["intent"] = intent
        data.add(temp)
    }

    private val sDisplayNameComparator: Comparator<Map<String, Any>> = object : Comparator<Map<String, Any>> {
        private val collator = Collator.getInstance()
        override fun compare(o1: Map<String, Any>, o2: Map<String, Any>): Int {
            return collator.compare(o1["title"], o2["title"])
        }
    }

    class CatalogAdapter(private val context: Context, private val data: List<*>) : RecyclerView.Adapter<Holder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
            val binding = com.ccnio.ware.databinding.CatalogItemBinding.inflate(parent.context.inflater, parent, false)
            binding.root.setOnClickListener {
                context.startActivity(binding.root.tag as Intent)
            }
            return Holder(binding)
        }

        override fun onBindViewHolder(holder: Holder, position: Int) {
            val map = data[position] as Map<*, *>
            val intent = map["intent"] as Intent
            holder.itemView.tag = intent
            holder.binding.titleView.text = map["title"] as String
        }

        override fun getItemCount() = data.size
    }

    class Holder(val binding: CatalogItemBinding) : RecyclerView.ViewHolder(binding.root)
}