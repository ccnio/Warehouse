## 分发流程概要
dispatchTouchEvent()、onTouchEvent()、onInterceptTouchEvent() 这3个方法的默认实现情况下：调用下层的方法 & 逐层返回，事件传递情况：（呈U型）

   1. 从上往下调用dispatchTouchEvent()：Activity A ->> ViewGroup B ->> View C
   2. 从下往上调用onTouchEvent()： View C ->> ViewGroup B ->> Activity A

 view 没有 onInterceptTouchEvent 方法， ViewGroup 才有。
```java
// ViewGroup
public boolean dispatchTouchEvent(MotionEvent ev) {
    boolean result = false;             // 默认状态为没有消费过

    if (!onInterceptTouchEvent(ev)) { // 如果没有拦截交给子View
        result = child.dispatchTouchEvent(ev); // 结果false的话父view还会再处理
    }

    if (!result) { // 如果事件没有被消费,询问自身onTouchEvent
        result = onTouchEvent(ev);
    }
    return result;
}

// View
public boolean dispatchTouchEvent(MotionEvent event) {  
    if ((mViewFlags & ENABLED_MASK) == ENABLED && 
          mOnTouchListener != null && mOnTouchListener.onTouch(this, event)) {  
        return true;  
    } 

    return onTouchEvent(event);  
}
```
## 分发流程分析
主要包括：Activity事件分发机制、ViewGroup事件分发机制、View事件分发机制
### Activity的事件分发
核心方法：**dispatchTouchEvent() 、onTouchEvent()**
入口 ViewRootImpl->WindowInputDispatcherReceiver::onInputEvent->**DecorView::dispatchTouchEvent**
```java
@Override
public boolean dispatchTouchEvent(MotionEvent ev) {
    final Window.Callback cb = mWindow.getCallback();
    // 交给应用Activity的dispatchTouchEvent处理触控事件
    return cb != null && !mWindow.isDestroyed() && mFeatureId < 0
        ? cb.dispatchTouchEvent(ev) : super.dispatchTouchEvent(ev);
}
```
Window.Callback指向当前Activity
```java
public boolean dispatchTouchEvent(MotionEvent ev) {
    ...
    // 先交给窗口PhoneWindow->DecorView->ViewGroup 处理
    if (getWindow().superDispatchTouchEvent(ev)) {
        return true;
    }
    // PhoneWindow没有消费事件则继续交给Activity的onTouchEvent继续处理
    return onTouchEvent(ev);
}

```
Activity中会先将事件交给PhoneWindow处理，实际上就是交给DecorView中处理。
### ViewGroup的事件分发
核心方法：dispatchTouchEvent()、onTouchEvent() 、onInterceptTouchEvent()。
#### onInterceptTouchEvent
onInterceptTouchEvent 并不能消费事件，它相当于是一个分叉口起到分流导流的作用。

**ViewGroup 拦截且消费事件**
即 onInterceptTouchEvent、onTouchEvent()返回true时，那么后续其他事件（Move、Up）将直接传递给此View的onTouchEvent()，不再经过 onInterceptTouchEvent，此时 TouchTarget 也会为空。

**拦截DOWN的后续事件**
若 ViewGroup 拦截了一个半路的事件（如MOVE），该事件将会被系统变成一个CANCEL事件 & 传递给之前处理该事件的子View:

- 该事件不会再传递给ViewGroup 的onTouchEvent()
- 只有再到来的事件才会传递到ViewGroup的onTouchEvent()
- 触发子view的cancel时会将执行TouchTarget的清空

所以后续事件将直接传递给ViewGroup B 的onTouchEvent()处理，而不会再传递给ViewGroup B 的onInterceptTouchEvent（），因该方法一旦返回一次true，就再也不会被调用了。
#### Down 事件的影响
先说一些细节，再看源码：

- down 事件时会清除 TouchTarget，重置一些事件状态。
- down 事件影响 move、up事件的处理：
   - down事件如果父控件没有拦截。 就会去找能处理该事件的子view，找到的话就将该子view赋值给mFirstTouchTarget。后续的 move、up事件 由于mFirstTouchTarget不为空，还会继续先经过父view的拦截判断才决定是否交给子 view（参见下面源码），如果原来子 View 处理了 down 事件，后面的 move/up 又被父 view 拦截了，那将会触发该子 View 的 Cancel 事件。
   - down事件如果被父控件消费了(消费)。mFirstTouchTarget也没有被赋值的机会，故后续的move和up事件均不会走拦截。
   - 由上述两条可见 onTouchEvent 的在 未设置 disallowIntercept 的执行时机：1. down 事件；2. 有子 View 处理事件。
