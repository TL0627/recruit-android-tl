package nz.co.test.transactions.data.datasources.remote.impl

import nz.co.test.transactions.data.datasources.remote.ITransactionRemoteDatasource
import nz.co.test.transactions.data.datasources.remote.models.TransactionResponse
import nz.co.test.transactions.data.infrastructures.restapiclient.IRestApiClient
import nz.co.test.transactions.data.infrastructures.restapiclient.RestApiException
import retrofit2.HttpException
import javax.inject.Inject

class TransactionRemoteDatasourceImpl @Inject constructor(
    private val restApiClient: IRestApiClient,
) : ITransactionRemoteDatasource {
    override suspend fun getTransactions(): Result<List<TransactionResponse>> {
        return runCatching {
            restApiClient.getTransactions()
        }.recoverCatching { e ->
            when (e) {
                is HttpException -> throw handleHttpException(e)
                else -> {
                    throw RestApiException.UnknownException(e.localizedMessage ?: "Unknown Exception")
                }
            }
        }
    }


    private fun handleHttpException(exception: HttpException): RestApiException {
        return when (exception.code()) {
            404 -> throw RestApiException.TransactionResourceNotFoundException
            in 500..599 -> throw RestApiException.RetrieveTransactionFailedException
            else -> throw RestApiException.UnknownException(exception.localizedMessage ?: "Unknown Exception")
        }
    }

}