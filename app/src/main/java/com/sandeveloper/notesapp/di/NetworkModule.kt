package com.sandeveloper.notesapp.di

import android.content.Context
import com.sandeveloper.notesapp.api.AuthInterceptor
import com.sandeveloper.notesapp.api.NotesAPI
import com.sandeveloper.notesapp.api.UserAPI
import com.sandeveloper.notesapp.utils.Constants.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.Retrofit.Builder
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

//setup to create retrofit object
@InstallIn(SingletonComponent::class)
@Module
class NetworkModule {
    private var context: Context? = null

    fun AppModule(context: Context?) {
        this.context = context
    }

    @Singleton
    @Provides
    fun providesRetrofitBuilder():Retrofit.Builder{
        return Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create())
    }
    //for header purpose
    @Singleton
    @Provides
    fun providesOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient{
        return OkHttpClient.Builder().addInterceptor(authInterceptor).build()
    }

    @Singleton
    @Provides
    fun providesUserAPI(retrofitBuilder: Retrofit.Builder): UserAPI {
         return retrofitBuilder.build().create(UserAPI::class.java)
    }


    //adding header NotesAPI adding client for authentication
    @Singleton
    @Provides
    fun providesNotesAPI(retrofitBuilder: Builder,okHttpClient: OkHttpClient):NotesAPI{
        return retrofitBuilder.client(okHttpClient).build().create(NotesAPI::class.java)
    }

    @Provides
    @Singleton
    fun provideContext(): Context? {
        return context
    }
}