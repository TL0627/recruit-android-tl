package nz.co.test.transactions.infrastructures.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import nz.co.test.transactions.domain.ITransactionInteractor
import nz.co.test.transactions.domain.impl.TransactionInteractorImpl

@Module
@InstallIn(SingletonComponent::class)
interface InteractorModule {
    @Binds
    fun bindTransactionInteractor(transactionInteractor: TransactionInteractorImpl): ITransactionInteractor
}
