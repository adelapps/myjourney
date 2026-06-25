package ir.safareman.k60.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import ir.safareman.k60.data.DtsStep
import ir.safareman.k60.data.SmokingTravel
import ir.safareman.k60.data.SubstanceTravel
import ir.safareman.k60.data.TravelDatabase
import ir.safareman.k60.data.TravelRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import saman.zamani.persiandate.PersianDate

class TravelViewModel(application: Application) : AndroidViewModel(application) {
  private val repository: TravelRepository

  val substanceTravel: StateFlow<SubstanceTravel?>
  val smokingTravel: StateFlow<SmokingTravel?>
  val dtsSteps: StateFlow<List<DtsStep>>

  init {
    val database = TravelDatabase.getDatabase(application)
    repository = TravelRepository(database.travelDao())
    
    substanceTravel = repository.substanceTravel.stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = null
    )
    
    smokingTravel = repository.smokingTravel.stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = null
    )

    dtsSteps = repository.allDtsSteps.stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = emptyList()
    )
  }

  fun saveDtsStep(
    id: Int,
    startDate: Long,
    morningDose: String,
    afternoonDose: String,
    nightDose: String,
    reminderEnabled: Boolean,
    onSaved: (Long) -> Unit = {}
  ) {
    viewModelScope.launch {
      val step = DtsStep(
        id = id,
        startDate = startDate,
        morningDose = morningDose,
        afternoonDose = afternoonDose,
        nightDose = nightDose,
        reminderEnabled = reminderEnabled
      )
      val newId = repository.saveDtsStep(step)
      onSaved(if (id == 0) newId else id.toLong())
    }
  }

  fun deleteDtsStep(id: Int) {
    viewModelScope.launch {
      repository.deleteDtsStep(id)
    }
  }

  fun saveSubstanceTravel(
    name: String,
    yearsOfAddictionDamage: String,
    lastAntiXSubstance: String,
    travelStartDate: Long?,
    guideName: String,
    legionName: String,
    treatmentMedicine: String
  ) {
    viewModelScope.launch {
      val current = substanceTravel.value ?: SubstanceTravel()
      repository.saveSubstanceTravel(
        current.copy(
          name = name,
          yearsOfAddictionDamage = yearsOfAddictionDamage,
          lastAntiXSubstance = lastAntiXSubstance,
          travelStartDate = travelStartDate,
          guideName = guideName,
          legionName = legionName,
          treatmentMedicine = treatmentMedicine
        )
      )
    }
  }

  fun endSubstanceTravel(endDate: Long = System.currentTimeMillis()) {
    viewModelScope.launch {
      val current = substanceTravel.value ?: SubstanceTravel()
      repository.saveSubstanceTravel(
        current.copy(
          isCompleted = true,
          travelEndDate = endDate
        )
      )
    }
  }

  fun resetSubstanceTravel() {
    viewModelScope.launch {
      repository.saveSubstanceTravel(SubstanceTravel())
    }
  }

  fun saveSmokingTravel(
    yearsOfSmokingDamage: String,
    travelStartDate: Long?,
    guideName: String,
    treatmentMedicine: String
  ) {
    viewModelScope.launch {
      val current = smokingTravel.value ?: SmokingTravel()
      repository.saveSmokingTravel(
        current.copy(
          yearsOfSmokingDamage = yearsOfSmokingDamage,
          travelStartDate = travelStartDate,
          guideName = guideName,
          treatmentMedicine = treatmentMedicine
        )
      )
    }
  }

  fun endSmokingTravel(endDate: Long = System.currentTimeMillis()) {
    viewModelScope.launch {
      val current = smokingTravel.value ?: SmokingTravel()
      repository.saveSmokingTravel(
        current.copy(
          isCompleted = true,
          travelEndDate = endDate
        )
      )
    }
  }

  fun resetSmokingTravel() {
    viewModelScope.launch {
      repository.saveSmokingTravel(SmokingTravel())
    }
  }

  // --- Helper Date & Format Calculations ---
  
  fun calculateDuration(startDateMillis: Long?, endDateMillis: Long?): Pair<Int, Int> {
    if (startDateMillis == null) return Pair(0, 0)
    val end = endDateMillis ?: System.currentTimeMillis()
    val diffMillis = end - startDateMillis
    val totalDays = (diffMillis / (1000 * 60 * 60 * 24)).coerceAtLeast(0)
    val months = (totalDays / 30).toInt()
    val days = (totalDays % 30).toInt()
    return Pair(months, days)
  }

  fun calculateFreedomDuration(endDateMillis: Long?): Pair<Int, Int> {
    if (endDateMillis == null) return Pair(0, 0)
    val currentMillis = System.currentTimeMillis()
    val diffMillis = currentMillis - endDateMillis
    val totalDays = (diffMillis / (1000 * 60 * 60 * 24)).coerceAtLeast(0)
    val months = (totalDays / 30).toInt()
    val days = (totalDays % 30).toInt()
    return Pair(months, days)
  }
}

object JalaliCalendar {
  fun formatJalali(timestamp: Long?): String {
    if (timestamp == null) return ""
    val pDate = PersianDate(timestamp)
    val jy = pDate.shYear
    val jm = pDate.shMonth
    val jd = pDate.shDay
    return "${toPersianDigits(jy)}/${toPersianDigits(jm).padStart(2, '۰')}/${toPersianDigits(jd).padStart(2, '۰')}"
  }

  fun toPersianDigits(number: Int): String {
    val persianDigits = charArrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')
    return number.toString().map { char ->
      if (char.isDigit()) persianDigits[char - '0'] else char
    }.joinToString("")
  }

  fun toPersianDigits(str: String): String {
    val persianDigits = charArrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')
    return str.map { char ->
      if (char.isDigit()) persianDigits[char - '0'] else char
    }.joinToString("")
  }
}
