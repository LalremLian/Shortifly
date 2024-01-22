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
import com.lazydeveloper.shortifly.utils.extensions.onClick
import com.lazydeveloper.shortifly.utils.extensions.showBottomSheetDialog
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

        binding.floatingButton onClick {
            showBottomSheetDialog(R.layout.fragment_bottom_sheet)
        }

        binding.customHeader.circleImageProfile.setOnClickListener {
            navController.navigate(R.id.profileFragment2)
        }
    }

    fun setCustomHeaderVisibility(isVisible: Boolean) {
        binding.customHeader.toolbarLayout.visibility = if (isVisible) View.VISIBLE else View.GONE
        if (isVisible) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
        } else {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        }
        binding.floatingButton.visibility = if (isVisible) View.VISIBLE else View.GONE
        binding.bottomNavigationView.visibility = if (isVisible) View.VISIBLE else View.GONE
    }
}