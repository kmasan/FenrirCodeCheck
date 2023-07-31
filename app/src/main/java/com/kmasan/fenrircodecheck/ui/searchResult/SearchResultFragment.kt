package com.kmasan.fenrircodecheck.ui.searchResult

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.kmasan.fenrircodecheck.MainApplication
import com.kmasan.fenrircodecheck.model.GourmetSearchParameter
import com.kmasan.fenrircodecheck.model.RestaurantData
import com.kmasan.fenrircodecheck.ui.theme.FenrirCodeCheckTheme

class SearchResultFragment : Fragment() {
    companion object {
        private const val LATITUDE = "latitude"
        const val LONGITUDE = "longitude"
        const val RANGE = "range"

        fun newInstance(lat: Double, lon: Double, range: Int) = SearchResultFragment().apply {
            arguments = Bundle().apply {
                putDouble(LATITUDE, lat)
                putDouble(LONGITUDE, lon)
                putInt(RANGE, range)
            }
        }
    }

    private lateinit var mainApp: MainApplication
    private lateinit var viewModel: SearchResultViewModel
    private lateinit var apiParameter: GourmetSearchParameter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val lat = it.getDouble(LATITUDE)
            val lon = it.getDouble(LONGITUDE)
            val range = it.getInt(RANGE)

            apiParameter = GourmetSearchParameter(lat, lon, range)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mainApp = requireActivity().application as MainApplication
        viewModel = ViewModelProvider(this,
            SearchResultViewModelFactory(mainApp)
        )[SearchResultViewModel::class.java]

        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val restaurantData = viewModel.shopList.observeAsState()
                FenrirCodeCheckTheme{
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        ShopList(messages = restaurantData.value)
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.searchShop(apiParameter)
    }

    @Composable
    fun ShopList(messages: List<RestaurantData>?) {
        LazyColumn(modifier = Modifier.padding(8.dp)) {
            messages?.forEach {
                item { ShopListRow(it) }
            }
        }
    }

    @Composable
    fun ShopListRow(data: RestaurantData) {
        Text(text = "${data.name}")
    }

    @Composable
    fun ShopDetail(data: RestaurantData){

    }
}

