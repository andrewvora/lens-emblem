package com.andrewvora.apps.lensemblem.heroeslist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.andrewvora.apps.lensemblem.R

/**
 * Created on 4/22/2018.
 * @author Andrew Vorakrajangthiti
 */
class HeroesListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hero_list)
    }

    companion object {
        fun start(context: Context): Intent {
            return Intent(context, HeroesListActivity::class.java)
        }
    }
}