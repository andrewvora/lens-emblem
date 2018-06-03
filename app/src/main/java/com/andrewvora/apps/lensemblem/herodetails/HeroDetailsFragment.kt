package com.andrewvora.apps.lensemblem.herodetails

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.andrewvora.apps.lensemblem.R
import com.andrewvora.apps.lensemblem.dagger.component
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.fragment_hero_details.*
import kotlinx.android.synthetic.main.toolbar.*
import javax.inject.Inject
import kotlin.math.roundToInt

/**
 * Created on 5/30/2018.
 * @author Andrew Vorakrajangthiti
 */
class HeroDetailsFragment : Fragment() {

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var heroDetailsViewModel: HeroDetailsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        activity?.application?.component()?.inject(this)

        heroDetailsViewModel = ViewModelProviders.of(this,
                viewModelFactory)[HeroDetailsViewModel::class.java]

        arguments?.let {
            val safeArgs = HeroDetailsFragmentArgs.fromBundle(it)

            heroDetailsViewModel.loadHero(safeArgs.heroId.toLong())
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_hero_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initViews()
        initObservers()
    }

    private fun initViews() {
        val parent = activity as AppCompatActivity
        parent.setSupportActionBar(toolbar)
        parent.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        parent.title = getString(R.string.hero_stats)

        hero_equipped_switch.setOnCheckedChangeListener { _, isChecked ->
            heroDetailsViewModel.loadStats(hero_rarity.rating.roundToInt(), isChecked)
        }
        hero_rarity.setOnRatingBarChangeListener { ratingBar, rating, _ ->
            val minRarity = heroDetailsViewModel.getHeroMinRarity()
            if (rating >= minRarity) {
                heroDetailsViewModel.loadStats(ratingBar.rating.roundToInt(), hero_equipped_switch.isChecked)
            } else {
                hero_rarity.rating = minRarity.toFloat()
            }
        }
    }

    private fun initObservers() {
        heroDetailsViewModel.getHeroLiveData().observe(this, Observer { hero ->
            hero?.let {
                it.stats?.minBy { it.rarity }?.rarity?.let {
                    hero_rarity.rating = it.toFloat()
                }

                hero_title_text_view.text = it.title
                hero_name_text_view.text = it.name

                it.imageUrl?.let {
                    val roundedCornerSize = resources.getDimensionPixelSize(R.dimen.hero_details_photo_rounded_corner_size)
                    Glide.with(this@HeroDetailsFragment)
                            .load(it)
                            .apply(RequestOptions().transforms(RoundedCorners(roundedCornerSize)))
                            .into(hero_image_view)
                }
            }
        })
        heroDetailsViewModel.getStatsToDisplayLiveData().observe(this, Observer {  statsPair ->
            statsPair?.first?.let {
                hero_hp_lvl1_text_view.text = it.hp.toString()
                hero_atk_lvl1_text_view.text = it.atk.toString()
                hero_spd_lvl1_text_view.text = it.spd.toString()
                hero_def_lvl1_text_view.text = it.def.toString()
                hero_res_lvl1_text_view.text = it.res.toString()
            }

            statsPair?.second?.let {
                hero_hp_lvl40_text_view.text = it.hp.toString()
                hero_atk_lvl40_text_view.text = it.atk.toString()
                hero_spd_lvl40_text_view.text = it.spd.toString()
                hero_def_lvl40_text_view.text = it.def.toString()
                hero_res_lvl40_text_view.text = it.res.toString()
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            android.R.id.home -> {
                activity?.onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}