package com.andrewvora.apps.lensemblem.heroeslist

import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.andrewvora.apps.lensemblem.R
import com.andrewvora.apps.lensemblem.models.FilterOption
import kotlinx.android.synthetic.main.bottom_sheet_filters.view.*
import kotlinx.android.synthetic.main.row_item_filter.view.*

/**
 * Created on 5/29/2018.
 * @author Andrew Vorakrajangthiti
 */
class HeroFiltersBottomSheet : BottomSheetDialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.bottom_sheet_filters, container, false)
        FILTERS.forEach {
            root.filters_container.addView(inflater.inflate(R.layout.row_item_filter, container, false).apply {
                this.filter_text.setText(it.titleId)
                this.filter_text.setCompoundDrawablesRelativeWithIntrinsicBounds(it.drawableId, 0, 0, 0)
            })
        }
        return root
    }

    companion object {
        private val FILTERS = arrayOf(
                FilterOption(R.drawable.ic_star_24dp, R.string.filter_rarity)
        )
        fun getInstance(): HeroFiltersBottomSheet {
            return HeroFiltersBottomSheet()
        }
    }
}