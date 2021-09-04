package com.prolearn.imageupload_jc.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.prolearn.imageupload_jc.model.UploadModel
import com.prolearn.imageupload_jc.repository.AppRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.HttpException
import java.io.File


class AppViewModel : ViewModel{
    private val appRepository : AppRepository;
    constructor(){
        Log.d("appdevelopment","AppViewModel constructor");

    }
    init{
        appRepository = AppRepository();
        Log.d("appdevelopment","AppViewModel init");

    }


    suspend fun uploadFile(filename : String,file:File,somedate:String,contentType : String){


        Log.d("appdevelopment","AppViewModel uploadFile");
        Log.d("appdevelopment","AppViewModel contentType ${contentType}");

        //var requestBody : RequestBody = RequestBody.create(MediaType.parse("image/*"),file);
        var requestBody : RequestBody = RequestBody.create(MediaType.parse(contentType),file);

        var parts : MultipartBody.Part = MultipartBody.Part.createFormData("avatar",filename,requestBody);
        var somedate : String = somedate


        Log.d("appdevelopment", " absolutePath ${file.absolutePath} ");

        Log.d("appdevelopment"," filename ${file.name}");


        try{
           val responsedata =  appRepository.uploadFile(parts ,somedate );
            Log.d("appdevelopment",responsedata.toString());
        }
        catch(httpException : HttpException){

            Log.d("appdevelopment","getCricket httpException");

            Log.d("appdevelopment",""+httpException.code());
            Log.d("appdevelopment",""+httpException.message());
            Log.d("appdevelopment",""+httpException.response());

            val errorBody: ResponseBody? =  httpException.response()?.errorBody()

            val adapter = Gson().getAdapter(UploadModel::class.java)
            val errorParser = adapter.fromJson(errorBody?.string())

            Log.d("appdevelopment",errorParser.toString());
            Log.d("appdevelopment",errorParser.data);

        }
        catch(e : Exception){
            Log.d("appdevelopment","getCricket exception");
            Log.d("appdevelopment",e.toString());
            val e_message = e.message.orEmpty()
            Log.d("appdevelopment",e_message)

            e.printStackTrace()
        }


    }
}

