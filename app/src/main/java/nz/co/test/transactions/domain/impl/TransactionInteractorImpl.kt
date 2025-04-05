package nz.co.test.transactions.domain.impl

import nz.co.test.transactions.data.repositories.ITransactionsRepository
import nz.co.test.transactions.data.repositories.model.TransactionSortOptions
import nz.co.test.transactions.domain.ITransactionInteractor
import javax.inject.Inject

class TransactionInteractorImpl @Inject constructor(
    private val repository: ITransactionsRepository
) : ITransactionInteractor {
    override fun calculateGst(rate: Float, amount: Float) = amount * rate

    override suspend fun updateGstInTransaction(transactionId: Int, gst: Float) {
        repository.updateGstInTransaction(transactionId, gst)
    }

    override suspend fun retrieveTransactions() = repository.retrieveTransactions()

    override fun observeTransactionsLatestAtTop() =
        repository.getTransactions(orderBy = TransactionSortOptions.TransactionDateDESC)

    override fun observeTransactionsOldestAtTop() =
        repository.getTransactions(orderBy = TransactionSortOptions.TransactionDateASC)

    override fun observeTransactionDetails(transactionId: Int) =
        repository.getTransaction(transactionId)

    override suspend fun getTransactionsCount() = repository.getTransactionsCount()
}