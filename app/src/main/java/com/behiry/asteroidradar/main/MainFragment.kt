package com.behiry.asteroidradar.main

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.behiry.asteroidradar.R
import com.behiry.asteroidradar.databinding.FragmentMainBinding

class MainFragment : Fragment() {

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this)[MainViewModel::class.java]
    }

    private lateinit var adapter: MainAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val binding = FragmentMainBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        setHasOptionsMenu(true)

        adapter = MainAdapter(MainAdapter.AsteroidListener { asteroid ->
            viewModel.onAsteroidClicked(asteroid)
        })

        binding.asteroidRecycler.adapter = adapter

        viewModel.allAsteroids.observe(viewLifecycleOwner) { asteroids ->
            asteroids?.let { adapter.submitList(asteroids) }
        }

        viewModel.detailedAsteroid.observe(viewLifecycleOwner) { asteroid ->
            asteroid?.let {
                this.findNavController()
                    .navigate(MainFragmentDirections.actionMainFragmentToDetailsFragment(it))
                viewModel.doneNavigating()
            }
        }
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        // Check which item menu clicked and return a new list to the adapter depending on it
        when (item.itemId) {
            R.id.show_week -> viewModel.allAsteroids.observe(viewLifecycleOwner) {
                it?.let { adapter.submitList(it) }  ////
                makeToast()
            }
            R.id.show_today -> viewModel.todayOnlyAsteroids.observe(viewLifecycleOwner) {
                it?.let { adapter.submitList(it) }
                makeToast()
            }
            R.id.show_saved -> viewModel.allAsteroids.observe(viewLifecycleOwner) {
                it?.let { adapter.submitList(it) }
                makeToast()
            }
        }
        return true
    }

private fun makeToast() {
    Toast.makeText(this.activity, "List changed successfully", Toast.LENGTH_SHORT).show()
}
}