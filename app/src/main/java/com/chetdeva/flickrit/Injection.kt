package com.chetdeva.flickrit

import android.content.Context
import com.chetdeva.flickrit.network.ApiClient
import com.chetdeva.flickrit.network.FlickrApiService
import com.chetdeva.flickrit.network.ImageDownloader
import com.chetdeva.flickrit.network.mapper.SearchMapper
import com.chetdeva.flickrit.search.SearchContract
import com.chetdeva.flickrit.search.SearchInteractor
import com.chetdeva.flickrit.util.executor.AppExecutors
import com.chetdeva.flickrit.util.executor.DiskIOThreadExecutor
import com.chetdeva.flickrit.util.image.ImageBitmapLoader
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level.BODY
import java.util.concurrent.TimeUnit


/**
 * Singleton holder to inject dependencies wherever needed
 *
 * @author chetansachdeva
 */

object Injection {

    private val imageOkHttpClient by lazy { OkHttpClient.Builder().build() }
    private val okHttpClient by lazy { okHttpClient() }
    private val gson by lazy { GsonBuilder().create() }
    private val searchMapper by lazy { SearchMapper() }
    private val appExecutors by lazy { AppExecutors(DiskIOThreadExecutor()) }

    fun provideSearchInteractor(): SearchContract.Interactor {
        return SearchInteractor(provideFlickrApiService(okHttpClient, gson), appExecutors, searchMapper)
    }

    private fun provideFlickrApiService(client: OkHttpClient, gson: Gson): FlickrApiService {
        return FlickrApiService.getInstance(provideApiClient(client), gson)
    }

    private fun provideApiClient(client: OkHttpClient): ApiClient {
        return ApiClient.getInstance(client)
    }

    private fun okHttpClient(): OkHttpClient {
        val logging = httpLoggingInterceptor()
        return OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(logging)
                .build()
    }

    private fun httpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply { level = BODY }
    }

    fun provideImageBitmapLoader(context: Context): ImageBitmapLoader {
        return ImageBitmapLoader.getInstance(context, appExecutors)
    }

    fun provideImageDownloader(): ImageDownloader {
        return ImageDownloader(provideApiClient(imageOkHttpClient))
    }
}