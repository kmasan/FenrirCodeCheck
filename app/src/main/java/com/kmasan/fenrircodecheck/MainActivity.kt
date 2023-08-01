package com.kmasan.fenrircodecheck

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.compose.setContent
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentContainerView
import coil.compose.AsyncImage
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
        Log.d(javaClass.name, "onCreate")
        setContent {
            FenrirCodeCheckTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ConstraintLayout(
                        modifier = Modifier.fillMaxSize(),
                    ){
                        val (icon, credit) = createRefs()

                        // 初期画面の検索条件の設定画面のFragmentを表示
                        Scaffold(
                            content = {
                                AndroidView(factory = { context ->
                                    FragmentContainerView(context).apply {
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

                        // API利用の際の表示義務対応
                        RecruitCredit(onClick = {
                            val url = "http://webservice.recruit.co.jp/"
                            try {
                                Intent(Intent.ACTION_VIEW).also {
                                    it.data = Uri.parse(url)
                                    startActivity(it)
                                }
                            } catch (e: ActivityNotFoundException) {
                                Log.d(javaClass.name, "can't open: $url")
                            }
                        }, modifier = Modifier
                            .constrainAs(credit) {
                                bottom.linkTo(parent.bottom, 4.dp)
                                end.linkTo(parent.end, 4.dp)
                            })
                    }
                }
            }
        }

        // Permissionチェック
        requestLocationPermission()
    }

    // Permissionチェック
    private fun requestLocationPermission() {
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

    // Permissionの許可がされたか確認
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
                    Log.d(javaClass.name, "disable permission")
                }else{
                    // 許可された　GPSの利用のために一度アプリを再起動
                    recreate()
                }
            }
        }
    }

    @Composable
    // API利用の際の表示義務対応
    fun RecruitCredit(onClick: ()->Unit, modifier: Modifier = Modifier) {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.End
        ) {
            IconButton(onClick = onClick,
                modifier = Modifier.size(137.dp, 17.dp)
            ){
                AsyncImage(
                    model = ImageRequest.Builder(applicationContext)
                        .data("http://webservice.recruit.co.jp/banner/hotpepper-s.gif")
                        .build(),
                    contentDescription = "RecruitCredit",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(137.dp, 17.dp)
                )
            }
            Text(text = "画像提供：ホットペッパー グルメ", fontSize = 8.sp)
        }
    }
}

