package nz.co.test.transactions.data.infrastructures.db.daos

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import nz.co.test.transactions.data.datasources.local.models.TransactionEntity

@Dao
interface TransactionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertTransactions(transactions: List<TransactionEntity>)

    @Query("UPDATE transactions SET gst = :amount WHERE id = :id")
    suspend fun updateGST(id: Int, amount: Float)

    @Query("SELECT * FROM transactions ORDER BY transactionDate DESC")
    fun getTransactionsOrderByTransactionDateDesc(): PagingSource<Int, TransactionEntity>

    @Query("SELECT * FROM transactions ORDER BY transactionDate ASC")
    fun getTransactionsOrderByTransactionDateAsc(): PagingSource<Int, TransactionEntity>

    @Query("SELECT * FROM transactions WHERE id = :id")
    fun observeTransaction(id: Int): Flow<TransactionEntity?>

    @Query("SELECT COUNT(*) FROM transactions")
    suspend fun getTransactionsCount(): Int
}