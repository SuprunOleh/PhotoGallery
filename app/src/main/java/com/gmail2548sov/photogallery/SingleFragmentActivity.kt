package com.gmail2548sov.photogallery

import android.content.res.Configuration
import android.content.res.Configuration.ORIENTATION_UNDEFINED
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

abstract class SingleFragmentActivity : AppCompatActivity() {


    abstract fun createFragment(): Fragment

    val mFragmentManager = supportFragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment)
        val fragment: Fragment? = mFragmentManager.findFragmentById(R.id.fragment_container)

        if (fragment == null) {
            mFragmentManager.beginTransaction().add(R.id.fragment_container, createFragment()).commit()

        }


    }

}