package com.kushalsharma.nasawalli

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET


const val BASE_URL = "https://api.nasa.gov/"
const val API_KEY = "AAZq7v3WHXOKIRC0MGeTLSkSbOyK4YE7xk3wbefH"

interface NasaInterface {

    @GET("planetary/apod?api_key=$API_KEY")
    fun getNasainfo(): Call<Nasa>

    @GET("techtransfer/patent/?telescope&api_key=$API_KEY")
    fun getNasaPatents(): Call<Patent>

}
//    https://api.nasa.gov/planetary/apod?api_key=AAZq7v3WHXOKIRC0MGeTLSkSbOyK4YE7xk3wbefH

//    https://api.nasa.gov/techtransfer/patent/?engine&api_key=AAZq7v3WHXOKIRC0MGeTLSkSbOyK4YE7xk3wbefH

object NasaService {
    val nasaInterface: NasaInterface

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        nasaInterface = retrofit.create(NasaInterface::class.java)
    }
}