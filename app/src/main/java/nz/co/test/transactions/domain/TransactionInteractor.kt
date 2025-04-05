package nz.co.test.transactions.domain

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import nz.co.test.transactions.data.repositories.model.Transaction

interface ITransactionInteractor {
    fun calculateGst(rate: Float = DEFAULT_GST_RATE, amount: Float): Float
    suspend fun updateGstInTransaction(transactionId: Int, gst: Float)
    suspend fun retrieveTransactions(): Result<Unit>
    fun observeTransactionsLatestAtTop(): Flow<PagingData<Transaction>>
    fun observeTransactionsOldestAtTop(): Flow<PagingData<Transaction>>
    fun observeTransactionDetails(transactionId: Int): Flow<Transaction?>
    suspend fun getTransactionsCount(): Int

    companion object {
        private const val DEFAULT_GST_RATE: Float = 0.15F
    }
}