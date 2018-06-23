package com.chetdeva.flickrit

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.chetdeva.flickrit.search.SearchFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            navigateToSearch()
        }
    }

    /**
     * navigates to [SearchFragment]
     */
    private fun navigateToSearch() {
        val searchFragment = SearchFragment()
        supportFragmentManager.beginTransaction()
                .add(android.R.id.content, searchFragment)
                .commit()
    }
}
