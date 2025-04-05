package nz.co.test.transactions.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import nz.co.test.transactions.R
import nz.co.test.transactions.ui.viewmodels.TransactionDetailsViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@Composable
fun TransactionDetailsScreen(
    modifier: Modifier = Modifier,
    viewModel: TransactionDetailsViewModel = hiltViewModel(),
    transactionId: Int,
    onBackClick: () -> Unit,
) {
    val uiState by viewModel.observeUiState(transactionId = transactionId).collectAsState(null)

    uiState?.let { state ->
        TransactionDetailsScreen(
            modifier = modifier,
            transactionId = transactionId,
            transactionDate = state.transactionDate,
            summary = state.summary,
            debit = state.debit,
            credit = state.credit,
            gst = state.gst,
            onBackClick = onBackClick,
            onCalculateGstClick = viewModel::onCalculateGstClick
        )
    } ?: run {
        Text(text = stringResource(R.string.loading))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDetailsScreen(
    modifier: Modifier = Modifier,
    transactionId: Int,
    transactionDate: Date,
    summary: String,
    debit: Float,
    credit: Float,
    debitTextColor: Color = Color.Red,
    creditTextColor: Color = Color.Green,
    gst: Float?,
    onBackClick: () -> Unit,
    onCalculateGstClick: (Int, Float) -> Unit,
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                modifier = Modifier.background(Color.White),
                title = {
                    Text(
                        modifier = Modifier.semantics {
                            contentDescription = "The id of this is $transactionId"
                        },
                        text = stringResource(R.string.transaction_details_title, transactionId),
                        style = MaterialTheme.typography.titleLarge
                    )
                },

                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors()
            )
        },
        content = { paddingValues ->
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Top
            ) {

                val formattedDateDisplay = remember(transactionDate) {
                    val dateFormat = SimpleDateFormat("dd/MMM/yy, EEE, HH:mm", Locale.getDefault())
                    dateFormat.timeZone = TimeZone.getDefault()
                    dateFormat.format(transactionDate)
                }

                ItemRow(
                    modifier = Modifier.fillMaxWidth(),
                    caption = stringResource(R.string.transaction_date),
                    content = formattedDateDisplay
                )

                DividerDefaults

                ItemRow(
                    modifier = Modifier.fillMaxWidth(),
                    caption = stringResource(R.string.summary),
                    content = summary
                )

                DividerDefaults

                ItemRow(
                    modifier = Modifier.fillMaxWidth(),
                    caption = stringResource(R.string.debit),
                    content = "$%.2f".format(debit),
                    contentTextColor = debitTextColor,
                )

                DividerDefaults

                ItemRow(
                    modifier = Modifier.fillMaxWidth(),
                    caption = stringResource(R.string.credit),
                    content = "$%.2f".format(credit),
                    contentTextColor = creditTextColor,
                )

                DividerDefaults

                ItemRow(
                    modifier = Modifier.fillMaxWidth(),
                    caption = stringResource(R.string.gst),
                    content = gst?.let { "$%.2f".format(it) } ?: stringResource(R.string.na),
                )

                DividerDefaults
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    onClick = {
                        onCalculateGstClick(transactionId, credit)
                    },
                ) {
                    Text(
                        modifier = Modifier.semantics {
                            contentDescription = "Start calculation of GST for transaction $transactionId"
                        },
                        text = stringResource(R.string.calculate_gst),
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                Text(
                    modifier = Modifier.padding(24.dp),
                    text = "* I am not sure how to calculate GST in this scenario, so assuming gst = credit * 15% ",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    )
}

@Composable
internal fun ItemRow(
    modifier: Modifier = Modifier,
    contentTextColor: Color = Color.Black,
    caption: String,
    content: String,
) {
    Row (
        modifier = modifier.padding(horizontal = 12.dp).semantics {
            contentDescription = "The $caption is $content"
        },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            textAlign = TextAlign.Center,
            text = "$caption: ",
            style = MaterialTheme.typography.labelLarge
        )

        Spacer(modifier = Modifier.width(6.dp))
        Text(
            textAlign = TextAlign.Center,
            text = content,
            color = contentTextColor,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Preview
@Composable
fun PreviewTransactionDetailsScreen() {
    TransactionDetailsScreen(
        modifier = Modifier,
        transactionId = 999,
        transactionDate = Date(),
        summary = "summary summary summary",
        debit = 123.01F,
        credit = 456.99F,
        gst = null,
        onBackClick = {  },
        onCalculateGstClick = { _, _ ->}
    )
}