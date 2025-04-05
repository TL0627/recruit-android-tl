package nz.co.test.transactions.data.infrastructures.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import nz.co.test.transactions.data.repositories.ITransactionsRepository
import nz.co.test.transactions.data.repositories.impl.TransactionsRepositoryImpl

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {
    @Binds
    fun bindTransactionsRepository(transactionsRepository: TransactionsRepositoryImpl): ITransactionsRepository
}
