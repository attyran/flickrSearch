package com.attyran.flickrsearch

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loadFragment(PhotoSearchFragment())
    }

    private fun loadFragment(fragment: androidx.fragment.app.Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction .replace(R.id.main_container, fragment)
        transaction.commit()
    }
}
