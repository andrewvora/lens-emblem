package com.andrewvora.apps.lensemblem.heroeslist

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.andrewvora.apps.lensemblem.R
import com.andrewvora.apps.lensemblem.dagger.component
import kotlinx.android.synthetic.main.fragment_hero_list.*
import javax.inject.Inject

/**
 * Created on 4/22/2018.
 * @author Andrew Vorakrajangthiti
 */
class HeroesListFragment : Fragment() {

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var heroListViewModel: HeroesListViewModel
    private lateinit var heroListAdapter: HeroesListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        heroListAdapter = HeroesListAdapter(emptyList())

        activity?.application?.component()?.inject(this)
        heroListViewModel = ViewModelProviders.of(this,
                viewModelFactory)[HeroesListViewModel::class.java]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_hero_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        initObservers()

        heroListViewModel.loadHeroes()
    }

    private fun initViews() {
        val parent = activity as AppCompatActivity
        parent.setSupportActionBar(toolbar)
        parent.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        parent.title = getString(R.string.heroes)

        hero_list_recycler_view.layoutManager = LinearLayoutManager(activity)
        hero_list_recycler_view.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL).apply {
            setDrawable(ContextCompat.getDrawable(activity!!, R.drawable.divider)!!)
        })
        hero_list_recycler_view.adapter = heroListAdapter
    }

    private fun initObservers() {
        heroListViewModel.getHeroes().observe(this, Observer {
            heroListAdapter.updateHeroes(it ?: emptyList())
        })

        heroListViewModel.getError().observe(this, Observer {
            Toast.makeText(activity, it.toString(), Toast.LENGTH_SHORT).show()
        })
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            android.R.id.home -> activity?.onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }
}