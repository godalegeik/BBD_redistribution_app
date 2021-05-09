package com.example.redistribution_app.ui.Ads

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
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
import com.example.redistribution_app.models.Ad
import com.example.redistribution_app.models.Org
import com.example.redistribution_app.ui.BaseActivity
import com.example.redistribution_app.ui.BaseActivity.realm
import com.example.redistribution_app.ui.BaseActivity.toString
import com.example.redistribution_app.ui.Orgs.OrgInfoActivity
import io.realm.*
import kotlinx.android.synthetic.main.activity_ads.*
import java.lang.Exception
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*

class AdsActivity : AppCompatActivity() {

    lateinit var notificationManager: NotificationManager
    lateinit var notificationChannel: NotificationChannel
    lateinit var nBuilder: Notification.Builder
    private val channelId = "com.example.redistributiuon_app"
    private val description = "test notification"
    lateinit var realm: Realm
    private lateinit var realmListener: RealmChangeListener<Realm>
    val dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ads)

        val mIntent = intent
        val loggedOrgId = mIntent.getIntExtra("loggedOrg", -1)
        //Log.v("bliot", loggedOrgId.toString())

        //Log.i("bliot", "from register: ${loggedOrgId}")
//        Log.v("bliot", "validTo $validFrom")
//        Log.v("bliot", "validFrom $validTo")




        realm = Realm.getDefaultInstance()
        var loggedOrg = realm.where(Org::class.java).equalTo("_id", loggedOrgId).findFirst()




        //Log.i("adsActivity", loggedOrgId.toString())
        // Log.i("adsActivity", loggedOrg.toString())
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        //realm = Realm.getInstance(Realm.getDefaultConfiguration())
        //loggedOrg = realm.where(Org::class.java).equalTo("_id", loggedOrgId).findFirst()
        BaseActivity.getDrawer(this, toolbar, loggedOrgId)
        //("bliot", loggedOrg.toString())

        val listView = findViewById<ListView>(R.id.ad_listView)

        var adList = mutableListOf<Ad>()

        // CIA CHANGE LUSTENERIS
        val ads = realm.where(Ad::class.java).findAll()
        // Set up the collection notification handler.
        val changeListener =
                OrderedRealmCollectionChangeListener { collection: RealmResults<Ad>?, changeSet: OrderedCollectionChangeSet ->

                    var newAd = collection?.last()
                    if (newAd?.creator?._id != loggedOrgId){
                        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                        adList = makeAdList(mIntent, loggedOrg)
                        //listView.adapter = AdAdapter(this, adList)
                        notifAd(newAd)

                    }
                }
      //  Log.v("bliotDebug", "here works dar "+ads)
        // Observe collection notifications.
        ads.addChangeListener(changeListener)

       // realm.addRealmObjectChangeListener(adChangeListener)

        adList = makeAdList(mIntent, loggedOrg)
        listView.adapter = AdAdapter(this, adList)
      //  Log.v("bliotDebug", "here works dar "+adList)
      //  Log.v("bliotRealm", realm.configuration.toString())

       // Log.v("bliotRealm after close", realm.isClosed.toString())
       // Log.v("bliotRealm after close", realm.configuration.toString())

        listView.setOnItemClickListener { adapterView: AdapterView<*>, view: View, position: Int, id: Long ->
            val intent = Intent(this, AdInfoActivity::class.java)
            adList = makeAdList(mIntent, loggedOrg)
            val selectedAd: Int = adList.get(position)._id
            intent.putExtra("loggedOrg", loggedOrgId)
            intent.putExtra("selectedAdId", selectedAd)
            realm.close()
            view.context.startActivity(intent)
            this.finish()

            //Log.i("adsActivity", selectedAd.toString())
        }

        ad_filter_btn.setOnClickListener { view: View ->

            //Get min and max days valid
            var minValid: Long = 1
            var maxValid: Long = 100
            adList = makeAdList(mIntent, loggedOrg)
            if (adList != null) {
                val validDate: LocalDate
                var diff: Long


                val minValidDate = LocalDate.parse(adList.get(0).valid, dtf)
                minValid =  ChronoUnit.DAYS.between(LocalDate.now(), minValidDate)

                val maxValidDate = LocalDate.parse(adList.last().valid, dtf)
                maxValid =  ChronoUnit.DAYS.between(LocalDate.now(), maxValidDate)
//                Log.v("bliot", "min "+minValidDate+" m ax "+ maxValidDate)
//                Log.v("bliot", "min $minValid max $maxValid")
            } else {
                Log.v("bliot", "adlist is nul??????")
            }

            val bundle = Bundle()
            bundle.putInt("loggedOrg", loggedOrgId)
            bundle.putLong("minValid", minValid)
            bundle.putLong("maxValid", maxValid)
            var activityName: String = this.componentName.toString()
            bundle.putString("activityName", activityName)


            // set Fragmentclass Arguments
            val dialog = AdFilterFragment()
            dialog.setArguments(bundle)
            dialog.show(supportFragmentManager, "ad filter dialog")


        }


        ad_sort_btn.setOnClickListener { view: View ->

        }

        //realm.close()
    }

    fun notifAd(ad: Ad?){

            val intentL = Intent(this, AdInfoActivity::class.java)
            intentL.putExtra("selectedAdId", 0)
            val pendingIntent = PendingIntent.getActivity(this, 0 , intentL, PendingIntent.FLAG_UPDATE_CURRENT)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                notificationChannel = NotificationChannel(channelId, description, NotificationManager.IMPORTANCE_HIGH)
                notificationChannel.enableLights(true)
                notificationChannel.lightColor = Color.GREEN
                notificationChannel.enableVibration(true)
                notificationManager.createNotificationChannel(notificationChannel)

                nBuilder = Notification.Builder(this, channelId)
                        .setContentTitle("AdNotification")
                        .setContentText("Skelbimų informacija pakito")
                        .setSmallIcon(R.mipmap.ic_launcher_round)
                        .setLargeIcon(BitmapFactory.decodeResource(this.resources,R.mipmap.ic_launcher))
                        .setContentIntent(pendingIntent)
            } else {
                nBuilder = Notification.Builder(this)
                        .setContentTitle("AdNotification")
                        .setContentText("Skelbimų informacija pakito")
                        .setSmallIcon(R.mipmap.ic_launcher_round)
                        .setLargeIcon(BitmapFactory.decodeResource(this.resources,R.mipmap.ic_launcher))
                        .setContentIntent(pendingIntent)
            }
            notificationManager.notify(1234, nBuilder.build())
        }


    fun makeAdList(mIntent: Intent, loggedOrg: Org?): MutableList<Ad>{
        val foodTypeFilter = mIntent.getStringExtra("foodType")
        val splitFilter = mIntent.getStringExtra("splittable")
        val validFrom = mIntent.getLongExtra("validMin", -1)
        val validTo = mIntent.getLongExtra("validMax", 1000)

        // Making a list of relevant ads
        val adListR: List<Ad> = when (loggedOrg?.type) {
            "LO" -> realm.where(Ad::class.java)
                    .notEqualTo("_id", 2.toInt())
                    .and()
                    .equalTo("type", "AS")
                    .and()
                    .notEqualTo("status", "EA")
                    .and()
                    .notEqualTo("status", "IA")
                    .and()
//                    .greaterThanOrEqualTo("valid", Date())
//                    .and()
                    .equalTo("status", "AA")
                    .or()
                    .equalTo("status", "RA")
                    .sort("valid", Sort.ASCENDING)
                    .findAll();
            "VI" -> realm.where(Ad::class.java)
                    .notEqualTo("_id", 2.toInt())
                    .and()
                    .equalTo("type", "AP")
                    .and()
                    .notEqualTo("status", "EA")
                    .and()
                    .notEqualTo("status", "IA")
                    .and()
//                    .greaterThanOrEqualTo("valid", Date())
//                    .and()
                    .equalTo("status", "AA")
                    .or()
                    .equalTo("status", "RA")
                    .sort("valid", Sort.ASCENDING)
                    .findAll();
            else -> realm.where(Ad::class.java)
                    .notEqualTo("_id", 2.toInt())
                    .and()
                    .sort("valid", Sort.ASCENDING)
//                    .and()
//                    .greaterThanOrEqualTo("valid", Date())
                    .and()
                    .notEqualTo("status", "EA")
                    .and()
                    .notEqualTo("status", "IA")
                    .and()
                    .equalTo("status", "AA")
                    .or()
                    .equalTo("status", "RA")
                    .findAll();
        }
        var adList = adListR.toMutableList()
        //dont show expired ads!
        var tempAdListD: MutableList<Ad> = mutableListOf()
        var expAdList: MutableList<Int> = mutableListOf()
        adList.forEach { ad: Ad ->
//            val validDate = Date(ad.valid)
//            val daysValid = (validDate.getTime() - Date().getTime()) / (1000 * 60 * 60 * 24)

            val validDateOld: Date
            val validDate: LocalDate
            var diff: Long
            try{
                validDate = LocalDate.parse(ad.valid, dtf)
                diff = ChronoUnit.DAYS.between(LocalDate.now(), validDate)
            } catch (e: Exception){
                validDateOld =  Date(ad.valid)
                diff = (validDateOld.getTime() - Date().getTime())/(1000*60*60*24)
            }
            if (diff>=0) {
                //ad.status = "EA" //setting status to expired
                tempAdListD.add(ad)
            } else {
                var copyAd  = realm.copyFromRealm(
                        realm.where(Ad::class.java).equalTo("_id", ad._id).findFirst()
                        )
               // Log.v("bliot", copyAd.toString())
                if(copyAd != null){
                    expAdList.add((copyAd._id))
                }
            }
        }
        adList.clear()
        adList = tempAdListD

      //  change expired ad statuses
        expAdList.forEach {i : Int ->
            realm.beginTransaction()
           var exAd = realm.where(Ad::class.java).equalTo("_id", i).findFirst()
            exAd?.status = "EA"
            realm.commitTransaction()
            Log.v("bliot", "ad status should be changed ")
        }

        //filtering by food type (if selected)
        if (foodTypeFilter != null) {
            var tempAdList: MutableList<Ad> = mutableListOf()
            // Log.v("bliot", foodTypeFilter)
            adList.forEach { ad: Ad ->
                if (ad.foodType == foodTypeFilter) {
                    tempAdList.add(ad)
                }
            }
            adList.clear()
            adList = tempAdList
        }

        //filtering by if the ad is splittable (if selected)
        if (splitFilter != null) {
            val splitFilter2 = splitFilter.toBoolean()
            var tempAdList: MutableList<Ad> = mutableListOf()
            adList.forEach { ad: Ad ->
//                Log.v("bliot", "splitFilter  " + splitFilter2)
//                Log.v("bliot", "ad.splittable  " + ad.splittable)
//                Log.v("bliot", "==  " + (ad.splittable == splitFilter2))
                if (ad.splittable == splitFilter2) {
                    tempAdList.add(ad)
                }
            }
            adList.clear()
            adList = tempAdList
        }

        //filtering by valid date (if selected)
        if (validFrom != (-1).toLong()) {
            var tempAdList: MutableList<Ad> = mutableListOf()
            adList.forEach { ad: Ad ->

                val validDate = LocalDate.parse(ad.valid, dtf)
                val daysValid = ChronoUnit.DAYS.between(LocalDate.now(), validDate)
//                Log.v("bliot", "daysValid $daysValid")
//                Log.v("bliot", "validTo $validTo")
//                Log.v("bliot", "validFrom $validFrom")
                if ((daysValid <= validTo) and (daysValid >= validFrom)) {
                    tempAdList.add(ad)
                }
            }
            adList.clear()
            adList = tempAdList
        }

        return adList

    }

    //fun filterByKey(isFilterChecked: Boolean)

    public class AdAdapter(context: Context, data: List<Ad>): BaseAdapter() {

        private val mContext: Context = context
        private val mData: List<Ad> = data


        override fun getCount(): Int {
            return mData!!.size

        }

        override fun getItemId(position: Int): Long {
           return position.toLong();
        }

        override fun getItem(position: Int): Any {
            return "placeholder string"
        }

        fun updateTime() {
            // Set Current Date

        }


        // Rendering each row
        override fun getView(position: Int, convertView: View?, viewGroup: ViewGroup?): View {

            val layoutInflater = LayoutInflater.from(mContext)
            val adRow = layoutInflater.inflate(R.layout.ad_row, viewGroup, false)
            if (Realm.getGlobalInstanceCount(Realm.getDefaultConfiguration()) >= 1){

                val adTimer = adRow.findViewById<TextView>(R.id.ad_timer)
//                val sdf = SimpleDateFormat("yyyy-MM-dd")
//                val valid_date = Date(mData.get(position).valid)
//                val diff = (valid_date.getTime() - Date().getTime())/(1000*60*60*24)+1
                val dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                val validDateOld: Date
                val validDate: LocalDate
                var diff: Long
                try{
                    validDate = LocalDate.parse(mData.get(position).valid, dtf)
                    diff = ChronoUnit.DAYS.between(LocalDate.now(), validDate)
                } catch (e: Exception){
                    validDateOld =  Date(mData.get(position).valid)
                    diff = (validDateOld.getTime() - Date().getTime())/(1000*60*60*24)+1
                }
                adTimer.text = "Galioja "+diff.toString()+" d."



                val nameText = adRow.findViewById<TextView>(R.id.ad_row_name)
                nameText.text = mData.get(position).name
                val validText = adRow.findViewById<TextView>(R.id.ad_row_valid)
                validText.text = mData.get(position).valid
                val status = adRow.findViewById<TextView>(R.id.ad_row_status)
                status.text = mData.get(position).status
                //Log.v("bliot-getView", "ads loaded well")
                //Log.v("bliot-getView", "instance somehow open?????")


            } else {
              //  Log.v("bliot-getView", "this right here officer")
                //Realm.getDefaultInstance().use { realmInstance -> realmInstance.executeTransaction { realm ->


               // }}
            }

            return adRow
        }

    }



    override fun onStop() {
        super.onStop()

        Log.v("bliot $this", "realm closed")
        realm.close()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Log.v("bliot $this", "realm closed")
        realm.close()
    }




    }

