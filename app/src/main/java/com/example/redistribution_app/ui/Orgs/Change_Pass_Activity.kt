package com.example.redistribution_app.ui.Orgs

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.redistribution_app.R
import com.example.redistribution_app.models.Org
import com.example.redistribution_app.ui.Ads.AdsActivity
import com.example.redistribution_app.utils.UserPreference
import com.lambdapioneer.argon2kt.Argon2Kt
import com.lambdapioneer.argon2kt.Argon2KtResult
import com.lambdapioneer.argon2kt.Argon2Mode
import io.realm.Realm
import io.realm.RealmConfiguration
import kotlinx.android.synthetic.main.activity_change__pass_.*
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*

class Change_Pass_Activity : AppCompatActivity() {

    lateinit var realm: Realm
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change__pass_)

        verify_pass_layout.visibility = View.VISIBLE
        new_password_layout.visibility = View.INVISIBLE

        val mIntent = intent
        val loggedOrgId = mIntent.getIntExtra("loggedOrg", -1)

//        Realm.init(this);
//        val config = RealmConfiguration.Builder()
//            .name("database.realm").build()
//        val realm = Realm.getInstance(config)

        realm = Realm.getDefaultInstance()

        realm.beginTransaction();
        var loggedOrg = realm.where(Org::class.java).equalTo("_id", loggedOrgId).findFirst()
        realm.commitTransaction();

        continue_button.setOnClickListener { view: View ->
            val userPreference = UserPreference(this)
            val userId = userPreference.getLoggedOrgId()

            if (loggedOrg != null){

                val passCheck = loggedOrg.password
                val entPassword = old_pass_veirfy.text.toString()

                // verify a password against an encoded string representation
                val argon2Kt = Argon2Kt()
                val charset = Charsets.UTF_8
                val passwordByteArray = entPassword.toByteArray(charset)
                val verificationResult : Boolean = argon2Kt.verify(
                    Argon2Mode.ARGON2_I,
                    passCheck,
                    passwordByteArray
                )
                if (verificationResult){

                    verify_pass_layout.visibility = View.INVISIBLE
                    new_password_layout.visibility = View.VISIBLE


                } else {
                    Toast.makeText(this,"wrong password",Toast.LENGTH_SHORT).show()
                }

                new_pass_accept.setOnClickListener { view: View ->
                    var newPass = new_pass.text.toString()
                    var newPass2 = new_pass2.text.toString()

                    if (newPass == newPass2) {

                        realm.beginTransaction();


                        //  org New password
                        var pass = ""
                        var salt = "tavomamagay"
                        pass = newPass
                        newPass = ""
                        newPass2 = ""

                        // initialize Argon2Kt and load the native library
                        val argon2Kt = Argon2Kt()

                        // make Byte arrays
                        val charset = Charsets.UTF_8
                        val passwordByteArray = pass.toByteArray(charset)
                        val saltByteArray = salt.toByteArray(charset)
                        pass = ""

                        //cia kaip atgal pervesti
                        //            println(byteArray.contentToString()) // [72, 101, 108, 108, 111]
                        //            println(byteArray.toString(charset)) // Hello


                        // hash a password
                        val hashResult : Argon2KtResult = argon2Kt.hash(
                            mode = Argon2Mode.ARGON2_I,
                            password = passwordByteArray,
                            salt = saltByteArray,
                            tCostInIterations = 5,
                            mCostInKibibyte = 65536
                        )

                        //check the hashing
                        //            Log.i("i", "Raw hash: ${hashResult.rawHashAsHexadecimal()}")
                        //            Log.i("i", "Encoded string: ${hashResult.encodedOutputAsString()}")
                        //
                        //
                        //            // verify a password against an encoded string representation
                        //            val verificationResult : Boolean = argon2Kt.verify(
                        //                Argon2Mode.ARGON2_I,
                        //                hashResult.encodedOutputAsString(),
                        //                passwordByteArray
                        //            )
                        //            Log.i("i", "verification res: ${verificationResult}")

                        loggedOrg.password = hashResult.encodedOutputAsString()

                        realm.commitTransaction();
                        Toast.makeText(this,"Password changed", Toast.LENGTH_SHORT).show()
                        //realm.close()
                        this.finish()

                    } else {
                        Toast.makeText(this,"Password confirmation incorrect", Toast.LENGTH_SHORT).show()
                    }
                }

            }

        }


        verify_pass_cancel.setOnClickListener { view: View ->
            this.finish()
        }

        new_pass_cancel.setOnClickListener { view: View ->
            this.finish()
        }
    }

    override fun onStop(){
        super.onStop()

        val openRealms = Realm.getGlobalInstanceCount(Realm.getDefaultConfiguration())
        if(openRealms > 0){
            for (i in 0..openRealms){
                realm.close()
            }
        }
    }
}