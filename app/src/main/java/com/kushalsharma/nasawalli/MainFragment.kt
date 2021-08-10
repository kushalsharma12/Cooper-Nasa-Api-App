package com.kushalsharma.nasawalli

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.fragment_main.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainFragment : Fragment() {

    lateinit var adapter: PatentAdapter

    var iod_description: String? = null
    var iod_title: String? = null
    var iod_ivUrl: String? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_main, container, false)


        val nasa = NasaService.nasaInterface.getNasainfo()
        nasa.enqueue(object : Callback<Nasa> {
            override fun onResponse(call: Call<Nasa>, response: Response<Nasa>) {

                val nasa = response.body()
                if (nasa != null) {

                    iod_title = nasa.title
                    iod_description = nasa.explanation
                    iod_ivUrl = nasa.url
                    Glide.with(this@MainFragment).load(nasa.url)

                        .transform(CenterCrop(),RoundedCorners(30))
                        .into(view.iv_picofDay)

                }


            }

            override fun onFailure(call: Call<Nasa>, t: Throwable) {
                Log.d("ERROR", "$t , $call")

            }

        })


        val nasaPatents = NasaService.nasaInterface.getNasaPatents()
        nasaPatents.enqueue(object : Callback<Patent>, iPostAdapter {
            override fun onResponse(call: Call<Patent>, response: Response<Patent>) {
                val nasaPatents = response.body()

                if (nasaPatents != null) {

                    adapter = PatentAdapter(this@MainFragment, nasaPatents,this)
                    view.rv_main.adapter = adapter

                }

            }

            override fun onFailure(call: Call<Patent>, t: Throwable) {
                Log.d("pERROR", "$t , $call")
            }

            override fun onItemClicked(title: String, descp : String, imgUrl : String) {

                val nasaIod = Nasa(
                    "", iod_description.toString(), "", "", "",
                    iod_title.toString(), iod_ivUrl.toString()
                )


                val action = MainFragmentDirections
                    .actionMainFragmentToClickedPatentFragment2(title,descp, imgUrl,nasaIod)
                findNavController().navigate(action)
            }


        })

        view.iv_picofDay.setOnClickListener {

//            toIotdFragment()

            val nasaIod = Nasa(
                "", iod_description.toString(), "", "", "",
                iod_title.toString(), iod_ivUrl.toString()
            )


            val action = MainFragmentDirections
                .actionMainFragmentToClickedPatentFragment2("null",
                    "null", "null",nasaIod)
            findNavController().navigate(action)

        }


        return view
    }

}