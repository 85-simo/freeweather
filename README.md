# FreeWeather

FreeWeather is a POC Android app that demonstrates the implementation of simple weather functionalities based on the [OpenWeather APIs](https://openweathermap.org/current).

## Functionality

The app provides the following functionality:
- On launch, weather info is displayed for a default location - in this case, London.
- Users can browse weather data on the selected location in the main screen.
- A refresh of the data displayed in the main screen can be requested through the swipe-to-refresh gesture.
- Retrieved information is cached locally for up to 24 hours. When an API request is made in the absence of an valid connection, the app will show cached data if present, or will display an error dialog.
- Cached data will also be displayed in the presence of a working connection, if the related cache item isn't older than 60 seconds.
- Users can mark a location as "favourite" by tapping on the little heart-shaped icon on the top-right corner of the main view.
- Locations can be searched by clicking on the magnifying glass icon and entering at least 3 characters in the search bar. The list below the bar will also display favourite locations if any are present.
- Dark mode support.
- Support to multiple screen sizes/densities (tested on Nexus One and Pixel C devices), although text sizes haven't been optimised for tablets.
- Support to orientation changes.

## Architecture

The app has been designed and developed according to the [Clean Architecture](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html) and [MVVM](https://developer.android.com/jetpack/guide) principles. It isolates I/O operations from the presentation layer and uses a [Repository](https://developer.android.com/jetpack/guide#fetch-data) to provide access to the layers above.
It also uses Jetpach arch and lifecycle components to implement its presentation layer (ViewModel, LiveData), and leverages Jetpack Navigation and SafeArgs to easily handle navigation actions.
Dependency injection has been used extensively in the project and has been implemented with Hilt. Database operations have been implemented with Room DB.

## Dependencies

The following third-party libraries have been used:
- Lifecycle-aware command delivery:
  - [LiveEvent](https://github.com/hadilq/LiveEvent) -> Enables sending single-use events that can be intercepted by multiple observers.
- API interaction
  - [Retrofit2](https://square.github.io/retrofit/)
  - [OkHttp](https://square.github.io/okhttp/)
  - [OkHttp Logging interceptor](https://github.com/square/okhttp/tree/master/okhttp-logging-interceptor)
  - [Gson](https://github.com/google/gson)
- Image downloading
  - [Picasso](https://github.com/square/picasso)
- Mocking
  - [Mockito Kotlin](https://github.com/mockito/mockito-kotlin)

## Build

The project shouldn't require any peculiar build steps, it should be sufficient to clone this repository to your local machine, import it into your Android Studio instance and build as usual.
  
## Attributions

The app icon has been created with icons made by [iconixar](https://www.flaticon.com/authors/iconixar) from [flaticon](https://www.flaticon.com/)
  
