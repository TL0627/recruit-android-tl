package nz.co.test.transactions.data

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import nz.co.test.transactions.data.datasources.local.ITransactionLocalDatasource
import nz.co.test.transactions.data.datasources.remote.ITransactionRemoteDatasource
import nz.co.test.transactions.data.repositories.ITransactionsRepository
import nz.co.test.transactions.data.repositories.TransactionLocalToDomainMapper
import nz.co.test.transactions.data.repositories.TransactionRemoteToLocalMapper
import nz.co.test.transactions.data.repositories.impl.TransactionsRepositoryImpl
import nz.co.test.transactions.data.repositories.model.TransactionSortOptions
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.random.Random

@OptIn(ExperimentalCoroutinesApi::class)
class TransactionsRepositoryImplTest {

    private lateinit var repository: ITransactionsRepository
    private val remoteDatasource: ITransactionRemoteDatasource = mock()
    private val localDatasource: ITransactionLocalDatasource = mock()
    private val remoteToLocalMapper = TransactionRemoteToLocalMapper()
    private val localToDomainMapper = TransactionLocalToDomainMapper()

    @Before
    fun setup() {
        repository = TransactionsRepositoryImpl(
            remoteDatasource = remoteDatasource,
            localDatasource = localDatasource,
            remoteToLocalMapper = remoteToLocalMapper,
            localToDomainMapper = localToDomainMapper,
        )
    }

    @Test
    fun retrieveTransactions_success_updatesLocalDb() = runTest {
        val remoteTransactions = listOf(createRemoteTransaction(), createRemoteTransaction())
        val localTransactions = listOf(createTransactionEntity(), createTransactionEntity())

        whenever(remoteDatasource.getTransactions()).thenReturn(Result.success(remoteTransactions))
        whenever(localDatasource.upsertTransactions(localTransactions)).thenReturn(Unit)

        val result = repository.retrieveTransactions()
        assert(result.isSuccess)
        verify(remoteDatasource).getTransactions()
        verify(localDatasource).upsertTransactions(any())
    }

    @Test
    fun retrieveTransactions_failure_returnsFailure() = runTest {
        val exception = Exception("Network error")
        whenever(remoteDatasource.getTransactions()).thenReturn(Result.failure(exception))

        val result = repository.retrieveTransactions()
        assert(result.isFailure)
        assert(result.exceptionOrNull() == exception)
        verify(remoteDatasource).getTransactions()
    }

    @Test
    fun getTransactions_sortAsc_callsLocalDatasource() = runTest {
        val pageSize = 10
        val fakeItems = listOf(
            createTransactionEntity(), createTransactionEntity(),
        ).sortedBy { it.transactionDate }

        whenever(localDatasource.getTransactionsOrderByTransactionDateAsc()).thenReturn(mockPagingSource(fakeItems))
        repository.getTransactions(pageSize = pageSize, orderBy = TransactionSortOptions.TransactionDateASC).first()
        verify(localDatasource).getTransactionsOrderByTransactionDateAsc()
    }

    @Test
    fun getTransactions_sortDesc_callsLocalDatasource() = runTest {
        val pageSize = 10
        val fakeItems = listOf(
            createTransactionEntity(), createTransactionEntity(),
        ).sortedBy { it.transactionDate }

        whenever(localDatasource.getTransactionsOrderByTransactionDateDesc()).thenReturn(mockPagingSource(fakeItems))
        repository.getTransactions(pageSize = pageSize, orderBy = TransactionSortOptions.TransactionDateDESC).first()
        verify(localDatasource).getTransactionsOrderByTransactionDateDesc()
    }

    @Test
    fun updateGstInTransaction_callsLocalDatasource() = runTest {
        val transactionId = Random.nextInt()
        val gst = Random.nextFloat()
        repository.updateGstInTransaction(transactionId = transactionId, gst = gst)
        verify(localDatasource).updateGst(id = transactionId, amount = gst)
    }

    @Test
    fun getTransactionsCount_callsLocalDatasource() = runTest {
        repository.getTransactionsCount()
        verify(localDatasource).getTransactionsCount()
    }
}