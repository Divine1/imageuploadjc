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


                    val type  = contentResolver.getType(imageurl)
                //image/jpeg

                Log.d("appdevelopment","type!!");

                Log.d("appdevelopment",type!!);
                var fileName : String="";
                val projection = arrayOf(MediaStore.MediaColumns.DISPLAY_NAME)

                val metaCursor: Cursor? = contentResolver.query(imageurl, projection, null, null, null)
                if (metaCursor != null) {
                    try {
                        if (metaCursor.moveToFirst()) {
                            fileName = metaCursor.getString(0)
                            var columnIndex = metaCursor.getColumnIndex(projection[0]);

                            Log.d("appdevelopment","columnIndex $columnIndex")

                            val picturePath: String = metaCursor.getString(columnIndex)
                            Log.d("appdevelopment","picturePath $picturePath")

                            // pexels-oliver-sjöström-1433052 (1).jpg
                            Log.d("appdevelopment",fileName);

                            Log.d("appdevelopment","columnNames");
                            Log.d("appdevelopment",""+metaCursor.count);


                        }
                    } finally {
                        metaCursor.close()
                    }
                }


                val resdata = appViewModel.uploadFile(imageUri.value,fileName,"divine");
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