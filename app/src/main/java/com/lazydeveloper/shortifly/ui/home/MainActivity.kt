package com.lazydeveloper.shortifly.ui.home

import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.lazydeveloper.shortifly.R
import com.lazydeveloper.shortifly.SearchListViewModel
import com.lazydeveloper.shortifly.coroutine.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

private val viewModel: SearchListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = this.resources.getColor(R.color.black)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        val navController = findNavController(R.id.nav_host_fragment)
        bottomNavigationView.setupWithNavController(navController)


        lifecycleScope.launch {
//            fetchFlowData()
        }
    }

    private suspend fun fetchFlowData(){
        viewModel.getFlowData().collect{ resource ->
            when (resource) {
                is Resource.Loading -> {
                    // Handle loading state (e.g., show a progress bar)
                }
                is Resource.Success -> {
                    val data = resource.data

                    Log.e("MainActivity", "fetchFlowData: $data" )
//                    postListAdapter.submitList(resource.data)
                }
                is Resource.Error -> {
                    // Handle error state (e.g., show an error message)
//                    val errorMessage = resource.message // Access the error message here
                    // Display the error message to the user
                }
            }
        }
    }
}