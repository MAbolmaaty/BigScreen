package com.emapps.bigscreen.ui.home.decorations

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.emapps.bigscreen.R

class MovieItemDecoration : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.bottom = parent.resources.getDimensionPixelSize(R.dimen.medium)
    }
}