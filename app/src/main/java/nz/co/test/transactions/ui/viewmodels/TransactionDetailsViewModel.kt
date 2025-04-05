package nz.co.test.transactions.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import nz.co.test.transactions.domain.ITransactionInteractor
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class TransactionDetailsViewModel @Inject constructor(
    private val transactionInteractor: ITransactionInteractor,
) : ViewModel() {

    fun observeUiState(transactionId: Int) =
        transactionInteractor.observeTransactionDetails(transactionId)
            .map { transaction ->
                transaction?.let {
                    TransactionDetailsUiState(
                        transactionDate = it.transactionDate,
                        summary = it.summary,
                        debit = it.debit,
                        credit = it.credit,
                        gst = it.gst,
                    )
                }
            }

    fun onCalculateGstClick(transactionId: Int, amount: Float) {
        viewModelScope.launch {
            //do gst calculation in default dispatcher
            val gst = withContext(Dispatchers.Default) {
                transactionInteractor.calculateGst(amount = amount)
            }

            transactionInteractor.updateGstInTransaction(
                transactionId = transactionId,
                gst = gst,
            )
        }
    }

    data class TransactionDetailsUiState(
        val transactionDate: Date,
        val summary: String,
        val debit: Float,
        val credit: Float,
        val gst: Float? = null,
        val promptMessageResId: Int? = null,
    )
}