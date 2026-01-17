package com.ccino.demo.util

import android.util.Log
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import dagger.multibindings.StringKey
import javax.inject.Inject
import javax.inject.Qualifier
import javax.inject.Singleton

private const val TAG = "HiltCase"

/******************* 管理模块 *******************/
const val ID_CATEGORY_FOOD = "id_food"
const val ID_CATEGORY_DRINK = "id_drink"

@Singleton
class Store @Inject constructor(@CategoryQualifier private val category: MutableMap<String, GoodsCategory>){
    init {
        Log.d(TAG, "Store init: size=${category.size}")
    }
//
//    fun printAllCategory() {
//        category.forEach {
//            Log.d(TAG, "printAllCategory: id=${it.key}")
//        }
//    }
//
//    fun printCategory(id: String) {
//        val name = category[id]?.name()
//        Log.d(TAG, "printCategory: name=$name")
//    }
}


/**
 * @Qualifier: 用于为 @Binds 或 @Provides 方法添加注解的限定符, 为同一类型提供多个绑定
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class CategoryQualifier

interface GoodsCategory {
    fun name(): String
}

/******************* food 业务 *******************/
class FoodCategory @Inject constructor() : GoodsCategory {
    override fun name() = "FOOD"
}

@Module
@InstallIn(SingletonComponent::class)
abstract class FoodModule {
    @Binds
    @CategoryQualifier
    @IntoMap
    @StringKey("id_food")
    abstract fun bindCategory(food: FoodCategory): GoodsCategory
}


/******************* drink 业务 *******************/
class DrinkCategory @Inject constructor() : GoodsCategory {
    override fun name() = "DRINK"
}

@Module
@InstallIn(SingletonComponent::class)
abstract class DrinkModule {
    @Binds
    @CategoryQualifier
    @IntoMap
    @StringKey("id_drink")
    abstract fun categoryDrink(drink: DrinkCategory): GoodsCategory
}