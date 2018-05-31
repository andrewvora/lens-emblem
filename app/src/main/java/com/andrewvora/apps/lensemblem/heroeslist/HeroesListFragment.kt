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
import android.support.v7.widget.RecyclerView
import android.view.*
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
        hero_list_recycler_view.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                when(newState) {
                    RecyclerView.SCROLL_STATE_IDLE, RecyclerView.SCROLL_STATE_SETTLING -> filter_fab.show()
                    else -> filter_fab.hide()
                }
            }
        })
        filter_fab.setOnClickListener {
            HeroFiltersBottomSheet.getInstance().show(childFragmentManager, "filters-fragment")
        }
    }

    private fun initObservers() {
        heroListViewModel.getHeroes().observe(this, Observer {
            heroListAdapter.updateHeroes(it ?: emptyList())
        })

        heroListViewModel.getError().observe(this, Observer {
            Toast.makeText(activity, it.toString(), Toast.LENGTH_SHORT).show()
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.menu_hero_list, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            android.R.id.home -> activity?.onBackPressed()
            R.id.menu_search -> {}
            R.id.menu_acknowledgements -> {}
            R.id.menu_reset_hero_data -> {}
            R.id.menu_sync_data -> {}
        }
        return super.onOptionsItemSelected(item)
    }
}