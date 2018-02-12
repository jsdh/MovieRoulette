package com.jdhdev.mm8.ui

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView


class PosterView(context: Context, attrs: AttributeSet) : ImageView(context, attrs) {

    override fun onMeasure(widthSpec: Int, heightSpec: Int) {
        val height = View.MeasureSpec.makeMeasureSpec(
                (View.MeasureSpec.getSize(widthSpec) * 40.0 / 27.0).toInt(),
                View.MeasureSpec.EXACTLY)
        super.onMeasure(widthSpec, height)
    }
}
