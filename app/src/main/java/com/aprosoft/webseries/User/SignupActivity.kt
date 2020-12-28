package com.aprosoft.webseries.User

import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Vibrator
import android.util.Log
import android.view.animation.CycleInterpolator
import android.view.animation.TranslateAnimation
import android.widget.Toast
import com.aprosoft.webseries.Fragments.jsonObject
import com.aprosoft.webseries.R
import com.aprosoft.webseries.Retrofit.ApiClient
import com.aprosoft.webseries.Shared.Singleton
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_signup.*
import kotlinx.android.synthetic.main.activity_signup.et_UserEmail
import kotlinx.android.synthetic.main.activity_signup.et_UserPassword
import okhttp3.ResponseBody
import org.json.JSONArray
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val vibrate: Vibrator = this.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        tv_loginNow.setOnClickListener {
            intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
            this.finish()
        }

        btn_signup.setOnClickListener {

            when{
                et_UserName.text.toString()=="" ->{
                    et_UserName.startAnimation(shakeError())
                    vibrate.vibrate(100)
                    et_UserName.background.setColorFilter(resources.getColor(R.color.red), PorterDuff.Mode.SRC_ATOP)
                }
                et_UserEmail.text.toString() ==""->{
                    et_UserEmail.startAnimation(shakeError())
                    vibrate.vibrate(100)
                    et_UserEmail.background.setColorFilter(resources.getColor(R.color.red), PorterDuff.Mode.SRC_ATOP)
                }
                et_UserPassword.text.toString()=="" ->{
                    et_UserPassword.startAnimation(shakeError())
                    vibrate.vibrate(100)
                    et_UserPassword.background.setColorFilter(resources.getColor(R.color.red), PorterDuff.Mode.SRC_ATOP)
                }
                else ->{
                    signup()
                }
            }
        }

    }

    fun shakeError(): TranslateAnimation? {
        val shake = TranslateAnimation(0F, 10F, 0F, 0F)
        shake.duration = 500
        shake.interpolator = CycleInterpolator(7F)

        return shake
    }

    private fun signup(){

        val kProgressHUD = Singleton().createLoading(this,"Loading...","")
        val signupParams = HashMap<String,String>()
        signupParams["name"]=et_UserName.text.toString()
        signupParams["email"] =et_UserEmail.text.toString()
        signupParams["password"]=  et_UserPassword.text.toString()

        val call:Call<ResponseBody> = ApiClient.getClient.signup(signupParams)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                kProgressHUD?.dismiss()
                val res = response.body()?.string()
                val jsonArray = JSONArray(res)
                val jsonObject = jsonArray.getJSONObject(0)
                val success = jsonObject.getBoolean("success")
                if (success) {
                    //Toast.makeText(applicationContext, "success", Toast.LENGTH_SHORT).show()
                    intent = Intent(this@SignupActivity, LoginActivity::class.java)
                    startActivity(intent)
                    this@SignupActivity.finish()
                } else {
//                    Toast.makeText(applicationContext, " not success", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(applicationContext, "$t", Toast.LENGTH_SHORT).show()
                Log.d("error","$t")
                kProgressHUD?.dismiss()
            }
        })
    }
}