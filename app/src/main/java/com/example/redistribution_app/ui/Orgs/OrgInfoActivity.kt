package com.example.redistribution_app.ui.Orgs

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.Toolbar
import com.example.redistribution_app.R
import com.example.redistribution_app.models.Ad
import com.example.redistribution_app.models.Org
import com.example.redistribution_app.ui.Ads.AdInfoActivity
import com.example.redistribution_app.ui.BaseActivity
import io.realm.Realm
import io.realm.RealmList
import kotlinx.android.synthetic.main.activity_org_info.*
import java.lang.Exception
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import java.time.temporal.ChronoUnit.DAYS


class OrgInfoActivity : AppCompatActivity() {

    lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_org_info)

        val mIntent = intent
        val selectedOrgId = mIntent.getIntExtra("selectedOrgId", -1)
        val loggedOrgId = mIntent.getIntExtra("loggedOrg", -1)

        org_info_layout.visibility = View.VISIBLE
        org_edit_layout.visibility = View.INVISIBLE


        Realm.init(this)
        realm = Realm.getDefaultInstance()
        realm.beginTransaction();
        val selectedOrg: Org? = realm.where(Org::class.java).equalTo("_id", selectedOrgId).findFirst()
        val loggedOrg = realm.where(Org::class.java).equalTo("_id", loggedOrgId).findFirst()
        var adList = loggedOrg?.createdAds
        if(selectedOrg != null){
            adList = selectedOrg?.createdAds
        }

        realm.commitTransaction();

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        BaseActivity.getDrawer(this, toolbar, loggedOrgId);

        if (selectedOrgId == loggedOrgId){
            org_info_edit.visibility = View.VISIBLE
            org_info_password_change.visibility = View.VISIBLE
        }

        val orgName = findViewById<TextView>(R.id.org_info_name)
        orgName.text = selectedOrg?.name
        val orgAdress = findViewById<TextView>(R.id.org_info_adress)
        orgAdress.text = selectedOrg?.adress
        val orgCode = findViewById<TextView>(R.id.org_info_code)
        orgCode.text = selectedOrg?.code
        val orgCity = findViewById<TextView>(R.id.org_info_city)
        orgCity.text = selectedOrg?.city
        val orgPerson = findViewById<TextView>(R.id.org_info_person)
        orgPerson.text = selectedOrg?.person
        val orgPhone = findViewById<TextView>(R.id.org_info_phone)
        orgPhone.text = selectedOrg?.phone
        val orgEmail = findViewById<TextView>(R.id.org_info_email)
        orgEmail.text = selectedOrg?.email
        val orgType = findViewById<TextView>(R.id.org_info_type)
        orgType.text = selectedOrg?.type

        val orgNameE = findViewById<EditText>(R.id.reg_org_name2)
        val orgAdressE = findViewById<EditText>(R.id.reg_org_adress2)
        val orgCodeE = findViewById<TextView>(R.id.reg_org_code2)
        var orgCityE = miestas_spinner2
            val cities = arrayOf("Vilnius", "Kaunas", "Klaipėda", "Alytus", "Visaginas")
            var cityNames: MutableList<String> = mutableListOf()
            cities.forEach(){ city ->
                cityNames.add(city)
            }
            orgCityE.adapter =
                    ArrayAdapter(
                            this,
                            android.R.layout.simple_spinner_dropdown_item,
                            cityNames
                    )
        val orgPersonE = findViewById<EditText>(R.id.reg_org_person2)
        val orgPhoneE = findViewById<EditText>(R.id.reg_org_phone2)
        val orgEmailE = findViewById<EditText>(R.id.reg_org_email2)


        val listView = findViewById<ListView>(R.id.org_info_listView)
        if (adList != null){
            listView.adapter = OrgInfoAdapter(this, adList)
        }
        //realm.close()

        listView.setOnItemClickListener { adapterView: AdapterView<*>, view: View, position: Int, id: Long ->
            val intent = Intent(this, AdInfoActivity::class.java)
            val selectedAd: Int? = adList?.get(position)?._id
            intent.putExtra("loggedOrg", loggedOrgId)
            intent.putExtra("selectedAdId", selectedAd)
            view.context.startActivity(intent)
            //realm.close()
            //this.finish()
        }

        org_info_edit.setOnClickListener { view: View ->
            orgNameE.setText(loggedOrg?.name)
            orgAdressE.setText(loggedOrg?.adress)
            orgCodeE.setText(loggedOrg?.code)
            var i = 0
            var cityId = 0
            cities.forEach(){city ->
                if(city == loggedOrg?.city){
                    cityId = i
                }
                i++
            }
            orgCityE.setSelection(cityId)
            orgPersonE.setText(loggedOrg?.person)
            orgPhoneE.setText(loggedOrg?.phone)
            orgEmailE.setText(loggedOrg?.email)
            org_info_layout.visibility = View.INVISIBLE
            org_edit_layout.visibility = View.VISIBLE
            //realm.close()

        }

        reg_org_accept2.setOnClickListener { view: View ->

            realm.beginTransaction()
            selectedOrg?.name = orgNameE.text.toString()
            selectedOrg?.adress = orgAdressE.text.toString()
            selectedOrg?.code = orgCodeE.text.toString()
            selectedOrg?.city = orgCityE.selectedItem.toString()
            selectedOrg?.person = orgPersonE.text.toString()
            selectedOrg?.phone = orgPhoneE.text.toString()
            selectedOrg?.email = orgEmailE.text.toString()
            realm.commitTransaction()

            orgName.text = selectedOrg?.name
            orgAdress.text = selectedOrg?.adress
            orgCode.text = selectedOrg?.code
            orgCity.text = selectedOrg?.city
            orgPhone.text = selectedOrg?.phone
            orgPerson.text = selectedOrg?.person
            orgEmail.text = selectedOrg?.email

            org_info_layout.visibility = View.VISIBLE
            org_edit_layout.visibility = View.INVISIBLE
           //realm.close()

        }

        reg_org_cancel2.setOnClickListener { view: View ->

            org_info_layout.visibility = View.VISIBLE
            org_edit_layout.visibility = View.INVISIBLE
           // realm.close()

        }

        org_info_password_change.setOnClickListener { view: View ->
            val intent = Intent(this, Change_Pass_Activity::class.java)
            intent.putExtra("loggedOrg",loggedOrgId)
            view.context.startActivity(intent)
            //realm.close()
        }


    }

    private class OrgInfoAdapter(context: Context, data: RealmList<Ad>?): BaseAdapter() {

        private val mContext: Context = context
        private val mData: RealmList<Ad>? = data


        override fun getCount(): Int {
            return mData!!.size;
        }

        override fun getItemId(position: Int): Long {
            return position.toLong();
        }

        override fun getItem(position: Int): Any {
            return "placeholder string"
        }


        // Rendering each row
        override fun getView(position: Int, convertView: View?, viewGroup: ViewGroup?): View {
            val layoutInflater = LayoutInflater.from(mContext)
            val adRow = layoutInflater.inflate(R.layout.ad_row, viewGroup, false)
            val adTimer = adRow.findViewById<TextView>(R.id.ad_timer)
           // val sdf = SimpleDateFormat("yyyy-MM-dd")
            val dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val validDateOld: Date
            val validDate: LocalDate
            var diff: Long
            try{
                validDate = LocalDate.parse(mData?.get(position)?.valid, dtf)
                diff = DAYS.between(LocalDate.now(), validDate)
            } catch (e: Exception){
                validDateOld =  Date(mData?.get(position)?.valid)
                diff = (validDateOld.getTime() - Date().getTime())/(1000*60*60*24)+1
            }

            adTimer.text = "Galioja "+diff.toString()+" d."

            val nameText = adRow.findViewById<TextView>(R.id.ad_row_name)
            nameText.text = mData?.get(position)?.name
            val validText = adRow.findViewById<TextView>(R.id.ad_row_valid)
            validText.text = mData?.get(position)?.valid
            val status = adRow.findViewById<TextView>(R.id.ad_row_status)
            status.text = mData?.get(position)?.status

            return adRow
        }

    }

    override fun onStop() {
        super.onStop()

        Log.v("bliot $this", "realm closed")
        realm.close()
    }

}

