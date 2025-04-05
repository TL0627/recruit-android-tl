package nz.co.test.transactions.data.datasources.remote

import nz.co.test.transactions.data.datasources.remote.models.TransactionResponse

interface ITransactionRemoteDatasource {
    suspend fun getTransactions(): Result<List<TransactionResponse>>
}