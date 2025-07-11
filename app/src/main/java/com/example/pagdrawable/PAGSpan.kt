package com.example.pagdrawable

import android.app.Activity
import android.graphics.Paint
import android.text.style.ImageSpan

class PAGSpan(activity: Activity, onUpdate: (() -> Unit)? = null) : ImageSpan(PAGDrawable(activity, onUpdate)) {

    companion object {
        private const val TAG = "PAGSpan"
    }

    var path: String?
        get() = pagDrawable.path
        set(value) {
            pagDrawable.path = value
        }
    val pagDrawable: PAGDrawable
        get() = drawable as PAGDrawable

    override fun getSize(
        paint: Paint,
        text: CharSequence?,
        start: Int,
        end: Int,
        fm: Paint.FontMetricsInt?
    ): Int {
        val width = (paint.measureText(text, start, end) + 0.5f).toInt()
        pagDrawable.setBounds(0, 0, width, paint.textSize.toInt())
        return width
    }
}