package com.ccino.demo.appfunction

import androidx.appfunctions.AppFunctionContext
import androidx.appfunctions.AppFunctionSerializable
import androidx.appfunctions.AppFunction

/**
 * 提供一组演示用的数学 AppFunction。
 *
 * 这个类同时包含：
 * 1. 使用结构化对象作为入参与返回值的示例。
 * 2. 使用基础类型作为入参与返回值的示例。
 */
class MathFunctions {

    /** 结构化加法入参。 */
    @AppFunctionSerializable(isDescribedByKDoc = true)
    data class AddParams(
        /** 第一个加数。 */
        val a: Double,
        /** 第二个加数。 */
        val b: Double,
    )

    /** 结构化计算结果。 */
    @AppFunctionSerializable(isDescribedByKDoc = true)
    data class AddResult(
        /** 计算结果值。 */
        val sum: Double,
    )

    /**
     * 计算两个数字之和，并以结构化对象返回结果。
     *
     * @param addParams 需要相加的两个数字。
     * @return 包含求和结果的结构化对象。
     */
    @AppFunction(isDescribedByKDoc = true)
    suspend fun add(
        appFunctionContext: AppFunctionContext,
        addParams: AddParams,
    ): AddResult {
        return AddResult(sum = addParams.a + addParams.b)
    }

    /**
     * 计算两个数字之和，并直接返回基础类型结果。
     *
     * @param a 第一个加数。
     * @param b 第二个加数。
     * @return 两数之和。
     */
    @AppFunction(isDescribedByKDoc = true)
    suspend fun addPrimitive(
        appFunctionContext: AppFunctionContext,
        a: Double,
        b: Double,
    ): Double {
        return a + b
    }

    /**
     * 计算一个数字的绝对值，并直接返回基础类型结果。
     *
     * @param value 输入数字。
     * @return 输入数字的绝对值。
     */
    @AppFunction(isDescribedByKDoc = true)
    suspend fun abs(
        appFunctionContext: AppFunctionContext,
        value: Double,
    ): Double {
        return kotlin.math.abs(value)
    }
}
