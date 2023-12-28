package com.parrosz.storyu.customview

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.parrosz.storyu.R

open class EditTextCustom : AppCompatEditText {
    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    /*override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
    }*/

    private fun init() {
        background = ContextCompat.getDrawable(context, R.drawable.edittext_custom)
    }
}