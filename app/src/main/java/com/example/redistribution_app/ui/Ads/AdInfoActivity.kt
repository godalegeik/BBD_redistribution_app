package com.example.redistribution_app.ui.Ads

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.redistribution_app.R
import com.example.redistribution_app.models.Ad
import com.example.redistribution_app.models.AdKey
import com.example.redistribution_app.models.Org
import com.example.redistribution_app.ui.BaseActivity
import io.realm.Realm
import io.realm.RealmConfiguration
import kotlinx.android.synthetic.main.activity_ad_info.*
import kotlinx.android.synthetic.main.activity_create_ad.*
import java.text.SimpleDateFormat
import java.util.*


class AdInfoActivity : AppCompatActivity() {

    lateinit var realm: Realm
    var cal = Calendar.getInstance()
    val dateSetListener =
        DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDateInView()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ad_info)

        val mIntent = intent
        val selectedAdId = mIntent.getIntExtra("selectedAdId", -1)
        val loggedOrgId = mIntent.getIntExtra("loggedOrg", -1)
        //Toast.makeText(this,loggedOrgId, Toast.LENGTH_SHORT).show()


//        Realm.init(this);
//        val config = RealmConfiguration.Builder()
//                .name("database.realm").build()
//        val realm = Realm.getInstance(config)

        realm = Realm.getDefaultInstance()

        realm.beginTransaction();

        var selectedAd: Ad? = realm.where(Ad::class.java).equalTo("_id", selectedAdId).findFirst();
        val loggedOrg = realm.where(Org::class.java).equalTo("_id", loggedOrgId).findFirst()


        realm.commitTransaction()

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        BaseActivity.getDrawer(this, toolbar, loggedOrgId);


        var adName = findViewById<TextView>(R.id.ad_info_name)
        adName.text = selectedAd?.name
        var adValid = findViewById<TextView>(R.id.ad_info_valid2)
        adValid.text = selectedAd?.valid
        val adCreated = findViewById<TextView>(R.id.ad_info_created2)
        adCreated.text = selectedAd?.created.toString()
        val adOrg = findViewById<TextView>(R.id.ad_info_org_name2)
        adOrg.text = selectedAd?.creator?.name.toString()
        val adAdress = findViewById<TextView>(R.id.ad_info_adress)
        adAdress.text = selectedAd?.creator?.adress.toString()
        val adEmail = findViewById<TextView>(R.id.ad_info_email2)
        adEmail.text = selectedAd?.creator?.email.toString()
        val adPhone = findViewById<TextView>(R.id.ad_info_phone)
        adPhone.text = selectedAd?.creator?.phone.toString()
        val adStatus = findViewById<TextView>(R.id.ad_info_status)
        adStatus.text = selectedAd?.status.toString()
        //Log.i("bliot", selectedAd?.creator.toString())
        var adComment = findViewById<TextView>(R.id.ad_info_comment)
        adComment.text = selectedAd?.comment
        var adKeys = findViewById<TextView>(R.id.ad_info_keys)
        var it = 0
        adKeys.text = ""
        selectedAd?.keys?.forEach(){k: AdKey ->
            if(it % 2 == 0){
                adKeys.text = adKeys.text.toString() + " #"+k.name + "\n"
                it++
            } else {
                adKeys.text = adKeys.text.toString() + " #"+k.name
                it++
            }
        }


        val adNameE = findViewById<EditText>(R.id.ad_info_nameE)
        val adValidE = findViewById<TextView>(R.id.ad_info_validE)
        val adCommentE = findViewById<EditText>(R.id.ad_info_commentE)


        fun updateAdInfoButtons() {
            adStatus.text = selectedAd?.status
            if (selectedAd?.creator?._id == loggedOrgId && selectedAd?.status == "AA") {

                ad_info_complete.visibility = View.VISIBLE
                ad_info_edit.visibility = View.VISIBLE
                ad_info_reserve.visibility = View.INVISIBLE
                ad_info_unreserve.visibility = View.INVISIBLE

            } else {
                ad_info_complete.visibility = View.INVISIBLE
                ad_info_edit.visibility = View.INVISIBLE
                when (selectedAd?.status) {

                    "AA" -> {

                        ad_info_reserve.visibility = View.VISIBLE
                        ad_info_unreserve.visibility = View.INVISIBLE
                    }

                    "RA" -> {
                        if (selectedAd?.selector?._id == loggedOrgId) {

                            ad_info_reserve.visibility = View.INVISIBLE
                            ad_info_unreserve.visibility = View.VISIBLE
                        } else {

                            ad_info_reserve.visibility = View.INVISIBLE
                            ad_info_unreserve.visibility = View.INVISIBLE
                        }
                    }
                    "IA" -> {
                        ad_info_complete.visibility = View.INVISIBLE
                        ad_info_edit.visibility = View.INVISIBLE
                        ad_info_reserve.visibility = View.INVISIBLE
                        ad_info_unreserve.visibility = View.INVISIBLE
                    }
                    "EA" -> {
                        ad_info_complete.visibility = View.INVISIBLE
                        ad_info_edit.visibility = View.INVISIBLE
                        ad_info_reserve.visibility = View.INVISIBLE
                        ad_info_unreserve.visibility = View.INVISIBLE
                    }
//                    "CA" -> {
//                    }
                    "DA" -> {
                    }
                }
            }
        }

        updateAdInfoButtons()
        ad_info_text.visibility = View.VISIBLE
        ad_edit_text.visibility = View.INVISIBLE

        ad_info_reserve.setOnClickListener { view: View ->
            realm.beginTransaction()
            selectedAd?.selector = loggedOrg
            selectedAd?.status = "RA"
            loggedOrg?.selectedAds?.add(selectedAd)
            realm.commitTransaction()
            updateAdInfoButtons()

           }

        // when you click on the button, show DatePickerDialog that is set with OnDateSetListener
        choose_ad_valid2.setOnClickListener {
            val dpd = DatePickerDialog(this@AdInfoActivity,
                dateSetListener,
                // set DatePickerDialog to point to today's date when it loads up
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH))
            dpd.getDatePicker().setMinDate(cal.getTimeInMillis());
            dpd.show()

        }

        ad_info_unreserve.setOnClickListener { view: View ->
            realm.beginTransaction()
            selectedAd?.selector = null
            selectedAd?.status = "AA"
            loggedOrg?.selectedAds?.remove(selectedAd)
            realm.commitTransaction()
            updateAdInfoButtons()
        }

        ad_info_complete.setOnClickListener { view: View ->
            realm.beginTransaction()
            selectedAd?.status = "IA"
            realm.commitTransaction()
            updateAdInfoButtons()
        }

        ad_info_edit.setOnClickListener { view: View ->
            adNameE.setText(selectedAd?.name)
            adValidE.setText(selectedAd?.valid)
            adCommentE.setText(selectedAd?.comment)

            ad_info_text.visibility = View.INVISIBLE
            ad_edit_text.visibility = View.VISIBLE
        }

        edit_ad_accept.setOnClickListener { view: View ->

            realm.beginTransaction()
            selectedAd?.name = adNameE.text.toString()
            adName.text = adNameE.text.toString()
            selectedAd?.valid = adValidE.text.toString()
            adValid.text = adValidE.text.toString()
            selectedAd?.comment = adCommentE.text.toString()
            adComment.text = adCommentE.text.toString()
            realm.commitTransaction()
            ad_info_text.visibility = View.VISIBLE
            ad_edit_text.visibility = View.INVISIBLE
        }

        edit_ad_cancel.setOnClickListener { view: View ->

//            adNameE.setText(selectedAd?.name)
//            adValidE.setText(selectedAd?.valid)
//            adCommentE.setText(selectedAd?.comment)

            ad_info_text.visibility = View.VISIBLE
            ad_edit_text.visibility = View.INVISIBLE
        }

    }
    private fun updateDateInView() {
        val myFormat = "yyyy-MM-dd" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        ad_info_validE.text = sdf.format(cal.getTime())

    }
    override fun onStop() {
        super.onStop()

        Log.v("bliot $this", "realm closed")
        realm.close()
    }
}