package com.pos_terminal.tamaktime_temirnal.presentation.fragments.productscreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.pos_terminal.tamaktime_temirnal.R
import com.pos_terminal.tamaktime_temirnal.common.UiState
import com.pos_terminal.tamaktime_temirnal.common.autoCleared
import com.pos_terminal.tamaktime_temirnal.data.remote.model.product.Product
import com.pos_terminal.tamaktime_temirnal.databinding.FragmentProductBinding
import com.pos_terminal.tamaktime_temirnal.presentation.fragments.cardscreen.cardauthed.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProductFragment : Fragment(), ProductAdapter.OnProductClickListener, MenuProvider {

    private var binding: FragmentProductBinding by autoCleared()

    private val viewModel: ProductViewModel by viewModels()

    private val sharedViewModel: SharedViewModel by activityViewModels()

    private lateinit var productAdapter: ProductAdapter

    private val args: ProductFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun navigateBack() =
        findNavController().popBackStack()

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return if (menuItem.itemId == android.R.id.home) {
            navigateBack()
            true
        } else {
            false
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            navigateBack()
        }

        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        productAdapter = ProductAdapter(this)
        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(requireContext(), 4)
            adapter = productAdapter
        }

        lifecycleScope.launch {
            sharedViewModel.products.collect { products ->
                productAdapter.submitList(products)
            }
        }

        val categoryId = args.categoryId
        val credentials = args.credentials
        val canteenId = args.canteenId
        viewModel.loadProducts(credentials, canteenId, categoryId)
        observeViewModel()
    }

    private fun observeViewModel() {
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
                            sharedViewModel.loadProducts(uiState.data.results)
                        }

                        is UiState.Error -> {
                            binding.progress.visibility = View.GONE
                            binding.recyclerView.visibility = View.GONE
                            Toast.makeText(requireContext(), uiState.message, Toast.LENGTH_LONG)
                                .show()
                        }
                    }
                }
            }
        }
    }

    override fun onProductClick(product: Product) {
        sharedViewModel.addProductToOrder(product)
    }

}