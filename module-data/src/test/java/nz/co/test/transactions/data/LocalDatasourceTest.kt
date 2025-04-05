package nz.co.test.transactions.data

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import nz.co.test.transactions.data.datasources.local.ITransactionLocalDatasource
import nz.co.test.transactions.data.datasources.local.impl.TransactionLocalDatasourceImpl
import nz.co.test.transactions.data.infrastructures.db.daos.TransactionDao
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.random.Random

@OptIn(ExperimentalCoroutinesApi::class)
class LocalDatasourceTest {

    private lateinit var datasource: ITransactionLocalDatasource
    private val fakeDao: TransactionDao = mock()

    @Before
    fun setup() {
        datasource = TransactionLocalDatasourceImpl(dao = fakeDao)
    }

    @Test
    fun upsertTransactions_callsDao() = runTest {
        val transactions = listOf(createTransactionEntity(), createTransactionEntity())
        datasource.upsertTransactions(transactions = transactions)
        verify(fakeDao).upsertTransactions(transactions = transactions)
    }

    @Test
    fun getTransactionsOrderByTransactionDateAsc_callsDao() = runTest {
        datasource.getTransactionsOrderByTransactionDateAsc()
        verify(fakeDao).getTransactionsOrderByTransactionDateAsc()
    }

    @Test
    fun getTransactionsOrderByTransactionDateDesc_callsDao() = runTest {
        datasource.getTransactionsOrderByTransactionDateDesc()
        verify(fakeDao).getTransactionsOrderByTransactionDateDesc()
    }

    @Test
    fun updateGst_callsDao() = runTest {
        val id = Random.nextInt()
        val amount = Random.nextFloat()
        datasource.updateGst(id = id, amount = amount)
        verify(fakeDao).updateGst(id = id, amount = amount)
    }

    @Test
    fun getTransaction_callsDao() = runTest {
        val id = Random.nextInt()
        val transaction = createTransactionEntity(id = id)
        whenever(fakeDao.observeTransaction(id = id)).thenReturn(flowOf(transaction))
        val result = datasource.getTransaction(id = id).first()
        assert(result == transaction)
        verify(fakeDao).observeTransaction(id = id)
    }

    @Test
    fun getTransactionsCount_callsDao() = runTest {
        datasource.getTransactionsCount()
        verify(fakeDao).getTransactionsCount()
    }
}