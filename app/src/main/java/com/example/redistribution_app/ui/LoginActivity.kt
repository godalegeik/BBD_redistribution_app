package com.example.redistribution_app.ui

//import com.google.android.gms.auth.api.credentials.Credentials


//import com.example.redistribution_app.models.test
//import com.google.android.gms.tasks.Task
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.redistribution_app.R
import com.example.redistribution_app.models.Org
import com.example.redistribution_app.ui.Ads.AdsActivity
import com.example.redistribution_app.utils.UserPreference
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.lambdapioneer.argon2kt.Argon2Kt
import com.lambdapioneer.argon2kt.Argon2Mode
import io.realm.Realm
import io.realm.mongodb.App
import io.realm.mongodb.AppConfiguration
import io.realm.mongodb.Credentials
import io.realm.mongodb.sync.SyncConfiguration
import io.realm.mongodb.sync.SyncSession
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {

    private lateinit var client: GoogleSignInClient
    lateinit var uiThreadRealm: Realm
    lateinit var redApp: App
    private lateinit var realmTest: Realm
    lateinit var realm: Realm


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


// Staying logged in with user prefference + id USED UNCOMMENT userPreference.setLoggedOrgId(checkedOrg.id) DOWM BELOW
        val prefs = getApplicationContext().getSharedPreferences("name", MODE_PRIVATE);
        var isLoggedIn = prefs.getBoolean("isLoggedIn", false);
        val userPreference = UserPreference(this)
        var userId = -1
        userId = userPreference.getLoggedOrgId()

     //    Initialising REALM local db and REALM APP on Atlas cluster
        Realm.init(this);
//        var localConfig = RealmConfiguration.Builder()
//            .name("s__partition.realm")
//            .build()
        //realm = Realm.getDefaultInstance()

        // save this configuration as the default for this entire app so other activities and threads can open their own realm instances
        // Realm.setDefaultConfiguration(config)

        val handler =
                SyncSession.ClientResetHandler { session, error ->
                    Log.e("EXAMPLE", "Client Reset required for: ${session.configuration.serverUrl} for error: $error")
                }

        val appID: String = "redistributionbakdb-ukpyz"
        val redApp = App(
                AppConfiguration.Builder(appID)
                        //.defaultClientResetHandler(handler)
                        .build())

        // Admin user login into realm db
        val credentials =
                Credentials.emailPassword(
                        "admin@admin.com",
                        "Admin1"
                )


        // Login to mongodb realm app and get data!
        redApp.loginAsync(
                credentials
        ) { result ->
            if (result.isSuccess) {
                Log.v("bliot", "Logged In Succ")
            } else {
                Log.v("bliot", "Failed to Login")
            }

            val user = redApp.currentUser()
            val config = SyncConfiguration.Builder(user, "_partition")
                    .allowQueriesOnUiThread(true)
                    .allowWritesOnUiThread(true)
                    .waitForInitialRemoteData()
                    .build()
            Realm.setDefaultConfiguration(config)


            Realm.getInstanceAsync(config, object : Realm.Callback() {
                override fun onSuccess(realmA: Realm) {
                    realm = realmA
                    realm.close()
//                    check = realm.where(Org::class.java).findAll()
                   // Log.v("bliot-local", realm.configuration.toString())
                    //       check if user is already logged in useing SharedPreferences

                }
            })

            if (isLoggedIn and (userId != -1)) {

                val intent = Intent(this, AdsActivity::class.java)
                intent.putExtra("loggedOrg", userId)
                this.startActivity(intent)
                this.finish()
                //  onLogin(this, userId)

            }

        }





        // User is not yet logged in, Login button is clicked
        login_button.setOnClickListener { view: View ->


           // realm = Realm.getDefaultInstance()



            val getEmail = login_email.text.toString()


            Realm.getDefaultInstance().use { realmInstance -> realmInstance.executeTransaction { realm ->
                //realm.insertOrUpdate(ad)
                var checkedOrg  = realm.where(Org::class.java).equalTo("email", getEmail).findFirst()

               // Log.v("bliot", checkedOrg?.password.toString())


                if (checkedOrg != null) {
                    val passCheck = checkedOrg.password
                    val entPassword = login_password.text.toString()

                    // verify a password against an encoded string representation
                    val argon2Kt = Argon2Kt()
                    val charset = Charsets.UTF_8
                    val passwordByteArray = entPassword.toByteArray(charset)
                    val verificationResult: Boolean = argon2Kt.verify(
                            Argon2Mode.ARGON2_I,
                            passCheck,
                            passwordByteArray
                    )
                    if (verificationResult) {

                        val editor = getSharedPreferences("name", Context.MODE_PRIVATE).edit()
                        editor.putString("email", checkedOrg.email)
                        editor.putString("password", checkedOrg.password)
                        editor.putBoolean("isLoggedIn", true)
                        //any other detail you want to save
                        editor.apply()

                        userPreference.setLoggedOrgId(checkedOrg._id)


                        val intent = Intent(this, AdsActivity::class.java)

                        intent.putExtra("loggedOrg", checkedOrg._id)
                        view.context.startActivity(intent)
                        //realm.close()
                        this.finish()
                    } else {
                        Toast.makeText(this, "wrong password", Toast.LENGTH_SHORT).show()
                    }

                } else {
                    Toast.makeText(this, "wrong email", Toast.LENGTH_SHORT).show()
                }
                } }



        }


        register_button.setOnClickListener { view: View ->
            val intent = Intent(this, RegisterActivity::class.java)
            view.context.startActivity(intent)
             realm.close()
            this.finish()
        }

        guest_button.setOnClickListener { view: View ->

            val intent = Intent(this, AdsActivity::class.java)
            view.context.startActivity(intent)
            realm.close()
            this.finish()

        }
    }

//    private fun onLogin(con: Context, orgId: Int){
//        val intent = Intent(this, AdsActivity::class.java)
//        intent.putExtra("loggedOrg", orgId)
//        realm.close()
//        con.startActivity(intent)
//        this.finish()
//    }

     fun onFinish() {
        super.onDestroy()

        // if a user hasn't logged out when we close the realm, still need to explicitly close
        realm.close()
    }
}
