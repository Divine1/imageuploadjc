package com.prolearn.imageupload_jc

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.prolearn.imageupload_jc.ui.theme.ImageuploadjcTheme
import com.prolearn.imageupload_jc.viewmodel.AppViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.provider.MediaStore
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ImageuploadjcTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    Greeting("Android")
                }
            }
        }
    }
}

fun getFileName(uri : Uri,contentResolver : ContentResolver) : String{

    val cursor : Cursor? = contentResolver.query(uri,null,null,null,null);
    var filename:String="";
    if(cursor?.moveToFirst() == true){
        val nameIndex = cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME)
        filename = cursor.getString(nameIndex);

        cursor.close();
    }

    return filename;

}
@SuppressLint("UnrememberedMutableState")
@Composable
fun Greeting(name: String) {
    val context = LocalContext.current

    val appViewModel: AppViewModel = viewModel();


    var imageUri = remember {
        mutableStateOf<Uri?>(null)
    };
    var launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()){
        imageUri.value = it


    }

    var coroutinescope = rememberCoroutineScope();

    var launchMutatestate = remember {
        mutableStateOf("");
    }


    if("launch".equals(launchMutatestate.value)){
        Log.d("appdevelopment","launching effect")
        LaunchedEffect(key1 = "UploadFiles"){
            coroutinescope.launch(Dispatchers.IO) {

                var contentResolver : ContentResolver = context.contentResolver;

                //render picked image in android app in android phone
                val imageurl : Uri  = imageUri.value!!;
                Log.d("appdevelopment","imageurl $imageurl");

                val contentType  = contentResolver.getType(imageurl)
                //image/jpeg

                Log.d("appdevelopment","type!!");
                Log.d("appdevelopment",contentType!!);
                val fileName = getFileName(imageurl,contentResolver)
                Log.d("appdevelopment","fileName $fileName");

                val parcelFileDescriptor = contentResolver.openFileDescriptor(imageurl,"r",null);

                val inputStream = FileInputStream(parcelFileDescriptor?.fileDescriptor);
                val file = File(context.cacheDir,fileName);
                val outputStream = FileOutputStream(file);
                inputStream.copyTo(outputStream);



                val resdata = appViewModel.uploadFile(fileName,file,"divine",contentType);
                Log.d("appdevelopment","received response in ui")


                launchMutatestate.value="";
            }
        }
    }
    else{
        Log.d("appdevelopment","not launching effect")

    }


    
    Column() {
        Text(text = "Hello $name!")
        Button(onClick = {
            Log.d("appdevelop","picking")
            launcher.launch("image/*")
        }) {
            Text(text = "Pick image")
        }


        if(imageUri.value != null){
            Image(
                painter = rememberImagePainter(
                    data = imageUri.value,
                    builder = {
                        crossfade(true)
                        transformations(CircleCropTransformation())
                    }
                ),
                modifier = Modifier.size(72.dp),
                contentDescription = "content desc"
            )
        }

        Button(onClick = {
            Log.d("appdevelop","uploading")

            launchMutatestate.value="launch";
            Log.d("appdevelop",launchMutatestate.value)


        }) {
            Text(text = "Upload image")
        }
    }


}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ImageuploadjcTheme {
        Greeting("Android")
    }
}