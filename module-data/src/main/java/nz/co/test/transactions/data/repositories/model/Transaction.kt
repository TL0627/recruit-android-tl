package nz.co.test.transactions.data.repositories.model

import java.util.Date

data class Transaction(
    val id: Int,
    val transactionDate: Date,
    val summary: String,
    val debit: Float,
    val credit: Float,
    val gst: Float? = null
)
