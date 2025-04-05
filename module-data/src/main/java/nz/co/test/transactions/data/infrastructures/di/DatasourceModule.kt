package nz.co.test.transactions.data.infrastructures.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import nz.co.test.transactions.data.datasources.local.ITransactionLocalDatasource
import nz.co.test.transactions.data.datasources.local.impl.TransactionLocalDatasourceImpl
import nz.co.test.transactions.data.datasources.remote.ITransactionRemoteDatasource
import nz.co.test.transactions.data.datasources.remote.impl.TransactionRemoteDatasourceImpl

@Module
@InstallIn(SingletonComponent::class)
interface DatasourceModule {
    @Binds
    fun bindTransactionRemoteDatasource(transactionRemoteDatasource: TransactionRemoteDatasourceImpl): ITransactionRemoteDatasource

    @Binds
    fun bindTransactionLocalDatasource(transactionLocalDatasource: TransactionLocalDatasourceImpl): ITransactionLocalDatasource
}
