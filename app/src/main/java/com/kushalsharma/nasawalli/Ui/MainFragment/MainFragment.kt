package com.kushalsharma.nasawalli.Ui.MainFragment

import android.content.Intent
import android.media.Image
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.data.DataFetcher
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.kushalsharma.nasawalli.Interface.NasaService
import com.kushalsharma.nasawalli.MainActivity
import com.kushalsharma.nasawalli.Models.Nasa
import com.kushalsharma.nasawalli.Models.Patent
import com.kushalsharma.nasawalli.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.fragment_main.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainFragment : Fragment() {

    lateinit var adapter: PatentAdapter

    var iod_description: String? = null
    var iod_title: String? = null
    var iod_ivUrl: String? = null

    var recyclerViewItems : RecyclerView? = null
    var progressLoaderAdp : ProgressBar? = null
    var progressLoaderPicofDay : ProgressBar? = null
    var imgOfTheDay : ImageView? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_main, container, false)

        recyclerViewItems = view.findViewById(R.id.rv_main) as RecyclerView
        progressLoaderAdp = view.findViewById(R.id.loader) as ProgressBar
        progressLoaderPicofDay = view.findViewById(R.id.imgOfDayProgressBar) as ProgressBar
        imgOfTheDay = view.findViewById(R.id.iv_picofDay) as ImageView

        progressLoaderPicofDay!!.visibility = View.VISIBLE

        view.toolbar_cardView_loader.setOnClickListener {
            //reload fragment or response and start anim
            view.toolbar_loader.playAnimation()
            view.toolbar_loader.speed = 1.5f
            (activity as MainActivity?)!!.showBanner(
                R.color.black,
                "   Loading...", 1000,
                R.drawable.ic_rocket
            )
            callingAdapter()
            callingImageOfTheDay()

        }

        callingImageOfTheDay()

        callingAdapter()



        view.layout_picOfDay.setOnClickListener {


//            toIotdFragment()

            val nasaIod = Nasa(
                "", iod_description.toString(), "", "", "",
                iod_title.toString(), iod_ivUrl.toString()
            )


            val action = MainFragmentDirections
                .actionMainFragmentToClickedPatentFragment2(
                    "null",
                    "null", "null", nasaIod
                )
            findNavController().navigate(action)

        }


        return view
    }

    private fun callingAdapter() {
        val nasaPatents = NasaService.nasaInterface.getNasaPatents()

        nasaPatents.enqueue(object : Callback<Patent>, iPostAdapter {
            override fun onResponse(call: Call<Patent>, response: Response<Patent>) {
                val nasaPatentsi = response.body()

                if (nasaPatentsi != null) {

                    adapter = PatentAdapter(this@MainFragment, nasaPatentsi, this)
                    recyclerViewItems!!.adapter = adapter
                }

            }

            override fun onFailure(call: Call<Patent>, t: Throwable) {
                Log.d("pERROR", "$t , $call")
                progressLoaderAdp!!.visibility = View.VISIBLE

            }

            override fun onItemClicked(title: String, descp: String, imgUrl: String) {

                val nasaIod = Nasa(
                    "", iod_description.toString(), "", "", "",
                    iod_title.toString(), iod_ivUrl.toString()
                )


                val action = MainFragmentDirections
                    .actionMainFragmentToClickedPatentFragment2(title, descp, imgUrl, nasaIod)
                findNavController().navigate(action)
            }


        })
    }

    private fun callingImageOfTheDay() {
        val nasa = NasaService.nasaInterface.getNasainfo()

        nasa.enqueue(object : Callback<Nasa> {
            override fun onResponse(call: Call<Nasa>, response: Response<Nasa>) {

                val nasai = response.body()
                if (nasai != null) {
                    progressLoaderPicofDay!!.visibility = View.VISIBLE

                    iod_title = nasai.title
                    iod_description = nasai.explanation
                    iod_ivUrl = nasai.url
                    Glide.with(this@MainFragment).load(nasai.url)
                        .transform(CenterCrop(), RoundedCorners(30))
                        .into(imgOfTheDay!!)
                    progressLoaderPicofDay!!.visibility = View.GONE
                } else {
                    progressLoaderPicofDay!!.visibility = View.VISIBLE
                }
            }

            override fun onFailure(call: Call<Nasa>, t: Throwable) {
                Log.d("ERROR", "$t , $call")
                progressLoaderPicofDay!!.visibility = View.VISIBLE
            }

        })


    }




}