package com.aprosoft.webseries

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.android.play.core.tasks.Task

class AppReviewActivity : AppCompatActivity() {
    private var reviewManager: ReviewManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_review)


        init()



    }

    private fun init() {
        reviewManager = ReviewManagerFactory.create(this@AppReviewActivity)
        showRateApp()
    }

    private fun showRateApp(){
        val request =reviewManager!!.requestReviewFlow()
        request.addOnCompleteListener { task: Task<ReviewInfo?> ->
            if (task.isSuccessful) {
                // We can get the ReviewInfo object
                val reviewInfo = task.result
                val flow =reviewManager!!.launchReviewFlow(this@AppReviewActivity, reviewInfo)
                flow.addOnCompleteListener { task1: Task<Void?>? -> }
            } else {
                // There was some problem, continue regardless of the result.
                // show native rate app dialog on error
                showRateAppFallbackDialog()
            }
        }
    }

    private fun showRateAppFallbackDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.rate_app_title)
            .setMessage(R.string.rate_app_message)
            .setPositiveButton(R.string.rate_btn_pos) { dialog, which -> }
            .setNegativeButton(
                R.string.rate_btn_neg
            ) { dialog, which -> }
            .setNeutralButton(
                R.string.rate_btn_nut
            ) { dialog, which -> }
            .setOnDismissListener(DialogInterface.OnDismissListener { dialog: DialogInterface? -> })
            .show()
    }

}