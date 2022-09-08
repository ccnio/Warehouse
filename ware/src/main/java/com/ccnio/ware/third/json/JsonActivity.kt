package com.ccnio.ware.third.json

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.ccnio.ware.R
import com.ccnio.ware.databinding.ActivityJsonBinding
import com.ccnio.ware.jetpack.viewbinding.viewBinding
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

private const val TAG = "JsonActivity"

/**
 * # moshi
 *  implementation("com.squareup.moshi:moshi-kotlin:1.13.0")
 *  kapt "com.squareup.moshi:moshi-kotlin-codegen:1.13.0"
 * ## 字段相关
 * 如果 bean 非空，但 json 无对应字段，则会报 "Required value 'name' missing at $"
 * 如果 bean 非空有默认值，但 json 无对应字段，则会使用默认字段
 * 如果 bean 非空，但 json 对应字段为 null，则会报 "Non-null value 'name' was null at $.name"
 * ## adapter
 * Json 数据格式与 Kotlin Class 定义不同，通过 Adapter 我们可以控制 Json 与 class 如何转换。
 * Adapter 就是一个普通的类不继承自任何父类。只需要定义两个函数分别用于 Json→Class 与 Class→Json 的转换，并分别加上 @FromJson 与 @ToJson 注解就行了。
 * ## retrofit
 * moshi配合retrofit
 * 添加依赖 api "com.squareup.retrofit2:converter-moshi:$retrofit"
 *
 * 1. 如果添加了 kapt/JsonAdapter 生成注解首选 注解，否则使用反射，使用反射时要避免混淆
 * 2. 添加了 JsonAdapter 注解的类不会混淆。但要注意从基类到子类一定要都添加注解
 * 3. 父类 @Json 字段映射不起作用，必须与接口字义一致且是 var 类型。
 */
class JsonActivity : AppCompatActivity() {
    private val bind by viewBinding(ActivityJsonBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_json)
        bind.moshiReflectView.setOnClickListener { moshiReflect() }
        bind.moshiKaptView.setOnClickListener { moshiKapt() }
    }

    private fun moshiKapt() {
        val person = PersonBean("xiaoming", 12, sex = Sex.MALE)
        val moshi = Moshi.Builder().add(SexAdapter()).build()
        //获取对应类的adapter
        val adapter = moshi.adapter(PersonBean::class.java)
        //adapter.toJson 把对象变成json
        Log.d(TAG, "moshiKapt: toJson = ${adapter.toJson(person)}")

        val json = "{\"name\":\"xiaoming\",\"age\":12,\"country\":\"中国\",\"sex\":true}"
        Log.d(TAG, "moshiKapt: fromJson = ${adapter.fromJson(json)}")


        /**
         * 复杂泛型
         */
        val users = mutableListOf<UserInfo>()
        for (i in 1..3) {
            val user = UserInfo("xeon", 28, arrayListOf(Hobby("游戏", "王者荣耀"), Hobby("运行", "跑步")))
            users.add(user)
        }

        val baseResp = BaseResp<List<UserInfo>>(200, "请求成功", users)

        /**
         * 重点在这 先生成List<User>这种类型
         * 处理不当可能会报：com.squareup.moshi.LinkedHashTreeMap cannot be cast to xxx，因为泛型擦除无法知晓类型
         */
        val listUserType = Types.newParameterizedType(List::class.java, UserInfo::class.java)
        /* 最后是BaseResp<List<User>> */
        val parameterizedType = Types.newParameterizedType(BaseResp::class.java, listUserType)

        val jsonAdapter = moshi.adapter<BaseResp<List<UserInfo>>>(parameterizedType)
        val toJson = jsonAdapter.toJson(baseResp)
        Log.d(TAG, "genericList: $toJson")
        val jsonStr = """
       {"code":200,"msg":"请求成功","data":[{"name":"xeon","age":28,"hobby":[{"type":"游戏","name":"王者荣耀"},{"type":"运行","name":"跑步"}]},{"name":"xeon","age":28,"hobby":[{"type":"游戏","name":"王者荣耀"},{"type":"运行","name":"跑步"}]},{"name":"xeon","age":28,"hobby":[{"type":"游戏","name":"王者荣耀"},{"type":"运行","name":"跑步"}]}]}
    """.trimIndent()
        val fromJson = jsonAdapter.fromJson(jsonStr)
        Log.d(TAG, "genericList: fromJson = $fromJson")
    }


    /**
     * 反射不需要 @JsonClass(generateAdapter = true)
     */
    private fun moshiReflect() {
        //1. 创建moshi
        val moshi = Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())//使用kotlin反射处理，要加上这个
            .build()

        /**
         * 处理 单Bean
         */
        //2. 声明adapter，指定要处理的类型
        val beanAdapter = moshi.adapter(UserBean::class.java)
        //序列化
        val user = UserBean("cc", 18)
        val toJson = beanAdapter.toJson(user)
