package com.jdhdev.mm8.ui

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView


class FourByThreeImageView(context: Context, attrs: AttributeSet) : ImageView(context, attrs) {

    override fun onMeasure(widthSpec: Int, heightSpec: Int) {
        val fourThreeHeight = View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthSpec) * 3 / 4,
                View.MeasureSpec.EXACTLY)
        super.onMeasure(widthSpec, fourThreeHeight)
    }
}
