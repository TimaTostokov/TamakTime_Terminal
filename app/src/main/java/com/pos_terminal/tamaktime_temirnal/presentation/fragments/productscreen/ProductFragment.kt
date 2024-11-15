package com.pos_terminal.tamaktime_temirnal.presentation.fragments.productscreen

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.pos_terminal.tamaktime_temirnal.R
import com.pos_terminal.tamaktime_temirnal.common.UiState
import com.pos_terminal.tamaktime_temirnal.common.autoCleared
import com.pos_terminal.tamaktime_temirnal.databinding.FragmentProductBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProductFragment : Fragment(), MenuProvider {

    private var binding: FragmentProductBinding by autoCleared()
    private val viewModel: ProductViewModel by viewModels()
    private val adapter: ProductAdapter by lazy { ProductAdapter(this::click) }

    private val args: ProductFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProductBinding.inflate(inflater, container, false)
        requireActivity().addMenuProvider(this, viewLifecycleOwner)
        setupRecyclerView()
        Log.d("ProductFragment", "onCreateView: View created")
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this) { navigateBack() }
        Log.d("ProductFragment", "onCreate: Fragment created")
    }

    private fun navigateBack() =
        findNavController().navigate(R.id.action_productFragment_to_categoryFragment)

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.menu, menu)
        Log.d("ProductFragment", "onCreateMenu: Menu created")
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
        Log.d("ProductFragment", "onViewCreated: View created")

        val categoryId = args.categoryId
        val credentials = args.credentials
        val canteenId = args.canteenId
        Log.d("ProductFragment", "onViewCreated: Received arguments - categoryId: $categoryId")
        viewModel.loadProducts(credentials,canteenId,categoryId)
        Log.d("ProductFragment", "onViewCreated: loadProducts called with categoryId: $categoryId")
        observeViewModel()
    }

    private fun setupRecyclerView() {
        binding.recyclerView.adapter = adapter
        Log.d("ProductFragment", "setupRecyclerView: RecyclerView setup completed")
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->
                    when (uiState) {
                        is UiState.Loading -> {
                            Log.d("ProductFragment", "observeViewModel: Loading state detected")
                            binding.progress.visibility = View.VISIBLE
                            binding.recyclerView.visibility = View.GONE
                        }
                        is UiState.Success -> {
                            Log.d("ProductFragment", "observeViewModel: Success state detected with data: ${uiState.data.results}")
                            binding.progress.visibility = View.GONE
                            binding.recyclerView.visibility = View.VISIBLE
                            adapter.submitList(uiState.data.results)
                        }
                        is UiState.Error -> {
                            Log.e("ProductFragment", "observeViewModel: Error state detected with message: ${uiState.message}")
                            binding.progress.visibility = View.GONE
                            binding.recyclerView.visibility = View.GONE
                            Toast.makeText(requireContext(), uiState.message, Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }

    private fun click(productId: String) {
        Log.d("ProductFragment", "click: Item clicked with productId: $productId")
        // Обработать клик на продукт, например, перейти к подробной информации о продукте
    }
}
