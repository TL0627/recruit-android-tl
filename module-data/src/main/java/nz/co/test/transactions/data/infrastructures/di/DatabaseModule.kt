package nz.co.test.transactions.data.infrastructures.di

import android.content.Context
import androidx.room.Room.databaseBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import nz.co.test.transactions.data.infrastructures.db.TransactionDatabase
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {
    @Singleton
    @Provides
    fun provideRoomDatabase(@ApplicationContext context: Context): TransactionDatabase {
        return databaseBuilder(
            context,
            TransactionDatabase::class.java,
            TransactionDatabase.NAME
        )
            .addMigrations(
                //add migration if needed
            )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun provideTransactionDao(db: TransactionDatabase) = db.transactionsDao()
}