package com.andrewvora.apps.lensemblem.heroeslist

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.andrewvora.apps.lensemblem.R
import com.andrewvora.apps.lensemblem.models.Hero
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
        }
    }

    interface ActionListener {
        fun onClicked(position: Int)
    }
}