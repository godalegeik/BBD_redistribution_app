package com.example.redistribution_app.ui.Ads

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.redistribution_app.R
import com.example.redistribution_app.models.AdKey
import com.example.redistribution_app.ui.BaseActivity.realm
import com.example.redistribution_app.ui.Orgs.OrgsActivity
import kotlinx.android.synthetic.main.activity_create_ad.view.*
import kotlinx.android.synthetic.main.fragment_ad_filter.*
import kotlinx.android.synthetic.main.fragment_ad_filter.view.*
import kotlinx.android.synthetic.main.fragment_org_filter.view.*
import java.util.ArrayList

class AdFilterFragment: DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        //get bundle from adsActivity
        //var adList = this.arguments?.getString("adList")
        val loggedOrgId = this.arguments?.getInt("loggedOrg")
        val minValid = this.arguments?.getLong("minValid")
        val maxValid = this.arguments?.getLong("maxValid")
        val act = this.arguments?.getString("activityName")
        Log.v("bliot", "min $minValid max $maxValid")
//

        // Used filters:
        var foodTypeFilter: Boolean = false
        var validFilter: Boolean= false
        var orgsFilter: Boolean= false
        var splittableFilter: Boolean= false
        var keyFilter: Boolean = false

        //show whats already checked code here


        //inflate fragment
        var rootView: View = inflater.inflate(R.layout.fragment_ad_filter, container, false)




        rootView.food_type_btn_group.visibility = View.GONE
        rootView.splittable_btn_group.visibility = View.GONE
        rootView.valid_slider.visibility = View.GONE

        rootView.splittable_filter.setOnCheckedChangeListener {buttonView, isChecked ->
            splittableFilter = isChecked
            if(isChecked){
                splittable_btn_group.visibility = View.VISIBLE
            } else {
                splittable_btn_group.visibility = View.GONE
            }
        }

        rootView.food_type_filter.setOnCheckedChangeListener { buttonView, isChecked ->
            foodTypeFilter = isChecked
            if(isChecked){
                food_type_btn_group.visibility = View.VISIBLE
            } else {
                food_type_btn_group.visibility = View.GONE
            }
        }


        rootView.valid_range_filter.setOnCheckedChangeListener { buttonView, isChecked ->
            validFilter = isChecked
            if(isChecked){
                if(minValid != null){
                    rootView.valid_slider.valueFrom = minValid.toFloat()
                }
                if(maxValid != null){
                    rootView.valid_slider.valueTo = maxValid.toFloat()
                }
                rootView.valid_slider.setValues(minValid?.toFloat(), maxValid?.toFloat())
                rootView.valid_slider.visibility = View.VISIBLE
            } else {
                rootView.valid_slider.visibility = View.GONE
            }
        }

        rootView.ad_key_filter.setOnCheckedChangeListener { buttonView, isChecked ->
            validFilter = isChecked
            if(isChecked){
                val keyData = realm.where(AdKey::class.java).findAll()
                var keys: ArrayList<String> = arrayListOf()
                for (key in keyData) {
                    keys.add(key.name)
                }
                if(act!=null){
                    (activity as CreateAdActivity).showDialog(AdKeysFragment(), keys, act)
                } else {
                    Log.v("bliot - filter fragment empty activity", "")
                }

            } else {
                rootView.valid_slider.visibility = View.GONE
            }
        }

//        rootView.orgs_filter.setOnCheckedChangeListener { buttonView, isChecked ->
//            orgsFilter = isChecked
//            if(isChecked){
//                val dialog = AlertDialog.Builder(this.context)
//                val view = layoutInflater.inflate(R.layout.org_row, null)
//                dialog.setView(view)
//
//              //  view.manage_people_title.text = "Manage People"
//
//                val adapter = OrgsActivity.OrgAdapter(result)
//                view.orgs_check_listView.adapter = adapter
//                view.orgs_check_listView.layoutManager = LinearLayoutManager(this)
//            } else {
//
//            }
//        }

        //intent to be sent to adsActivity
        val intent = Intent(requireContext(), AdsActivity::class.java)
        intent.putExtra("loggedOrg", loggedOrgId)

        rootView.apply_filter.setOnClickListener { view: View ->

            if(foodTypeFilter){
                val foodTypeButton = rootView.food_type_btn_group.checkedRadioButtonId
                var foodType = resources.getResourceEntryName(foodTypeButton)
                if(foodType == "long_valid_filter"){
                    intent.putExtra("foodType", "long_valid")
                } else if (foodType == "short_valid_filter"){
                    intent.putExtra("foodType", "short_valid")
                }

            }
            if(splittableFilter){
                val splitButton = rootView.splittable_btn_group.checkedRadioButtonId
                var split = resources.getResourceEntryName(splitButton)
                if(split == "splittable_yes"){
                    intent.putExtra("splittable", splittableFilter.toString())
                } else if (split == "splittable_no"){
                    intent.putExtra("splittable", (!splittableFilter).toString())
                }
            }
            if(validFilter){
                val sliderValues = valid_slider.getValues()
                val valueMin = sliderValues[0].toLong()
                intent.putExtra("validMin", valueMin)
                val valueMax = sliderValues[1].toLong()
                intent.putExtra("validMax", valueMax)
//                Log.v("bliot", "validTo $valueMin")
//                Log.v("bliot", "validFrom $valueMax")
            }

            if(keyFilter){

            }
            activity?.finish()
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)



        }






        rootView.cancel_filter.setOnClickListener { view: View ->
            dismiss()
        }

        return rootView
    }
}