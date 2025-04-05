package nz.co.test.transactions.data.repositories.impl

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import nz.co.test.transactions.data.datasources.local.ITransactionLocalDatasource
import nz.co.test.transactions.data.datasources.remote.ITransactionRemoteDatasource
import nz.co.test.transactions.data.repositories.ITransactionsRepository
import nz.co.test.transactions.data.repositories.TransactionLocalToDomainMapper
import nz.co.test.transactions.data.repositories.TransactionRemoteToLocalMapper
import nz.co.test.transactions.data.repositories.model.Transaction
import nz.co.test.transactions.data.repositories.model.TransactionSortOptions
import timber.log.Timber
import javax.inject.Inject

class TransactionsRepositoryImpl @Inject constructor(
    private val remoteDatasource: ITransactionRemoteDatasource,
    private val localDatasource: ITransactionLocalDatasource,
    private val remoteToLocalMapper: TransactionRemoteToLocalMapper,
    private val localToDomainMapper: TransactionLocalToDomainMapper,
) : ITransactionsRepository {

    override suspend fun retrieveTransactions(): Result<Unit> {
        return remoteDatasource.getTransactions().fold (
            onSuccess = { response ->
                localDatasource.upsertTransactions(
                    response.map { remoteToLocalMapper(it) }
                )
                Result.success(Unit)
            },
            onFailure = {
                Timber.e("Error retrieving transactions: ${it.localizedMessage}")
                Result.failure(it)
            },
        )
    }

    override fun getTransactions(pageSize: Int, orderBy: TransactionSortOptions): Flow<PagingData<Transaction>> {
        val pagingSourceFactory = when (orderBy) {
            TransactionSortOptions.TransactionDateASC -> localDatasource::getTransactionsOrderByTransactionDateAsc
            TransactionSortOptions.TransactionDateDESC -> localDatasource::getTransactionsOrderByTransactionDateDesc
        }

        return Pager(
            config = PagingConfig(
                pageSize = pageSize,
                prefetchDistance = pageSize,
                enablePlaceholders = true,
            ),
            pagingSourceFactory = pagingSourceFactory,
        ).flow.map { pagingData -> pagingData.map { localToDomainMapper(it) }}
    }

    override suspend fun updateGstInTransaction(transactionId: Int, gst: Float) {
        localDatasource.updateGst(id = transactionId, amount = gst)
    }

    override fun getTransaction(transactionId: Int) =
        localDatasource.getTransaction(id = transactionId).map { entity -> entity?.let { localToDomainMapper(it) } }

    override suspend fun getTransactionsCount() = localDatasource.getTransactionsCount()
}

