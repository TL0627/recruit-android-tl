package nz.co.test.transactions.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import nz.co.test.transactions.data.datasources.local.models.TransactionEntity
import nz.co.test.transactions.data.datasources.remote.models.TransactionResponse
import java.util.Date
import kotlin.random.Random

internal fun createTransactionEntity(id: Int = Random.nextInt()) = TransactionEntity(
    id = id,
    transactionDate = Date(),
    summary = "summary",
    debit = Random.nextFloat(),
    credit = Random.nextFloat(),
    gst = null,
)

internal fun createRemoteTransaction() = TransactionResponse(
    id = Random.nextInt(),
    transactionDate = Date(),
    summary = "summary",
    debit = Random.nextFloat(),
    credit = Random.nextFloat(),
)

internal fun createTransactionEntity() = TransactionEntity(
    id = Random.nextInt(),
    transactionDate = Date(),
    summary = "summary",
    debit = Random.nextFloat(),
    credit = Random.nextFloat(),
    gst = null,
)

internal fun mockPagingSource(items: List<TransactionEntity>): PagingSource<Int, TransactionEntity> {
    return object : PagingSource<Int, TransactionEntity>() {
        override fun getRefreshKey(state: PagingState<Int, TransactionEntity>): Int? = null

        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, TransactionEntity> {
            return LoadResult.Page(
                data = items,
                prevKey = null,
                nextKey = null
            )
        }
    }
}