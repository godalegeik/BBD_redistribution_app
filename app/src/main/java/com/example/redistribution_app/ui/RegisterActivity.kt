package com.example.redistribution_app.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.redistribution_app.R
import com.example.redistribution_app.models.Org
import com.example.redistribution_app.ui.Ads.AdsActivity
import com.example.redistribution_app.utils.UserPreference
import com.lambdapioneer.argon2kt.Argon2Kt
import com.lambdapioneer.argon2kt.Argon2KtResult
import com.lambdapioneer.argon2kt.Argon2Mode
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.mongodb.App
import io.realm.mongodb.AppConfiguration
import io.realm.mongodb.User
import io.realm.mongodb.mongo.MongoClient
import io.realm.mongodb.mongo.MongoCollection
import io.realm.mongodb.mongo.MongoDatabase
import kotlinx.android.synthetic.main.activity_register.*
import org.bson.Document


class RegisterActivity : AppCompatActivity() {

    lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)




            val cities = arrayOf("Vilnius", "Kaunas", "Klaipėda", "Alytus", "Visaginas")
            var spinner = miestas_spinner
            //var cities = realm.where(City::class.java).findAll();
            var cityNames: MutableList<String> = mutableListOf()
            cities.forEach(){ city ->
                cityNames.add(city)
            }
            spinner.adapter =
                ArrayAdapter(
                    this,
                    android.R.layout.simple_spinner_dropdown_item,
                    cityNames
                )






        reg_org_accept.setOnClickListener {view: View ->

            val appID : String = "red_app_2-lfkew"
            val redApp = App(
                AppConfiguration.Builder(appID)
                    .build())

//            Realm.init(this);
//            val config = RealmConfiguration.Builder()
//                    .name("database.realm").build()
//            val realm = Realm.getInstance(config)

            realm = Realm.getDefaultInstance()





            // Password deal
            var orgPass = reg_org_password.text.toString()
            var orgPass2 = reg_org_password2.text.toString()

            if (orgPass == orgPass2) {

                realm.beginTransaction();

                    // organisation ID
                    val size = realm.where(Org::class.java).count().toInt()
                    //Create org
                    val org = realm.createObject(Org::class.java,size)

                    //New org data
                    val orgTypeButton = org_type.checkedRadioButtonId
                    var orgType = resources.getResourceEntryName(orgTypeButton)
                    org.type= orgType
                    var orgName = reg_org_name.text.toString()
                    org.name= orgName
                    var orgCode = reg_org_code.text.toString()
                    org.code= orgCode
                    var orgPerson = reg_org_person.text.toString()
                    org.person= orgPerson
                    var orgAdress = reg_org_adress.text.toString()
                    org.adress= orgAdress
                    var orgCity = miestas_spinner.getSelectedItem().toString()
                    org.city= orgCity
                    var orgEmail = reg_org_email.text.toString()
                    org.email= orgEmail
                    var orgPhone = reg_org_phone.text.toString()
                    org.phone= orgPhone
                    var orgStatus = "OV"
                    org.status= orgStatus

                    // New org password
                    var pass = ""
                    var salt = "tavomamagay"
                    pass = orgPass

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

                    org.password = hashResult.encodedOutputAsString()
                   // val orgPass = org.password


                realm.commitTransaction();
                realm.close()

//                Realm.getInstanceAsync(config, object : Realm.Callback() {
//                    override fun onSuccess(realmA: Realm) {
//                        realmA.executeTransaction { r: Realm ->
//                            r.insert(org)
//                        }
//                        // Don't forget to close your realm!
//                        realmA.close()
//
//                    }
//                })

                //creating a user for realm db app
//                redApp.getEmailPassword().registerUserAsync(org.email, org.password, it ->{
//                    if(it.isSuccess()){ } else { }
//                })
//                redApp.getEmailPassword().registerUserAsync(org.email, org.password) { it ->
//                    if (it.isSuccess()) {
//                        Log.v("User", "Registered with email successfully")
//                    } else {
//                        Log.v("User", "Registration Failed")
//                    }
//                }
//                val credentials1 = Credentials.emailPassword(
//                    org.email,
//                    org.password
//                )
//
//                redApp.loginAsync(
//                    credentials1
//                ) { result ->
//                    if (result.isSuccess) {
//                        Log.v("bliot", "Logged In Succ")
//                    } else {
//                        Log.v("bliot", "Failed to Login")
//                    }
//                }
//                var mongoDatabase: MongoDatabase? = null
//                var mongoClient: MongoClient? = null
//                val user: User? = redApp.currentUser()
//                mongoClient = user?.getMongoClient("mongodb-atlas")
//                mongoDatabase = mongoClient?.getDatabase("database")
//                val mongoCollection: MongoCollection<Document> =
//                    mongoDatabase!!.getCollection("RedData")
//
//                mongoCollection.insertOne(
//                    Document("userid", org.id)
//                        .append("Org", org)
//                ).getAsync { result ->
//                    if (result.isSuccess) {
//                        Log.v("Data", "Data Inserted Successfully")
//                    } else {
//                        Log.v("Data", "Error:" + result.error.toString())
//                    }
//                }

//                 taskApp.emailPassword.registerUserAsync(orgName, orgPass) {
//                // re-enable the buttons after user registration returns a result
////                createUserButton.isEnabled = true
////                loginButton.isEnabled = true
////                if (!it.isSuccess) {
////                    onLoginFailed("Could not register user.")
////                    Log.e("bliot", "Error: ${it.error}")
////                } else {
////                    Log.i("bliot", "Successfully registered user.")
////                    // when the account has been created successfully, log in to the account
////                    login(false)
////                }
//            }
                val userPreference = UserPreference(this)
                userPreference.setLoggedOrgId(size)

                val intent = Intent(this, AdsActivity::class.java)
                intent.putExtra("loggedOrg", size)
                view.context.startActivity(intent)
                this.finish()
            } else {
                Toast.makeText(this,"Password confirmation incorrect", Toast.LENGTH_SHORT).show()
            }





        }

        reg_org_cancel.setOnClickListener { view: View ->

            val intent = Intent(this, LoginActivity::class.java)
            view.context.startActivity(intent)
            this.finish()

        }





    }
}