package com.andrewvora.apps.lensemblem.tutorial

import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.navigation.Navigation.findNavController
import com.andrewvora.apps.lensemblem.R
import com.andrewvora.apps.lensemblem.dagger.component
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_tutorial.*
import javax.inject.Inject

/**
 * Created on 10/3/2018.
 * @author Andrew Vorakrajangthiti
 */
class TutorialFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var tutorialViewModel: TutorialViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activity?.application?.component()?.inject(this)
        tutorialViewModel = ViewModelProviders.of(this,
                viewModelFactory)[TutorialViewModel::class.java]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_tutorial, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tutorialIds = intArrayOf(
                R.layout.tutorial_starting_service,
                R.layout.tutorial_game_screen,
                R.layout.tutorial_tap_notification,
                R.layout.tutorial_bounds)

        tutorial_view_pager.adapter = TutorialPagerAdapter(tutorialIds)
        tutorial_view_pager.addOnPageChangeListener(object: ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                if (position == tutorialIds.lastIndex) {
                    finish_tutorial_button.animate().alpha(1.0f).setDuration(100).start()
                    finish_tutorial_button.isEnabled = true
                    skip_tutorial_button.alpha = 0f
                } else {
                    finish_tutorial_button.animate().alpha(0.0f).setDuration(100).start()
                    finish_tutorial_button.isEnabled = false
                    skip_tutorial_button.alpha = 1.0f
                }
            }
        })
        page_indicator.setViewPager(tutorial_view_pager)
        skip_tutorial_button.setOnClickListener {
            tutorial_view_pager.setCurrentItem(tutorialIds.size - 1, true)
        }
        finish_tutorial_button.setOnClickListener {
            findNavController(it).navigateUp()
        }
    }
}

class TutorialPagerAdapter(private val layoutIds: IntArray) : PagerAdapter() {

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = LayoutInflater.from(container.context).inflate(
                layoutIds[position],
                container,
                false)
        container.addView(view)

        when (layoutIds[position]) {
            R.layout.tutorial_game_screen -> {
                bindProfileImage(view)
            }
            R.layout.tutorial_bounds -> {
                bindBoundsPickerImage(view)
            }
        }

        return view
    }

    private fun bindProfileImage(root: View) {
        root.findViewById<ImageView>(R.id.tutorial_image)?.let {
            Glide.with(root.context)
                    .load(R.drawable.bride_tharja)
                    .into(it)
        }
    }

    private fun bindBoundsPickerImage(root: View) {
        root.findViewById<ImageView>(R.id.tutorial_image)?.let {
            Glide.with(root.context)
                    .load(R.drawable.bounds_picking)
                    .into(it)
        }
    }

    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return view == obj
    }

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        container.removeView(obj as? View)
    }

    override fun getCount(): Int {
        return layoutIds.size
    }
}