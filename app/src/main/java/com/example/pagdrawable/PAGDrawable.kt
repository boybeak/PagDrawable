package com.example.pagdrawable

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.Log
import java.lang.ref.WeakReference
import kotlin.math.min

class PAGDrawable(activity: Activity, private val onUpdate: (() -> Unit)? = null) : Drawable(), PAGDrawableManager.OnPAGDrawCallback {

    companion object {
        private const val TAG = "PAGDrawable"
    }

    private var activityRef = WeakReference<Activity>(activity)
    private var bitmapRef: WeakReference<Bitmap>? = null
    var path: String? = null
        set(value) {
            val oldValue = field
            field = value
            if (oldValue != null) {
                stop()
            }
            if (value != null) {
                start()
            } else {
                stop()
            }
        }

    private val srcRect by lazy { Rect() }
    private val dstRect by lazy { Rect() }

    fun start() {
        val p = path ?: throw IllegalStateException("set path value before call start")
        val activity = activityRef.get() ?: return
        PAGDrawableManager.obtain(activity).register(p, this)
    }
    fun stop() {
        val p = path ?: return
        val activity = activityRef.get() ?: return
        PAGDrawableManager.obtain(activity).unregister(p, this)
    }

    override fun draw(canvas: Canvas) {
        val bitmap = bitmapRef?.get() ?: return

        // 获取原始尺寸和目标尺寸
        val srcWidth = bitmap.width.toFloat()
        val srcHeight = bitmap.height.toFloat()
        val dstWidth = bounds.width().toFloat()
        val dstHeight = bounds.height().toFloat()

        // 计算缩放比例（取宽度和高度比例中较小的）
        val scale = min(dstWidth / srcWidth, dstHeight / srcHeight)

        // 计算缩放后尺寸
        val scaledWidth = srcWidth * scale
        val scaledHeight = srcHeight * scale

        // 计算居中位置
        val left = (dstWidth - scaledWidth) / 2
        val top = (dstHeight - scaledHeight) / 2

        // 设置源矩形（完整原始图片）
        srcRect.set(0, 0, bitmap.width, bitmap.height)

        // 创建目标矩形（保持比例并居中）
        dstRect.set(
            left.toInt(),
            top.toInt(),
            (left + scaledWidth).toInt(),
            (top + scaledHeight).toInt()
        )
        canvas.drawBitmap(bitmap, srcRect, dstRect, null)

        onUpdate?.invoke()
    }

    override fun onDraw(bitmap: Bitmap) {
        bitmapRef = WeakReference(bitmap)
        invalidateSelf()
    }

    override fun setAlpha(alpha: Int) {}

    override fun setColorFilter(colorFilter: ColorFilter?) {}

    @Deprecated("Deprecated in Java")
    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }

}