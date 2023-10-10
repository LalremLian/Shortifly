package com.lazydeveloper.shortifly.ui.base

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.annotation.*
import androidx.appcompat.app.AppCompatActivity
import com.lazydeveloper.shortifly.R


@Keep
abstract class KotlinBaseActivity: AppCompatActivity(), KotlinBaseListener {

    private var isDialogShow: Boolean? = false

    override fun showProgress() {
        try {
            progress?.hide()
            isDialogShow = true
            progress?.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun hideProgress() {
        try {
            if (this.isDialogShow == true) {
                progress?.hide()
                isDialogShow = false
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private var progress: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        initDialog()
    }

    override fun onPause() {
        super.onPause()
        progress?.dismiss()
    }
//    private fun initDialog() {
//        progress = Dialog(this)
//        progress?.setContentView(R.layout.progress_layout)
//        progress?.setCancelable(false)
//        progress?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//    }

}
