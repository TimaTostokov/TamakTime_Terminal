package com.pos_terminal.tamaktime_temirnal.presentation.fragments.productscreen

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.pos_terminal.tamaktime_temirnal.R
import com.pos_terminal.tamaktime_temirnal.common.CardState
import com.pos_terminal.tamaktime_temirnal.common.autoCleared
import com.pos_terminal.tamaktime_temirnal.data.remote.model.product.Product
import com.pos_terminal.tamaktime_temirnal.databinding.FragmentProductBinding
import com.pos_terminal.tamaktime_temirnal.presentation.fragments.cardscreen.cardviewmodel.CardFragmentViewModel
import com.pos_terminal.tamaktime_temirnal.presentation.fragments.productscreen.adapter.ProductAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class ProductFragment : Fragment(), ProductAdapter.ProductItemListener,
    SwipeRefreshLayout.OnRefreshListener, MenuProvider {

    private var binding: FragmentProductBinding by autoCleared()
    private val viewModel: ProductFragmentViewModel by viewModels()
    private val cardViewModel: CardFragmentViewModel by activityViewModels()

    private val adapter: ProductAdapter by lazy { ProductAdapter(this) }
    private var categoryId: Long = -1L
    private var credentials: String? = null
    private var canteenId: Long = -1L
    private val productList = mutableListOf<Product>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProductBinding.inflate(inflater, container, false)
        requireActivity().addMenuProvider(this, viewLifecycleOwner)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this) { navigateBack() }
    }

    private fun navigateBack() =
        findNavController().navigate(R.id.action_productFragment_to_categoryFragment)

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
        binding.refreshSwipe.setOnRefreshListener(this)
        arguments?.let {
            categoryId = it.getLong("id")
            credentials = it.getString("credentials")
            canteenId = it.getLong("canteenId")
        }

        setupRecyclerView()
        setupObservers()

        Timber.e("catId: $categoryId, \ncreds: $credentials, \ncanteenId: $canteenId")

        if (categoryId > 0 && !credentials.isNullOrEmpty() && canteenId > 0) onRefresh()
    }

    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 4)
        binding.recyclerView.adapter = adapter
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.loading.collect { isLoading ->
                        binding.progress.visibility = if (isLoading) View.VISIBLE else View.GONE
                        binding.recyclerView.visibility = if (isLoading) View.GONE else View.VISIBLE
                        binding.refreshSwipe.isRefreshing = false
                    }
                }

                launch {
                    viewModel.productsResponse.collect { response ->
                        binding.emptyPh.visibility =
                            if (response?.itemsCount == 0) View.VISIBLE else View.GONE
                        response?.results?.takeIf { it.isNotEmpty() }
                            ?.let { adapter.setItems(ArrayList(it)) }
                    }
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onClickedProduct(product: Product) {
        Timber.d("${product.title} chosen")
        if (cardViewModel.cardState.value in listOf(
                CardState.AUTHENTICATED,
                CardState.ORDER
            ) && product.count!! > 0
        ) {
            product.count = product.count!!.minus(1)
            if (product.count!! > 0) {
                Log.d("Adding product:", " ${product.count}")
                cardViewModel.addToCart(product)
            } else {
                Log.d("Msg", "Product is null or count is 0")
            }
            adapter.notifyDataSetChanged()
        }
    }

    override fun onRefresh() {
        viewModel.loadProducts(credentials!!, canteenId, categoryId)
    }

}