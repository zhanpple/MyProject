package com.zmp.javaproject

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

/**
 * Created at 14:24 2020/9/7
 *
 * @author zmp
 *
 *
 * des:
 */
class MyView : View {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(
        context,
        attrs
    )

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr)

    private val MODE_MASK = 0x3 shl 30
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
//        super.onMeasure(widthMeasureSpec,heightMeasureSpec)

        val mode = MeasureSpec.getMode(heightMeasureSpec)
        val size = MeasureSpec.getSize(heightMeasureSpec)
        val height = layoutParams.height
//        when (height) {
//            ViewGroup.LayoutParams.WRAP_CONTENT -> {
//                Log.e(TAG, "LayoutParams: WRAP_CONTENT")
//            }
//            ViewGroup.LayoutParams.MATCH_PARENT -> {
//                Log.e(TAG, "LayoutParams: MATCH_PARENT")
//            }
//            else -> {
//                Log.e(TAG, "LayoutParams: $height")
//            }
//        }
        when (mode) {
            MeasureSpec.AT_MOST -> {//wrap_content
                Log.e(TAG, "AT_MOST: $size")
            }
            MeasureSpec.EXACTLY -> {//match_parent或者具体大小 如：100px
                Log.e(TAG, "EXACTLY: $size")
            }
            MeasureSpec.UNSPECIFIED -> {//当父布局为NestedScrollView时由父布局传递 作用是不限制子View的大小
                Log.e(TAG, "UNSPECIFIED: $size")
            }
        }
        setMeasuredDimension(200, size)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return super.onTouchEvent(event)
    }

    companion object {
        private const val TAG = "MyView"
    }
}