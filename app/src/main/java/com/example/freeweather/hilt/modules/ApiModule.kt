package com.example.freeweather.hilt.modules

import android.content.Context
import com.example.freeweather.data.api.RestGeocoding
import com.example.freeweather.data.api.RestWeather
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


private const val REST_CLIENT_CONNECTION_TIMEOUT_SECONDS = 20L
private const val REST_CLIENT_WRITE_TIMEOUT_SECONDS = 20L
private const val REST_CLIENT_READ_TIMEOUT_SECONDS = 20L
private const val API_CACHE_DIR = "api_cache"
private const val API_CACHE_SIZE = 10 * 1024L * 1024L // 10 MiB
private const val BASE_URL = "http://api.openweathermap.org"
private const val API_KEY_PARAM_NAME = "appid"
private const val API_KEY_VALUE = "0db5caf45f1986305c1379cb056d6d34"

@InstallIn(SingletonComponent::class)
@Module
object ApiModule {

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor() = Interceptor { chain ->
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
    @Singleton
    fun provideApiCache(context: Context): Cache {
        val cacheFile = File("${context.cacheDir.absolutePath}${File.pathSeparator}$API_CACHE_DIR")
        return Cache(cacheFile, API_CACHE_SIZE)
    }

    @Provides
    @Singleton
    fun provideHttpClient(interceptor: Interceptor, apiCache: Cache) = OkHttpClient.Builder()
        .connectTimeout(REST_CLIENT_CONNECTION_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .readTimeout(REST_CLIENT_READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .writeTimeout(REST_CLIENT_WRITE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .cache(apiCache)
        .addInterceptor(interceptor)
        .build()

    @Provides
    @Singleton
    fun provideRetrofit(httpClient: OkHttpClient, gson: Gson) = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create(gson))
        .baseUrl(BASE_URL)
        .client(httpClient)
        .build()

    @Provides
    @Singleton
    fun provideRestWeatherInterface(retrofit: Retrofit) = retrofit.create(RestWeather::class.java)

    @Provides
    @Singleton
    fun provideRestGeocodingInterface(retrofit: Retrofit) = retrofit.create(RestGeocoding::class.java)
}