package com.example.pagdrawable

import android.os.Bundle
import android.text.SpannableString
import android.text.style.ImageSpan
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    private val imageView by lazy { findViewById<ImageView>(R.id.imageView) }
    private val textView by lazy { findViewById<TextView>(R.id.textView) }
    private val button by lazy { findViewById<Button>(R.id.button) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

//        val pagDrawable = PAGDrawable(this)
//        pagDrawable.path = "assets://live_follow.pag"
//        imageView.setImageDrawable(pagDrawable)

        val span = PAGSpan(this) {
            textView.invalidate()
        }
        span.path = "assets://live_follow.pag"
        val spannableString = SpannableString("Hello, World!")
        spannableString.setSpan(span, 0, 2, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
        textView.text = spannableString

        button.setOnClickListener {
            textView.invalidate()
        }
    }
}