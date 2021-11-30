package com.example.freeweather.presentation.search

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.freeweather.R
import com.example.freeweather.data.repository.Repository
import com.example.freeweather.domain.City
import com.example.freeweather.presentation.search.SearchViewModel.*
import com.example.freeweather.presentation.search.SearchViewModel.Command.SelectLocation
import com.example.freeweather.presentation.search.SearchViewModel.Command.ShowDialog
import com.hadilq.liveevent.LiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val MIN_SEARCH_QUERY_LENGTH = 2

interface SearchViewModel {
    sealed class Command {
        data class SelectLocation(val locationName: String, val latitude: Double, val longitude: Double) : Command()
        data class ShowDialog(@StringRes val titleResId: Int, @StringRes val contentResId: Int) : Command()
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
    fun locationSelected(searchResult: SearchResult)
}

@HiltViewModel
internal class SearchViewModelImpl @Inject constructor(
    private val repository: Repository
) : SearchViewModel, ViewModel() {
    override val commands = LiveEvent<Command>()
    override val viewState = MutableLiveData<ViewState>()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val favouriteLocations = getListOfFavouriteLocations()
                viewState.postValue(ViewState(favouriteLocations))
            } catch (e: Exception) {
                commands.postValue(ShowDialog(R.string.generic_error_title, R.string.generic_error_content))
            }
        }
    }

    override fun locationSearchSubmitted(searchString: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                when {
                    searchString.length > MIN_SEARCH_QUERY_LENGTH -> {
                        val cities = repository.getCitiesByName(searchString)
                        val searchResults = cities.map { it.toSearchResult() }
                        viewState.postValue(ViewState(searchResults))
                    }
                    searchString.isEmpty() -> {
                        val favouriteLocations = getListOfFavouriteLocations()
                        viewState.postValue(ViewState(favouriteLocations))
                    }
                    else -> {
                        viewState.postValue(ViewState(emptyList()))
                    }
                }
            } catch (e: Exception) {
                commands.postValue(ShowDialog(R.string.generic_error_title, R.string.generic_error_content))
            }
        }
    }

    override fun locationSelected(searchResult: SearchResult) {
        viewState.value = ViewState(emptyList())
        commands.value = SelectLocation(searchResult.locationName, searchResult.latitude, searchResult.longitude)
    }

    private suspend fun getListOfFavouriteLocations() = repository.getFavouriteCities()
        .map { city ->
            city.toSearchResult()
        }
}

private fun City.toSearchResult() = SearchResult(
    locationName = listOfNotNull(name, state, country).joinToString(", "),
    latitude = latitude,
    longitude = longitude
)