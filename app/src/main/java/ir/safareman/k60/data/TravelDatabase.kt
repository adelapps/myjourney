package ir.safareman.k60.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

import androidx.room.Update
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Entity(tableName = "substance_travel")
data class SubstanceTravel(
  @PrimaryKey val id: Int = 1,
  val name: String = "",
  val yearsOfAddictionDamage: String = "",
  val lastAntiXSubstance: String = "",
  val travelStartDate: Long? = null,
  val guideName: String = "",
  val legionName: String = "",
  val treatmentMedicine: String = "",
  val isCompleted: Boolean = false,
  val travelEndDate: Long? = null
)

@Entity(tableName = "smoking_travel")
data class SmokingTravel(
  @PrimaryKey val id: Int = 1,
  val yearsOfSmokingDamage: String = "",
  val travelStartDate: Long? = null,
  val guideName: String = "",
  val treatmentMedicine: String = "",
  val isCompleted: Boolean = false,
  val travelEndDate: Long? = null
)

@Entity(tableName = "dts_step")
data class DtsStep(
  @PrimaryKey(autoGenerate = true) val id: Int = 0,
  val startDate: Long,
  val morningDose: String,
  val afternoonDose: String,
  val nightDose: String,
  val reminderEnabled: Boolean = true
)

@Dao
interface TravelDao {
  @Query("SELECT * FROM substance_travel WHERE id = 1")
  fun getSubstanceTravel(): Flow<SubstanceTravel?>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertSubstanceTravel(travel: SubstanceTravel)

  @Query("SELECT * FROM smoking_travel WHERE id = 1")
  fun getSmokingTravel(): Flow<SmokingTravel?>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertSmokingTravel(travel: SmokingTravel)

  @Query("SELECT * FROM dts_step ORDER BY startDate ASC")
  fun getAllDtsSteps(): Flow<List<DtsStep>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertDtsStep(step: DtsStep): Long

  @Query("DELETE FROM dts_step WHERE id = :id")
  suspend fun deleteDtsStepById(id: Int)
}

@Database(entities = [SubstanceTravel::class, SmokingTravel::class, DtsStep::class], version = 2, exportSchema = false)
abstract class TravelDatabase : RoomDatabase() {
  abstract fun travelDao(): TravelDao

  companion object {
    @Volatile
    private var INSTANCE: TravelDatabase? = null

    val MIGRATION_1_2 = object : Migration(1, 2) {
      override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
          "CREATE TABLE IF NOT EXISTS `dts_step` (" +
            "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
            "`startDate` INTEGER NOT NULL, " +
            "`morningDose` TEXT NOT NULL, " +
            "`afternoonDose` TEXT NOT NULL, " +
            "`nightDose` TEXT NOT NULL, " +
            "`reminderEnabled` INTEGER NOT NULL DEFAULT 1" +
          ")"
        )
      }
    }

    fun getDatabase(context: Context): TravelDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          TravelDatabase::class.java,
          "travel_database"
        )
        .addMigrations(MIGRATION_1_2)
        .fallbackToDestructiveMigration()
        .build()
        INSTANCE = instance
        instance
      }
    }
  }
}

class TravelRepository(private val travelDao: TravelDao) {
  val substanceTravel: Flow<SubstanceTravel?> = travelDao.getSubstanceTravel()
  val smokingTravel: Flow<SmokingTravel?> = travelDao.getSmokingTravel()
  val allDtsSteps: Flow<List<DtsStep>> = travelDao.getAllDtsSteps()

  suspend fun saveSubstanceTravel(travel: SubstanceTravel) {
    travelDao.insertSubstanceTravel(travel)
  }

  suspend fun saveSmokingTravel(travel: SmokingTravel) {
    travelDao.insertSmokingTravel(travel)
  }

  suspend fun saveDtsStep(step: DtsStep): Long {
    return travelDao.insertDtsStep(step)
  }

  suspend fun deleteDtsStep(id: Int) {
    travelDao.deleteDtsStepById(id)
  }
}
