package com.example.freeweather.hilt.modules

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.freeweather.data.api.RestClient
import com.example.freeweather.data.api.RestClientImpl
import com.example.freeweather.data.api.RestGeocoding
import com.example.freeweather.data.api.RestWeather
import com.google.gson.Gson
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton


private const val REST_CLIENT_CONNECTION_TIMEOUT_SECONDS = 20L
private const val REST_CLIENT_WRITE_TIMEOUT_SECONDS = 20L
private const val REST_CLIENT_READ_TIMEOUT_SECONDS = 20L
private const val API_CACHE_DIR = "api_cache"
private const val API_CACHE_SIZE = 10 * 1024L * 1024L // 10 MiB
private const val BASE_URL = "https://api.openweathermap.org"
private const val API_KEY_PARAM_NAME = "appid"
private const val API_KEY_VALUE = "0db5caf45f1986305c1379cb056d6d34"

@InstallIn(SingletonComponent::class)
@Module
abstract class ApiModule {

    @Binds
    @Singleton
    internal abstract fun bindRestClient(client: RestClientImpl): RestClient

    companion object {
        @Provides
        @Named("API_KEY")
        @Singleton
        fun provideApiKeyInterceptor() = Interceptor { chain ->
            val originalReq = chain.request()
            val originalUrl = originalReq.url
            val newUrl = originalUrl.newBuilder()
                .addQueryParameter(API_KEY_PARAM_NAME, API_KEY_VALUE)
                .build()
            val newRequest = originalReq.newBuilder()
                .url(newUrl)
                .build()
            chain.proceed(newRequest)
        }

        @Provides
        @Named("ONLINE")
        @Singleton
        fun provideOnlineInterceptor() = Interceptor { chain ->
            val response = chain.proceed(chain.request())
            val maxAge = 60 // read from cache for 60 seconds even if there is internet connection
            response.newBuilder()
                .header("Cache-Control", "public, max-age=$maxAge")
                .removeHeader("Pragma")
                .build()
        }

        @Provides
        @Named("OFFLINE")
        @Singleton
        fun provideOfflineInterceptor(context: Context) = Interceptor { chain ->
            var request = chain.request()
            if (!isInternetAvailable(context)) {
                val maxStale = 60 * 60 * 24 // Offline cache available for 24 hours
                request = request.newBuilder()
                    .header("Cache-Control", "public, only-if-cached, max-stale=$maxStale")
                    .removeHeader("Pragma")
                    .build();
            }
            chain.proceed(request)
        }

        @Provides
        @Singleton
        fun provideHttpLoggingInterceptor() = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        @Provides
        @Singleton
        fun provideApiCache(context: Context): Cache {
            val cacheFile = File("${context.cacheDir.absolutePath}${File.pathSeparator}$API_CACHE_DIR")
            return Cache(cacheFile, API_CACHE_SIZE)
        }

        @Provides
        @Singleton
        fun provideHttpClient(
            @Named("API_KEY") apiKeyInterceptor: Interceptor,
            @Named("OFFLINE") offlineInterceptor: Interceptor,
            @Named("ONLINE") onlineInterceptor: Interceptor,
            httpLoggingInterceptor: HttpLoggingInterceptor,
            apiCache: Cache
        ) = OkHttpClient.Builder()
            .connectTimeout(REST_CLIENT_CONNECTION_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(REST_CLIENT_READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(REST_CLIENT_WRITE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .cache(apiCache)
            .addInterceptor(httpLoggingInterceptor)
            .addInterceptor(apiKeyInterceptor)
            .addInterceptor(offlineInterceptor)
            .addInterceptor(onlineInterceptor)
            .build()

        @Provides
        @Singleton
        fun provideRetrofit(httpClient: OkHttpClient, gson: Gson): Retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl(BASE_URL)
            .client(httpClient)
            .build()

        @Provides
        @Singleton
        fun provideRestWeatherInterface(retrofit: Retrofit): RestWeather = retrofit.create(RestWeather::class.java)

        @Provides
        @Singleton
        fun provideRestGeocodingInterface(retrofit: Retrofit): RestGeocoding = retrofit.create(RestGeocoding::class.java)

        private fun isInternetAvailable(context: Context): Boolean {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkCapabilities = connectivityManager.activeNetwork ?: return false
            val actNw =
                connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
            return when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        }
    }
}