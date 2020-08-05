package com.ware.widget.ui

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.ware.face.DisplayUtil
import kotlin.math.min


/**
 * Created by jianfeng.li on 20-8-3.
 */
private const val TAG = "PathView"

class PathView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : View(context, attrs, defStyle) {
    private val path = Path()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var minX = Float.MAX_VALUE
    private val rectF = RectF()
    private val pathEffect = CornerPathEffect(DisplayUtil.dip2px(2f).toFloat())

    init {
        val list = arrayListOf<Point>()

        list.add(Point(650, 381))
        list.add(Point(554, 315))
        list.add(Point(524, 303))
        list.add(Point(535, 292))
        list.add(Point(494, 263))
        list.add(Point(478, 265))
        list.add(Point(443, 324))
        list.add(Point(415, 480))
        list.add(Point(417, 493))
        list.add(Point(448, 495))
        list.add(Point(483, 450))
        list.add(Point(483, 547))
        list.add(Point(463, 617))
        list.add(Point(501, 644))
        list.add(Point(532, 660))
        list.add(Point(523, 674))
        list.add(Point(520, 649))
        list.add(Point(373, 458))
        list.add(Point(331, 437))
        list.add(Point(317, 467))
        list.add(Point(322, 503))
        list.add(Point(355, 502))
        list.add(Point(339, 540))
        list.add(Point(305, 593))
        list.add(Point(300, 661))
        list.add(Point(256, 764))
        list.add(Point(353, 955))
        list.add(Point(396, 988))
        list.add(Point(438, 989))
        list.add(Point(491, 917))
        list.add(Point(511, 907))
        list.add(Point(536, 924))
        list.add(Point(515, 954))
        list.add(Point(488, 959))
        list.add(Point(480, 955))
        list.add(Point(422, 896))
        list.add(Point(368, 777))
        list.add(Point(360, 706))
        list.add(Point(404, 532))
        list.add(Point(454, 526))
        list.add(Point(485, 553))
        list.add(Point(498, 602))
        list.add(Point(675, 837))
        list.add(Point(723, 836))
        list.add(Point(701, 860))
        list.add(Point(683, 943))
        list.add(Point(693, 957))
        list.add(Point(704, 928))
        list.add(Point(698, 916))
        list.add(Point(694, 769))
        list.add(Point(752, 688))
        list.add(Point(769, 672))
        list.add(Point(765, 664))
        list.add(Point(771, 644))
        list.add(Point(743, 692))
        list.add(Point(750, 711))
        list.add(Point(739, 676))
        list.add(Point(743, 661))
        list.add(Point(758, 662))
        list.add(Point(760, 653))
        list.add(Point(746, 655))
        list.add(Point(668, 602))
        list.add(Point(743, 560))
        list.add(Point(720, 505))
        list.add(Point(738, 498))
        list.add(Point(755, 551))
        list.add(Point(672, 670))
        list.add(Point(655, 706))
        list.add(Point(655, 690))
        list.add(Point(676, 638))
        list.add(Point(697, 626))
        list.add(Point(694, 627))
        list.add(Point(724, 659))
        list.add(Point(719, 665))
        list.add(Point(739, 653))
        list.add(Point(734, 639))
        list.add(Point(592, 541))
        list.add(Point(575, 544))
        list.add(Point(567, 552))
        list.add(Point(565, 561))
        list.add(Point(567, 526))
        list.add(Point(597, 534))
        list.add(Point(600, 532))

        for (i in list.indices) {
            val point = list[i]
            if (i == 0) path.moveTo(point.x.toFloat(), point.y.toFloat())
            else path.lineTo(point.x.toFloat(), point.y.toFloat())

            minX = min(minX, point.x.toFloat())
        }

        paint.color = Color.GREEN
        paint.strokeWidth = DisplayUtil.dip2px(2f).toFloat()
        paint.style = Paint.Style.STROKE
        paint.pathEffect = pathEffect
        paint.strokeCap = Paint.Cap.ROUND

        val scaleMatrix = Matrix()
        path.computeBounds(rectF, true)
        scaleMatrix.setScale(0.5f, 0.5f, rectF.centerX(), rectF.centerY())
//        scaleMatrix.postTranslate(DisplayUtil.screenWidth / 2f, )
        path.transform(scaleMatrix)
//        minX /= 0.5f
        path.computeBounds(rectF, true)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthSpec = MeasureSpec.makeMeasureSpec(rectF.width().toInt(), MeasureSpec.EXACTLY)
        val heightSpec = MeasureSpec.makeMeasureSpec(rectF.height().toInt(), MeasureSpec.EXACTLY)
        super.onMeasure(widthSpec, heightSpec)
        Log.d(TAG, "onMeasure: ${rectF.toShortString()}")
//        setMeasuredDimension(rectF.width().toInt(), rectF.height().toInt())
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        Log.d(TAG, "onDraw: $width, $height")
        canvas.save()
        canvas.translate(-rectF.left, -rectF.top)
        canvas.drawPath(path, paint)
//        canvas.drawLine(minX, 0f, minX, height.toFloat(), paint)
        canvas.drawRect(rectF, paint)
        canvas.restore()
    }
}