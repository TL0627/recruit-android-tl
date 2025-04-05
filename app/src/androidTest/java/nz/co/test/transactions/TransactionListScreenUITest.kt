package nz.co.test.transactions

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.flow.flowOf
import nz.co.test.transactions.data.repositories.model.Transaction
import nz.co.test.transactions.ui.screens.TransactionListScreen
import org.junit.Rule
import org.junit.Test
import java.util.Date
import kotlin.random.Random

class TransactionListScreenUITest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun transactionListScreenDisplaysLoadingState() {
        composeTestRule.setContent {
            MaterialTheme {
                TransactionListScreen(
                    isLoading = true,
                    promptMessageResId = null,
                    lazyPagingItems = flowOf(PagingData.empty<Transaction>()).collectAsLazyPagingItems(),
                    onResetClick = {},
                    sortOptions = listOf("Ascending", "Descending"),
                    selectedSortOptionIndex = 0,
                    onSortOptionChanged = {},
                    onTransactionClick = {},
                    onDialogDismiss = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Retrieving").assertIsDisplayed()
    }

    @Test
    fun transactionListScreenDisplaysNotLoadingState() {
        composeTestRule.setContent {
            MaterialTheme {
                TransactionListScreen(
                    isLoading = false,
                    promptMessageResId = null,
                    lazyPagingItems = flowOf(PagingData.empty<Transaction>()).collectAsLazyPagingItems(),
                    onResetClick = {},
                    sortOptions = listOf("Ascending", "Descending"),
                    selectedSortOptionIndex = 0,
                    onSortOptionChanged = {},
                    onTransactionClick = {},
                    onDialogDismiss = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Transactions").assertIsDisplayed()
    }

    @Test
    fun transactionListScreenDisplaysTransactions() {
        val transactions = listOf(
            createTransaction(summary = "Transaction 1", gst = 100.01f),
            createTransaction(summary = "Transaction 2")
        )

        composeTestRule.setContent {
            MaterialTheme {
                TransactionListScreen(
                    isLoading = false,
                    promptMessageResId = null,
                    lazyPagingItems = flowOf(PagingData.from(transactions)).collectAsLazyPagingItems(),
                    onResetClick = {},
                    sortOptions = listOf("Ascending", "Descending"),
                    selectedSortOptionIndex = 0,
                    onSortOptionChanged = {},
                    onTransactionClick = {},
                    onDialogDismiss = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Reset").assertIsDisplayed()
        composeTestRule.onNodeWithText("Ascending").assertIsDisplayed()
        composeTestRule.onNodeWithText("Descending").assertIsDisplayed()
        composeTestRule.onNodeWithText("Transaction 1").assertIsDisplayed()
        composeTestRule.onNodeWithText("Transaction 2").assertIsDisplayed()
        composeTestRule.onNodeWithText("GST: ").assertIsDisplayed()
        composeTestRule.onNodeWithText("$100.01").assertIsDisplayed()
    }

    private fun createTransaction(summary: String, gst: Float? = null) = Transaction(
        id = Random.nextInt(),
        transactionDate = Date(),
        summary = summary,
        debit = Random.nextFloat(),
        credit = Random.nextFloat(),
        gst = gst
    )
}