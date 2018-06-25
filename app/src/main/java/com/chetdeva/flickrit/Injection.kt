package com.chetdeva.flickrit

import com.chetdeva.flickrit.network.FlickrApiService
import com.chetdeva.flickrit.network.ApiClient
import com.chetdeva.flickrit.network.ImageClient
import com.chetdeva.flickrit.network.mapper.SearchMapper
import com.chetdeva.flickrit.search.SearchContract
import com.chetdeva.flickrit.search.SearchInteractor
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level.BODY
import java.util.concurrent.TimeUnit


/**
 * @author chetansachdeva
 */

object Injection {

    private val okHttpClient by lazy { provideOkHttpClient() }
    private val gson by lazy { GsonBuilder().create() }
    private val searchMapper by lazy { SearchMapper() }

    fun provideImageClient(): ImageClient {
        return ImageClient(provideApiClient(okHttpClient))
    }

    fun provideSearchInteractor(): SearchContract.Interactor {
        return SearchInteractor(provideFlickrApiService(okHttpClient, gson), searchMapper)
    }

    private fun provideFlickrApiService(client: OkHttpClient, gson: Gson): FlickrApiService {
        return FlickrApiService.getInstance(provideApiClient(client), gson)
    }

    private fun provideApiClient(client: OkHttpClient): ApiClient {
        return ApiClient.getInstance(client)
    }

    private fun provideOkHttpClient(): OkHttpClient {
        val logging = provideHttpLoggingInterceptor()
        return OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(logging)
                .build()
    }

    private fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply { level = BODY }
    }
}