package com.andrewvora.apps.lensemblem.heroeslist

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.navigation.fragment.NavHostFragment.findNavController
import com.andrewvora.apps.lensemblem.R
import com.andrewvora.apps.lensemblem.acknowledgements.AcknowledgementsDialog
import com.andrewvora.apps.lensemblem.dagger.component
import kotlinx.android.synthetic.main.fragment_hero_list.*
import kotlinx.android.synthetic.main.toolbar.*
import javax.inject.Inject

/**
 * Created on 4/22/2018.
 * @author Andrew Vorakrajangthiti
 */
class HeroesListFragment : Fragment(), HeroesListAdapter.ActionListener {

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var heroListViewModel: HeroesListViewModel
    private lateinit var heroListAdapter: HeroesListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        heroListAdapter = HeroesListAdapter(emptyList(), this)

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

        hero_list_refresh_layout.setOnRefreshListener {
            heroListViewModel.refreshHeroes()
        }
        hero_list_recycler_view.layoutManager = LinearLayoutManager(activity)
        hero_list_recycler_view.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL).apply {
            setDrawable(ContextCompat.getDrawable(activity!!, R.drawable.divider)!!)
        })
        hero_list_recycler_view.adapter = heroListAdapter
        /* // hide for now until the feature is more fleshed out
        hero_list_recycler_view.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                when(newState) {
                    RecyclerView.SCROLL_STATE_IDLE, RecyclerView.SCROLL_STATE_SETTLING -> filter_fab.show()
                    else -> filter_fab.hide()
                }
            }
        })
        */
        filter_fab.setOnClickListener {
            HeroFiltersBottomSheet.getInstance().show(childFragmentManager, "filters-fragment")
        }
    }

    private fun initObservers() {
        heroListViewModel.getHeroes().observe(this, Observer {
            heroListAdapter.updateHeroes(it ?: emptyList())
            hero_list_refresh_layout.isRefreshing = false
        })

        heroListViewModel.getError().observe(this, Observer {
            hero_list_refresh_layout.isRefreshing = false
            Toast.makeText(activity, it.toString(), Toast.LENGTH_SHORT).show()
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.menu_hero_list, menu)

        val searchView = menu?.findItem(R.id.menu_search)?.actionView?.takeIf { it is SearchView } as SearchView?
        searchView?.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = true
            override fun onQueryTextChange(newText: String?): Boolean {
                heroListViewModel.findHeroesMatching(newText)
                return true
            }
        })

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            android.R.id.home -> {
                activity?.onBackPressed()
                return true
            }
            R.id.menu_acknowledgements -> {
                activity?.fragmentManager?.let {
                    AcknowledgementsDialog().show(it, "acknowledgements")
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onClicked(position: Int) {
        closeKeyboard()

        val toHeroDetailsDirection = HeroesListFragmentDirections.actionToHeroDetails()
        toHeroDetailsDirection.setHeroId(heroListAdapter.heroes[position].id)

        findNavController(this).navigate(toHeroDetailsDirection)
    }

    private fun closeKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(view?.windowToken, 0)
    }
}