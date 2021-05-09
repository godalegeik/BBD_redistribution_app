package com.example.redistribution_app.ui.Ads

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.redistribution_app.R
import kotlinx.android.synthetic.main.activity_orgs.view.*
import kotlinx.android.synthetic.main.fragment_ad_keys.view.*
import java.util.*

class AdKeysFragment: DialogFragment() {

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        //inflate fragment


        val act = this.arguments?.getString("activity")
        val keys = this.arguments?.getStringArrayList("keys")
        var sKeys = this.arguments?.getStringArrayList("selectedKeys");

        //sKeys = this.arguments?.getStringArrayList("selectedKeys");
        Log.v("bliot keys from act", act.toString())
//        Log.v("bliot", sKeys.toString())


        var rootView: View = inflater.inflate(R.layout.fragment_ad_keys, container, false)
         if (act == "ComponentInfo{com.example.redistribution_app/com.example.redistribution_app.ui.Ads.AdsActivity}"){
             rootView.newKeyLayout.visibility = View.GONE
         }

        var adapter = KeyAdapter(requireContext(), keys, sKeys);
        var keyRV = rootView.ad_keys_recView as RecyclerView?
        keyRV?.layoutManager = LinearLayoutManager(requireContext())//ConstraintLayoutManager(requireContext())
        keyRV?.itemAnimator = DefaultItemAnimator()
        keyRV?.adapter = adapter

        rootView.new_key_submit.setOnClickListener { view: View ->

            // See which keys are selected ATM
            var sKeysLocal = sKeys
            if(sKeysLocal==null){
                sKeysLocal = adapter.getSelectedKeys()
            } else {
                for (k in adapter.getSelectedKeys()){
                    if(!sKeysLocal.contains(k)){
                        sKeysLocal.add(k)
                    }
                }
            }
            sKeys = sKeysLocal
            Log.v("bliot - add selected keys to sKeys",sKeys.toString())


            // Add new key to fragment lists
            val newKey = rootView.new_ad_key.text.toString()
            if (keys!!.contains(newKey)) {
                if (!((adapter.checkedKeys).contains(newKey))) {
                    adapter.checkedKeys.add(newKey)
                    keyRV?.adapter = adapter
                    Log.v("bliot - add existing entered key to checkedKeys",adapter.checkedKeys.toString())
                }
            } else {
                keys.add(newKey)
                sKeys?.add(newKey)
                adapter = KeyAdapter(requireContext(), keys, sKeys)
                //adapter.checkedKeys.add(newKey)
                keyRV?.adapter = adapter
                Log.v("bliot - add new key to checkedKeys",adapter.checkedKeys.toString())
            }



        }

        rootView.apply_ad_keys.setOnClickListener{view: View ->
            if(adapter.checkedKeys.size < 1){
                Toast.makeText(requireContext(),"Pasirinkite bent 1 raktažodį",Toast.LENGTH_SHORT).show()
            } else {
                if(act!= null) {
                    var intent = Intent(requireContext(), act::class.java)
                    intent.putExtra("selectedKeys", adapter.checkedKeys)
                    Log.v("bliot adapterCHeckedKeys", adapter.checkedKeys.toString())
                    if(keys!=null){
                        when (act){
                            "ComponentInfo{com.example.redistribution_app/com.example.redistribution_app.ui.Ads.CreateAdActivity}"->
                                (activity as CreateAdActivity?)!!.applyKeys(adapter.checkedKeys, keys)

//                            "ComponentInfo{com.example.redistribution_app/com.example.redistribution_app.ui.Ads.AdsActivity}" ->
//                              //  (activity as AdsActivity?)!!.applyKeys(adapter.checkedKeys, keys)
                        }
                        dismiss()
                    } else {
                        Toast.makeText(requireContext(),"Pasirinkite bent 1 raktažodį",Toast.LENGTH_SHORT).show()
                    }
                }
            }


        }

        rootView.cancel_ad_keys.setOnClickListener { view: View ->
            dismiss()
        }

        fun getAdapter(): KeyAdapter? {
            return adapter
        }



        return rootView
        }

    }


//

     private class KeyAdapter(var context: Context, var keys: ArrayList<String>?, var sKeys:ArrayList<String>?): RecyclerView.Adapter<KeyAdapter.KeyHolder>() {
            var checkedKeys = ArrayList<String>()



        //view holder initialised
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KeyHolder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.row_ad_key, parent, false)
                return KeyHolder(v)
            }

        // fun inflateRecycler()

        // Data bound to views
        override fun onBindViewHolder (holder: KeyHolder, position: Int){
            val key = keys!![position]
            val localSKeys = sKeys
            holder.keyCheckBox.text = key
            if(localSKeys!= null){
                if (localSKeys.contains(key)){
                    holder.keyCheckBox.isChecked
                    checkedKeys.add(key)
                }
            }

            holder.setItemClickListener(object: KeyHolder.ItemClickListener{
                override fun onItemClick (v: View, pos:Int){
                    val keyCheckBox = v as CheckBox
                    val currentKey = keys!![pos]

                    if(keyCheckBox.isChecked){
                        if(!checkedKeys.contains(currentKey)){
                            checkedKeys.add(currentKey)
                        }

                    } else {
                        if(checkedKeys.contains(currentKey)){
                            checkedKeys.remove(currentKey)
                        }
                    }
                    sKeys = checkedKeys
                }
            })

        }

        override fun getItemCount(): Int {
            return keys!!.size
        }

        fun getSelectedKeys(): ArrayList<String>{
            return checkedKeys
        }
        fun newAddeddata(newKeyValue: String) {
            keys?.add(newKeyValue)
            notifyDataSetChanged()
        }

        internal class KeyHolder(itemView: View): RecyclerView.ViewHolder(itemView), View.OnClickListener{
            var keyCheckBox: CheckBox

            lateinit var keyItemClickListener: ItemClickListener

            init {
                keyCheckBox = itemView.findViewById(R.id.ad_key_checkBox)
                keyCheckBox.setOnClickListener(this)
            }

            fun setItemClickListener (ic: ItemClickListener){
                this.keyItemClickListener = ic
            }

            override fun onClick(v: View){
                this.keyItemClickListener.onItemClick(v, layoutPosition)
            }

            internal interface ItemClickListener {
                fun onItemClick(v: View, pos: Int)
            }
        }

    }

