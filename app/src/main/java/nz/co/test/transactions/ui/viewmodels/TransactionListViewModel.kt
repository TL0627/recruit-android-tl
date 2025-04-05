package nz.co.test.transactions.ui.viewmodels

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import nz.co.test.transactions.R
import nz.co.test.transactions.data.repositories.model.Transaction
import nz.co.test.transactions.domain.ITransactionInteractor
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class TransactionListViewModel @Inject constructor(
    private val transactionInteractor: ITransactionInteractor,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        TransactionListUiState()
    )
    val uiState: StateFlow<TransactionListUiState> = _uiState

    init {
        retrieveTransactions()
    }

    private var retrieveTransactionsJob: Job? = null

    fun retrieveTransactions(isToShowDialog: Boolean = false) {
        retrieveTransactionsJob?.cancel()
        retrieveTransactionsJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            transactionInteractor.retrieveTransactions()
                .onSuccess {
                    _uiState.update {
                        if (isToShowDialog) {
                            it.copy(
                                isLoading = false,
                                promptMessageResId = R.string.transaction_reset
                            )
                        } else {
                            it.copy(
                                isLoading = false,
                            )
                        }

                    }
                }
                .onFailure {
                    Timber.tag("123").e("Error retrieving transactions: ${it.localizedMessage}")
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            promptMessageResId = R.string.transaction_loaded_failed
                        )
                    }
                }
        }
    }

    fun observeTransactions(selectedSortOption: SortOptions) : Flow<PagingData<Transaction>> {
        return when (selectedSortOption) {
            SortOptions.Descending -> return transactionInteractor.observeTransactionsLatestAtTop()
            SortOptions.Ascending -> return transactionInteractor.observeTransactionsOldestAtTop()
        }
    }

    fun updateSortOption(selectedSortOption: SortOptions) {
        _uiState.update {
            it.copy(
                selectedSortOption = selectedSortOption
            )
        }
    }

    fun onResetClick() {
        retrieveTransactions(isToShowDialog = true)
    }

    fun removePromptMessage() {
        _uiState.update { it.copy(promptMessageResId = null) }
    }

    enum class SortOptions(@StringRes val labelResId: Int) {
        Descending(R.string.sort_desc),
        Ascending(R.string.sort_asc),
    }

    data class TransactionListUiState(
        val isLoading: Boolean = false,
        val sortOptions: List<SortOptions> = listOf(
            SortOptions.Descending, SortOptions.Ascending,
        ),
        val selectedSortOption: SortOptions = SortOptions.Descending,
        val promptMessageResId: Int? = null,
    )
}