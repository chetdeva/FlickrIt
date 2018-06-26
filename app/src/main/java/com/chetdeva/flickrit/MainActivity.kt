package com.chetdeva.flickrit

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v7.app.AppCompatActivity
import com.chetdeva.flickrit.search.SearchContract
import com.chetdeva.flickrit.search.SearchFragment
import com.chetdeva.flickrit.search.SearchPresenter
import com.chetdeva.flickrit.util.extension.replaceFragmentInActivity
import com.chetdeva.flickrit.util.extension.setupActionBar

class MainActivity : AppCompatActivity() {

    private lateinit var searchPresenter: SearchContract.Presenter

    private val SPLASH_TIMOUT_IN_MILLIS: Long = 1500

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupActionBar(R.id.toolbar)

        navigateToSplash()
        Handler(Looper.getMainLooper()).postDelayed({
            navigateToSearch()
        }, SPLASH_TIMOUT_IN_MILLIS)
    }

    /**
     * navigate to [SplashFragment]
     */
    private fun navigateToSplash() {
        val didFindSplashFragment = supportFragmentManager.findFragmentById(R.id.content) as? SplashFragment?
        didFindSplashFragment ?: SplashFragment.newInstance()
                .also { fragment ->
                    replaceFragmentInActivity(fragment, R.id.content)
                }
    }

    /**
     * navigate to [SearchFragment]
     */
    private fun navigateToSearch() {
        val didFindSearchFragment = supportFragmentManager.findFragmentById(R.id.content) as? SearchFragment?
        val searchFragment = didFindSearchFragment ?: SearchFragment.newInstance()
                .also { fragment ->
                    replaceFragmentInActivity(fragment, R.id.content)
                }

        inject(searchFragment)
    }

    /**
     * inject dependencies required for [SearchFragment]
     */
    private fun inject(searchFragment: SearchFragment) {
        val interactor = Injection.provideSearchInteractor()
        val imageClient = Injection.provideImageClient()
        searchPresenter = SearchPresenter(interactor, imageClient, searchFragment)
    }

}
