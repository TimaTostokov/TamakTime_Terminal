package com.pos_terminal.tamaktime_temirnal.presentation.fragments.categoryscreen

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.pos_terminal.tamaktime_temirnal.R
import com.pos_terminal.tamaktime_temirnal.common.Extensions.changeLanguage
import com.pos_terminal.tamaktime_temirnal.common.UiState
import com.pos_terminal.tamaktime_temirnal.common.autoCleared
import com.pos_terminal.tamaktime_temirnal.data.remote.model.category.Category
import com.pos_terminal.tamaktime_temirnal.databinding.FragmentCategoryBinding
import com.pos_terminal.tamaktime_temirnal.presentation.activity.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CategoryFragment : Fragment(), MenuProvider {

    private var binding: FragmentCategoryBinding by autoCleared()

    private val viewModel: CategoryViewModel by viewModels()

    private val adapter: CategoryAdapter by lazy {
        CategoryAdapter { category -> onCategoryClick(category) }
    }

    private var creds: String? = null
    private var canteenId: Long = -1L

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)

        setupRecyclerView()
        setupObservers()
    }

    private fun setupRecyclerView() {
        binding.recyclerView.adapter = adapter
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->
                    when (uiState) {
                        is UiState.Loading -> {
                            binding.progress.visibility = View.VISIBLE
                            binding.recyclerView.visibility = View.GONE
                        }

                        is UiState.Success -> {
                            binding.progress.visibility = View.GONE
                            binding.recyclerView.visibility = View.VISIBLE
                            Log.d("arsenchik", "${uiState.data}")
                            adapter.submitList(uiState.data)
                        }

                        is UiState.Error -> {
                            binding.progress.visibility = View.GONE
                            binding.recyclerView.visibility = View.GONE
                            Log.d("Error: ", uiState.message)
                        }
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.credentials.collect { credentials ->
                creds = credentials
                if (!creds.isNullOrEmpty() && canteenId != -1L) {
                    loadCategories()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.canteenId.collect { id ->
                canteenId = id ?: -1L
                if (canteenId != -1L && !creds.isNullOrEmpty()) {
                    loadCategories()
                }
            }
        }
    }

    private fun loadCategories() {
        creds?.let { credentials ->
            if (credentials.isNotEmpty() && canteenId != -1L) {
                viewModel.getAllCategories(credentials, canteenId)
            }
        }
    }

    private fun onCategoryClick(category: Category) {
        val action = CategoryFragmentDirections.actionCategoryFragmentToProductFragment(
            categoryId = category.id,
            credentials = creds ?: "",
            canteenId = canteenId
        )
        findNavController().navigate(action)
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.action_exit -> {
                (activity as? MainActivity)?.let { mainActivity ->
                    mainActivity.lifecycleScope.launch {
//                        mainActivity.viewModel.exit()
                        mainActivity.showExitConfirmationDialog()
                    }
                }
                return true
            }
            R.id.ic_menu -> {
                (activity as? MainActivity)?.changeLanguage()
                return true
            }
        }
        return false
    }

}