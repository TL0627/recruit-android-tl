package nz.co.test.transactions.data.datasources.remote.models

import java.util.Date

data class TransactionResponse(
    val id: Int,
    val transactionDate: Date,
    val summary: String,
    val debit: Float,
    val credit: Float,
)
