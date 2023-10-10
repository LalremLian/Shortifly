package com.lazydeveloper.shortifly.interfaces

import android.os.Bundle
import android.view.View
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections

interface OnFragmentInteractionListener {

    fun gotoFragment(@IdRes destinationResId: Int){}

    fun gotoFragment(@IdRes destinationResId: Int, data: Bundle){}

    fun <T : Any> gotoFragment(@IdRes destinationResId: Int, data: T) {}

    fun gotoFragment(navDirections: NavDirections){}

    fun setFragment(bottomNavItemId: Int){}

    fun popBackStack(){}


}