package com.example.pagdrawable

import android.app.Activity
import android.app.Application
import android.graphics.Bitmap
import android.os.Bundle
import android.view.ViewGroup
import org.libpag.PAGImageView
import java.lang.ref.WeakReference
import java.util.LinkedList
import java.util.WeakHashMap

class PAGDrawableManager private constructor(activity: Activity) : PAGImageView.PAGImageViewListener {

    companion object {

        private val instanceMap = WeakHashMap<Activity, PAGDrawableManager>()

        private val activityLifecycleCallbacks = object : Application.ActivityLifecycleCallbacks {
            var isRegistered = false
            override fun onActivityCreated(
                activity: Activity,
                savedInstanceState: Bundle?
            ) {}

            override fun onActivityStarted(activity: Activity) {}

            override fun onActivityResumed(activity: Activity) {
                instanceMap[activity]?.resume()
            }

            override fun onActivityPaused(activity: Activity) {
                instanceMap[activity]?.pause()
            }

            override fun onActivityStopped(activity: Activity) {}

            override fun onActivitySaveInstanceState(
                activity: Activity,
                outState: Bundle
            ) {}

            override fun onActivityDestroyed(activity: Activity) {
                instanceMap.remove(activity)?.clear()
            }
        }

        fun obtain(activity: Activity): PAGDrawableManager {
            return instanceMap.getOrPut(activity) {
                if (!activityLifecycleCallbacks.isRegistered) {
                    activity.application.registerActivityLifecycleCallbacks(activityLifecycleCallbacks)
                }
                PAGDrawableManager(activity)
            }
        }

    }

    private var activityRef: WeakReference<Activity>? = WeakReference(activity)

    private val callbackMap = WeakHashMap<PAGImageView, LinkedList<OnPAGDrawCallback>>()

    fun pause() {
        callbackMap.keys.forEach {
            it.pause()
        }
    }
    fun resume() {
        callbackMap.keys.forEach {
            it.play()
        }
    }

    fun register(path: String, callback: OnPAGDrawCallback) {
        val activity = activityRef?.get() ?: return
        val pagImageView = getOrPutPAGImageView(activity, path)
        val callbacks = callbackMap.getOrPut(pagImageView) {
            LinkedList()
        }
        if (callbacks.contains(callback)) {
            return
        }
        callbacks.add(callback)
    }

    fun unregister(path: String, callback: OnPAGDrawCallback) {
        val activity = activityRef?.get() ?: return
        val decorView = activity.window.decorView as ViewGroup
        val pagImageView = decorView.findViewWithTag<PAGImageView>(path) ?: return
        pagImageView.pause()
        val callbacks = callbackMap[pagImageView] ?: return
        callbacks.remove(callback)
        if (callbacks.isEmpty()) {
            callbackMap.remove(pagImageView)
            pagImageView.removeListener(this)
            decorView.removeView(pagImageView)
        }
    }

    private fun getOrPutPAGImageView(activity: Activity, path: String): PAGImageView {
        val decorView = activity.window.decorView as ViewGroup
        var guessTarget = decorView.findViewWithTag<PAGImageView>(path)
        if (guessTarget == null) {
            guessTarget = PAGImageView(activity)
            guessTarget.translationX = Float.NEGATIVE_INFINITY
            guessTarget.translationY = Float.NEGATIVE_INFINITY

            guessTarget.tag = path
            decorView.addView(guessTarget, ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT))

            guessTarget.path = path
            guessTarget.setRepeatCount(-1)
            guessTarget.addListener(this)

            guessTarget.play()
        }

        return guessTarget
    }

    private fun clear() {
        activityRef?.clear()
        activityRef = null
    }

    override fun onAnimationStart(p0: PAGImageView?) {
    }

    override fun onAnimationEnd(p0: PAGImageView?) {
    }

    override fun onAnimationCancel(p0: PAGImageView?) {
    }

    override fun onAnimationRepeat(p0: PAGImageView?) {
    }

    override fun onAnimationUpdate(p0: PAGImageView?) {
        val pagImageView = p0 ?: return
        val callbacks = callbackMap[pagImageView] ?: return
        val bitmap = pagImageView.currentImage() ?: return
        ArrayList(callbacks).forEach {
            it.onDraw(bitmap)
        }
    }

    interface OnPAGDrawCallback {
        fun onDraw(bitmap: Bitmap)
    }

}