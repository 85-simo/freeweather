package com.example.freeweather.presentation.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
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
                if (searchString.length > 2) {
                    searchViewModel.locationSearchSubmitted(searchString)
                }
            }
        }
        searchViewModel.commands.observe(viewLifecycleOwner) { command ->
            when (command) {
                is NavigateBack -> findNavController().popBackStack()
            }
        }
        searchViewModel.viewState.observe(viewLifecycleOwner) { viewState ->
            val results = viewState.searchResults
        }
    }
}