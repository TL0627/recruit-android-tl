package nz.co.test.transactions

import androidx.paging.PagingData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import nz.co.test.transactions.data.repositories.ITransactionsRepository
import nz.co.test.transactions.data.repositories.model.Transaction
import nz.co.test.transactions.data.repositories.model.TransactionSortOptions
import nz.co.test.transactions.domain.ITransactionInteractor
import nz.co.test.transactions.domain.impl.TransactionInteractorImpl
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.Date
import kotlin.random.Random
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class InteractorTest {

    private lateinit var interactor: ITransactionInteractor
    private val repository: ITransactionsRepository = mock()

    @Before
    fun setup() {
        interactor = TransactionInteractorImpl(repository)
    }

    @Test
    fun `calculateGst calculates GST correctly`() {
        val amount = 100.0F
        val expectedGst = 15.0F
        val actualGst = interactor.calculateGst(amount = amount)
        assertTrue (
            areFloatsEqual(expectedGst, actualGst)
        )
    }

    @Test
    fun `calculateGst calculates GST correctly with custom rate`() {
        val amount = 200.0F
        val rate = 0.10F
        val expectedGst = 20.0F
        val actualGst = interactor.calculateGst(rate = rate, amount = amount)
        assertTrue (
            areFloatsEqual(expectedGst, actualGst)
        )
    }

    @Test
    fun `updateGstInTransaction calls repository`() = runTest {
        val transactionId = Random.nextInt()
        val gst = Random.nextFloat()
        interactor.updateGstInTransaction(transactionId = transactionId, gst = gst)
        verify(repository).updateGstInTransaction(transactionId = transactionId, gst = gst)
    }

    @Test
    fun `retrieveTransactions calls repository`() = runTest {
        interactor.retrieveTransactions()
        verify(repository).retrieveTransactions()
    }

    @Test
    fun `observeTransactionsLatestAtTop calls repository with correct parameters`() = runTest {
        val transactions = listOf(createTransaction(), createTransaction())
        val pagingData = PagingData.from(transactions)
        val flow: Flow<PagingData<Transaction>> = flowOf(pagingData)
        whenever(repository.getTransactions(orderBy = TransactionSortOptions.TransactionDateDESC)).thenReturn(flow)

        interactor.observeTransactionsLatestAtTop().first()
        verify(repository).getTransactions(orderBy = TransactionSortOptions.TransactionDateDESC)
    }

    @Test
    fun `observeTransactionsOldestAtTop calls repository with correct parameters`() = runTest {
        val transactions = listOf(createTransaction(), createTransaction())
        val pagingData = PagingData.from(transactions)
        val flow: Flow<PagingData<Transaction>> = flowOf(pagingData)
        whenever(repository.getTransactions(orderBy = TransactionSortOptions.TransactionDateASC)).thenReturn(flow)

        interactor.observeTransactionsOldestAtTop().first()
        verify(repository).getTransactions(orderBy = TransactionSortOptions.TransactionDateASC)
    }

    @Test
    fun `observeTransactionDetails calls repository and returns flow`() = runTest {
        val transactionId = Random.nextInt()
        val transaction = createTransaction()
        val flow: Flow<Transaction?> = flowOf(transaction)
        whenever(repository.getTransaction(transactionId = transactionId)).thenReturn(flow)

        val result = interactor.observeTransactionDetails(transactionId).first()
        assertEquals(transaction, result)
        verify(repository).getTransaction(transactionId = transactionId)
    }

    @Test
    fun `getTransactionsCount calls repository`() = runTest {
        interactor.getTransactionsCount()
        verify(repository).getTransactionsCount()
    }

    private fun createTransaction() = Transaction(
        id = Random.nextInt(),
        transactionDate = Date(),
        summary = "Test Transaction",
        debit = Random.nextFloat(),
        credit = Random.nextFloat(),
        gst = null
    )

    private fun areFloatsEqual(a: Float, b: Float, epsilon: Float = 1e-6f): Boolean {
        return kotlin.math.abs(a - b) < epsilon
    }
}
