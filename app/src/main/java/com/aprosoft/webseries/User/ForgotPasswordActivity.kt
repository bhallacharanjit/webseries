package com.aprosoft.webseries.User

import android.content.Context
import android.graphics.PorterDuff
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Vibrator
import android.view.animation.CycleInterpolator
import android.view.animation.TranslateAnimation
import com.aprosoft.webseries.R
import kotlinx.android.synthetic.main.activity_forgot_password.*
import kotlinx.android.synthetic.main.activity_login.*

class ForgotPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        val vibrate: Vibrator = this.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        btn_forgotPassword.setOnClickListener {
            when{
                et_ForgotUserEmail.text.toString()=="" ->{
                    et_ForgotUserEmail.startAnimation(shakeError())
                    vibrate.vibrate(100)
                    et_ForgotUserEmail.background.setColorFilter(resources.getColor(R.color.red), PorterDuff.Mode.SRC_ATOP)
                }
                else ->{
//                    login()
                }
            }
        }
    }
    private fun shakeError(): TranslateAnimation? {
        val shake = TranslateAnimation(0F, 10F, 0F, 0F)
        shake.duration = 500
        shake.interpolator = CycleInterpolator(7F)
        return shake
    }



}