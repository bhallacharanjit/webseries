package com.aprosoft.webseries.User

import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Vibrator
import android.util.Log
import android.view.View
import android.view.animation.CycleInterpolator
import android.view.animation.TranslateAnimation
import android.widget.Toast
import com.aprosoft.webseries.MainActivity
import com.aprosoft.webseries.R
import com.aprosoft.webseries.Retrofit.ApiClient
import com.aprosoft.webseries.Shared.Singleton
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_login.*
import okhttp3.ResponseBody
import org.json.JSONArray
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    var fcmtoken:String?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val vibrate: Vibrator = this.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        if (Singleton().getUserFromSharedPrefrence(this)== null){

        }else{
            intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
            this.finish()
        }

        tv_goToRegister.setOnClickListener {
            intent = Intent(this,SignupActivity::class.java)
            startActivity(intent)

            this.finish()
        }

        tv_forgotPassword.setOnClickListener {
            intent = Intent(this,ForgotPasswordActivity::class.java)
            startActivity(intent)
        }


        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("TAG", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            fcmtoken = task.result

            // Log and toast
//            val msg = getString(R.string.msg_token_fmt, token)
            Log.d("TAG", fcmtoken)
            //Toast.makeText(baseContext, fcmtoken, Toast.LENGTH_SHORT).show()
        })



        btn_login.setOnClickListener {
            when{
                et_UserEmail.text.toString()=="" ->{
                    et_UserEmail.startAnimation(shakeError())
                    vibrate.vibrate(100)
                    et_UserEmail.background.setColorFilter(resources.getColor(R.color.red), PorterDuff.Mode.SRC_ATOP)
                }
                et_UserPassword.text.toString() ==""->{
                    et_UserPassword.startAnimation(shakeError())
                    vibrate.vibrate(100)
                    et_UserPassword.background.setColorFilter(resources.getColor(R.color.red), PorterDuff.Mode.SRC_ATOP)
                }
                else ->{
                    login()
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

    private fun login(){

        val kProgressHUD = Singleton().createLoading(this,"Logging In...","")
//        avi.visibility = View.VISIBLE
        val loginParams = HashMap<String, String>()
        loginParams["email"]= et_UserEmail.text.toString()
        loginParams["password"] = et_UserPassword.text.toString()
        loginParams["fcmToken"]= fcmtoken.toString()

        val call:Call<ResponseBody> = ApiClient.getClient.login(loginParams)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
//                avi.visibility = View.GONE
                kProgressHUD?.dismiss()
                val res = response.body()?.string()
                val jsonArray = JSONArray(res)
                val jsonObject = jsonArray.getJSONObject(0)
                val success = jsonObject.getBoolean("success")
                val msg = jsonObject.getString("msg")
                if (success) {
                    //Toast.makeText(applicationContext, "$msg", Toast.LENGTH_SHORT).show()
                    Singleton().setSharedPrefrence(this@LoginActivity, jsonObject)
                    intent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                    this@LoginActivity.finish()
                } else {
//                    avi.visibility = View.GONE
                    Toast.makeText(applicationContext, "$msg", Toast.LENGTH_SHORT).show()
                }

            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
//                avi.visibility = View.GONE
                kProgressHUD?.dismiss()
                Toast.makeText(applicationContext, "$t", Toast.LENGTH_SHORT).show()
            }

        })



    }
}