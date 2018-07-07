package com.andrewvora.apps.lensemblem.heroeslist

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.andrewvora.apps.lensemblem.R
import com.andrewvora.apps.lensemblem.models.Hero
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.row_item_hero.view.*

/**
 * Created on 4/24/2018.
 * @author Andrew Vorakrajangthiti
 */
class HeroesListAdapter(var heroes: List<Hero>, val listener: ActionListener) : RecyclerView.Adapter<HeroesListAdapter.HeroViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeroViewHolder {
        return HeroViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.row_item_hero, parent, false))
    }

    override fun getItemCount(): Int {
        return heroes.size
    }

    override fun onBindViewHolder(holder: HeroViewHolder, position: Int) {
        holder.bind(heroes[position])
        holder.itemView.setOnClickListener { listener.onClicked(holder.adapterPosition) }
    }

    fun updateHeroes(heroes: List<Hero>) {
        this.heroes = heroes
        notifyDataSetChanged()
    }

    class HeroViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(hero: Hero) {
            view.hero_title.text = hero.title
            view.hero_name.text = hero.name

            hero.weaponTypeUrl?.let {
                Glide.with(view)
                        .load(it)
                        .into(view.weapon_type_image_view)
            }
            hero.movementTypeUrl?.let {
                Glide.with(view)
                        .load(it)
                        .into(view.movement_type_image_view)
            }
            hero.imageUrl?.let {
                val cornerRadius = view.context.resources.getDimensionPixelSize(R.dimen.hero_details_photo_rounded_corner_size)
                Glide.with(view)
                        .load(it)
                        .apply(RequestOptions().transforms(RoundedCorners(cornerRadius)))
                        .into(view.hero_image_view)
            }
        }
    }

    interface ActionListener {
        fun onClicked(position: Int)
    }
}