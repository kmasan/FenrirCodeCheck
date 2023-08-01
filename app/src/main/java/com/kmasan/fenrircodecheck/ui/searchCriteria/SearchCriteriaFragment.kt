package com.kmasan.fenrircodecheck.ui.searchCriteria

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.kmasan.fenrircodecheck.model.GourmetSearchParameter
import com.kmasan.fenrircodecheck.ui.searchResult.SearchResultFragment
import com.kmasan.fenrircodecheck.ui.theme.FenrirCodeCheckTheme
import kotlinx.coroutines.launch

class SearchCriteriaFragment: Fragment() {
    private val viewModel: SearchCriteriaViewModel by viewModels { SearchCriteriaViewModel.factory(requireActivity()) }
    private lateinit var mBackPressedCallback: OnBackPressedCallback

    private val ranges = mutableStateOf("1000m")
    private val expandFragment = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(this.javaClass.name, "onCreate")

//        viewModel = ViewModelProvider(this,
//            SearchCriteriaViewModel.factory(requireActivity())
//        )[SearchCriteriaViewModel::class.java]

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                Log.d(this@SearchCriteriaFragment.javaClass.name, "viewModel.uiState.value: ${viewModel.uiState.value}")
                expandFragment.value = viewModel.uiState.value
                viewModel.startGPSLogger()
                viewModel.setLastLocation()
            }
        }
    }
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val range = remember { ranges }
                val apiParameter by viewModel.parameter.observeAsState()
                FenrirCodeCheckTheme{
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.secondary
                    ) {
                        Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                        ) {
//                            Text(text = "lat: 35.171126")
//                            Text(text = "lng: 136.909612")
                            Dropdown(
                                "range",
                                listOf(
                                    "300m",
                                    "500m",
                                    "1000m",
                                    "2000m",
                                    "3000m"
                                ),
                                range
                            )

                            Button(onClick = {
                                Log.d(this.javaClass.name, "$ranges, $range")
                                expandFragment.value = false
                                viewModel.searchShop(when(ranges.value){
                                    "300m" -> 1
                                    "500m" -> 2
                                    "2000m" -> 4
                                    "3000m" -> 5
                                    else -> 3
                                })
                            }) {
                                Text("Search")
                            }
                        }
                        if(expandFragment.value && apiParameter != null){
                            SetFragment(apiParameter!!, modifier = Modifier.wrapContentSize())
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
                Log.d(this@SearchCriteriaFragment.javaClass.name, "handleOnBackPressed")
                if(expandFragment.value){
                    expandFragment.value = false
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, mBackPressedCallback)

        viewModel.parameter.observe(viewLifecycleOwner){
            Log.d(this.javaClass.name, "$it")
            expandFragment.value = true
            viewModel.resultFragmentExpand(true)
        }
    }

    companion object{
        fun newInstance() = SearchCriteriaFragment()
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun SetFragment(parameter: GourmetSearchParameter ,modifier: Modifier = Modifier){
        AndroidView(modifier = modifier, factory = { context ->
            FragmentContainerView(context).apply {
                val frameId = 2
                id = frameId
                setBackgroundColor(Color.Red.hashCode())
            }
        }, update = {
            val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()
            transaction.replace(it.id, SearchResultFragment.newInstance(
                parameter.lat,
                parameter.lng,
                parameter.range
            ))
            transaction.addToBackStack(null)
            transaction.commit()
        })
    }

    @Composable
    fun Dropdown(label: String, options: List<String>, selectedOptionText: MutableState<String>) {
        val expanded = remember { mutableStateOf(false) }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = "$label: ", fontSize = 20.sp)
            Box(
                contentAlignment = Alignment.CenterStart,
                modifier = Modifier
                    .size(100.dp, 50.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .border(BorderStroke(1.dp, Color.LightGray), RoundedCornerShape(4.dp))
                    .clickable { expanded.value = !expanded.value },
            ) {
                Text(
                    text = selectedOptionText.value,
                    modifier = Modifier.padding(start = 10.dp)
                )
                Icon(
                    Icons.Filled.ArrowDropDown, "contentDescription",
                    Modifier.align(Alignment.CenterEnd)
                )
                DropdownMenu(
                    expanded = expanded.value,
                    onDismissRequest = { expanded.value = false }
                ) {
                    options.forEach { selectionOption ->
                        DropdownMenuItem(
                            text = { Text(text = selectionOption) },
                            onClick = {
                                selectedOptionText.value = selectionOption
                                expanded.value = false
                            }
                        )
                    }
                }
            }
        }
    }
}

