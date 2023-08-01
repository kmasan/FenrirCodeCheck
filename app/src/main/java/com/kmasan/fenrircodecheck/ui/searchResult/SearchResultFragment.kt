package com.kmasan.fenrircodecheck.ui.searchResult

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.kmasan.fenrircodecheck.model.GourmetSearchParameter
import com.kmasan.fenrircodecheck.model.ShopData
import com.kmasan.fenrircodecheck.ui.theme.FenrirCodeCheckTheme

class SearchResultFragment : Fragment() {
    companion object {
        private const val LATITUDE = "latitude"
        private const val LONGITUDE = "longitude"
        private const val RANGE = "range"

        fun newInstance(lat: Double, lon: Double, range: Int) = SearchResultFragment().apply {
            arguments = Bundle().apply {
                putDouble(LATITUDE, lat)
                putDouble(LONGITUDE, lon)
                putInt(RANGE, range)
            }
        }
    }

    private val viewModel: SearchResultViewModel by viewModels { SearchResultViewModel.factory(requireActivity()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 検索条件を整理
        arguments?.let {
            val lat = it.getDouble(LATITUDE)
            val lon = it.getDouble(LONGITUDE)
            val range = it.getInt(RANGE)

            viewModel.apiParameter = GourmetSearchParameter(lat, lon, range)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val shopData = viewModel.shopList.observeAsState()
                val selectShopData = viewModel.selectShopData.observeAsState()
                val expandDetail by remember { viewModel.expandDetail }
                FenrirCodeCheckTheme{
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        if(shopData.value != null) {
                            // 検索結果を表示
                            ShopList(list = shopData.value!!)
                        }
                        if(expandDetail){
                            // 店の詳細情報を表示
                            selectShopData.value?.let { ShopDetail(it) }
                        }
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 戻るボタンの挙動を設定
        val mBackPressedCallback = object :OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                // 店の詳細画面が表示されているか
                if(viewModel.expandDetail.value){
                    // 表示されていたら検索結果画面に戻る
                    viewModel.expandDetail.value = false
                }else{
                    // 表示されていなかったら検索条件設定画面に戻る
                    val transaction: FragmentManager = parentFragmentManager
                    transaction.popBackStack()
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, mBackPressedCallback)

        // 条件から検索
        viewModel.searchShop(viewModel.apiParameter)
    }

    @Composable
    // 検索結果をリストで表示
    fun ShopList(list: List<ShopData>) {
        val listState = rememberLazyListState()
        LazyColumn(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.Start,
            state = listState,
        ) {
            list.forEach {
                item {
                    ShopListRow(
                        it, modifier = Modifier
                            .wrapContentHeight()
                            .fillMaxWidth()
                    )
                }
            }
        }

        // リストの終端に近いかどうか
        val listEnd by remember {
            derivedStateOf {
                listState.firstVisibleItemIndex >= list.size - 15
            }
        }

        // リストの終端に近い場合は追加の検索結果を取得
        if (listEnd){
            viewModel.addSearchShop(viewModel.apiParameter)
        }
    }

    @Composable
    // リストの１要素分
    fun ShopListRow(data: ShopData, modifier: Modifier = Modifier) {
        // ボタンでその店の詳細を表示
        Button(
            onClick = {
                viewModel.setSelectShopData(data)
                viewModel.expandDetail.value = true },
            modifier = modifier,
            colors = ButtonDefaults.textButtonColors(
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.onBackground
            ),
            shape = RectangleShape,
        ) {
            // 表示要素：サムネイル，店舗名，アクセス
            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(
                        model = ImageRequest.Builder(requireActivity())
                            .data(data.thumbnailURL)
                            .build(),
                        contentDescription = "thumbnail",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .size(80.dp)
                    )
                    Text(text = "店舗名: ${data.name}")
                }
                Text(text = "アクセス: ${data.access}")
            }
        }
    }

    @Composable
    // 店の詳細画面
    fun ShopDetail(data: ShopData){
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
            // 表示要素：店舗名，画像，住所，営業時間
            Column(modifier = Modifier.padding(top = 12.dp)) {
                Text(text = "店舗名: ${data.name}")
                AsyncImage(
                    model = ImageRequest.Builder(requireActivity())
                        .data(data.shopTopPhoto)
                        .build(),
                    contentDescription = "thumbnail",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(text = "住所: ${data.address}")
                Text(text = "営業時間: ${data.open}")
                Button(onClick = { openMap(data.name) }) {
                    Text(text = "Open GoogleMap")
                }
            }
        }
    }

    private fun openMap(address: String){
        val url = "https://www.google.com/maps/search/?api=1&query=$address"
        try {
            Intent(Intent.ACTION_VIEW).also {
                it.data = Uri.parse(url)
                startActivity(it)
            }
        } catch (e: ActivityNotFoundException) {
            Log.d(javaClass.name, "can't open: $url")
        }
    }
}

