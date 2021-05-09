package com.example.redistribution_app.ui.Orgs

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.redistribution_app.R
import com.example.redistribution_app.models.Org
import com.example.redistribution_app.ui.BaseActivity
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_orgs.*

class OrgsActivity : AppCompatActivity() {

    lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orgs)

        val mIntent = intent
        val loggedOrgId = mIntent.getIntExtra("loggedOrg", -1)
        val orgTypeFilter = mIntent.getStringExtra("orgType")

        val listView = findViewById<ListView>(R.id.org_listView)

//        Realm.init(this);
//        val config = RealmConfiguration.Builder()
//                .name("database.realm").build()
//        val realm = Realm.getInstance(config)

        Realm.init(this)
        realm = Realm.getDefaultInstance()
        realm.beginTransaction();

        // Get logged in organisation
        val loggedOrg = realm.where(Org::class.java).equalTo("_id", loggedOrgId).findFirst()

        // Get other organisations list
        val orgList2: List<Org> = realm.where(Org::class.java).findAll();
        var orgList = orgList2.toMutableList()
        orgList2.forEach{org: Org ->
            if (org._id == loggedOrgId){
                orgList.remove(org)
            }
        }
        realm.commitTransaction()

        //filtering by food type (if selected)
        if (orgTypeFilter != null){
            var tempOrgList: MutableList<Org> = mutableListOf()

             //Log.v("bliot", orgTypeFilter)
            if((orgTypeFilter!="both")){
                orgList.forEach{org: Org ->
                    if(org.type == orgTypeFilter){
                        tempOrgList.add(org)
                    }
                }
                orgList.clear()
                orgList = tempOrgList
            }
        }

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        BaseActivity.getDrawer(this, toolbar, loggedOrgId)

        listView.adapter =
                OrgAdapter(
                        this,
                        orgList
                )
        listView.setOnItemClickListener { adapterView: AdapterView<*>, view: View, position: Int, id: Long ->
            val intent = Intent(this, OrgInfoActivity::class.java)
            val selectedOrg: Int = orgList.get(position)._id
            intent.putExtra("selectedOrgId", selectedOrg)
            intent.putExtra("loggedOrg", loggedOrgId)

            view.context.startActivity(intent)
            //realm.close()
            //Log.i("adsActivity", selectedAd.toString())
             this.finish()
        }

        org_filter_btn.setOnClickListener { view: View ->


            val bundle = Bundle()
            bundle.putInt("loggedOrg", loggedOrgId)



            // set Fragmentclass Arguments
            val dialog = OrgFilterFragment()
            dialog.setArguments(bundle)
            dialog.show(supportFragmentManager, "org filter dialog")


        }
    }

    private class OrgAdapter(context: Context, data: List<Org>): BaseAdapter() {

        private val mContext: Context = context
        private val mData: List<Org> = data


        override fun getCount(): Int {
            return mData.size;
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
            val orgRow = layoutInflater.inflate(R.layout.row_org, viewGroup, false)

            val nameText = orgRow.findViewById<TextView>(R.id.org_row_name)
            nameText.text = mData.get(position).name
            val adressText = orgRow.findViewById<TextView>(R.id.org_row_adress)
            adressText.text = mData.get(position).adress
            val typeText = orgRow.findViewById<TextView>(R.id.org_row_type)
            typeText.text = mData.get(position).type
            return orgRow
        }

    }

//    override fun onStop(){
//        super.onStop()
//
//        val openRealms = Realm.getGlobalInstanceCount(Realm.getDefaultConfiguration())
//        if(openRealms > 0){
//            for (i in 0..openRealms){
//                Log.v("bliot $this", "realm closed")
//                realm.close()
//            }
//        }
//    }

        override fun onStop() {
            super.onStop()

            Log.v("bliot $this", "realm closed")
            realm.close()
        }


}