//        val toJson = jsonAdapter.indent(" ").toJson(user) 格式化 json
        Log.d(TAG, "moshi: toJson = $toJson")
        //反序列化
        val fromJson = beanAdapter.fromJson(toJson)
        //val fromJson = jsonAdapter.fromJson("{\"name\":null,\"age\":18}")
        //val fromJson = jsonAdapter.fromJson("{\"age\":18}")
        Log.d(TAG, "moshi: fromJson = $fromJson")

        /**
         * 处理 list
         */
        val users = mutableListOf<UserBean>()
        for (i in 1..3) {
            val u = UserBean("cc", 25 + i)
            users.add(u)
        }
        /*声明adapter，指定要处理的类型*/
        val listParameterizedType =
            Types.newParameterizedType(List::class.java, UserBean::class.java)
        val jsonAdapter = moshi.adapter<List<UserBean>>(listParameterizedType)
        val toJsonList = jsonAdapter.toJson(users)
        Log.d(TAG, "moshiReflect: list toJson = $toJsonList")
        val jsonStr = """
        [{"name":"cc","age":26},{"name":"cc","age":27},{"name":"cc","age":28}]
    """.trimIndent()
        val beanList = jsonAdapter.fromJson(jsonStr)
        Log.d(TAG, "moshiReflect: list fromJson = $beanList")

        /**
         * 处理泛型
         */
        val baseResp = BaseResp(200, "成功", user)
        /*声明adapter，指定要处理的类型*/
        val parameterizedType =
            Types.newParameterizedType(BaseResp::class.java, UserBean::class.java)
        val genericAdapter = moshi.adapter<BaseResp<UserBean>>(parameterizedType)
        val genericToJson = genericAdapter.toJson(baseResp)
        Log.d(TAG, "moshiReflect: generic toJson = $genericToJson")
        val str = """
        {"code":200,"msg":"请求成功","data":{"name":"cc","age":28}}
    """.trimIndent()
        val genericFromJson = genericAdapter.fromJson(str)
        Log.d(TAG, "moshiReflect: generic fromJson = $genericFromJson")

        /**
         * 处理复杂类型 Hobby,UserInfo
         */
        val hobbyResp = BaseResp(
            200,
            "请求成功",
            UserInfo("cc", 28, arrayListOf(Hobby("游戏", "王者荣耀"), Hobby("运动", "跑步")))
        )
        /*声明adapter，指定要处理的类型*/
        val hobbyParameterizedType =
            Types.newParameterizedType(BaseResp::class.java, UserInfo::class.java)
        val hobbyJsonAdapter = moshi.adapter<BaseResp<UserInfo>>(hobbyParameterizedType)
        val hobbyToJson = hobbyJsonAdapter.toJson(hobbyResp)
        Log.d(TAG, "moshiReflect: complex toJson = $hobbyToJson")
        val hobbyJsonStr = """
        {"code":200,"msg":"请求成功","data":{"name":"xeon","age":28,"hobby":[{"type":"游戏","name":"王者荣耀"},{"type":"运行","name":"跑步"}]}}
    """.trimIndent()
        val hobbyFromJson = hobbyJsonAdapter.fromJson(hobbyJsonStr)
        Log.d(TAG, "moshiReflect: complex fromJson = $hobbyFromJson")

        /**
         * moshi处理泛型直接是个List的场景,
         * 再来看看data直接就是一个List的场景,数据类跟上面一样不变
         */
        moshiGenericList()
        moshiRetrofit()
    }

    private fun moshiRetrofit() {
        //创建Retrofit的时候把moshi转换器添加进去
        val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
        val retrofit = Retrofit.Builder()
            .baseUrl("https://github.com")
            .client(OkHttpClient())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    private fun moshiGenericList() {
        /*创建moshi*/
        val moshi = Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())//使用kotlin反射处理，要加上这个
            .build()
        val users = mutableListOf<UserInfo>()
        for (i in 1..3) {
            val user = UserInfo("xeon", 28, arrayListOf(Hobby("游戏", "王者荣耀"), Hobby("运行", "跑步")))
            users.add(user)
        }

        val baseResp = BaseResp<List<UserInfo>>(200, "请求成功", users)

        /**
         * 重点在这 先生成List<User>这种类型
         * 处理不当可能会报：com.squareup.moshi.LinkedHashTreeMap cannot be cast to xxx，因为泛型擦除无法知晓类型
         */
        val listUserType = Types.newParameterizedType(List::class.java, UserInfo::class.java)
        /* 最后是BaseResp<List<User>> */
        val parameterizedType = Types.newParameterizedType(BaseResp::class.java, listUserType)

        val jsonAdapter = moshi.adapter<BaseResp<List<UserInfo>>>(parameterizedType)
        val toJson = jsonAdapter.toJson(baseResp)
        Log.d(TAG, "genericList: $toJson")
        val jsonStr = """
       {"code":200,"msg":"请求成功","data":[{"name":"xeon","age":28,"hobby":[{"type":"游戏","name":"王者荣耀"},{"type":"运行","name":"跑步"}]},{"name":"xeon","age":28,"hobby":[{"type":"游戏","name":"王者荣耀"},{"type":"运行","name":"跑步"}]},{"name":"xeon","age":28,"hobby":[{"type":"游戏","name":"王者荣耀"},{"type":"运行","name":"跑步"}]}]}
    """.trimIndent()
        val fromJson = jsonAdapter.fromJson(jsonStr)
        Log.d(TAG, "genericList: fromJson = $fromJson")

    }

}