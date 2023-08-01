package com.kmasan.fenrircodecheck.ui.searchResult

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
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
import androidx.lifecycle.ViewModelProvider
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.kmasan.fenrircodecheck.model.GourmetSearchParameter
import com.kmasan.fenrircodecheck.model.ShopData
import com.kmasan.fenrircodecheck.ui.searchCriteria.SearchCriteriaViewModel
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
    private val searchCriteriaViewModel: SearchCriteriaViewModel by viewModels()
    private lateinit var apiParameter: GourmetSearchParameter
    private lateinit var mBackPressedCallback: OnBackPressedCallback

    private val expandDetail = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val lat = it.getDouble(LATITUDE)
            val lon = it.getDouble(LONGITUDE)
            val range = it.getInt(RANGE)

            apiParameter = GourmetSearchParameter(lat, lon, range)
        }

//        viewModel = ViewModelProvider(this,
//            SearchResultViewModel.factory(requireActivity())
//        )[SearchResultViewModel::class.java]
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
                FenrirCodeCheckTheme{
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        if(shopData.value != null) {
                            ShopList(list = shopData.value!!)
                        }
                        if(expandDetail.value){
                            selectShopData.value?.let { ShopDetail(it) }
                        }
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBackPressedCallback = object :OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                if(expandDetail.value){
                    expandDetail.value = false
                }else{
                    val transaction: FragmentManager = parentFragmentManager
                    transaction.popBackStack()
//                    searchCriteriaViewModel.resultFragmentExpand(false)
                }
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, mBackPressedCallback)
        viewModel.searchShop(apiParameter)
    }

    @Composable
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

        val listEnd by remember {
            derivedStateOf {
                listState.firstVisibleItemIndex >= list.size - 15
            }
        }

        if (listEnd){
            viewModel.addSearchShop(apiParameter)
        }
    }

    @Composable
    fun ShopListRow(data: ShopData, modifier: Modifier = Modifier) {
        Button(
            onClick = {
                        viewModel.setSelectShopData(data)
                        expandDetail.value = true
                      },
            modifier = modifier,
            colors = ButtonDefaults.textButtonColors(
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.onBackground
            ),
            shape = RectangleShape,
        ) {
            Column {
                Row(modifier = Modifier, verticalAlignment = Alignment.CenterVertically) {
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
    fun ShopDetail(data: ShopData){
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
            Column(modifier = Modifier.padding(4.dp)) {
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
            }
        }
    }
}

