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

/**
 * Collection of interfaces and classes necessary to implement the main view's presentation and business logic.
 * ViewModels hold the state of the view they refer to, and handle updates in said state through emissions of a specific LiveData stream.
 * Commands are instead to be considered one-shot operations that need to be consumed, and that could potentially be listened to by observers
 * from other views, hence the choice of LiveEvent as the concrete implementation for the command stream. This level of indirection is very useful
 * in ensuring views are as 'dumb' as possible, thus maximizing the unit-testability of business logic.
 * The 'internal' visibility of the implementation is merely symbolical, as this project consists of a single module only.
 * It is however still useful in conveying the necessity not to directly instantiate the class from outside this layer.
 */

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
        // On launch, populate the list with saved locations.
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val favouriteLocations = getListOfFavouriteLocations()
                viewState.postValue(ViewState(favouriteLocations))
            } catch (e: Exception) {
                commands.postValue(ShowDialog(R.string.generic_error_title, R.string.generic_error_content))
            }
        }
    }

    // To be invoked each time a new search submission occurs. It obtains coordinates for places that the user could be looking for.
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

    // This signals that the user clicked on one of the entries in the list, reacts by clearing the list results (this viewmodel is scoped to the activity)
    // and notifying observers of what they're required to do.
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