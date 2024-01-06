## Window、WindowManger

### window

Window对象代表一个窗口，这是一块矩形的图像绘制区域。WindowManagerService不关心各种View,只管理和调度Window对象，Activity的View树不管有多么复杂，最后输出的也只是一张由各个View合成的图像而已。

Window是一个抽象的概念，每一个Window都对应着一个View和一个ViewRootImpl，Window又通过ViewRootImpl与View建立联系，View才是Window的实体。

对Window的访问必须通过WindowManager，其实就是对view的操作(添加，更新，删除)，这些操作又都是经过ViewRootImpl实现的。 WindowManager->Window->ViewRootImpl->View

```
interface WindowManager extends ViewManager

public interface ViewManager {
    public void addView(View view, ViewGroup.LayoutParams params);
    public void updateViewLayout(View view, ViewGroup.LayoutParams params);
    public void removeView(View view);
}
```

### WMS/WindowManager/WindowManagerGlobal 关系

![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/fa09b5321f1343f2bc42b8534d828bc8~tplv-k3u1fbpfcp-jj-mark:0:0:0:0:q75.image#?w=933&h=926&s=376410&e=png&b=f6f6f6)

在每个Android应用中，都存在一个唯一的WindowManagerGlobal对象，这个对象中包含了能和WMS进行双向Binder通信的通道。如上图所示；

```
public final class WindowManagerGlobal {
    private static final String TAG = "WindowManager";
    private static WindowManagerGlobal sDefaultWindowManager;
    //窗口服务WindowManagerService的引用类
    private static IWindowManager sWindowManagerService;
    private static IWindowSession sWindowSession;
    private final Object mLock = new Object();

    private final ArrayList<View> mViews = new ArrayList<View>();
    private final ArrayList<ViewRootImpl> mRoots = new ArrayList<ViewRootImpl>();
    private final ArrayList<WindowManager.LayoutParams> mParams =
    new ArrayList<WindowManager.LayoutParams>();
    private final ArraySet<View> mDyingViews = new ArraySet<View>();

    private WindowManagerGlobal() {
    }
}
```

这三个集合，保存了应用中所有顶层View相关信息。

-   mViews：保存的是所有顶层View的对象，（实际上是DecorView对象）。
-   mRoots：保存的是和顶层View关联的ViewRootImpl对象。
-   mParams保存的是创建顶层View的layout参数；

### Window 类型

总的来说Window分为三大类型，每个大类型中又分很多小类型，通过LayoutParams.type设置。

-   应用窗口，对应于一个Activity。加载Activity由AmS完成，创建一个应用窗口只能在Activity内部完成（层级1~99）。
-   子窗口，必须依附于任何类型的父窗口，比如对话框等（层级1000~1999）。
-   系统窗口，不需要对应任何Activity，比如输入法，Toast，墙纸等,普通应用程序不能创建系统窗口，必须要有系统应用权限.（层级2000~2999）。

WindowManager为这个三类进行了细化，把每一种类型都有int常量标识，WmS进行窗口叠加的时候会按照该int常量的大小分配不同层，int值越大层位置越靠上面。层级在 2000（FIRST_SYSTEM_WINDOW）以下的是不需要申请弹窗权限的。

### Dialog的Window创建过程

-   创建Window——同样是通过PolicyManager的makeNewWindow方法完成，与Activity创建过程一致
-   初始化DecorView并将Dialog的视图添加到DecorView中——和Activity一致(setContentView)
-   将DecorView添加到Window中并显示——在Dialog的show方法中，通过WindowManager将DecorView添加到Window中(mWindowManager.addView(mDecor, 1))
-   Dialog关闭时会通过WindowManager来移除DecorView：mWindowManager.removeViewImmediate(mDecor)
-   Dialog可以采用Activity的Context，也可以将Dialog的Window通过type设置为系统Window(SYSTEM_ALERT,需要申请权限)。

### Dialog和PopupWindow的区别

Dialog在创建时会新建一个PhoneWindow，和activity的window是相互独立的，同时也会创建DecorView作为根View，然后在调用show方法时使用ViewRootImpl来管理DecroView，简直和Activity一毛一样。

但是PopupWindow就不一样了，不会创建新的window。PopupWindow的DecorView是PopupDecorView，PopupDecorView是继承FrameLayout,所以本质上PopupWindow就是一个View，使用WindowManager将PopupView添加到当前界面的DecorView里。

## Activity、view绑定

![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/305b3763cc404b42a613fda228f08a24~tplv-k3u1fbpfcp-jj-mark:0:0:0:0:q75.image#?w=1125&h=383&s=74769&e=png&b=253139)

activity 在创建、启动时，主要会经历如下流程：

1.  调用Activity.attach()：创建Window，window关联WindowManager。
1.  调用Activity.onCreate()：setContentView
1.  调用Activity的onResume()：处理View的呈现。performResumeActivity()处理Activity的onRestart onResume的生命周期，通过wm.addView 时才创建 ViewRootImpl，且关联 ViewRootImpl、DecorView。

### attach

在Activity.attach()方法执行时会创建PhoneWindow，并为Window关联WindowManager。

```
final void attach(Context context, ActivityThread aThread,
        CharSequence title, Activity parent, String id,) {
    attachBaseContext(context);
    //创建Window
    mWindow = new PhoneWindow(this);
    mWindow.setCallback(this);
    mWindow.setOnWindowDismissedCallback(this);
    mWindow.getLayoutInflater().setPrivateFactory(this);
    //设置UI线程
    mUiThread = Thread.currentThread();
    mMainThread = aThread;
    //关联WindowManager
    mWindow.setWindowManager(
            (WindowManager)context.getSystemService(Context.WINDOW_SERVICE),
            mToken, mComponent.flattenToString(),
            (info.flags & ActivityInfo.FLAG_HARDWARE_ACCELERATED) != 0);
    if (mParent != null) {
        mWindow.setContainer(mParent.getWindow());
    }
    mWindowManager = mWindow.getWindowManager();
}
```

### setContentView

Activity.setContentView()内又直接调用了 PhoneWindow.setContentView()，主要是DecorView的创建。DecorView的布局虽然不同，但它们都一个Id为R.id.content的FrameLayout。Activity.setContentView()就是在这个FrameLayout中添加子View。

执行到这里，Activity、Window、DecorView都已创建完成，接下来就是他们之间的逻辑交互了。

```
private DecorView mDecor;
//setContentView传过来的View会被add到mContentParent中。mContentParent的Id是R.id.content。
private ViewGroup mContentParent;

@Override
public void setContentView(View view, ViewGroup.LayoutParams params) {
    if (mContentParent == null) {
        installDecor();
    } else if (!hasFeature(FEATURE_CONTENT_TRANSITIONS)) {
        mContentParent.removeAllViews();
    }
}

private void installDecor() {
    if (mDecor == null) {
        //初始化DecorView
        mDecor = generateDecor();//new DecorView(context, featureId, this, getAttributes())
        mDecor.setIsRootNamespace(true);
        if (!mInvalidatePanelMenuPosted && mInvalidatePanelMenuFeatures != 0) {
            mDecor.postOnAnimation(mInvalidatePanelMenuRunnable);
        }
    }
    if (mContentParent == null) {
        mContentParent = generateLayout(mDecor);
        //省略代码。。。
    }
}
```

### handleResumeActivity

handleResumeActivity()处理的事情比较多：

1.  performResumeActivity()处理Activity的onRestart onResume的生命周期。
1.  将DecorView设置为InVisible。
1.  通过WindowManager.addView()将DecorView绘制完成。
1.  将DecorView设置为Visiable。
1.  通知AMS Activity启动完成。

```
final void handleResumeActivity(IBinder token, boolean clearHide, boolean isForward, boolean reallyResume) {
    //处理Activity的onRestart onResume生命周期。
    ActivityClientRecord r = performResumeActivity(token, clearHide);

    if (r != null) {
        if (r.window == null && !a.mFinished) {
            r.window = r.activity.getWindow();
            View decor = r.window.getDecorView();
            //设置DecorView不可见
            decor.setVisibility(View.INVISIBLE);
           
            ViewManager wm = a.getWindowManager();
            WindowManager.LayoutParams l = r.window.getAttributes();
          
            if (a.mVisibleFromClient) {
                a.mWindowAdded = true;
                //利用WindowManager添加DecorView。
                wm.addView(decor, l);
            }
        }
        if (!r.activity.mFinished && r.activity.mDecor != null) {
            if (r.activity.mVisibleFromClient) {
                //设置DecorView可见。
                r.activity.makeVisible();
            }
        }

        //IPC调用，通知AMS Activity启动完成。
        ActivityManagerNative.getDefault().activityResumed(token);
        
    } else {
        ActivityManagerNative.getDefault().finishActivity(token, Activity.RESULT_CANCELED, null, false);
    }
}
```

### WindowManger.addView()

-   WindowManagerImpl.addView会调用WindowManagerGlobal.addView()。
-   addView 时首先会创建ViewRootImpl且绑定DecorView，调用ViewRootImpl.setView()方法。

```
public final class WindowManagerImpl implements WindowManager {
    private IBinder mDefaultToken;

    @Override
    //这里的View是PhoneWindow内创建的DecorView。
    public void addView(@NonNull View view, @NonNull ViewGroup.LayoutParams params) {
        applyDefaultToken(params);
        mGlobal.addView(view, params, mDisplay, mParentWindow);
    }
}
```

addView 时才创建 ViewRootImpl 且关联 DecorView:

```
//这里的View是PhoneWindow内创建的DecorView。
public void addView(View view, ViewGroup.LayoutParams params, Display display, Window parentWindow) {
    final WindowManager.LayoutParams wparams = (WindowManager.LayoutParams) params;
    //省略代码....
    root = new ViewRootImpl(view.getContext(), display);

    mViews.add(view);
    mRoots.add(root);
    mParams.add(wparams);
    
    root.setView(view, wparams, panelParentView); //关联 decorView
}
```

## ViewRootImpl

![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/805be14466054b91a036d3c14a3cabde~tplv-k3u1fbpfcp-jj-mark:0:0:0:0:q75.image#?w=554&h=308&s=222700&e=png&b=fbf9f9)

先看下 ViewRootImpl 的功能

Android2.2以后用ViewRootImpl代替ViewRoot，ViewRootImpl 实现了 ViewParent，但它却不是 View。主要作用如下：

-   链接WindowManager和DecorView的纽带，实现了View与WindowManager之间的通信协议。
-   完成View的绘制过程，包括measure、layout、draw过程。
-   向DecorView分发收到的用户发起的event事件，如按键，触屏等事件。

**一个Activity有多少个ViewRootImpl**

ViewRootImpl是管理这window中一个View链的所有View，当Activity调用一次mWindowManager.addView时，就有创建一个新的ViewRootImpl。比如我在一个界面，添加了3个悬浮框，那就有4个ViewRootImpl，UI刷新互不干扰。

上段中 addView 时 调用 ViewRootImpl.setView，继续看。

1.  ViewRootImpl.setView() 将DecorView设置到了ViewRootImpl。
1.  检查 ui 绘制线程是在此处，并且开始scheduleTraversals，发送一个异步消息。
1.  因为mWindowSession.addToDisplay()就是通过IPC通知WMS去渲染。

scheduleTraversals会把本次请求封装成一个TraversalRunnable对象，这个对象最后会交给Handler去处理。最后ViewRootImpl.performTraversals()被调用。performTraversals()主要是处理View树的measure、layout、draw等流程。

```
public class ViewRootImpl{
    View mView; //PhoneWindow内创建的DecorView
  
    public void setView(View view, WindowManager.LayoutParams attrs, View panelParentView) {
        synchronized (this) {
            if (mView == null) {
                mView = view;
                //省略代码。。。
                requestLayout();
                //IPC通信，通知WMS渲染。
                mWindowSession.addToDisplay(mWindow, mSeq, mWindowAttributes);
            }
        }
    }
  
    @Override
    public void requestLayout() {
        if (!mHandlingLayoutInLayoutRequest) {
            //检查当前执行的线程是不是UI线程
            checkThread();
            //处理DecorView的measure、layout、draw。
            scheduleTraversals();
        }
    }
    
    void scheduleTraversals() {
        if (!mTraversalScheduled) {
            mChoreographer.postCallback(Choreographer.CALLBACK_TRAVERSAL, mTraversalRunnable, null);
            //省略代码。。。
        }
    }
}
```

从以上分析可以看出，应用UI线程的绘制最终是通过往Choreographer中放入一个CALLBACK_TRAVERSAL类型的绘制任务而触发，Choreographer会先向系统申请Vsync信号，待Vsync信号到来后，向应用主线程MessageQueue发送一个异步消息，触发在主线程中执行ViewRootImpl#doTraversal绘制任务动作。我们接着看看ViewRootImpl的doTraversal函数执行绘制流程的简化代码流程：

```
/*frameworks/base/core/java/android/view/ViewRootImpl.java*/
void doTraversal() {
     if (mTraversalScheduled) {
         mTraversalScheduled = false;
         // 调用removeSyncBarrier及时移除主线程MessageQueue中的Barrier同步栏删，以避免主线程发生“假死”
         mHandler.getLooper().getQueue().removeSyncBarrier(mTraversalBarrier);
         ...
         // 执行具体的绘制任务
         performTraversals();
         ...
    }
}

private void performTraversals() {
     ...
     windowSizeMayChange |= measureHierarchy(...);
     ...
     if (mFirst...) {
         relayoutResult = relayoutWindow(params, viewVisibility, insetsPending);
     }
     ...
     performLayout(lp, mWidth, mHeight);
     ...
     performDraw();
     ...
}
```

## View 绘制流程

```
private void performTraversals() {
    ...
    //mWidth 屏幕尺寸， lp.width: match_parent
    int childWidthMeasureSpec = getRootMeasureSpec(mWidth, lp.width);
    int childHeightMeasureSpec = getRootMeasureSpec(mHeight, lp.height);
    ...
    performMeasure(childWidthMeasureSpec, childHeightMeasureSpec);
    ...
    performLayout(lp, desiredWindowWidth, desiredWindowHeight);
    ...
    performDraw();
}
```

### 父子执行顺序

1、测量过程是先测量子View，再测量父View（当前View），因为父View的宽高需要用到子View的测量结果。

2、View布局的核心方法是View#layout方法，先对父View（当前View）进行布局，然后调用onLayout方法对子View进行布局定位。

3、View绘制的核心方法是View#draw方法，先对父View（当前View）进行绘制，然后调用dispatchDraw方法对子View进行绘制。

### measure

#### 基本概念

**MeasureSpec**

MeasureSpec是一个32位的整形值，高2位表示测量模式SpecMode，低30位表示某种测量模式下的规格大小SpecSize。

-   UNSPECIFIED: 不指定测量模式, 父视图没有限制子视图的大小，子视图可以是想要的任何尺寸，如ScrollView
-   EXACTLY: 精确测量模式，视图宽高(LayoutParam)指定为match_parent或具体数值时生效，表示父视图已经决定了子视图的精确大小，这种模式下View的测量值就是SpecSize的值。
-   AT_MOST: 最大值测量模式，当视图宽高(LayoutParam)指定为wrap_content时生效，此时子视图的尺寸可以是不超过父视图允许的最大尺寸的任何尺寸。

![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/0bbe138ed9594bf09916945ac98cd7f8~tplv-k3u1fbpfcp-jj-mark:0:0:0:0:q75.image#?w=1515&h=571&s=194050&e=png&b=f6f6f6)

**measureSpec从哪来的**

每个View 的spec是从父view测量时传递下来的，父view根据子View的layoutParam、父view尺寸来确定最终的mode、size再传递给子View( 见 下文)。而顶层DecorView的跟activity 跟屏幕尺寸有关，dialog可能是wrap content.

**measure与onMeasure关系**

1.  measure 入口即 measure()方法是View中定义的final类型，子类不能重写此方法，所以无论是 View 还是 ViewGroup 最终逻辑都是在 onMeasure 方法。
1.  setMeasuredDimension()：存储测量后的View宽 / 高, 保存在mMeasuredWidth/mMeasuredHeight，父View获取子View的测量尺寸时获取的就是这些值。

```
public final void measure(int widthMeasureSpec, int heightMeasureSpec) {
    int cacheIndex = (mPrivateFlags & PFLAG_FORCE_LAYOUT) == PFLAG_FORCE_LAYOUT ? -1 :
    mMeasureCache.indexOfKey(key);

    if (cacheIndex < 0 || sIgnoreMeasureCache) {
        onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
```

#### ViewGroup onMeasure

不同的ViewGroup(如LinearLayout、RelativeLayout、自定义ViewGroup子类等）具备不同的布局特性，这导致它们测量方法各有不同，所以onMeasure()的实现也会有所不同。复写onMeasure()的步骤主要分为三步：

1.  遍历所有子View及测量：measureChildren()
1.  合并所有子View的尺寸大小,最终得到ViewGroup父视图的测量值：需自定义实现
1.  存储测量后View宽/高的值：setMeasuredDimension()


根据不同的父容器的模式及子View的layoutParams来决定子View的规格尺寸模式，父容器的MeasureSpec和子View的LayoutParams的共同影响了子View的MeasureSpec，见**getChildMeasureSpec**。接着会调用View#measure，child.measure方法，则继续调用onMeasure方法，直到它的所有子view的大小都测量完成为止，这在上面说过了，这里不再赘述

看下FrameLayout的测量

```
@Override  
protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {  
    //获取布局内子View数量  
    int count = getChildCount();  

    final boolean measureMatchParentChildren =  
            MeasureSpec.getMode(widthMeasureSpec) != MeasureSpec.EXACTLY ||  
            MeasureSpec.getMode(heightMeasureSpec) != MeasureSpec.EXACTLY;  
    mMatchParentChildren.clear();  

    int maxHeight = 0;  
    int maxWidth = 0;  
    int childState = 0;  
    //遍历所有子View中可见的View,也就不为GONE的View;  
    for (int i = 0; i < count; i++) {  
        final View child = getChildAt(i);  
        if (mMeasureAllChildren || child.getVisibility() != GONE) {  
           // 测量子view  
            measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);  
            // 获取子view的布局参数  
            final LayoutParams lp = (LayoutParams) child.getLayoutParams();  
            // 记录子view的最大宽度和高度
            maxWidth = Math.max(maxWidth,  
                child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin);  
            maxHeight = Math.max(maxHeight,  
                child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin);  
            childState = combineMeasuredStates(childState, child.getMeasuredState());  
            // 记录所有跟父布局有着相同宽或高的子view  
            if (measureMatchParentChildren) {  
                if (lp.width == LayoutParams.MATCH_PARENT || lp.height == LayoutParams.MATCH_PARENT) {  
                    mMatchParentChildren.add(child);  
                }  
            }  
        }  
    }  

    // 子view的最大宽高计算出来后，还要加上父View自身的padding  
    maxWidth += getPaddingLeftWithForeground() + getPaddingRightWithForeground();  
    maxHeight += getPaddingTopWithForeground() + getPaddingBottomWithForeground();  
...  
     //保存测量结果  
    setMeasuredDimension(resolveSizeAndState(maxWidth, widthMeasureSpec, childState),  
            resolveSizeAndState(maxHeight, heightMeasureSpec,  
                    childState << MEASURED_HEIGHT_STATE_SHIFT));  
    //从子View中获取match_parent的个数  
    count = mMatchParentChildren.size();  
    if (count > 1) {  
        for (int i = 0; i < count; i++) {  
            final View child = mMatchParentChildren.get(i);  
            final MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();  

            final int childWidthMeasureSpec;  
            // 如果子view的宽是MATCH_PARENT,那么宽度 = 父view的宽 - 父Padding - 子Margin  
            if (lp.width == LayoutParams.MATCH_PARENT) {  
                final int width = Math.max(0, getMeasuredWidth()  
                        - getPaddingLeftWithForeground() - getPaddingRightWithForeground()  
                        - lp.leftMargin - lp.rightMargin);  
                childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(  
                        width, MeasureSpec.EXACTLY);  
            } else {  
                childWidthMeasureSpec = getChildMeasureSpec(widthMeasureSpec,  
                        getPaddingLeftWithForeground() + getPaddingRightWithForeground() +  
                        lp.leftMargin + lp.rightMargin,  
                        lp.width);  
            }  

...  
            //对于这部分的子View,重新执行measure  
            child.measure(childWidthMeasureSpec, childHeightMeasureSpec);  
        }  
    }  
}  
```

onMeasure中有个measureChildWithMargin：它主要作用是测量子View，根据父/子View的状态生成子View的 MeasureSpec，那么我们直接看这个方法，ViewGroup#measureChildWithMargins。

```
protected void measureChildWithMargins(View child,
        int parentWidthMeasureSpec, int widthUsed,
        int parentHeightMeasureSpec, int heightUsed) {
    // 这里对应的是 child 自己想要的尺寸
    final MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();

    final int childWidthMeasureSpec = getChildMeasureSpec(parentWidthMeasureSpec,
            mPaddingLeft + mPaddingRight + lp.leftMargin + lp.rightMargin
                    + widthUsed, lp.width);
    final int childHeightMeasureSpec = getChildMeasureSpec(parentHeightMeasureSpec,
            mPaddingTop + mPaddingBottom + lp.topMargin + lp.bottomMargin
                    + heightUsed, lp.height);

    child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
}

public static int getChildMeasureSpec(int spec, int padding, int childDimension) {
    int specMode = MeasureSpec.getMode(spec);//本view
    int specSize = MeasureSpec.getSize(spec);//与子View无关
    //size 表示子View可用空间：父容器尺寸减去padding
    int size = Math.max(0, specSize - padding);
    
    int resultSize = 0;
    int resultMode = 0;
    
    switch (specMode) {
        // 父容器给子View确切的size,(具体数值或MATCH_PARENT)的情况下
        case MeasureSpec.EXACTLY:
          if (childDimension >= 0) {
              resultSize = childDimension;
              resultMode = MeasureSpec.EXACTLY;
          } else if (childDimension == LayoutParams.MATCH_PARENT) {
              // 子view想成为父容器的大小
              resultSize = size;
              resultMode = MeasureSpec.EXACTLY;
          } else if (childDimension == LayoutParams.WRAP_CONTENT) {
              //子View确定自己的的size,但是不能超过父容器
              resultSize = size;//注意这里给的是child最大可用，至于child怎么用看child本身实现
              resultMode = MeasureSpec.AT_MOST;
          }
          break;
        
        //  父容器对子View施加了最大的限制(即父容器大小赋值为WRAP_CONTENT)的情况下
        case MeasureSpec.AT_MOST:
         ...
          break;
        // 父容器不限制子View大小，子View需要多大就多
        case MeasureSpec.UNSPECIFIED:
        ...
         break;
    }
    //noinspection ResourceType
    return MeasureSpec.makeMeasureSpec(resultSize, resultMode);
}
```

#### View onMeasure

![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/a033ef08220545418000a442530d3014~tplv-k3u1fbpfcp-jj-mark:0:0:0:0:q75.image#?w=1008&h=453&s=56430&e=png&b=fdfdfd)

```
protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {  
    setMeasuredDimension(getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec),  
        getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec)); 
}

//作用：存储测量后的View宽 / 高
protected final void setMeasuredDimension(int measuredWidth, int measuredHeight) {  
    mMeasuredWidth = measuredWidth;  
    mMeasuredHeight = measuredHeight;  

    mPrivateFlags |= PFLAG_MEASURED_DIMENSION_SET;  
} 

public static int getDefaultSize(int size, int measureSpec) {  
    // 设置默认大小
    int result = size; 

    // 获取宽/高测量规格的模式 & 测量大小
    int specMode = MeasureSpec.getMode(measureSpec);  
    int specSize = MeasureSpec.getSize(measureSpec);  

    switch (specMode) {  
            // 模式为UNSPECIFIED时，使用提供的默认大小 = 参数Size
        case MeasureSpec.UNSPECIFIED:  
            result = size;  
            break;  

            // 模式为AT_MOST,EXACTLY时，使用View测量后的宽/高值 = measureSpec中的Size
            //view的测量在父类中实现，见下文
        case MeasureSpec.AT_MOST:  
        case MeasureSpec.EXACTLY:  
            result = specSize;  
            break;  
    }  

    // 返回View的宽/高值
    return result;  
}    
```

可以看到测试规格的模式(mode)是UNSPECIFIED时，使用的是提供的默认大小(即getDefaultSize()的第一个参数size)即：getSuggestedMinimumWidth() / getSuggestedMinimumHeight()。

```
protected int getSuggestedMinimumWidth() {
    return (mBackground == null) ? mMinWidth : max(mMinWidth,mBackground.getMinimumWidth());
}
// 逻辑说明
// 1. 若View无设置背景，那么View的宽度 = mMinWidth
// 即android:minWidth属性所指定的值，若无指定则为0.
// 2. 若View设置了背景，View的宽度为mMinWidth和mBackground.getMinimumWidth()中的最大值
// mBackground.getMinimumWidth()的大小 = 背景图Drawable的原始宽度
```

#### getWidth/getMeasuredWidth 区别

getMeasuredWidth: 获得 View测量的宽 / 高 是onMeasure 以后拿到的值,获得的值是setMeasuredDimension方法设置的值

getWidth: 获得View最终的宽 / 高 是onLayout 以后拿到的值,获得是layout方法中传递的四个参数中的mRight-mLeft

```
// 获得View测量的宽 / 高
public final int getMeasuredWidth() {  
    return mMeasuredWidth & MEASURED_SIZE_MASK;  	
    // measure过程中返回的mMeasuredWidth
}  

// 获得View最终的宽 / 高
public final int getWidth() {  
    return mRight - mLeft;  
    // View最终的宽 = 子View的右边界 - 子view的左边界。
}  
```

一般情况下，二者获取的宽 / 高是相等的。那么，“非一般”情况是什么？通过重写View的 layout或者 viewgroup的onLayout 强行设置

```
@Override
public void layout( int l , int t, int r , int b){
   // 改变传入的顶点位置参数
   super.layout(l，t，r+100，b+100)；
}
```

### layout

1.  layout()中对自身View进行了位置计算：setFrame() / setOpticalFrame()，都是设置 left/right/top/bottom 值
1.  view的layout可重写，viewgroup final修饰不可以重写，两者都有 layout/onLayout

<!---->

2.  onLayout 调用子View的 layout，单一View是没有子View的，故onLayout()是一个空实现
2.  ViewGroup 先在layout()中设置自己的位置(即setFrame)，再在onLayout中调用子view的layout。

#### View的layout过程

![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/0f7a6c78d2324531aa1f87c88744e6b0~tplv-k3u1fbpfcp-jj-mark:0:0:0:0:q75.image#?w=310&h=360&s=18415&e=png&b=ffffff)

```
//确定View本身的位置，即设置View本身的四个顶点位置
public void layout(int l, int t, int r, int b) {  

    // 当前视图的四个顶点
    int oldL = mLeft;  
    int oldT = mTop;  
    int oldB = mBottom;  
    int oldR = mRight;  

    // 1. 确定View的位置：setFrame() / setOpticalFrame(),两者逻辑相似
    boolean changed = isLayoutModeOptical(mParent) ? setOpticalFrame(l, t, r, b) : setFrame(l, t, r, b);
    // 2. 若视图的大小 & 位置发生变化会重新确定该View所有的子View在父容器的位置：onLayout()
    if (changed || (mPrivateFlags & PFLAG_LAYOUT_REQUIRED) == PFLAG_LAYOUT_REQUIRED) {  
        onLayout(changed, l, t, r, b);
    }  
}

protected boolean setFrame(int left, int top, int right, int bottom) {
    // 通过以下赋值语句记录下了视图的位置信息，从而确定了视图的位置
    if (mLeft != left || mRight != right || mTop != top || mBottom != bottom) {
        changed = true;
        mLeft = left;
        mTop = top;
        mRight = right;
        mBottom = bottom;

        mRenderNode.setLeftTopRightBottom(mLeft, mTop, mRight, mBottom);
    }
    return changed;
}

protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
}  
```

#### ViewGroup layout过程

![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/677df9a6d76146bfbf9e71da8a4b125e~tplv-k3u1fbpfcp-jj-mark:0:0:0:0:q75.image#?w=945&h=793&s=269636&e=png&b=fefefe)

在自定义ViewGroup时必须复写onLayout（）！

遍历子View 、计算当前子View的四个位置值 ，下面是 LinearLayout 的 布局实现：

```
@Override
protected void onLayout(boolean changed, int l, int t, int r, int b) {
    // 根据自身方向属性，而选择不同的处理方式
    if (mOrientation == VERTICAL) {
        layoutVertical(l, t, r, b);
    } else {
        layoutHorizontal(l, t, r, b);
    }
}
// 此处仅分析垂直方向（Vertical）
void layoutVertical(int left, int top, int right, int bottom) {
    // 子View的数量
    final int count = getVirtualChildCount();

    // 1. 遍历子View
    for (int i = 0; i < count; i++) {
        final View child = getVirtualChildAt(i);
        if (child == null) {
            childTop += measureNullChild(i);
        } else if (child.getVisibility() != GONE) {
            // 2. 计算子View的测量宽 / 高值
            final int childWidth = child.getMeasuredWidth();
            final int childHeight = child.getMeasuredHeight();
            // 3. 确定子View的位置
            setChildFrame(child, childLeft, childTop + getLocationOffset(child),childWidth, childHeight);

            // childTop逐渐增大，即后面的子元素会被放置在靠下的位置
            // 这符合垂直方向的LinearLayout的特性
            childTop += childHeight + lp.bottomMargin + getNextLocationOffset(child);
            i += getChildrenSkipCount(child, i);
        }
    }
}

// setChildFrame()仅仅只是调用了子View的layout()而已
private void setChildFrame( View child, int left, int top, int width, int height){
    child.layout(left, top, left ++ width, top + height);
}
```

### draw

draw 也可以重写，但一般建议重写 onDraw.

#### View的draw过程

![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/415bb1af63fc48a0b73fd8afaebf3c3b~tplv-k3u1fbpfcp-jj-mark:0:0:0:0:q75.image#?w=513&h=761&s=116541&e=png&b=fdfdfd)

```
public void draw(Canvas canvas) {
    ...
    if (!dirtyOpaque) {//这里 view/viewgroup 值会不一样
        drawBackground(canvas); 
    }

    final int viewFlags = mViewFlags;
    if (!verticalEdges && !horizontalEdges) {
        if (!dirtyOpaque) //这里 view/viewgroup 值会不一样
            onDraw(canvas);
        
        //绘制子View View中：默认为空实现
        dispatchDraw(canvas);

        //绘制装饰，如滑动条、前景色等等
        onDrawScrollBars(canvas);
        return;
    }
    ...    
}
```

#### ViewGroup的draw过程

draw/onDraw 方法调用的是View里的，覆写了 dispatchDraw

![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/200d01f36bc34517ba39f3ade5289813~tplv-k3u1fbpfcp-jj-mark:0:0:0:0:q75.image#?w=990&h=833&s=198165&e=png&b=fdfdfd)

```
protected void dispatchDraw(Canvas canvas) {
    ......

    // 1. 遍历子View
    final int childrenCount = mChildrenCount;
    ......
    for (int i = 0; i < childrenCount; i++) {
       ......
       if ((transientChild.mViewFlags & VISIBILITY_MASK) == VISIBLE || transientChild.getAnimation() != null) {
            // 2. 绘制子View视图 ->>分析1
            more |= drawChild(canvas, transientChild, drawingTime);
        }
       ....
    }
}

protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
    // 最终还是调用了子 View 的 draw （）进行子View的绘制
    return child.draw(canvas, this, drawingTime);
}
```

**onDraw() 和dispatchDraw()的区别**

-   绘制View本身的内容，通过调用View.onDraw(canvas)函数实现
-   绘制自己的孩子通过dispatchDraw（canvas）实现

draw过程会调用onDraw(Canvas canvas)方法，然后就是dispatchDraw(Canvas canvas)方法, dispatchDraw()主要是分发给子组件进行绘制

**默认状态下，ViewGroup为什么不走onDraw()**

在默认状态下，ViewGroup的背景为透明的，而在View中的draw方法中表示只有不透明才执行onDraw，这也就导致后续ViewGroup的onDraw失效。

解决办法

-   可将onDraw中的处理移至到dispatchDraw()中。
-   setWillNotDraw(false); 默认情况下：View 不启用该标记位（设置为false）；ViewGroup 默认启用（设置为true）
-   直接在ViewGroup的xml下添加一个背景。

**为什么View没有限制**

在View的初始化中对于Flag的设置就与ViewGroup相反，也就导致在默认情况下View是不受影响的。

## 常用Layout measure性能对比

FrameLayout会measure一次或两次，见上面viewGroup measure源码。两次测量的情况：

1.  FrameLayout 有宽或高不是MeasureSpec.EXACTLY。
1.  child中有宽或者高是LayoutParams.MATCH_PARENT的
1.  2中child数量超过>1个。[  
    ](https://blog.csdn.net/thh159/article/details/104738135)

RelativeLayout的child 大多数情况下会做两次measure。这是由于RelativeLayout是基于相对位置的，需要在横向和纵向分别进行一次measure过程。

LinearLayout 无weight情况下会进行一次测量，有weight情况下会做两次测量；在有weight情况下，且有类似 FrameLayout 两次测量那种场景下是会有三次测量的。

ConstraintLayout针对测量次数做了大量优化，尽可能的降低了绘制次数。child大部分情况下是一次，即使它支持的特性很多，更重要的是它在降低层级上有绝对的优势。

## invalide/requestLayout 区别

-   view的invalidate不会导致ViewRootImpl的invalidate被调用，而是递归调用父view的invalidateChildInParent，直到ViewRootImpl的peformTraversals，会导致当前view被重绘。由于mLayoutRequested为false，不会导致onMeasure和onLayout被调用，而OnDraw会被调用
-   requestLayout会直接递归调用父窗口的requestLayout，直到ViewRootImpl,然后触发peformTraversals，由于mLayoutRequested为true，会导致onMeasure和onLayout被调用。不一定会触发OnDraw，requestLayout触发onDraw可能是因为在在layout过程中发现l,t,r,b和以前不一样，那就会触发一次invalidate
-   一般来说，只要刷新的时候就调用invalidate，需要重新measure就调用requestLayout。[  
    ](https://blog.csdn.net/goodlixueyong/article/details/51396803)![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/e18bf69d70434aa2af5b6da026bec36d~tplv-k3u1fbpfcp-jj-mark:0:0:0:0:q75.image#?w=510&h=576&s=21250&e=png&b=fdfdfd)

##