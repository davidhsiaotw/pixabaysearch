package com.example.pixabaysearch

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.compose.collectAsLazyPagingItems
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.pixabaysearch.ui.theme.PixabaySearchTheme
import com.google.mlkit.nl.languageid.LanguageIdentification
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val photoViewModel: PhotoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PixabaySearchTheme {
                MainScreen(photoViewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalGlideComposeApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(viewModel: PhotoViewModel) {
//    val apiKey = BuildConfig.API_KEY
    var text by rememberSaveable { mutableStateOf("") }
    var active by rememberSaveable { mutableStateOf(false) }
    var display by rememberSaveable { mutableStateOf("List") }
    var history by rememberSaveable { mutableStateOf(mutableSetOf<String>()) }
    val photos: List<Photo> by viewModel.photos.observeAsState(initial = emptyList())
//    val test = viewModel.test.collectAsLazyPagingItems()

    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            viewModel.searchPhotos(
                mapOf(
                    "key" to BuildConfig.API_KEY, "q" to "", "lang" to "en", "page" to 1
                )
            )
        }
    }

    Log.d("MainScreen", "There are ${photos.size} photos")
    Scaffold() {
        Column(modifier = Modifier.fillMaxSize()) {
            SearchBar(
                modifier = Modifier.fillMaxWidth(),
                query = text,
                onQueryChange = {
                    text = it
//                    Log.d("MainScreen", "Searching $text...")
                },
                onSearch = {
                    history.add(it)
                    active = false
                    var language = "en"
                    // identify input text's language
                    LanguageIdentification.getClient().identifyLanguage(it)
                        .addOnSuccessListener { languageCode ->
                            if (languageCode == "und") {
                                Log.d("DetectLanguage", "Can't identify language.")
                            } else {
                                Log.d("DetectLanguage", "Language: $languageCode")
                                language = languageCode
                            }
                        }.addOnFailureListener { e ->
                            Log.d("DetectLanguage", "$e")
                        }
                    // search for photos
                    coroutineScope.launch {
                        viewModel.searchPhotos(
                            mapOf(
                                "key" to BuildConfig.API_KEY,
                                "q" to text,
                                "lang" to language,
                                "page" to 1
                            )
                        )
                    }
                },
                active = active,
                onActiveChange = { active = it },
                placeholder = { Text("Search") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search, contentDescription = "Search Icon"
                    )
                },
                trailingIcon = {
                    if (active) {
                        Icon(
                            modifier = Modifier.clickable {
                                if (text.isNotEmpty()) {
                                    text = ""
                                } else {
                                    active = false
                                }
                            },
                            imageVector = Icons.Default.Close, contentDescription = "Close Icon"
                        )
                    }
                }
            ) {
                history.forEach {
                    Row(modifier = Modifier
                        .padding(12.dp)
                        .fillMaxWidth()
                        .clickable {
                            text = it
                        }) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "History Icon",
                            modifier = Modifier.padding(end = 12.dp)
                        )
                        Text(text = it)
                    }
                }
            }

            FilledTonalButton(
                onClick = {
                    if (display == "List") {
                        display = "Grid"
                    } else if (display == "Grid") {
                        display = "List"
                    }
                }, modifier = Modifier
                    .padding(6.dp)
                    .align(Alignment.End)
            ) {
                Text(text = display)
            }

            Photos(display = display, photos)

        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun Photos(display: String, photos: List<Photo>) {
    // photos is empty before successfully connecting to API,
    if (photos.isEmpty()) {
        Row(modifier = Modifier.fillMaxSize()) {
            Text(text = "No Results Found", fontSize = 24.sp)
        }
        return
    }
    if (display == "Grid") {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(120.dp), contentPadding = PaddingValues(6.dp)
        ) {
            items(photos) {
                Card(
                    Modifier
                        .padding(6.dp)
                        .aspectRatio(1F),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                ) {
                    GlideImage(
                        model = it.webformatUrl,
                        modifier = Modifier
                            .fillMaxSize()
                            .align(Alignment.CenterHorizontally),
                        contentDescription = "Photo",
                        contentScale = ContentScale.Crop
                    )
                }

            }
        }
    } else if (display == "List") {
        LazyColumn {
            items(photos) {
                Card(
                    Modifier
                        .padding(6.dp)
                        .fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                ) {
                    GlideImage(
                        model = it.webformatUrl,
                        modifier = Modifier.size(120.dp),
                        contentDescription = "Photo"
                    )
                }

            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SearchPreview() {
    PixabaySearchTheme {
        MainScreen(PhotoViewModel())
    }
}