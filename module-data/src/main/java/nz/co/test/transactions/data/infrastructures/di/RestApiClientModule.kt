package nz.co.test.transactions.data.infrastructures.di

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import nz.co.test.transactions.data.infrastructures.restapiclient.HttpConfig
import nz.co.test.transactions.data.infrastructures.restapiclient.IRestApiClient
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RestApiClientModule {

    @Provides
    fun provideRestApiClient(okHttpClient: OkHttpClient, gson: Gson) : IRestApiClient {
        return IRestApiClient.newInstance(
            okHttpClient, HttpConfig.REST_ENDPOINT_BASE_URL, gson,
        )
    }

    @Singleton
    @Provides
    fun providePostAuthOkHttpClient(
        @ApplicationContext context: Context,
    ): OkHttpClient {
        return createHttpClient(
            context,
        )
    }

    private fun createHttpClient(context: Context, vararg interceptors: Interceptor): OkHttpClient {
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(HttpConfig.CONNECTION_TIME_OUT_IN_SECOND, TimeUnit.SECONDS)
            .readTimeout(HttpConfig.READ_WRITE_TIME_OUT_IN_SECOND, TimeUnit.SECONDS)
            .writeTimeout(HttpConfig.READ_WRITE_TIME_OUT_IN_SECOND, TimeUnit.SECONDS).apply {
                interceptors.map { interceptor -> addInterceptor(interceptor) }
            }

        return okHttpClient.build()
    }

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            //"2022-02-17T10:44:35"
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            //register TypeAdapters and any other configurations if required
            .create()
    }
}

