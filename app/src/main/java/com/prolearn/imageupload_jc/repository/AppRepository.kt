package com.prolearn.imageupload_jc.repository

import com.prolearn.imageupload_jc.model.UploadModel
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import java.util.concurrent.TimeUnit


class AppRepository {

    interface UploadApi{

        @Multipart
        @POST("profile")
        //suspend fun uploadFile(@Part  part : MultipartBody.Part, @Part("somedate")  requestBody : RequestBody): UploadModel
        suspend fun uploadFile(@Part  part : MultipartBody.Part, @Part("somedate")  somedate : String): UploadModel
    }
    private lateinit var apiobj : UploadApi;

    init {
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(1, TimeUnit.MINUTES)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build();

        val retrofit = Retrofit.Builder()
            //.baseUrl("https://apiprojectdemo.wdc-np.tas.vmware.com/sports/")
            .baseUrl("http://192.168.1.5:3000/sports/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build();


        apiobj = retrofit.create(UploadApi::class.java);
    }

    suspend fun uploadFile(part : MultipartBody.Part,somedate : String) : UploadModel {
        return apiobj.uploadFile(part,somedate);
    }
}