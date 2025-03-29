# PagDrawable

A showcase project that display PAG animations in a Drawable and in a SpannableString.

## Showcase
<img src="./art/span.gif" width="240">

## Code
```kotlin
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
```

If the animation is not displayed, click the **refresh** button.
