package nz.co.test.transactions.data.datasources.local.models

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "transactions",
    indices = [
        Index("transactionDate"),
    ]
)
data class TransactionEntity(
    @PrimaryKey val id: Int,
    val transactionDate: Date,
    val summary: String,
    val debit: Float,
    val credit: Float,
    val gst: Float? = null
)
