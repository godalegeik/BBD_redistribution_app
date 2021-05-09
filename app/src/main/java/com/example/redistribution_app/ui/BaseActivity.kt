package com.example.redistribution_app.ui

import android.R
import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.appcompat.widget.Toolbar
import com.example.redistribution_app.models.Org
import com.example.redistribution_app.ui.Ads.AdsActivity
import com.example.redistribution_app.ui.Ads.CreateAdActivity
import com.example.redistribution_app.ui.Orgs.OrgInfoActivity
import com.example.redistribution_app.ui.Orgs.OrgsActivity
import com.example.redistribution_app.utils.UserPreference
import com.mikepenz.materialdrawer.AccountHeaderBuilder
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.model.DividerDrawerItem
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.mikepenz.materialdrawer.model.ProfileDrawerItem
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem
import io.realm.Realm


object BaseActivity {

    lateinit var realm: Realm

     fun getDrawer(activity: Activity?, toolbar: Toolbar, orgS: Int) {

        //if you want to update the items at a later time it is recommended to keep it in a variable
       // val tempOrgId = orgB?._id.toString()
         realm = Realm.getDefaultInstance()
         var org = realm.where(Org::class.java).equalTo("_id", orgS).findFirst()


        val drawerEmptyItem = PrimaryDrawerItem().withIdentifier(0).withName("")
        drawerEmptyItem.withEnabled(false)
        val advertsItem: PrimaryDrawerItem = PrimaryDrawerItem().withIdentifier(1)
           .withName("Ad list").withIcon(R.drawable.gallery_thumb)

        val otherItem: PrimaryDrawerItem = PrimaryDrawerItem()
            .withIdentifier(2).withName("Organisation list").withIcon(R.drawable.gallery_thumb)

        val newAdItem: PrimaryDrawerItem = PrimaryDrawerItem()
            .withIdentifier(3).withName("Create ad").withIcon(R.drawable.gallery_thumb)


        val logoutItem: SecondaryDrawerItem = SecondaryDrawerItem().withIdentifier(4)
            .withName("Logout").withIcon(R.drawable.gallery_thumb)
        val profileItem: SecondaryDrawerItem = SecondaryDrawerItem().withIdentifier(5)
            .withName("Profile").withIcon(R.drawable.gallery_thumb)
//        val drawerItemHelp: SecondaryDrawerItem = SecondaryDrawerItem().withIdentifier(5)
//            .withName(R.string.help).withIcon(R.drawable.ic_help_black_24px)
//        val drawerItemDonate: SecondaryDrawerItem = SecondaryDrawerItem().withIdentifier(6)
//            .withName(R.string.donate).withIcon(R.drawable.ic_payment_black_24px)


        //create the drawer and remember the `Drawer` result object

//        val mContext: Context = context
//        override fun getView(position: Int, convertView: View?, viewGroup: ViewGroup?): View {
//            val layoutInflater = LayoutInflater.from(mContext)
//            public val headerView = layoutInflater.inflate(com.example.redistribution_app.R.layout.nav_header_main, viewGroup, false)
//
//        }

//        Realm.init(activity!!);
//        val config = RealmConfiguration.Builder()
//            .name("database.realm").build()
//        val realm = Realm.getInstance(config)
//        realm.beginTransaction();
//
//        realm.commitTransaction();


         //Log.i("adsActivity", org?.name)


       val header = AccountHeaderBuilder()
            .withActivity(activity!!)
            .withTranslucentStatusBar(true)
            .withHeaderBackground(R.drawable.dark_header)
            .addProfiles(
                ProfileDrawerItem().withName(org?.name).withEmail(org?.email)
            )
            .build();
        realm.close()

        val result = DrawerBuilder()
            .withActivity(activity!!)
            .withToolbar(toolbar)
            .withAccountHeader(header)
            .withActionBarDrawerToggle(true)
            .withActionBarDrawerToggleAnimated(true)
            .withCloseOnClick(true)
            .withSelectedItem(-1)
            .addDrawerItems(
                advertsItem,
                otherItem,
                newAdItem,
                DividerDrawerItem(),
                    profileItem,
                    logoutItem
            )
            .withOnDrawerItemClickListener { view, position,   drawerItem ->
                realm = Realm.getDefaultInstance()
                var org = realm.where(Org::class.java).equalTo("_id", orgS).findFirst()
                when (drawerItem.identifier) {
                    1L -> if (activity !is AdsActivity) {
                        val intent = Intent(activity, AdsActivity::class.java)
                        intent.putExtra("loggedOrg",org?._id)
                        view.context.startActivity(intent)
                        realm.close()
                        activity.finish()
                    }

                    2L -> if (activity !is OrgsActivity) {
                        val intent = Intent(activity, OrgsActivity::class.java)

                        intent.putExtra("loggedOrg",org?._id)
                        view.context.startActivity(intent)
                        realm.close()
                        activity.finish()
                    }

                    3L -> if (activity !is CreateAdActivity) {
                        val intent = Intent(activity, CreateAdActivity::class.java)
                        intent.putExtra("loggedOrg",org?._id)
                        view.context.startActivity(intent)
                        realm.close()
                        activity.finish()
                    }

                    4L -> if (activity !is LoginActivity) {

                        val userPreference = UserPreference(activity)
                        userPreference.setLoggedOrgId(-1)

                        val intent = Intent(activity, LoginActivity::class.java)
                        view.context.startActivity(intent)
                       // Log.v("bliotBase", realm.configuration.toString())
                        realm.close()
                        realm.close()
                        activity.finish()
                    }

                    5L -> if (activity !is CreateAdActivity) {
                        val intent = Intent(activity, OrgInfoActivity::class.java)
                        intent.putExtra("loggedOrg",org?._id)
                        intent.putExtra("selectedOrgId",org?._id)

                        realm.close()
                        view.context.startActivity(intent)
                        activity.finish()
                    }

                }

                true
                //how to close???

            }
            .build()
            //realm.close()
    }
}