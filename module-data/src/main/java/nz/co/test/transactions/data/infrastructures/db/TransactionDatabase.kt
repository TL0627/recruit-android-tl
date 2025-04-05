package nz.co.test.transactions.data.infrastructures.db


import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import nz.co.test.transactions.data.datasources.local.models.TransactionEntity
import nz.co.test.transactions.data.infrastructures.db.daos.TransactionDao
import java.util.Date

@Database(
    entities = [ TransactionEntity::class ],
    version = 1,
    exportSchema = true,
)
@TypeConverters(
    DateConverter::class,
)
abstract class TransactionDatabase : RoomDatabase() {
    abstract fun transactionsDao(): TransactionDao

    companion object {
        const val NAME = "TransactionsDB"
    }
}

class DateConverter {
    @TypeConverter
    fun fromDate(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun toDate(milliSeconds: Long?): Date? {
        return if (milliSeconds == null) null
        else Date(milliSeconds)
    }
}