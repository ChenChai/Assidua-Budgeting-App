package ai.chench.assidua

import android.content.Context
import androidx.room.*
import java.math.BigDecimal
import java.util.*

@Database(entities = [Expenditure::class, Budget::class], version = 1)
@TypeConverters(Converters::class)
public abstract class AssiduaRoomDatabase : RoomDatabase() {
    abstract fun expenditureDAO() : ExpenditureDAO

    companion object {
        @Volatile
        private var INSTANCE: AssiduaRoomDatabase? = null

        fun getDatabase(context: Context): AssiduaRoomDatabase {
            val instance = Room.databaseBuilder(context.applicationContext,
                    AssiduaRoomDatabase::class.java,
                    "Budget_Database").build()
            INSTANCE = instance
            return instance
        }
    }
}


