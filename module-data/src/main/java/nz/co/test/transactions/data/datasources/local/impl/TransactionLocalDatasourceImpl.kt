package nz.co.test.transactions.data.datasources.local.impl

import kotlinx.coroutines.flow.Flow
import nz.co.test.transactions.data.datasources.local.ITransactionLocalDatasource
import nz.co.test.transactions.data.datasources.local.models.TransactionEntity
import nz.co.test.transactions.data.infrastructures.db.daos.TransactionDao
import javax.inject.Inject

class TransactionLocalDatasourceImpl @Inject constructor(
    private val dao: TransactionDao,
) : ITransactionLocalDatasource {

    override suspend fun upsertTransactions(transactions: List<TransactionEntity>) = dao.upsertTransactions(transactions)

    override fun getTransactionsOrderByTransactionDateAsc() = dao.getTransactionsOrderByTransactionDateAsc()

    override fun getTransactionsOrderByTransactionDateDesc() = dao.getTransactionsOrderByTransactionDateDesc()

    override suspend fun updateGst(id: Int, amount: Float) = dao.updateGst(id = id, amount = amount)

    override fun getTransaction(id: Int): Flow<TransactionEntity?> = dao.observeTransaction(id = id)

    override suspend fun getTransactionsCount() = dao.getTransactionsCount()
}