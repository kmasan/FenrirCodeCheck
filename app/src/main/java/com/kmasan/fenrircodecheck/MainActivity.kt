package com.kmasan.fenrircodecheck

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.imageLoader
import coil.request.ImageRequest
import com.kmasan.fenrircodecheck.ui.searchCriteria.SearchCriteriaFragment
import com.kmasan.fenrircodecheck.ui.theme.FenrirCodeCheckTheme

class MainActivity : AppCompatActivity() {
    companion object{
        const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FenrirCodeCheckTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.secondary
                ) {
                    ConstraintLayout(
                        modifier = Modifier.fillMaxSize(),
                    ){
                        val (icon, credit) = createRefs()

                        Scaffold(
                            content = {
                                AndroidView(factory = { context ->
                                    FrameLayout(context).apply {
                                        id = View.generateViewId()
                                    }
                                }, update = {
                                    val fragment = SearchCriteriaFragment.newInstance()
                                    val transaction = supportFragmentManager.beginTransaction()
                                    transaction.replace(it.id, fragment)
                                    transaction.commit()
                                })
                            },
                            modifier = Modifier
                                .padding(4.dp)
                                .constrainAs(icon) {
                                    top.linkTo(parent.top)
                                    bottom.linkTo(credit.top)
                                    start.linkTo(parent.start)
                                    end.linkTo(parent.end)
                                },
                        )
                        
                        RecruitCredit(onClick = { /*TODO*/ }, context = applicationContext,
                        modifier = Modifier
                            .size(137.dp, 17.dp)
                            .constrainAs(credit) {
                                bottom.linkTo(parent.bottom, 4.dp)
                                end.linkTo(parent.end, 4.dp)
                            })
                    }
                }
            }
        }

        requestLocationPermission()
    }

    fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED){
            // 権限が許可されていない場合はリクエストする
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if ((grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED)) {
                    // 許可されなかった

                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Composable
fun RecruitCredit(onClick: ()->Unit, context: Context ,modifier: Modifier = Modifier) {
    IconButton(onClick = onClick,
        modifier = modifier
    ){
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data("http://webservice.recruit.co.jp/banner/hotpepper-s.gif")
                .build(),
            contentDescription = "RecruitCredit",
            contentScale = ContentScale.FillWidth,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    FenrirCodeCheckTheme {

    }
}