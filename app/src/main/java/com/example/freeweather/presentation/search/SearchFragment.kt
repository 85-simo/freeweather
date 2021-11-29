package com.example.freeweather.presentation.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.freeweather.databinding.FragmentSearchBinding
import com.example.freeweather.presentation.BaseFragment
import com.example.freeweather.presentation.search.SearchViewModel.Command.NavigateBack
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : BaseFragment<FragmentSearchBinding>() {
    private val searchViewModel: SearchViewModel by activityViewModels<SearchViewModelImpl>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.searchbar.addTextChangedListener { editable ->
            editable?.toString()?.let {  searchString ->
                searchViewModel.locationSearchSubmitted(searchString)
            }
        }
        val linearLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        val defaultItemAnimator = DefaultItemAnimator()
        val divider = DividerItemDecoration(requireActivity(), LinearLayout.HORIZONTAL)
        val searchAdapter = SearchAdapter(emptyList())
        with(binding.searchResults) {
            layoutManager = linearLayoutManager
            itemAnimator = defaultItemAnimator
            addItemDecoration(divider)
            adapter = searchAdapter
        }

        searchViewModel.commands.observe(viewLifecycleOwner) { command ->
            when (command) {
                is NavigateBack -> findNavController().popBackStack()
            }
        }
        searchViewModel.viewState.observe(viewLifecycleOwner) { viewState ->
            searchAdapter.submitList(viewState.searchResults)
        }
    }
}