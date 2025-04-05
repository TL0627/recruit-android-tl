package nz.co.test.transactions.data

import android.util.Log
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import nz.co.test.transactions.data.datasources.remote.ITransactionRemoteDatasource
import nz.co.test.transactions.data.datasources.remote.impl.TransactionRemoteDatasourceImpl
import nz.co.test.transactions.data.infrastructures.di.RestApiClientModule
import nz.co.test.transactions.data.infrastructures.restapiclient.IRestApiClient
import nz.co.test.transactions.data.infrastructures.restapiclient.RestApiException
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class RemoteDatasourceTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var restApiClient: IRestApiClient
    private lateinit var dataSource: ITransactionRemoteDatasource

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        restApiClient = IRestApiClient.newInstance(
            baseUrl = mockWebServer.url("/"),
            gson = RestApiClientModule.provideGson()
        )

        dataSource = TransactionRemoteDatasourceImpl(restApiClient)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun return200() = runTest {
        //mock transactions
        val mockResponse = MockResponse()
            .setBody("""
                [
                    {
                        "id": 1,
                        "transactionDate": "2021-08-31T15:47:10",
                        "summary": "Hackett, Stamm and Kuhn",
                        "debit": 9379.55,
                        "credit": 0
                    },
                    {
                        "id": 2,
                        "transactionDate": "2022-02-17T10:44:35",
                        "summary": "Hettinger, Wilkinson and Kshlerin",
                        "debit": 3461.35,
                        "credit": 0
                    }
                ]
            """.trimIndent())
            .setResponseCode(200)
        mockWebServer.enqueue(mockResponse)

        val response = dataSource.getTransactions()
            .onFailure{
                Log.e("", it.localizedMessage ?: "unknown error message")
            }.getOrNull()

        assertNotNull(response)
        assertEquals(2, response.size)
        assertEquals(1, response[0].id)
        assertEquals(2, response[1].id)
    }

    @Test
    fun return404() = runTest {
        val mockResponse = MockResponse()
            .setResponseCode(404)
        mockWebServer.enqueue(mockResponse)

        val exception = dataSource.getTransactions().exceptionOrNull()
        assertNotNull(exception)
        assertTrue( exception is RestApiException.TransactionResourceNotFoundException)
    }

    @Test
    fun return500() = runTest {
        val mockResponse = MockResponse()
            .setResponseCode(500)
        mockWebServer.enqueue(mockResponse)

        val exception = dataSource.getTransactions().exceptionOrNull()
        assertNotNull(exception)
        assertTrue( exception is RestApiException.RetrieveTransactionFailedException)

    }

    @Test
    fun testReturns403() = runTest {
        val mockResponse = MockResponse()
            .setResponseCode(403)
        mockWebServer.enqueue(mockResponse)

        val exception = dataSource.getTransactions().exceptionOrNull()
        assertNotNull(exception)
        assertTrue( exception is RestApiException.UnknownException)
    }

    @Test
    fun getTransactions_callsApiClient() = runTest {
        val fakeRestApiClient: IRestApiClient = mock()
        val fakeRemoteDatasource = TransactionRemoteDatasourceImpl(fakeRestApiClient)
        fakeRemoteDatasource.getTransactions()
        verify(fakeRestApiClient).getTransactions()
    }
}
