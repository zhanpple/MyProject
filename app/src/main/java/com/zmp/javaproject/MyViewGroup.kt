package com.zmp.javaproject

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.ViewGroup
import androidx.core.view.children
import androidx.core.view.marginBottom
import androidx.core.view.marginTop

/**
 * Created at 14:24 2020/9/7
 *
 * @author zmp
 *
 *
 * des:
 */
class MyViewGroup : ViewGroup {
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


    val arrayList = arrayListOf<Rect>()


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val mode = MeasureSpec.getMode(heightMeasureSpec)
        val size = MeasureSpec.getSize(heightMeasureSpec)
        Log.e(TAG, "EXACTLY: $mode")
        Log.e(TAG, "size: $size")

        var max = 0
        for (child in children) {
            measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0)
            max += child.measuredHeight + child.marginTop + child.marginBottom
        }
        setMeasuredDimension(500, max)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        arrayList.clear()
        children.forEach {
            arrayList.add(Rect(it.left, it.top, it.right, it.bottom))
        }
    }


    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var margin = 0
        children.forEach {
            it.layout(
                it.left,
                it.marginTop + margin,
                it.right + it.measuredWidth,
                margin + it.measuredHeight
            )
            margin += it.measuredHeight + it.marginBottom
        }

    }

    /**
     * 重写布局解析参数  解析margin
     */
    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams? {
        return MarginLayoutParams(context, attrs)
    }

    companion object {
        private const val TAG = "MyGroupView"
    }
}