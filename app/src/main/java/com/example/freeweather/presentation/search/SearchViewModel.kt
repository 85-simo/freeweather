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

interface SearchViewModel {
    sealed class Command {
        object NavigateBack : Command()
    }

    data class SearchResult(
        val locationId: Long,
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
        viewModelScope.launch(Dispatchers.IO) {
            val cities = repository.getCitiesByName(searchString)
            val searchResults = cities.map { it.toSearchResult() }
            viewState.postValue(ViewState(searchResults))
        }
    }
}

private fun City.toSearchResult() = SearchResult(
    locationId = id,
    locationName = listOfNotNull(name, state, country).joinToString(),
    latitude = latitude,
    longitude = longitude
)