#### TouchTarget 记录事件消费的子 View
TouchTarget的作用场景在事件派发流程中，**用于记录派发目标**，即消费了事件的子view。在ViewGroup中有一个成员变量**mFirstTouchTarget**，作为TouchTarget链表的头节点，使用链表是因为存在多点触摸情况下，需要将事件拆分后派发给不同的child。
```java

private static final class TouchTarget {
    // target 相关的 view
    public View child;
    // 当前 target 所绑定的 pointerId，利用2进制整数来判断
    public int pointerIdBits;
    // 使用链表维护多个 target
    public TouchTarget next;
    //...
}

```
若当次ev是ACTION_DOWN，则对当前ViewGroup来说，是一次全新的事件序列开始，那么需要保证清空旧的TouchTarget链表，以保证接下来mFirstTouchTarget可以正确保存派发目标。

在ViewGroup.dispatchTouchEvent()遇到非拦截事件，且事件类型为ACTION_DOWN或ACTION_POINTER_DOWN，则会触发一个遍历子控件以查找"触摸目标"的流程。TouchTarget是对消耗事件的View以链表方式保存，且记录各个View对应的触控点列表，以实现后续的事件派分处理。
同时可以推理出：

1. 非多点触控：mFirstTouchTarget链表退化成单个TouchTarget对象。
2. 多点触控，目标相同：同样为单个TouchTarget对象，只是pointerIdBits保存了多个pointerId信息。
3. 多点触控，目标不同：mFirstTouchTarget成为链表。

