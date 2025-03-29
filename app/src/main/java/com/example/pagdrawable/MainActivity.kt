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

    private val text = "This is a very long text that should be <span> wrapped to multiple lines. And some spans should <span> be displayed here."

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val spannableString = SpannableString(text)
        var end = 0
        do {
            val start = spannableString.indexOf("<span>", end)
            end = start + 6
            if (start >= 0) {
                val span = PAGSpan(this) {
                    textView.invalidate()
                }
                span.path = "assets://live_follow.pag"
                spannableString.setSpan(span, start, end, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        } while (start >= 0)

        textView.text = spannableString

        button.setOnClickListener {
            textView.invalidate()
        }
    }
}