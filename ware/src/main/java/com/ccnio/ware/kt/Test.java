package com.ccnio.ware.kt;

import android.content.Intent;
import android.util.Log;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.Boxing;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.BuildersKt;
import kotlinx.coroutines.Dispatchers;


/**
 * Created by ccino on 2022/3/2.
 */
public class Test {
    public void test(){
//
//        BuildersKt.withContext((CoroutineContext) Dispatchers.getIO(), (Function2)(new Function2((Continuation)null) {
//            int label;
//
//            @Nullable
//            public final Object invokeSuspend(@NotNull Object var1) {
//                Object var2 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
//                switch(this.label) {
//                    case 0:
//                        ResultKt.throwOnFailure(var1);
//                        Log.d("Demo", "getVisitedCount: user = " + user);
//                        return Boxing.boxInt(100);
//                    default:
//                        throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
//                }
//            }
//
//            @NotNull
//            public final Continuation create(@Nullable Object value, @NotNull Continuation completion) {
//                Intrinsics.checkNotNullParameter(completion, "completion");
//                Function2 var3 = new <anonymous constructor>(completion);
//                return var3;
//            }
//
//            public final Object invoke(Object var1, Object var2) {
//                return ((<undefinedtype>)this.create(var1, (Continuation)var2)).invokeSuspend(Unit.INSTANCE);
//            }
//        }), $completion)
    }
}
