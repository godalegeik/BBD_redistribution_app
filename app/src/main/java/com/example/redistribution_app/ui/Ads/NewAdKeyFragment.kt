package com.example.redistribution_app.ui.Ads

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.redistribution_app.R
import kotlinx.android.synthetic.main.fragment_new_ad_key.view.*

class NewAdKeyFragment: DialogFragment() {

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        //inflate fragment
        var rootView: View = inflater.inflate(R.layout.fragment_ad_filter, container, false)


        rootView.new_key_submit2.setOnClickListener { view: View ->

            val act = this.arguments?.getString("acitvity")
            Log.v("bliot", act!!)
//            val intent = Intent(requireContext(), act!!::class.java)
//            intent.putExtra("newKey", rootView.new_ad_key.text)
        }


        return rootView
    }



}