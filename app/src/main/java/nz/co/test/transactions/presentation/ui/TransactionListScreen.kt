package nz.co.test.transactions.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.flow.flowOf
import nz.co.test.transactions.R
import nz.co.test.transactions.data.repositories.model.Transaction
import nz.co.test.transactions.presentation.viewmodels.TransactionListViewModel
import nz.co.test.transactions.presentation.viewmodels.TransactionListViewModel.SortOptions
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionListScreen(
    modifier: Modifier = Modifier,
    viewModel: TransactionListViewModel = hiltViewModel(),
    onTransactionClick: (Int) -> Unit,
) {

    val uiState by viewModel.uiState.collectAsState()
    val transactions = viewModel.observeTransactions(uiState.selectedSortOption)

    TransactionListScreen(
        modifier = modifier,
        isLoading = uiState.isLoading,
        sortOptions = uiState.sortOptions.map { stringResource(it.labelResId) },
        onSortOptionChanged = { index ->
            when (index) {
                SortOptions.Descending.ordinal -> viewModel.updateSortOption(SortOptions.Descending)
                SortOptions.Ascending.ordinal -> viewModel.updateSortOption(SortOptions.Ascending)
            }
        },
        selectedSortOptionIndex = uiState.selectedSortOption.ordinal,
        promptMessageResId = uiState.promptMessageResId ,
        lazyPagingItems = transactions.collectAsLazyPagingItems(),
        onResetClick = viewModel::onResetClick,
        onTransactionClick = onTransactionClick,
        onDialogDismiss = viewModel::removePromptMessage,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TransactionListScreen(
    modifier: Modifier = Modifier,
    isLoading: Boolean,
    promptMessageResId: Int?,
    lazyPagingItems: LazyPagingItems<Transaction>,
    onResetClick: () -> Unit,
    sortOptions: List<String>,
    selectedSortOptionIndex: Int,
    onSortOptionChanged: (Int) -> Unit,
    onTransactionClick: (Int) -> Unit,
    onDialogDismiss: () -> Unit,
) {
    promptMessageResId?.let {
        MessageDialog(
            message = stringResource(it),
            onDismiss = onDialogDismiss
        )
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                modifier = Modifier.background(Color.Companion.White),
                title = {
                    Text(
                        text = stringResource(
                            when (isLoading) {
                                true -> R.string.retrieving
                                false -> R.string.app_name
                            }
                        ),
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                actions = {
                    TextButton(
                        onClick = {
                            if (!isLoading) onResetClick()
                        },
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.reset),
                            style = MaterialTheme.typography.titleSmall
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors()
            )
        },

        content = { paddingValues ->
            Column (modifier = Modifier.padding(paddingValues)) {
                RadioButtons(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    options = sortOptions,
                    initSelectedIndex = selectedSortOptionIndex,
                    onCheck = onSortOptionChanged
                )

                TransactionList(
                    lazyPagingItems = lazyPagingItems,
                    onTransactionClick = onTransactionClick,
                )
            }
        }
    )
}

@Composable
internal fun TransactionList(
    modifier: Modifier = Modifier,
    lazyPagingItems: LazyPagingItems<Transaction>,
    onTransactionClick: (Int) -> Unit
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            count = lazyPagingItems.itemCount,
            key = { index -> lazyPagingItems[index]?.id ?: 0 }
        ) { index ->
            lazyPagingItems[index]?.let { transaction ->
                TransactionItem(transaction, onTransactionClick)
            }
        }

        lazyPagingItems.apply {
            when {
                loadState.refresh is LoadState.Loading -> {
                    item { LoadingItem(stringResource(R.string.loading)) }
                }
                loadState.append is LoadState.Loading -> {
                    item { LoadingItem(stringResource(R.string.loading_more)) }
                }
                loadState.refresh is LoadState.Error -> {
                    val e = loadState.refresh as LoadState.Error
                    item { ErrorItem(message = e.error.localizedMessage ?: stringResource(R.string.loading_failed)) }
                }
                loadState.append is LoadState.Error -> {
                    val e = loadState.append as LoadState.Error
                    item { ErrorItem(message = e.error.localizedMessage ?: stringResource(R.string.loading_more_failed)) }
                }
            }
        }
    }
}


@Composable
internal fun TransactionItem(
    transaction: Transaction,
    onTransactionClick: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
            .semantics {
                contentDescription = "Item ${transaction.id}, details ${transaction.summary}"
            },
        elevation = CardDefaults.cardElevation(4.dp),
        onClick = {
            onTransactionClick(transaction.id)
        },
    ) {
        Row(
            modifier = Modifier.padding(16.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) { // 让左侧内容占据可用空间
                Text(
                    text = transaction.summary,
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(4.dp))
                val formattedDateDisplay = remember(transaction.transactionDate) {
                    val dateFormat = SimpleDateFormat("dd/MM/yyyy EEE HH:mm", Locale.getDefault())
                    dateFormat.timeZone = TimeZone.getDefault()
                    dateFormat.format(transaction.transactionDate)
                }
                Text(
                    text = formattedDateDisplay,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                )
            }

            transaction.gst?.let {
                ItemRow(
                    modifier = Modifier.padding(start = 16.dp),
                    caption = stringResource(R.string.gst),
                    content = "$%.2f".format(it)
                )
            }
        }
    }
}

