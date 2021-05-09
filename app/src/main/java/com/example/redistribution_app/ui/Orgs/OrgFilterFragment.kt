package com.example.redistribution_app.ui.Orgs

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.redistribution_app.R
import kotlinx.android.synthetic.main.fragment_org_filter.view.*

class OrgFilterFragment: DialogFragment() {
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        //get bundle from orgsActivity
        val loggedOrgId = this.arguments?.getInt("loggedOrg")
        //var orgTypeFilter: String = ""

        //show whats already checked code here

        //intent to be sent to OrgsActivity
        val intent = Intent(requireContext(), OrgsActivity::class.java)
        intent.putExtra("loggedOrg", loggedOrgId)

        var rootView: View = inflater.inflate(R.layout.fragment_org_filter, container, false)

        rootView.org_apply_filter.setOnClickListener { view: View ->


            if(!rootView.LO_filter.isChecked and !rootView.VI_filter.isChecked){
                Toast.makeText(this.context,"Pasirinkmite bent vieną tipą", Toast.LENGTH_SHORT).show()
            } else {
                if(rootView.VI_filter.isChecked and !rootView.LO_filter.isChecked)
                    intent.putExtra("orgType", "VI")
                if(rootView.LO_filter.isChecked and !rootView.VI_filter.isChecked)
                    intent.putExtra("orgType", "LO")
                if(rootView.LO_filter.isChecked and rootView.VI_filter.isChecked)
                    intent.putExtra("orgType", "both")

                activity?.finish()
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                startActivity(intent)
            }

//            val orgTypeButton = rootView.org_type_btn_group.checkedRadioButtonId
//            var orgType = resources.getResourceEntryName(orgTypeButton)
//            if (orgType != null) {
//                intent.putExtra("orgType", orgType)
//            }


        }


        rootView.org_cancel_filter.setOnClickListener { view: View ->
            dismiss()
        }

        return rootView
    }


}