package com.example.freeweather.presentation.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.freeweather.databinding.SearchResultItemBinding
import com.example.freeweather.presentation.search.SearchViewModel.SearchResult

class SearchAdapter(private var searchResults: List<SearchResult>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return SearchViewHolder(SearchResultItemBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentItem = searchResults[position]
        if (holder is SearchViewHolder) {
            holder.bind(currentItem)
        }
    }

    override fun getItemCount() = searchResults.size

    fun submitList(searchResults: List<SearchResult>) {
        this.searchResults = searchResults
        notifyDataSetChanged()
    }
}

private class SearchViewHolder(private val binding: SearchResultItemBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(searchResult: SearchResult) {
        binding.searchResult.text = searchResult.locationName
    }
}