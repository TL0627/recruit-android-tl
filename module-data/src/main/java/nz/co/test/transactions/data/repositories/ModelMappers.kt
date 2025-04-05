package nz.co.test.transactions.data.repositories

import nz.co.test.transactions.data.datasources.local.models.TransactionEntity
import nz.co.test.transactions.data.datasources.remote.models.TransactionResponse
import nz.co.test.transactions.data.repositories.model.Transaction
import javax.inject.Inject

class TransactionRemoteToLocalMapper @Inject constructor() {
    operator fun invoke(transactionResponse: TransactionResponse): TransactionEntity {
        return TransactionEntity(
            id = transactionResponse.id,
            transactionDate = transactionResponse.transactionDate,
            summary = transactionResponse.summary,
            debit = transactionResponse.debit,
            credit = transactionResponse.credit,
        )
    }
}

class TransactionLocalToDomainMapper @Inject constructor() {
    operator fun invoke(transactionEntity: TransactionEntity): Transaction {
        return Transaction(
            id = transactionEntity.id,
            transactionDate = transactionEntity.transactionDate,
            summary = transactionEntity.summary,
            debit = transactionEntity.debit,
            credit = transactionEntity.credit,
            gst = transactionEntity.gst
        )
    }
}