package com.example.favbookscompose.screens.stats

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.sharp.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.favbookscompose.components.ReaderAppBar
import com.example.favbookscompose.model.Item
import com.example.favbookscompose.model.MBook
import com.example.favbookscompose.navigation.ReaderScreens
import com.example.favbookscompose.screens.home.HomeScreenViewModel
import com.example.favbookscompose.screens.search.BookRow
import com.example.favbookscompose.utils.formatDate
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ReaderStatsScreen(navController: NavController, viewModel: HomeScreenViewModel = hiltViewModel()) {
    var listBooks: List<MBook>
    val currentUser = FirebaseAuth.getInstance().currentUser
    Scaffold(
        topBar = {
            ReaderAppBar(
                title = "Book Stats",
                icon = Icons.Default.ArrowBack,
                showProfile = false,
                navController = navController
            ) {
                navController.popBackStack()
            }
        }
    ) {
        Surface {
            //only books by user that have been read
            listBooks = if(viewModel.data.value.data.isNullOrEmpty().not()) {
                viewModel.data.value.data?.filter { mBook ->
                    (mBook.userId == currentUser?.uid)
                }?: emptyList()
            } else {
                emptyList()
            }
            Column(
                modifier = Modifier.padding(top = 60.dp, start = 10.dp)
            ) {
                Row {
                    Box(
                        modifier = Modifier
                            .size(45.dp)
                            .padding(2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Sharp.Person,
                            contentDescription = "Person icon"
                        )
                    }
                    Text(
                        text = "Hi, ${
                            currentUser?.email.toString()
                                .split("@")[0].uppercase(Locale.getDefault())
                        }"
                    )
                }
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    shape = CircleShape,
                    elevation = CardDefaults.cardElevation(5.dp)
                ) {
                    val readBooksList: List<MBook> =
                        if (viewModel.data.value.data.isNullOrEmpty().not()) {
                            listBooks.filter { mBook ->
                                (mBook.userId == currentUser?.uid) && (mBook
                                    .finishedReading != null)
                            }
                        } else emptyList()
                    val readingBooks = readBooksList.filter { mBook ->
                        (mBook.startedReading != null && mBook.finishedReading != null)
                    }
                    Column(
                        modifier = Modifier
                            .padding(start = 25.dp, top = 4.dp, bottom = 4.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = "Your Stats",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Divider()
                        Text(text = "You're reading: ${readingBooks.size} books")
                        Text(text = "You're read: ${readingBooks.size} books")
                    }
                }
                if(viewModel.data.value.loading == true) {
                    //LinearProgressIndicator()
                } else {
                    Divider()
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        //filter books by finish ones
                        val readBooks: List<MBook> = if(viewModel.data.value.data.isNullOrEmpty().not()) {
                            viewModel.data.value.data?.filter { mBook ->
                                (mBook.userId == currentUser?.uid) && (mBook.finishedReading != null)
                            } ?: emptyList()
                        } else {
                            emptyList()
                        }
                        items(items = readBooks) { book->
                            BookStats(book = book)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BookStats(book: MBook) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(3.dp),
        shape = RectangleShape,
        elevation = CardDefaults.cardElevation(7.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(5.dp),
            verticalAlignment = Alignment.Top
        ) {
            //val imageUrl = "http://books.google.com/books/content?id=4dkuBQAAQBAJ&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api"
            val imageUrl = book?.photoUrl.toString().apply {
                if (this.isNullOrEmpty())
                    "http://books.google.com/books/content?id=4dkuBQAAQBAJ&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api"
                else this
            }
            Image(
                painter = rememberImagePainter(
                    data = imageUrl
                ),
                contentDescription = "Book image",
                modifier = Modifier
                    .width(80.dp)
                    .fillMaxHeight()
                    .padding(4.dp)
            )
            Column {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = book.title.toString(), overflow = TextOverflow.Ellipsis)
                    if((book?.rating!!>=4)) {
                        Spacer(
                            modifier = Modifier
                            .fillMaxWidth(0.8f)
                        )
                        Icon(
                            imageVector = Icons.Default.ThumbUp,
                            contentDescription = "Thumbs up icon",
                            tint = Color.Green.copy(alpha = 0.5f)
                        )
                    } else Box {}
                }
                Text(
                    text = "Author: ${book.authors}",
                    overflow = TextOverflow.Clip,
                    fontStyle = FontStyle.Italic,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Started: ${formatDate(book.startedReading?: Timestamp.now())}",
                    softWrap = true,
                    overflow = TextOverflow.Clip,
                    fontStyle = FontStyle.Italic,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Finished: ${formatDate(book.finishedReading?: Timestamp.now())}",
                    overflow = TextOverflow.Clip,
                    fontStyle = FontStyle.Italic,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}