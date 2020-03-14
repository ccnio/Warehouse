package com.ware.point;

import androidx.annotation.IntRange;

/**
 * Created by jianfeng.li on 2020/3/13.
 * 位运算的运算优先级：[~]>[<</>>]>[&]>[^]>[|]>[&=/^=/|=]
 * <p>
 * A | B: 添加标志B，可以添加多个（只要不冲突）
 * 例如：mViewFlags = SOUND_EFFECTS_ENABLED | HAPTIC_FEEDBACK_ENABLED | FOCUSABLE_AUTO;
 * <p>
 * A & B: 判断A中是否有标志B
 * 以(mViewFlags & FOCUSABLE_AUTO) != 0这个判断语句为例：若为真，表示flag之中有该标志
 * <p>
 * A & ~B: 从A中去除标志B
 * 例如：mViewFlags = (mViewFlags & ~FOCUSABLE) | newFocus用于去除原有的标志位并附上新的标志位（相当于更新）
 * <p>
 * A ^ B（异或运算（xor） ^表示，当两个相同位对应的数字不同的时候为1，否则为0）：取出A与B不同的部分
 * 一般用于判断A是否发生改变
 * int changed = mViewFlags ^ old; change == 0 表示相同
 */
public class BitOp {
    /*************************Flags******************************/
    /**
     * 适用场景：各flag可以互相组合，不是单一互斥的
     */
    //1111 1111: 8 bit represent 8 type
    private boolean allowSelect;
    private boolean allowInsert;
    private boolean allowDelete;
    private boolean allowUpdate;
    public static final int ALLOW_SELECT = 0x00000001; // 0001 1<<0
    public static final int ALLOW_INSERT = 0x00000002; // 0010 1<<1
    public static final int ALLOW_UPDATE = 0x00000004; // 0100 1<<2
    public static final int ALLOW_DELETE = 0x00000008; // 1000 1<<3

    private int flag; // 存储目前的权限状态

    /**
     * 重新设置权限
     */
    public void setPermission(int permission) {
        flag = permission;
    }

    /**
     * 添加一项或多项权限
     */
    public void enable(int permission) {
        flag |= permission;
    }

    /**
     * 删除一项或多项权限
     */
    public void disable(int permission) {
        flag &= ~permission;
    }

    /**
     * 是否拥某些权限
     */
    public boolean isAllow(int permission) {
        return (flag & permission) == permission;
    }

    /**
     * 是否禁用了某些权限
     */
    public boolean isNotAllow(int permission) {
        return (flag & permission) == 0;
    }

    /**
     * 是否仅仅拥有某些权限
     */
    public boolean isOnlyAllow(int permission) {
        return flag == permission;
    }

    /*************************Flags with Value******************************/
    /**
     * 适用场景：只能存在一种模式，此模式可以携带状态或者值
     */
    //pre 8 bit represent flag, later 24 represent value
    private static final int MODE_SHIFT = 24; // 声明位移量
    private static final int MODE_MASK = 0xff << MODE_SHIFT; //1111 1111 截取SpecMode或SpecSize时使用的变量

    // 三种模式对应的值
    public static final int UNSPECIFIED = 0 << MODE_SHIFT;
    public static final int EXACTLY = 1 << MODE_SHIFT;
    public static final int AT_MOST = 2 << MODE_SHIFT;
    private int measureSpec = makeMeasureSpec(0, UNSPECIFIED);

    public int makeMeasureSpec(@IntRange(from = 0, to = (1 << MODE_SHIFT) - 1) int size, int mode) {
        //分别进行位移得到高8位,低24位 进行或运算
        return (size & ~MODE_MASK) | (mode & MODE_MASK);
    }

    public static int getMode(int measureSpec) {
        //让低24位的值变为0，只保留高8位的值
        return (measureSpec & MODE_MASK);
    }

    public static int getSize(int measureSpec) {
        //1. 非运算直接让MASK值变成int值高8位为0，低24位为1
        //2. 进行与运算，直接将高2位的值变为0
        return (measureSpec & ~MODE_MASK);
    }

    //重新设置
    public void setMeasureSpec(int measureSpec) {
        this.measureSpec = measureSpec;
    }

    //是否某个mode
    public boolean isMode(int mode) {
        return (measureSpec & MODE_MASK) == mode;
    }

}
