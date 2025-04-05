package nz.co.test.transactions

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import nz.co.test.transactions.data.repositories.model.Transaction
import nz.co.test.transactions.ui.screens.TransactionDetailsScreen
import org.junit.Rule
import org.junit.Test
import java.util.Date
import kotlin.random.Random

class TransactionDetailsScreenUITest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun transactionDetailsScreenWithoutGst() {
        val transaction = createTransaction()
        composeTestRule.setContent {
            MaterialTheme {
                TransactionDetailsScreen (
                    transactionId = transaction.id,
                    transactionDate = transaction.transactionDate,
                    summary = transaction.summary,
                    debit = transaction.debit,
                    credit = transaction.credit,
                    gst = transaction.gst,
                    onBackClick = {},
                    onCalculateGstClick = {_, _ ->},
                )
            }
        }

        composeTestRule.onNodeWithText("ID: ${transaction.id}").assertIsDisplayed()
        composeTestRule.onNodeWithText(transaction.summary).assertIsDisplayed()
        composeTestRule.onNodeWithText("$%.2f".format(transaction.debit)).assertIsDisplayed()
        composeTestRule.onNodeWithText("$%.2f".format(transaction.credit)).assertIsDisplayed()
        composeTestRule.onNodeWithText("N/A").assertIsDisplayed()
        composeTestRule.onNodeWithText("Calculate GST").assertIsDisplayed()
    }

    @Test
    fun transactionDetailsScreenWithGst() {
        val transaction = createTransaction(gst = 100.01f)
        composeTestRule.setContent {
            MaterialTheme {
                TransactionDetailsScreen (
                    transactionId = transaction.id,
                    transactionDate = transaction.transactionDate,
                    summary = transaction.summary,
                    debit = transaction.debit,
                    credit = transaction.credit,
                    gst = transaction.gst,
                    onBackClick = {},
                    onCalculateGstClick = {_, _ ->},
                )
            }
        }

        composeTestRule.onNodeWithText("ID: ${transaction.id}").assertIsDisplayed()
        composeTestRule.onNodeWithText(transaction.summary).assertIsDisplayed()
        composeTestRule.onNodeWithText("$%.2f".format(transaction.debit)).assertIsDisplayed()
        composeTestRule.onNodeWithText("$%.2f".format(transaction.credit)).assertIsDisplayed()
        composeTestRule.onNodeWithText("$%.2f".format(transaction.gst)).assertIsDisplayed()
        composeTestRule.onNodeWithText("Calculate GST").assertIsDisplayed()
    }

    private fun createTransaction(gst: Float? = null) = Transaction(
        id = Random.nextInt(),
        transactionDate = Date(),
        summary = "summary",
        debit = Random.nextFloat(),
        credit = Random.nextFloat(),
        gst = gst
    )
}