package com.example.favbookscompose.di

import com.example.favbookscompose.network.BooksApi
import com.example.favbookscompose.repository.BookRepository
import com.example.favbookscompose.repository.FirebaseRepository
import com.example.favbookscompose.utils.Constants.BASE_URL
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun provideBookApi(): BooksApi {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(BooksApi::class.java)
    }

    @Singleton
    @Provides
    fun provideBookRepository(api: BooksApi) = BookRepository(api)

    @Singleton
    @Provides
    fun provideFirebaseBookRepository() =
        FirebaseRepository(
            queryBook = FirebaseFirestore.getInstance()
            .collection("books")
        )
}