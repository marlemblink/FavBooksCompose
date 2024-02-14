
package com.example.favbookscompose.screens.home

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.favbookscompose.components.FABContent
import com.example.favbookscompose.components.ListCard
import com.example.favbookscompose.components.ReaderAppBar
import com.example.favbookscompose.components.TitleSection
import com.example.favbookscompose.model.MBook
import com.example.favbookscompose.navigation.ReaderScreens
import com.google.firebase.auth.FirebaseAuth

//@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(
    navController: NavController = NavController(LocalContext.current),
    homeViewModel: HomeScreenViewModel = hiltViewModel()
) {
    Scaffold(
        topBar = {
          ReaderAppBar(title = "A.Reader", navController = navController)
        },
        floatingActionButton = {
            FABContent {
                navController.navigate(ReaderScreens.SearchScreen.name)
            }
        }
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            HomeContent(navController = navController, viewModel = homeViewModel)
        }
    }
}

@Composable
fun HomeContent(navController: NavController, viewModel: HomeScreenViewModel) {
    var listOfBooks = emptyList<MBook>()
    val currentUser = FirebaseAuth.getInstance().currentUser
    if(viewModel.data.value.data.isNullOrEmpty().not()) {
        listOfBooks = viewModel.data.value?.data?.toList()?.filter { mBook->
            mBook.userId == currentUser?.uid.toString()
        } ?: emptyList()
        Log.d("*******","HomeContent: ${listOfBooks.toString()}")
    }
    /*val listOfBooks = listOf(
        MBook(id = "1234", title = " Blackpink", authors = "bp", notes = null),
        MBook(id = "1235", title = " Twice", authors = "tw", notes = null),
        MBook(id = "1236", title = " BTS", authors = "bts", notes = null),
        MBook(id = "1237", title = " IVE", authors = "ive", notes = null),
        MBook(id = "1238", title = " Stray Kids", authors = "sk", notes = null),
        MBook(id = "1239", title = " New Jeans", authors = "nj", notes = null)
    )*/
    val email = FirebaseAuth.getInstance().currentUser?.email
    val currentUserName = if(email.isNullOrEmpty().not()) email?.split("@")?.get(0) else "N/A"
    Column(
        Modifier
            .padding(2.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Row(
            modifier = Modifier
                .align(alignment = Alignment.Start)
        ) {
            TitleSection(label = "Your reading \n" + "activity right now..")
            Spacer(modifier = Modifier.fillMaxWidth(0.7f))
            Column {
                Icon(
                    imageVector = Icons.Filled.AccountCircle,
                    contentDescription = "Profile icon",
                    modifier = Modifier
                        .clickable {
                            navController.navigate(ReaderScreens.ReaderStatsScreen.name)
                        }
                        .size(45.dp),
                    tint = MaterialTheme.colorScheme.secondary
                )
                Text(text = currentUserName?:"",
                    modifier = Modifier
                        .padding(2.dp),
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.Red,
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Clip)
                Divider()
            }
        }
        ReadingRightNowArea(books = listOfBooks, navController = navController)
        TitleSection(label = "Reading List")
        BookListArea(listOfBooks = listOfBooks, navController = navController)
    }
}

@Composable
fun BookListArea(listOfBooks: List<MBook>, navController: NavController) {
    val addedBooks = listOfBooks.filter { mBook ->
        mBook.startedReading == null && mBook.finishedReading == null
    }
    HorizontalScrollableComponent(listOfBooks) {
        //on card click nav to details
        navController.navigate(ReaderScreens.UpdateScreen.name+"/$it")
        Log.d("*******", "click on details book: $it ")
    }
}

@Composable
fun HorizontalScrollableComponent(listOfBooks: List<MBook>, viewModel: HomeScreenViewModel = hiltViewModel(), onCardPressed: (String) -> Unit) {
    val scrollState = rememberScrollState()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(280.dp)
            .horizontalScroll(scrollState)
    ) {
        if(viewModel.data.value.loading == true) {
            //LinearProgressIndicator()
        } else {
            if(listOfBooks.isNullOrEmpty()) {
                Surface(
                    modifier = Modifier.padding(23.dp)
                ) {
                    Text(
                        text = "No books found, Add a Book",
                        style = TextStyle(
                            color = Color.Red.copy(alpha = 0.4f),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    )
                }
            } else {
                for(book in listOfBooks) {
                    ListCard(book) {
                        onCardPressed(book.googleBookId.toString())
                    }
                }
            }
        }
    }
}

@Composable
fun ReadingRightNowArea(
    books: List<MBook>, navController: NavController
) {
    //books reading now
    val readingNowList = books.filter { mBook->
        mBook.startedReading != null && mBook.finishedReading == null
    }
    HorizontalScrollableComponent(listOfBooks = readingNowList) {
        Log.d("******","BookListArea: $it")
        navController.navigate(ReaderScreens.UpdateScreen.name + "/$it")
    }
    //ListCard(books)
}
