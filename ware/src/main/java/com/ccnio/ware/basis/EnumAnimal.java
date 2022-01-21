package com.ccnio.ware.basis;

/**
 * Created by ccino on 2022/1/21.
 */
public enum EnumAnimal {
    DOG, CAT
}
/*
最终生成大概是这样的类，跟两个静态INT常量比内存，那肯定是多得多的。其次，我们也能顺便回答，枚举对象为什么是单例了。
很多“语法糖”类似的东西，都能按照这样的思路去了解它的原理:jad -sjava example1.class。

//1. 继承 java.lang.Enum 类，所以不能继承其他父类;
//2. 默认使用 final 修饰，因此不能派生子类；
public final class Animal extends Enum {
    //java.lang.Enum 类实现了 java.lang.Serializable 和 java.lang.Comparable 接口；

    public static Animal[] values() {
        return (Animal[]) $VALUES.clone();
    }

    public static Animal valueOf(String s) {
        return (Animal) Enum.valueOf(Animal, s);
    }

    //构造器使用 private 修饰
    private Animal(String s, int i) {
        super(s, i);
    }

    public static final Animal DOG;
    public static final Animal CAT;
    private static final Animal $VALUES[];

    static {
        DOG = new Animal("DOG", 0);
        CAT = new Animal("CAT", 1);
        $VALUES = (new Animal[]{
                DOG, CAT
        });
    }
}*/