![image.png](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/ba4538a9941045de8576c06dc3ed6dde~tplv-k3u1fbpfcp-jj-mark:0:0:0:0:q75.image#?w=1004&h=451&s=212074&e=png&b=ccfafc)
假设childA、childB都能响应事件：

- 当触摸点1落于childA时，产生事件ACTION_DOWN，ViewGroup会为childA生成一个TouchTarget，后续滑动事件将派发给它。
- 当触摸点2落于childA时，产生ACTION_POINTER_DOWN事件，此时可以复用TouchTarget，并给它添加触摸点2的ID。
- 当触摸点3落于childB时，产生ACTION_POINTER_DOWN事件，ViewGroup会再生成一个TouchTarget，此时ViewGroup中有两个TouchTarget，后续产生滑动事件，将根据触摸点信息对事件进行拆分，之后再将拆分事件派发给对应的child。
#### ViewGroup 事件处理流程
接下来看 ViewGroup 的事件分发大概流程：
```java
//源码分析：ViewGroup.dispatchTouchEvent（）
public boolean dispatchTouchEvent(MotionEvent ev) { 
  	... 

    // Down 事件重置状态：FristTouchTarget 清除
    if (actionMasked == MotionEvent.ACTION_DOWN) {
        // 清空 FristTouchTarget, mFristTouchTarget 置为空。
        cancelAndClearTouchTargets(ev);
        resetTouchState();
    }

//判定当前事件是否需要拦截：down 或 其它事件且mFristTouchTarget不为空(说明已经找到了该事件的目标消费者)
    final boolean intercepted;
    if (actionMasked == MotionEvent.ACTION_DOWN || mFirstTouchTarget != null) {
        //可由View调用requestDisallowInterceptTouchEvent设置标记
        final boolean disallowIntercept = (mGroupFlags & FLAG_DISALLOW_INTERCEPT) != 0;
        if (!disallowIntercept) {
          //调用拦截方法
            intercepted = onInterceptTouchEvent(ev);
            ev.setAction(action); // restore action in case it was changed
        } else {
            intercepted = false;
        }
    } else {
       //没有触摸targets 且 非ACTION_DOWN，所以需要 自己处理
        intercepted = true;
    }   
    
  if (!intercepted) {
    // 逆序遍历子view，即先查询上面的，并调用 child.dispatchTouchEvent
    for (int i = count - 1; i >= 0; i--) {  
        final View child = children[i];  
        if (frame.contains(scrolledXInt, scrolledYInt)) {  
            // 事件传递到了子View
            if (child.dispatchTouchEvent(ev))  { 
                // 子View把 Down 事件消费掉了，并赋值 mFirstTouchTarget
                mFirstTouchTarget = child; 
                return true; 
             }  
          }  
      }  
   }  
  ...

    boolean handled;
    if (mFirstTouchTarget == null) {
        // ViewGroup自己处理事件
        handled = super.dispatchTouchEvent(ev);
    } else {
        TouchTarget target = mFirstTouchTarget;//遍历链表，使用链表是因为多指触摸
        while (target != null) { //target view处理事件
            final TouchTarget next = target.next;
            final boolean cancelChild = resetCancelNextUpFlag(target.child)
                || intercepted; 
            // 派发给子 View cancel 事件
            if (dispatchTransformedTouchEvent(ev, cancelChild,
                    target.child, target.pointerIdBits)) {
                handled = true;
            }
            ...
            target = next;
        }
    }    

  return handled;
}

public boolean onInterceptTouchEvent(MotionEvent ev) {  
    return false;// 默认不拦截
} 
```
### View的事件分发
核心方法：dispatchTouchEvent()、onTouchEvent()。

**onTouch和onTouchEvent**

- onTouch方法：onTouch方法是View的 OnTouchListener接口中定义的方法。当一个View绑定了OnTouchLister后，当有touch事件触发时，就会调用onTouch方法。
- onTouchEvent方法：onTouchEvent方法是override 的Activity、View的方法。当屏幕有touch事件时，此方法就会别调用。

当给一个view添加了onTouch事件的监听后，事件首先是onTouch方法执行，如果onTouch返回值为true，表示这个touch事件被onTouch方法处理完毕，不会把touch事件再传递给Activity/View，也就是说onTouchEvent方法不会被调用，如果onTouch的返回值是false，表示这个touch事件没有被view完全处理，onTouch返回以后，touch事件被传递给Activity/View，onTouchEvent方法被调用。

onTouch()、onTouchEvent()、onClick()是依序执行的。若执行到View的onTouchEvent，默认情况下：可点击，就一定返回true；不可点击就一定返回false。
```java
public boolean dispatchTouchEvent(MotionEvent event) { 
	//如果view不可用 或者mOnTouchListener不为空且ontouch返回了真
	// 那就调用不到 onTouchEvent 方法
    if ( (mViewFlags & ENABLED_MASK) == ENABLED && 
          mOnTouchListener != null && mOnTouchListener.onTouch(this, event)) {  
        return true;  
    } 

    return onTouchEvent(event);  
}

public boolean onTouchEvent(MotionEvent event) {  
  ...

  // 若该控件可点击，则进入switch判断中
  if (((viewFlags & CLICKABLE) == CLICKABLE || (viewFlags & LONG_CLICKABLE) == LONG_CLICKABLE)) {  
    // 根据当前事件类型进行判断处理
    switch (event.getAction()) { 
        case MotionEvent.ACTION_UP:
                performClick(); // up 处理 click 事件
                break;  
        case MotionEvent.ACTION_DOWN:
            postDelayed(mPendingCheckForTap, ViewConfiguration.getTapTimeout());  
            break;  
        case MotionEvent.ACTION_CANCEL:
            refreshDrawableState();  
            removeTapCallback();  
            break;
        case MotionEvent.ACTION_MOVE:
            final int x = (int) event.getX();  
            final int y = (int) event.getY();  

            int slop = mTouchSlop;  
            if ((x < 0 - slop) || (x >= getWidth() + slop) ||  
                    (y < 0 - slop) || (y >= getHeight() + slop)) {  
                removeTapCallback();  
                if ((mPrivateFlags & PRESSED) != 0) {  
                    removeLongPressCallback();  
                    mPrivateFlags &= ~PRESSED;  
                    refreshDrawableState();  
                }  
            }  
            break;  
    }  

    // 若该控件可点击，就一定返回true
    return true;  
  }  
  // 默认情况下若该控件不可点击，就一定返回false
  return false;  
}
```
## ACTION_CANCEL
如果某一个子 View **已处理**了 Down 事件，那么随之而来的 Move、Up事件也会交给它处理。但是交给它处理之前，父 View还是可以拦截事件的，如果拦截了事件，那么子 View 就会收到一个 Cancel 事件，并且不会收到后续的 Move、Up 事件。
解释参见上面 ViewGroup 的事件分发。

常见场景就是ListView中Item内部有一个Button，我们让ACTION_DOWN落在这个Button上，然后上下滑动，此时MOVE事件就会被ListView拦截，那么Button就会收到ACTION_CANCEL事件了。

## 多指触控
**MotionEvent**
Android中，事件信息通过MotionEvent来展现，可以用event.getAction()或者getActionMasked()来获取具体事件（两者区别接下来会说明），常用的事件有如下
MotionEvent.ACTION_DOWN：**第一个**触点被按下时触发
MotionEvent.ACTION_MOVE：有触点移动时触发，所有触点的移动都是这个事件
MotionEvent.ACTION_UP：**最后一个**触点放开时触发
MotionEvent.ACTION_CANCEL：见上段
MotionEvent.ACTION_POINTER_DOWN：多个触点时，按下**非第一个**点时触发
MotionEvent.ACTION_POINTER_UP：多个触点时，松开**非最后一个**点时触发

```java
@Override
public boolean onTouchEvent(MotionEvent event) {
    int action = event.getAction(); 
    int actionMasked = action & MotionEvent.ACTION_MASK;//得到事件类型 或者通过 event.getActionMasked()
    
    switch (actionMasked) {
        case MotionEvent.ACTION_DOWN:
            Log.e("lijf", "onTouchEvent: ACTION_DOWN");
            break;
        case MotionEvent.ACTION_MOVE:
            Log.e("lijf", "onTouchEvent: ACTION_MOVE");
            break;
        case MotionEvent.ACTION_POINTER_DOWN:
            Log.e("lijf", "onTouchEvent: ACTION_POINTER_DOWN");
            break;
        case MotionEvent.ACTION_POINTER_UP:
            Log.e("lijf", "onTouchEvent: ACTION_POINTER_UP");
            break;
    }
    return true;
}
```

**getActionMasked()、getAction()**
Android用一个32位的整数值来表示一个TouchEvent事件，低8位表示Touch事件的具体动作，例如按下、抬起、移动等动作。高8位表示Touch事件中多点触控的索引值。

- getAction：表示触摸动作的原始32位信息，包括Touch事件的具体动作和触控点信息。
- getActionMasked：表示触摸的动作，按下、抬起、移动等信息。getActionMasked = getAction & MotionEvent.ACTION_MASK（0xff），所以getActionMasked方法只保留了低8位的信息，也就是说只保留了触摸的动作信息。
- getActionMasked()、getAction()：只有一个触点时，两者返回结果是一致的；多个触点时，返回值就会有差别了。

getActionIndex：表示多点触控中触控点的信息。
getAction()中包含有：值的前8位与pointerIndex有关，后8位代表事件类型。pointerIndex稍后会介绍到。
如果要从 getAction() 中获取事件类型，那就需要进行一些运算处理了，MotionEvent也提供了掩码分别获取用来获取pointerIndex、事件类型，ACTION的掩码：
```java
public static final int ACTION_MASK                = 0xff; //用于获取低8位
public static final int ACTION_POINTER_INDEX_MASK  = 0xff00; //用于获取高8位，再右移8位即pointerIndex
```
当有多个手指在屏幕上操作时，如何区分是哪根手指的操作哪，又该如何获取这根手指的手势信息哪？

**pointerId、pointerIndex**
MotionEven**t对象**会存储当前所有触点(pointer)的信息，即使多个触点只有一个在活动。

- pointerId，pointer从down到up之间一直是不变的，注意当此pointer失效后，它的pointerId会被新来的pointer使用。
- pointerIndex，其值范围[0,getPointerCount())，但不像pointerId，pointerIndex对于同一个触点是会变化的。比如有两个pointer，它们的index分别为0、1，当index为0的pointer失效后，剩余pointer的index就会变成0。

两者可以互相转换：

| getPointerId(int pointerIndex) |
| --- |
| findPointerIndex(int pointerId) |


MotionEvent类中的很多方法需要一个pointerIndex值作为参数。比如getX(pointerIndex)、getY(pointerIndex)，返回的就是pointerIndex所代表的触摸点相关事件坐标值。pointer的id在整个事件流中是不会发生变化的，但是pointerIndex会发生变化。所以，要记录一个触点的事件流时，就需要保存其id，然后使用findPointerIndex(int)来获得其index值，然后再获得其他信息。

| getActionIndex() | 获取该事件是哪个指针(手指)产生的。 |
| --- | --- |
| getPointerCount() | 获取在屏幕上手指的个数。 |
| getPointerId(int pointerIndex) | 获取一个指针(手指)的唯一标识符ID，在手指按下和抬起之间ID始终不变。 |
| findPointerIndex(int pointerId) | 通过PointerId获取到当前状态下PointIndex，之后通过PointIndex获取其他内容。 |
| getX(int pointerIndex) | 获取某一个指针(手指)的X坐标 |
| getY(int pointerIndex) | 获取某一个指针(手指)的Y坐标 |

[
](https://blog.csdn.net/dzy_mails/article/details/53419566)
