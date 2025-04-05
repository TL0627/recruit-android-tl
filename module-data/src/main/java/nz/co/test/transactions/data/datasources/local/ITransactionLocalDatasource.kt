package nz.co.test.transactions.data.datasources.local

import androidx.paging.PagingSource
import kotlinx.coroutines.flow.Flow
import nz.co.test.transactions.data.datasources.local.models.TransactionEntity

interface ITransactionLocalDatasource {
    suspend fun updateGST(id: Int, amount: Float)

    suspend fun upsertTransactions(transactions: List<TransactionEntity>)

    fun getTransactionsOrderByTransactionDateAsc(): PagingSource<Int, TransactionEntity>
    fun getTransactionsOrderByTransactionDateDesc(): PagingSource<Int, TransactionEntity>

    fun getTransaction(id: Int): Flow<TransactionEntity?>

    suspend fun getTransactionsCount(): Int
}