@Composable
internal fun LoadingItem(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = message)
    }
}

@Composable
internal fun ErrorItem(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.error, message),
            color = Color.Red
        )
    }
}

@Composable
internal fun MessageDialog(message: String, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.background,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = message, style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onDismiss) {
                    Text(stringResource(R.string.okay))
                }
            }
        }
    }
}

@Composable
internal fun RadioButtons(
    modifier: Modifier = Modifier,
    options: List<String>,
    initSelectedIndex: Int? = null,
    onCheck: (Int) -> Unit,
) {
    var selectedIndex by remember { mutableIntStateOf(initSelectedIndex ?: 0) }

    HorizontalRadioButtons(
        modifier = modifier,
        options = options,
        selectedIndex = initSelectedIndex,
        onOptionSelected = { index ->
            selectedIndex = index
            onCheck(index)
        }
    )
}

@Composable
internal fun HorizontalRadioButtons(
    modifier: Modifier = Modifier,
    options: List<String>,
    selectedIndex: Int? = null,
    onOptionSelected: (Int) -> Unit,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start //.SpaceEvenly
    ) {
        options.forEachIndexed { index, option ->
            RadioButton(
                selected = selectedIndex == index,
                onClick = { onOptionSelected(index) },
                label = option
            )
            Spacer(modifier = Modifier.width(12.dp))
        }
    }
}

@Composable
internal fun RadioButton(
    modifier: Modifier = Modifier,
    selected: Boolean,
    label: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .clickable { onClick() }
            .padding(horizontal = 0.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.size(20.dp),
            imageVector = if (selected) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
            tint = if (selected) Color.Blue else Color.LightGray,
            contentDescription = if (selected) "selected" else "unselected",
        )

        Text(
            text = label,
            color = Color.Black,
            fontSize = 16.sp,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Preview
@Composable
fun PreviewTransactionListScreen() {

    val transactions = listOf(
        Transaction(
            id = 0,
            transactionDate = Date(),
            summary = "summary summary summary",
            debit = 123.01F,
            credit = 456.99F,
            gst = 789.10F,
        ),

        Transaction(
            id = 1,
            transactionDate = Date(),
            summary = "summary summary summary",
            debit = 123.01F,
            credit = 456.99F,
            gst = 100.01F,
        ),
    )

    TransactionListScreen(
        modifier = Modifier,
        isLoading = true,
        sortOptions = listOf("Ascending", "Descending"),
        onSortOptionChanged = { _ -> },
        selectedSortOptionIndex = 0,
        promptMessageResId = null ,
        lazyPagingItems = flowOf(PagingData.from(transactions)).collectAsLazyPagingItems(),
        onResetClick = { },
        onTransactionClick = {},
        onDialogDismiss = {},
    )
}
