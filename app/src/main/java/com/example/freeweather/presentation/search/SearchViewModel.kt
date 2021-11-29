package com.example.freeweather.presentation.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.freeweather.data.repository.Repository
import com.example.freeweather.domain.City
import com.example.freeweather.presentation.search.SearchViewModel.*
import com.hadilq.liveevent.LiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val MIN_SEARCH_QUERY_LENGTH = 2

interface SearchViewModel {
    sealed class Command {
        object NavigateBack : Command()
    }

    data class SearchResult(
        val locationName: String,
        val latitude: Double,
        val longitude: Double
        )
    data class ViewState(
        val searchResults: List<SearchResult>
    )

    val commands: LiveData<Command>
    val viewState: LiveData<ViewState>

    fun locationSearchSubmitted(searchString: String)
}

@HiltViewModel
internal class SearchViewModelImpl @Inject constructor(
    private val repository: Repository
) : SearchViewModel, ViewModel() {
    override val commands = LiveEvent<Command>()
    override val viewState = MutableLiveData<ViewState>()

    override fun locationSearchSubmitted(searchString: String) {
        if (searchString.length > MIN_SEARCH_QUERY_LENGTH) {
            viewModelScope.launch(Dispatchers.IO) {
                val cities = repository.getCitiesByName(searchString)
                val searchResults = cities.map { it.toSearchResult() }
                viewState.postValue(ViewState(searchResults))
            }
        } else {
            viewState.postValue(ViewState(emptyList()))
        }
    }
}

private fun City.toSearchResult() = SearchResult(
    locationName = listOfNotNull(name, state, country).joinToString(),
    latitude = latitude,
    longitude = longitude
)