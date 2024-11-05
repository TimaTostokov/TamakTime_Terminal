package com.pos_terminal.tamaktime_temirnal.presentation.fragments.categoryscreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.pos_terminal.tamaktime_temirnal.R
import com.pos_terminal.tamaktime_temirnal.common.autoCleared
import com.pos_terminal.tamaktime_temirnal.data.remote.model.category.Category
import com.pos_terminal.tamaktime_temirnal.databinding.FragmentCategoryBinding
import com.pos_terminal.tamaktime_temirnal.presentation.fragments.categoryscreen.adapter.CategoryAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class CategoryFragment : Fragment(), CategoryAdapter.CategoryItemListener,
    SwipeRefreshLayout.OnRefreshListener {

    private var binding: FragmentCategoryBinding by autoCleared()
    private val viewModel: CategoryFragmentViewModel by viewModels()
    private val adapter: CategoryAdapter by lazy { CategoryAdapter(this) }
    private var creds: String? = null
    private var canteenId: Long = -1L

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.refreshSwipe.setOnRefreshListener(this)
        setupRecyclerView()
        setupObservers()
    }

    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 4)
        binding.recyclerView.adapter = adapter
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            viewModel.loading.collect { isLoading ->
                binding.progress.visibility = if (isLoading) View.VISIBLE else View.GONE
                binding.recyclerView.visibility = if (isLoading) View.GONE else View.VISIBLE
                binding.refreshSwipe.isRefreshing = isLoading
            }
        }

        lifecycleScope.launch {
            viewModel.catList.collect { categories ->
                if (categories.isNotEmpty()) {
                    adapter.setItems(ArrayList(categories))
                }
            }
        }

        lifecycleScope.launch {
            viewModel.credentials.collect { credentials ->
                creds = credentials
                if (!creds.isNullOrEmpty() && canteenId != -1L) {
                    loadCategories()
                }
            }
        }

        lifecycleScope.launch {
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
                Timber.e("$credentials | $canteenId")
                viewModel.getAllCategories(credentials, canteenId)
            }
        }
    }

    override fun onClickedCategory(category: Category) {
        Timber.d("${category.name} clicked")
        findNavController().navigate(
            R.id.action_categoryFragment_to_productFragment,
            bundleOf(
                "id" to category.id,
                "credentials" to creds,
                "canteenId" to canteenId
            )
        )
    }

    override fun onRefresh() {
        loadCategories()
    }

}