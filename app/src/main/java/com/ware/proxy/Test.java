package com.ware.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class Test {
    public static void main(String[] args) {
        try {
            Class<?> opeClass = Class.forName("com.ware.proxy.Operation");
            Object opeObj = opeClass.newInstance();
            Class<?> resultClass = Class.forName("com.ware.proxy.OnResult");
            Method doOperateMethod = opeClass.getMethod("doOperate", resultClass);

            //代理 OnResult 实例
            InvocationHandler handler = new DynamicProxy();
            /**
             InvocationHandler handler = new DynamicProxy(new MOnResult());
             */
            Object instanceC = Proxy.newProxyInstance(handler.getClass().getClassLoader(), new Class[]{resultClass}, handler);

            doOperateMethod.invoke(opeObj, instanceC);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}


class DynamicProxy implements InvocationHandler {

    private OnResult mTarget;

    public DynamicProxy(OnResult result) {
        mTarget = result;
    }

    public DynamicProxy() {
    }

    @Override
    public Object invoke(Object object, Method method, Object[] args)
            throws Throwable {
        System.out.println("before: " + method.getName());
        /**
         // 当代理对象调用真实对象的方法时，其会自动的跳转到代理对象关联的handler对象的invoke方法来进行调用
         return method.invoke(mTarget, args);
         **/
        System.out.println("after");
        return null;
    }
}

class MOnResult implements OnResult {

    @Override
    public void onSuccess(Param param) {
        System.out.println("MOnResult onSuccess");
    }

    @Override
    public void onProcess(int process) {
        System.out.println("MOnResult onProcess");
    }
}