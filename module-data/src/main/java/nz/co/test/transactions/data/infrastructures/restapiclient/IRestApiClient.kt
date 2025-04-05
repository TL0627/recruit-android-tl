package nz.co.test.transactions.data.infrastructures.restapiclient

import androidx.annotation.VisibleForTesting
import com.google.gson.Gson
import nz.co.test.transactions.data.datasources.remote.models.TransactionResponse
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface IRestApiClient {
    @GET("Josh-Ng/500f2716604dc1e8e2a3c6d31ad01830/raw/4d73acaa7caa1167676445c922835554c5572e82/test-data.json")
    suspend fun getTransactions(): List<TransactionResponse>

    companion object {
        @VisibleForTesting
        fun newInstance(baseUrl: HttpUrl, gson: Gson): IRestApiClient {
            val builder = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))

            return builder.build().create(IRestApiClient::class.java)
        }

        fun newInstance(okHttpClient: OkHttpClient, baseUrl: String, gson: Gson): IRestApiClient {
            val builder = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClient)

            return builder.build().create(IRestApiClient::class.java)
        }
    }
}
