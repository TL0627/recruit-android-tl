package nz.co.test.transactions

import androidx.paging.PagingData
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
import nz.co.test.transactions.ui.viewmodels.TransactionListViewModel
import nz.co.test.transactions.ui.viewmodels.TransactionListViewModel.SortOptions
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.Date
import kotlin.random.Random

@OptIn(ExperimentalCoroutinesApi::class)
class TransactionListViewModelTest {

    private lateinit var viewModel: TransactionListViewModel
    private val interactor: ITransactionInteractor = mock()
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = TransactionListViewModel(interactor)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init calls retrieveTransactions and sets initial state`() = runTest {
        verify(interactor).retrieveTransactions()
    }

    @Test
    fun `onSortOrderChanged updates sort order and refreshes transactions`() = runTest {
        val transactions = listOf(createTransaction(), createTransaction())
        val pagingData = PagingData.from(transactions)
        val flow: Flow<PagingData<Transaction>> = flowOf(pagingData)
        whenever(interactor.observeTransactionsLatestAtTop()).thenReturn(flow)
        whenever(interactor.observeTransactionsOldestAtTop()).thenReturn(flow)

        viewModel.observeTransactions(SortOptions.Descending).first()
        verify(interactor).observeTransactionsLatestAtTop()

        viewModel.observeTransactions(SortOptions.Ascending).first()
        verify(interactor).observeTransactionsOldestAtTop()
    }


    // Helper function to create dummy Transaction data
    private fun createTransaction() = Transaction(
        id = Random.nextInt(),
        transactionDate = Date(),
        summary = "Test Transaction",
        debit = Random.nextFloat(),
        credit = Random.nextFloat(),
        gst = null
    )
}