package com.example.redistribution_app.ui.Ads

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.DialogFragment
import com.example.redistribution_app.R
import com.example.redistribution_app.models.Ad
import com.example.redistribution_app.models.AdKey
import com.example.redistribution_app.models.Org
import com.example.redistribution_app.ui.BaseActivity
import com.example.redistribution_app.ui.Orgs.OrgInfoActivity
import io.realm.Realm
import io.realm.RealmList
import kotlinx.android.synthetic.main.activity_create_ad.*
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*


class CreateAdActivity : AppCompatActivity() {
    //calendar instance for datepicker
    lateinit var realm: Realm
    var cal = Calendar.getInstance()
    val dateSetListener =
        DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDateInView()
        }
    val myFormat = "yyyy-MM-dd" // mention the format you need
    val sdf = SimpleDateFormat(myFormat)
    val dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    //
    var adKeys = ArrayList<AdKey>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_ad)

        val mIntent = intent
        val loggedOrgId = mIntent.getIntExtra("loggedOrg", -1)



//        Realm.init(this);
//        val config = RealmConfiguration.Builder()
//            .name("database.realm").build()

        // This is how we initialise realm now!!!
        realm = Realm.getDefaultInstance()
        realm.beginTransaction();
        val loggedOrg = realm.where(Org::class.java).equalTo("_id", loggedOrgId).findFirst()
        val keyData = realm.where(AdKey::class.java).findAll()
       // Log.v("bliot keydata", keyData.toString())
        realm.commitTransaction()

        // Key search functionality
        var keys: ArrayList<String> = arrayListOf()
        for (key in keyData){
            keys.add(key.name)
           // selectKey(key.name)
        }
      //  Log.v("bliot keys", keys.toString())


        // Toodbar inflation
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        BaseActivity.getDrawer(this, toolbar, loggedOrgId);

        // Vienos dienos vs ilgesnio galiojimo masitas
        short_valid.setOnClickListener { view: View ->
            choose_ad_valid.visibility = View.INVISIBLE
            new_ad_valid.text = dtf.format(LocalDate.now().plusDays(1))
        }
        long_valid.setOnClickListener { view: View ->
            choose_ad_valid.visibility = View.VISIBLE
        }


        new_ad_accept.setOnClickListener { view: View ->


            realm.beginTransaction();

            val size = realm.where(Ad::class.java).count()
            val ad = realm.createObject(Ad::class.java,size)

            //datat from form
            ad.name = new_ad_name.text.toString()
            ad.valid = new_ad_valid.text.toString()
            ad.created = sdf.format(Date())
            Log.v("bliot", "is_splittable.isSelected"+is_splittable.isChecked)
            if (is_splittable.isChecked){
            ad.splittable = true
            }
            val foodTypeButton = food_type.checkedRadioButtonId
            var foodType = resources.getResourceEntryName(foodTypeButton)
            ad.foodType = foodType
            ad.comment = new_ad_comment.text.toString()
//            ad.creator.add(loggedOrg)
            loggedOrg?.createdAds?.add(ad)
            ad.creator = loggedOrg

            //default ad data
            ad.status = "AA"
            when (ad.creator?.type){
                "LO" -> ad.type = "AP"
                "VI" -> ad.type = "AS"
            }

            // Ad keys
            adKeys.forEach(){k: AdKey ->
                ad.keys?.add(k)
            }


            realm.commitTransaction()




            val intent = Intent(this, OrgInfoActivity::class.java)
            intent.putExtra("loggedOrg",loggedOrgId)
            intent.putExtra("selectedOrgId",loggedOrgId)
            realm.close()
            view.context.startActivity(intent)
            this.finish()

        }

        new_ad_cancel.setOnClickListener { view: View ->


            val intent = Intent(this, AdsActivity::class.java)
            intent.putExtra("loggedOrg",loggedOrgId)
            view.context.startActivity(intent)
            realm.close()
            this.finish()

        }

        // when you click on the button, show DatePickerDialog that is set with OnDateSetListener
        choose_ad_valid.setOnClickListener {
          val dpd = DatePickerDialog(this@CreateAdActivity,
                dateSetListener,
                // set DatePickerDialog to point to today's date when it loads up
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH))
            dpd.getDatePicker().setMinDate(cal.getTimeInMillis());
            dpd.show()

        }

        // Keyworf filter button
        var activityName: String = this.componentName.toString()
        new_ad_new_key.setOnClickListener { view: View ->
            showDialog(AdKeysFragment(), keys, activityName)
        }

    }

    public fun showDialog(dialogFragment: DialogFragment, keys: ArrayList<String>, activityName: String){
        val bundle = Bundle()
        bundle.putStringArrayList("keys", keys)
//        bundle.putStringArrayList("skeys", keys)
        Log.v("bliot", activityName)
        bundle.putString("activity", activityName)
        val dialog = dialogFragment
        dialog.arguments = bundle
        dialog.show(supportFragmentManager, "new ad key dialog")
    }

    fun applyKeys(sKeys: ArrayList<String>, keys: ArrayList<String>){
        // Get existing keys
        var rKeys = realm.where(AdKey::class.java).findAll()
        var rKeyNames = ArrayList<String>()
        rKeys.forEach(){rk: AdKey->
            rKeyNames.add(rk.name)
        }

        // Add new keys to DB if they were created
        keys.forEach(){k:String->
            if(!rKeyNames.contains(k)){
                realm.beginTransaction()
                var newKey = realm.createObject(AdKey::class.java, rKeys.size)
                newKey.name = k
                realm.commitTransaction()
            }
        }

        // Add selected keys to global array list
        sKeys.forEach {k:String ->
            val adKey = realm.where(AdKey::class.java).equalTo("name", k).findFirst()
            if(adKey!= null){
                adKeys.add(adKey)
            }

        }
    }

    private fun updateDateInView() {

        new_ad_valid.text = sdf.format(cal.getTime())

    }

    override fun onStop(){
        super.onStop()

//        val openRealms = Realm.getGlobalInstanceCount(Realm.getDefaultConfiguration())
//        if(openRealms > 0){
//            for (i in 0..openRealms){
                realm.close()
//            }
//        }
    }


}