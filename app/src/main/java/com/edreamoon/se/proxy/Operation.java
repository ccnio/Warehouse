package com.edreamoon.se.proxy;

public class Operation {
    public int doOperate(OnResult result) {
        result.onProcess(2);
        result.onSuccess(new Param());
        return 2;
    }
}
