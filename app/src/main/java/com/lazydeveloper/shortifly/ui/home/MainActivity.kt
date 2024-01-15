package com.lazydeveloper.shortifly.ui.home

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.lazydeveloper.shortifly.R
import com.lazydeveloper.shortifly.databinding.ActivityMainBinding
import com.lazydeveloper.shortifly.ui.fragments.BottomSheetFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initial()
    }

    private fun initial() {
        binding.floatingButton.drawable?.setTint(Color.WHITE)

        val customHeader = binding.customHeader

        val window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = resources.getColor(R.color.black)

        customHeader.imgSearch.setOnClickListener {
            Toast.makeText(this, "Search", Toast.LENGTH_SHORT).show()
        }

        val bottomNavigationView = binding.bottomNavigationView
        val navController = findNavController(R.id.nav_host_fragment)
        bottomNavigationView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.shortsFragment2) {
                customHeader.toolbarLayout.visibility = View.GONE
            } else {
                customHeader.toolbarLayout.visibility = View.VISIBLE
            }
        }

        binding.floatingButton.setOnClickListener {
            val bottomSheetFragment = BottomSheetFragment()
            bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)
        }

        binding.customHeader.circleImageProfile.setOnClickListener {
            navController.navigate(R.id.profileFragment2)
        }
    }

}