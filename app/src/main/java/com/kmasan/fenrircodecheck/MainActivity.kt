package com.kmasan.fenrircodecheck

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.FrameLayout
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.kmasan.fenrircodecheck.searchCriteria.SearchCriteriaFragment
import com.kmasan.fenrircodecheck.ui.theme.FenrirCodeCheckTheme

class MainActivity : AppCompatActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val frameLayoutId = 1
            FenrirCodeCheckTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(
                        content = {
                            AndroidView(factory = { context ->
                                FrameLayout(context).apply {
                                    id = frameLayoutId
                                }
                            }, update = {
                                val fragment = SearchCriteriaFragment.newInstance()
                                val transaction = supportFragmentManager.beginTransaction()
                                transaction.replace(it.id, fragment)
                                transaction.addToBackStack(null)
                                transaction.commit()
                            })
                        }
                    )
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

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    FenrirCodeCheckTheme {
        Greeting("Android")
    }
}