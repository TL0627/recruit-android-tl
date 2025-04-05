package nz.co.test.transactions.data.repositories

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import nz.co.test.transactions.data.repositories.model.Transaction
import nz.co.test.transactions.data.repositories.model.TransactionSortOptions

interface ITransactionsRepository {
    suspend fun retrieveTransactions(): Result<Unit>

    fun getTransactions(pageSize: Int = LIST_PAGE_SIZE, orderBy: TransactionSortOptions =
        TransactionSortOptions.TransactionDateDESC): Flow<PagingData<Transaction>>

    suspend fun updateGstInTransaction(transactionId: Int, gst: Float)

    fun getTransaction(transactionId: Int): Flow<Transaction?>

    suspend fun getTransactionsCount(): Int

    companion object {
        private const val LIST_PAGE_SIZE: Int = 15
    }
}