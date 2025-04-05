package nz.co.test.transactions

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import nz.co.test.transactions.data.repositories.model.Transaction
import nz.co.test.transactions.domain.ITransactionInteractor
import nz.co.test.transactions.ui.viewmodels.TransactionDetailsViewModel
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.Date
import kotlin.random.Random

@OptIn(ExperimentalCoroutinesApi::class)
class TransactionDetailsViewModelTest {

    private lateinit var viewModel: TransactionDetailsViewModel
    private val interactor: ITransactionInteractor = mock()
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = TransactionDetailsViewModel(interactor)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `observeUiState emits correct UiState when transaction is available`() = runTest {
        val transactionId = Random.nextInt()
        val transaction = createTransaction(id = transactionId)
        val flow: Flow<Transaction?> = flowOf(transaction)
        whenever(interactor.observeTransactionDetails(transactionId)).thenReturn(flow)

        val uiState = viewModel.observeUiState(transactionId).first()
        assertEquals(transaction.transactionDate, uiState?.transactionDate)
        assertEquals(transaction.summary, uiState?.summary)
        assertEquals(transaction.debit, uiState?.debit)
        assertEquals(transaction.credit, uiState?.credit)
        assertEquals(transaction.gst, uiState?.gst)
        verify(interactor).observeTransactionDetails(transactionId)
    }

    @Test
    fun `observeUiState emits null when transaction is not available`() = runTest {
        val transactionId = Random.nextInt()
        val flow: Flow<Transaction?> = flowOf(null)
        whenever(interactor.observeTransactionDetails(transactionId)).thenReturn(flow)

        val uiState = viewModel.observeUiState(transactionId).first()
        assertEquals(null, uiState)
        verify(interactor).observeTransactionDetails(transactionId)
    }

    @Test
    fun `onCalculateGstClick calculates and updates GST`() = runTest {
        val transactionId = Random.nextInt()
        val amount = 100.0f
        val calculatedGst = 15.0f
        whenever(interactor.calculateGst(amount = amount)).thenReturn(calculatedGst)

        viewModel.onCalculateGstClick(transactionId, amount)

        verify(interactor).calculateGst(amount = amount)
        verify(interactor).updateGstInTransaction(transactionId = transactionId, gst = calculatedGst)
    }

    private fun createTransaction(id: Int = Random.nextInt()) = Transaction(
        id = id,
        transactionDate = Date(),
        summary = "Test Transaction",
        debit = Random.nextFloat(),
        credit = Random.nextFloat(),
        gst = null
    )
}
