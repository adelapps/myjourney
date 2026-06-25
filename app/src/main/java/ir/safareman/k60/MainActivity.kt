package ir.safareman.k60

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SmokingRooms
import androidx.compose.material.icons.filled.SportsKabaddi
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.foundation.clickable
import androidx.compose.material3.IconButton
import androidx.compose.foundation.layout.widthIn
import saman.zamani.persiandate.PersianDate
import java.util.Calendar
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import ir.safareman.k60.data.SmokingTravel
import ir.safareman.k60.data.SubstanceTravel
import ir.safareman.k60.ui.theme.TravelTheme
import ir.safareman.k60.ui.theme.VazirmatnFontFamily
import ir.safareman.k60.viewmodel.JalaliCalendar
import ir.safareman.k60.viewmodel.TravelViewModel
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.runtime.rememberCoroutineScope
import ir.safareman.k60.data.DtsStep
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.delay
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.ui.draw.alpha

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      TravelTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
          var showSplash by remember { mutableStateOf(true) }

          if (showSplash) {
            SplashScreen(onSplashFinished = { showSplash = false })
          } else {
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
              MainScreen(modifier = Modifier.padding(innerPadding))
            }
          }
        }
      }
    }
  }
}

@Composable
fun SplashScreen(onSplashFinished: () -> Unit) {
  var startAnimation by remember { mutableStateOf(false) }
  val alphaAnim by animateFloatAsState(
    targetValue = if (startAnimation) 1f else 0f,
    animationSpec = tween(durationMillis = 800),
    label = "SplashAlpha"
  )

  LaunchedEffect(key1 = true) {
    startAnimation = true
    delay(2000)
    onSplashFinished()
  }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background),
    contentAlignment = Alignment.Center
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center,
      modifier = Modifier.alpha(alphaAnim)
    ) {
      Image(
        painter = painterResource(id = R.drawable.img_app_icon),
        contentDescription = "App Icon",
        modifier = Modifier
          .size(160.dp)
          .clip(RoundedCornerShape(32.dp)),
        contentScale = androidx.compose.ui.layout.ContentScale.Crop
      )
      Spacer(modifier = Modifier.height(24.dp))
      Text(
        text = "سفر من",
        style = MaterialTheme.typography.headlineLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        fontFamily = VazirmatnFontFamily,
        textAlign = TextAlign.Center
      )
    }
  }
}

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
  val viewModel: TravelViewModel = viewModel()
  val substanceTravel by viewModel.substanceTravel.collectAsState()
  val smokingTravel by viewModel.smokingTravel.collectAsState()

  var selectedTab by remember { mutableIntStateOf(0) }
  val tabs = listOf("اعلام سفر", "DTS", "سفر مواد", "سفر سیگار")

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
  ) {
    // Elegant Editorial Header (With reduced padding & margins to maximize available content space)
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .background(MaterialTheme.colorScheme.background)
        .padding(top = 10.dp, bottom = 4.dp, start = 16.dp, end = 16.dp),
      verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
      Text(
        text = "سفر من",
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        fontFamily = VazirmatnFontFamily
      )
      Text(
        text = "مدیریت سفر و رهایی",
        style = MaterialTheme.typography.bodySmall,
        fontSize = 12.sp,
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
        fontFamily = VazirmatnFontFamily
      )
    }

    // Clean Tab Row (Matching Tailwind Nav styling with reduced sizes and optimized padding)
    TabRow(
      selectedTabIndex = selectedTab,
      containerColor = MaterialTheme.colorScheme.background,
      contentColor = MaterialTheme.colorScheme.primary,
      indicator = { tabPositions ->
        TabRowDefaults.SecondaryIndicator(
          modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
          color = MaterialTheme.colorScheme.primary,
          height = 3.dp
        )
      },
      modifier = Modifier.padding(horizontal = 4.dp)
    ) {
      tabs.forEachIndexed { index, title ->
        Tab(
          selected = selectedTab == index,
          onClick = { selectedTab = index },
          text = {
            Text(
              text = title,
              fontFamily = VazirmatnFontFamily,
              fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium,
              fontSize = 14.sp
            )
          },
          icon = {
            when (index) {
              0 -> Icon(Icons.Default.VolumeUp, contentDescription = null, modifier = Modifier.size(18.dp))
              1 -> Icon(Icons.Default.Timeline, contentDescription = null, modifier = Modifier.size(18.dp))
              2 -> Icon(Icons.Default.SportsKabaddi, contentDescription = null, modifier = Modifier.size(18.dp))
              3 -> Icon(Icons.Default.SmokingRooms, contentDescription = null, modifier = Modifier.size(18.dp))
            }
          }
        )
      }
    }

    // Tab Content Panel
    Box(
      modifier = Modifier
        .weight(1f)
        .fillMaxWidth()
    ) {
      when (selectedTab) {
        0 -> AnnouncementTab(
          substance = substanceTravel,
          smoking = smokingTravel,
          viewModel = viewModel
        )
        1 -> DtsTab(
          viewModel = viewModel
        )
        2 -> SubstanceTravelTab(
          travel = substanceTravel,
          viewModel = viewModel
        )
        3 -> SmokingTravelTab(
          travel = smokingTravel,
          viewModel = viewModel
        )
      }
    }
  }
}

@Composable
fun SubstanceTravelTab(
  travel: SubstanceTravel?,
  viewModel: TravelViewModel
) {
  val currentTravel = travel ?: SubstanceTravel()

  var name by remember(travel) { mutableStateOf(currentTravel.name) }
  var yearsDamage by remember(travel) { mutableStateOf(currentTravel.yearsOfAddictionDamage) }
  var lastAntiX by remember(travel) { mutableStateOf(currentTravel.lastAntiXSubstance) }
  var startDate by remember(travel) { mutableStateOf(currentTravel.travelStartDate) }
  var guideName by remember(travel) { mutableStateOf(currentTravel.guideName) }
  var legionName by remember(travel) { mutableStateOf(currentTravel.legionName) }
  var treatmentMedicine by remember(travel) {
    mutableStateOf(currentTravel.treatmentMedicine.ifBlank { "شربت اوتی (OT)" })
  }
  var showEndTravelDatePicker by remember { mutableStateOf(false) }

  val scrollState = rememberScrollState()
  val context = LocalContext.current

  if (showEndTravelDatePicker) {
    PersianDatePickerDialog(
      initialTimestamp = System.currentTimeMillis(),
      onDismissRequest = { showEndTravelDatePicker = false },
      onDateSelected = { selectedDate ->
        viewModel.saveSubstanceTravel(
          name = name,
          yearsOfAddictionDamage = yearsDamage,
          lastAntiXSubstance = lastAntiX,
          travelStartDate = startDate,
          guideName = guideName,
          legionName = legionName,
          treatmentMedicine = treatmentMedicine
        )
        viewModel.endSubstanceTravel(selectedDate)
        Toast.makeText(context, "پایان سفر مواد ثبت و تبریک رهایی شما ثبت شد!", Toast.LENGTH_LONG).show()
        showEndTravelDatePicker = false
      }
    )
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(scrollState)
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Editorial Status Badge
    StatusHeader(
      isCompleted = currentTravel.isCompleted,
      completionDate = currentTravel.travelEndDate,
      onReset = {
        viewModel.resetSubstanceTravel()
        Toast.makeText(context, "اطلاعات سفر مواد بازنشانی شد.", Toast.LENGTH_SHORT).show()
      }
    )

    Card(
      modifier = Modifier.fillMaxWidth(),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      shape = RoundedCornerShape(24.dp),
      border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)),
      elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
      Column(
        modifier = Modifier.padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        Text(
          text = "مشخصات سفر مواد (مسافر آنتی ایکس)",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.primary,
          fontFamily = VazirmatnFontFamily
        )

        OutlinedTextField(
          value = name,
          onValueChange = { name = it },
          label = { Text("نام مسافر", fontFamily = VazirmatnFontFamily) },
          modifier = Modifier.fillMaxWidth().testTag("substance_name_field"),
          leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
          shape = RoundedCornerShape(16.dp)
        )

        OutlinedTextField(
          value = yearsDamage,
          onValueChange = { yearsDamage = it },
          label = { Text("سال‌های تخریب اعتیاد", fontFamily = VazirmatnFontFamily) },
          modifier = Modifier.fillMaxWidth().testTag("substance_damage_field"),
          leadingIcon = { Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          shape = RoundedCornerShape(16.dp)
        )

        OutlinedTextField(
          value = lastAntiX,
          onValueChange = { lastAntiX = it },
          label = { Text("آخرین آنتی ایکس مصرفی", fontFamily = VazirmatnFontFamily) },
          placeholder = { Text("مثال: تریاک، هروئین، شیشه", fontFamily = VazirmatnFontFamily) },
          modifier = Modifier.fillMaxWidth().testTag("substance_antix_field"),
          shape = RoundedCornerShape(16.dp)
        )

        DatePickerField(
          label = "تاریخ شروع سفر مواد",
          currentTimestamp = startDate,
          onDateSelected = { startDate = it },
          modifier = Modifier.testTag("substance_date_picker")
        )

        OutlinedTextField(
          value = guideName,
          onValueChange = { guideName = it },
          label = { Text("نام راهنمای درمان", fontFamily = VazirmatnFontFamily) },
          modifier = Modifier.fillMaxWidth().testTag("substance_guide_field"),
          shape = RoundedCornerShape(16.dp)
        )

        OutlinedTextField(
          value = legionName,
          onValueChange = { legionName = it },
          label = { Text("نام یا شماره لژیون", fontFamily = VazirmatnFontFamily) },
          placeholder = { Text("مثال: لژیون دهم", fontFamily = VazirmatnFontFamily) },
          modifier = Modifier.fillMaxWidth().testTag("substance_legion_field"),
          shape = RoundedCornerShape(16.dp)
        )

        OutlinedTextField(
          value = treatmentMedicine,
          onValueChange = { treatmentMedicine = it },
          label = { Text("داروی درمان", fontFamily = VazirmatnFontFamily) },
          modifier = Modifier.fillMaxWidth().testTag("substance_medicine_field"),
          shape = RoundedCornerShape(16.dp)
        )
      }
    }

    // Elegant Button layout
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      Button(
        onClick = {
          viewModel.saveSubstanceTravel(
            name = name,
            yearsOfAddictionDamage = yearsDamage,
            lastAntiXSubstance = lastAntiX,
            travelStartDate = startDate,
            guideName = guideName,
            legionName = legionName,
            treatmentMedicine = treatmentMedicine
          )
          Toast.makeText(context, "اطلاعات سفر مواد ذخیره شد.", Toast.LENGTH_SHORT).show()
        },
        modifier = Modifier
          .weight(1f)
          .height(54.dp)
          .testTag("substance_save_button"),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = MaterialTheme.colorScheme.primary,
          contentColor = MaterialTheme.colorScheme.onPrimary
        )
      ) {
        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "ذخیره اطلاعات",
          fontWeight = FontWeight.Bold,
          fontSize = 12.sp,
          fontFamily = VazirmatnFontFamily,
          maxLines = 1,
          softWrap = false
        )
      }

      if (!currentTravel.isCompleted && startDate != null) {
        Button(
          onClick = {
            showEndTravelDatePicker = true
          },
          modifier = Modifier
            .weight(1.1f)
            .height(54.dp)
            .testTag("substance_end_button"),
          shape = RoundedCornerShape(16.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.tertiary,
            contentColor = MaterialTheme.colorScheme.onTertiary
          )
        ) {
          Icon(Icons.Default.Assignment, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "اتمام سفر (رهایی)",
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            fontFamily = VazirmatnFontFamily,
            maxLines = 1,
            softWrap = false
          )
        }
      }
    }
    
    Spacer(modifier = Modifier.height(24.dp))
  }
}

@Composable
fun SmokingTravelTab(
  travel: SmokingTravel?,
  viewModel: TravelViewModel
) {
  val currentTravel = travel ?: SmokingTravel()

  var yearsDamage by remember(travel) { mutableStateOf(currentTravel.yearsOfSmokingDamage) }
  var startDate by remember(travel) { mutableStateOf(currentTravel.travelStartDate) }
  var guideName by remember(travel) { mutableStateOf(currentTravel.guideName) }
  var treatmentMedicine by remember(travel) {
    mutableStateOf(currentTravel.treatmentMedicine.ifBlank { "آدامس نیکوتین" })
  }
  var showEndTravelDatePicker by remember { mutableStateOf(false) }

  val scrollState = rememberScrollState()
  val context = LocalContext.current

  if (showEndTravelDatePicker) {
    PersianDatePickerDialog(
      initialTimestamp = System.currentTimeMillis(),
      onDismissRequest = { showEndTravelDatePicker = false },
      onDateSelected = { selectedDate ->
        viewModel.saveSmokingTravel(
          yearsOfSmokingDamage = yearsDamage,
          travelStartDate = startDate,
          guideName = guideName,
          treatmentMedicine = treatmentMedicine
        )
        viewModel.endSmokingTravel(selectedDate)
        Toast.makeText(context, "پایان سفر سیگار ثبت شد! به سلامتی ریه‌های پاک شما تبریک می‌گوییم.", Toast.LENGTH_LONG).show()
        showEndTravelDatePicker = false
      }
    )
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(scrollState)
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    StatusHeader(
      isCompleted = currentTravel.isCompleted,
      completionDate = currentTravel.travelEndDate,
      onReset = {
        viewModel.resetSmokingTravel()
        Toast.makeText(context, "اطلاعات سفر سیگار بازنشانی شد.", Toast.LENGTH_SHORT).show()
      }
    )

    Card(
      modifier = Modifier.fillMaxWidth(),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      shape = RoundedCornerShape(24.dp),
      border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)),
      elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
      Column(
        modifier = Modifier.padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        Text(
          text = "مشخصات سفر سیگار (ویلیام وایت)",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.secondary,
          fontFamily = VazirmatnFontFamily
        )

        OutlinedTextField(
          value = yearsDamage,
          onValueChange = { yearsDamage = it },
          label = { Text("سال‌های تخریب سیگار", fontFamily = VazirmatnFontFamily) },
          modifier = Modifier.fillMaxWidth().testTag("smoking_damage_field"),
          leadingIcon = { Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.secondary) },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          shape = RoundedCornerShape(16.dp)
        )

        DatePickerField(
          label = "تاریخ شروع سفر سیگار",
          currentTimestamp = startDate,
          onDateSelected = { startDate = it },
          modifier = Modifier.testTag("smoking_date_picker")
        )

        OutlinedTextField(
          value = guideName,
          onValueChange = { guideName = it },
          label = { Text("نام راهنمای ویلیام وایت", fontFamily = VazirmatnFontFamily) },
          modifier = Modifier.fillMaxWidth().testTag("smoking_guide_field"),
          shape = RoundedCornerShape(16.dp)
        )

        OutlinedTextField(
          value = treatmentMedicine,
          onValueChange = { treatmentMedicine = it },
          label = { Text("داروی درمان سیگار", fontFamily = VazirmatnFontFamily) },
          modifier = Modifier.fillMaxWidth().testTag("smoking_medicine_field"),
          shape = RoundedCornerShape(16.dp)
        )
      }
    }

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      Button(
        onClick = {
          viewModel.saveSmokingTravel(
            yearsOfSmokingDamage = yearsDamage,
            travelStartDate = startDate,
            guideName = guideName,
            treatmentMedicine = treatmentMedicine
          )
          Toast.makeText(context, "اطلاعات سفر سیگار ذخیره شد.", Toast.LENGTH_SHORT).show()
        },
        modifier = Modifier
          .weight(1f)
          .height(54.dp)
          .testTag("smoking_save_button"),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = MaterialTheme.colorScheme.primary,
          contentColor = MaterialTheme.colorScheme.onPrimary
        )
      ) {
        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "ذخیره اطلاعات",
          fontWeight = FontWeight.Bold,
          fontSize = 12.sp,
          fontFamily = VazirmatnFontFamily,
          maxLines = 1,
          softWrap = false
        )
      }

      if (!currentTravel.isCompleted && startDate != null) {
        Button(
          onClick = {
            showEndTravelDatePicker = true
          },
          modifier = Modifier
            .weight(1.1f)
            .height(54.dp)
            .testTag("smoking_end_button"),
          shape = RoundedCornerShape(16.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.tertiary,
            contentColor = MaterialTheme.colorScheme.onTertiary
          )
        ) {
          Icon(Icons.Default.Assignment, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "اتمام سفر (رهایی)",
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            fontFamily = VazirmatnFontFamily,
            maxLines = 1,
            softWrap = false
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(24.dp))
  }
}

@Composable
fun AnnouncementTab(
  substance: SubstanceTravel?,
  smoking: SmokingTravel?,
  viewModel: TravelViewModel
) {
  val context = LocalContext.current
  val generatedAnnotatedText = generateAnnouncementAnnotatedText(substance, smoking, viewModel)
  
  // Extract clean text representation for copying to clipboard
  val cleanTextString = generatedAnnotatedText.text

  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(10.dp),
    verticalArrangement = Arrangement.spacedBy(10.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    if (substance?.isCompleted == true) {
      val (months, days) = viewModel.calculateFreedomDuration(substance.travelEndDate)
      val monthsPersian = JalaliCalendar.toPersianDigits(months)
      val daysPersian = JalaliCalendar.toPersianDigits(days)
      val endDatePersian = JalaliCalendar.formatJalali(substance.travelEndDate)

      Card(
        modifier = Modifier.fillMaxWidth().testTag("substance_freedom_card"),
        colors = CardDefaults.cardColors(
          containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f)
        ),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Text(
              text = "🌱 رهایی مواد",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.primary,
              fontFamily = VazirmatnFontFamily
            )
            Text(
              text = "$monthsPersian ماه و $daysPersian روز",
              style = MaterialTheme.typography.titleLarge,
              fontWeight = FontWeight.ExtraBold,
              color = MaterialTheme.colorScheme.onSurface,
              fontFamily = VazirmatnFontFamily
            )
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              Text(
                text = "تاریخ رهایی:",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontFamily = VazirmatnFontFamily
              )
              Text(
                text = endDatePersian,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                fontFamily = VazirmatnFontFamily
              )
            }
          }
          Box(
            modifier = Modifier
              .size(44.dp)
              .background(
                MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                shape = RoundedCornerShape(12.dp)
              ),
            contentAlignment = Alignment.Center
          ) {
            Text("🌱", fontSize = 22.sp)
          }
        }
      }
    }

    if (smoking?.isCompleted == true) {
      val (months, days) = viewModel.calculateFreedomDuration(smoking.travelEndDate)
      val monthsPersian = JalaliCalendar.toPersianDigits(months)
      val daysPersian = JalaliCalendar.toPersianDigits(days)
      val endDatePersian = JalaliCalendar.formatJalali(smoking.travelEndDate)

      Card(
        modifier = Modifier.fillMaxWidth().testTag("smoking_freedom_card"),
        colors = CardDefaults.cardColors(
          containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f)
        ),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Text(
              text = "🚭 رهایی سیگار",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.primary,
              fontFamily = VazirmatnFontFamily
            )
            Text(
              text = "$monthsPersian ماه و $daysPersian روز",
              style = MaterialTheme.typography.titleLarge,
              fontWeight = FontWeight.ExtraBold,
              color = MaterialTheme.colorScheme.onSurface,
              fontFamily = VazirmatnFontFamily
            )
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              Text(
                text = "تاریخ رهایی:",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontFamily = VazirmatnFontFamily
              )
              Text(
                text = endDatePersian,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                fontFamily = VazirmatnFontFamily
              )
            }
          }
          Box(
            modifier = Modifier
              .size(44.dp)
              .background(
                MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                shape = RoundedCornerShape(12.dp)
              ),
            contentAlignment = Alignment.Center
          ) {
            Text("🚭", fontSize = 22.sp)
          }
        }
      }
    }

    Card(
      modifier = Modifier
        .fillMaxWidth()
        .weight(1f),
      colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)
      ),
      shape = RoundedCornerShape(20.dp),
      border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)),
      elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(16.dp)
      ) {
        Column(
          modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
        ) {
          Text(
            text = generatedAnnotatedText,
            style = MaterialTheme.typography.bodyLarge.copy(
              lineHeight = 32.sp,
              fontSize = 19.sp
            ),
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Justify,
            modifier = Modifier.fillMaxWidth().testTag("announcement_text")
          )
        }
      }
    }

    // Large Copy Button Styled after Editorial Aesthetic
    Button(
      onClick = {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Congress 60 Announcement", cleanTextString)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "متن اعلام سفر در حافظه کپی شد!", Toast.LENGTH_SHORT).show()
      },
      modifier = Modifier
        .fillMaxWidth()
        .height(52.dp)
        .testTag("copy_announcement_button"),
      shape = RoundedCornerShape(14.dp),
      colors = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary
      ),
      elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
    ) {
      Icon(
        imageVector = Icons.Default.ContentCopy,
        contentDescription = "کپی متن",
        modifier = Modifier.size(20.dp)
      )
      Spacer(modifier = Modifier.width(10.dp))
      Text(
        text = "کپی متن اعلام سفر",
        fontFamily = VazirmatnFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 15.sp
      )
    }
  }
}

@Composable
fun StatusHeader(
  isCompleted: Boolean,
  completionDate: Long?,
  onReset: () -> Unit
) {
  var showConfirmReset by remember { mutableStateOf(false) }

  Card(
    modifier = Modifier.fillMaxWidth(),
    colors = CardDefaults.cardColors(
      containerColor = if (isCompleted) {
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
      } else {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
      }
    ),
    shape = RoundedCornerShape(16.dp),
    border = BorderStroke(
      width = 1.dp,
      color = if (isCompleted) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
      } else {
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
      }
    )
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
        Icon(
          imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.Info,
          contentDescription = null,
          tint = if (isCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
          modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column {
          Text(
            text = if (isCompleted) "وضعیت: سفر به اتمام رسیده (آزاد و رها)" else "وضعیت: در حال سفر (پله‌های درمانی)",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            fontFamily = VazirmatnFontFamily
          )
          if (isCompleted && completionDate != null) {
            Text(
              text = "تاریخ رهایی: ${JalaliCalendar.formatJalali(completionDate)}",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              fontFamily = VazirmatnFontFamily
            )
          }
        }
      }

      // Small Reset Button styled with thin outline and error tint
      OutlinedButton(
        onClick = { showConfirmReset = true },
        modifier = Modifier.height(36.dp),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.3f)),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
      ) {
        Icon(Icons.Default.Refresh, contentDescription = "شروع مجدد", modifier = Modifier.size(15.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text("شروع مجدد", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, fontFamily = VazirmatnFontFamily)
      }
    }
  }

  if (showConfirmReset) {
    AlertDialog(
      onDismissRequest = { showConfirmReset = false },
      title = { Text("شروع مجدد سفر", fontWeight = FontWeight.Bold, fontFamily = VazirmatnFontFamily) },
      text = { Text("آیا مطمئن هستید که میخواهید اطلاعات این سفر را پاک کرده و سفر جدیدی آغاز کنید؟", fontFamily = VazirmatnFontFamily) },
      confirmButton = {
        TextButton(
          onClick = {
            onReset()
            showConfirmReset = false
          }
        ) {
          Text("تأیید", color = MaterialTheme.colorScheme.error, fontFamily = VazirmatnFontFamily, fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { showConfirmReset = false }) {
          Text("انصراف", fontFamily = VazirmatnFontFamily)
        }
      }
    )
  }
}

@Composable
fun DatePickerField(
  label: String,
  currentTimestamp: Long?,
  onDateSelected: (Long) -> Unit,
  modifier: Modifier = Modifier
) {
  var showDialog by remember { mutableStateOf(false) }
  val formattedDate = if (currentTimestamp != null) {
    JalaliCalendar.formatJalali(currentTimestamp)
  } else {
    "تاریخ انتخاب نشده است"
  }

  Card(
    onClick = { showDialog = true },
    modifier = modifier.fillMaxWidth(),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)
    ),
    shape = RoundedCornerShape(16.dp),
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)),
    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
  ) {
    Row(
      modifier = Modifier
        .padding(horizontal = 16.dp, vertical = 14.dp)
        .fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text(
          text = label,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          fontFamily = VazirmatnFontFamily
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = formattedDate,
          style = MaterialTheme.typography.bodyMedium,
          fontWeight = FontWeight.Bold,
          color = if (currentTimestamp != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
          fontFamily = VazirmatnFontFamily
        )
      }
      Icon(
        imageVector = Icons.Default.CalendarToday,
        contentDescription = "انتخاب تاریخ",
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(20.dp)
      )
    }
  }

  if (showDialog) {
    PersianDatePickerDialog(
      initialTimestamp = currentTimestamp,
      onDismissRequest = { showDialog = false },
      onDateSelected = onDateSelected
    )
  }
}

@Composable
fun PersianDatePickerDialog(
  initialTimestamp: Long?,
  onDismissRequest: () -> Unit,
  onDateSelected: (Long) -> Unit
) {
  val initialDate = remember(initialTimestamp) {
    PersianDate(initialTimestamp ?: System.currentTimeMillis())
  }

  var selectedYear by remember { mutableIntStateOf(initialDate.shYear) }
  var selectedMonth by remember { mutableIntStateOf(initialDate.shMonth) } // 1 to 12
  var selectedDay by remember { mutableIntStateOf(initialDate.shDay) }

  AlertDialog(
    onDismissRequest = onDismissRequest,
    confirmButton = {
      TextButton(
        onClick = {
          val pDate = PersianDate()
          pDate.initJalaliDate(selectedYear, selectedMonth, selectedDay)
          pDate.hour = 12
          pDate.minute = 0
          pDate.second = 0
          onDateSelected(pDate.time)
          onDismissRequest()
        }
      ) {
        Text("تأیید", fontFamily = VazirmatnFontFamily, fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismissRequest) {
        Text("انصراف", fontFamily = VazirmatnFontFamily)
      }
    },
    title = {
      Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
          text = "انتخاب تاریخ",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          fontFamily = VazirmatnFontFamily
        )
        Spacer(modifier = Modifier.height(4.dp))
        val formattedSel = JalaliCalendar.toPersianDigits(selectedYear) + "/" +
            JalaliCalendar.toPersianDigits(selectedMonth).padStart(2, '۰') + "/" +
            JalaliCalendar.toPersianDigits(selectedDay).padStart(2, '۰')
        Text(
          text = formattedSel,
          style = MaterialTheme.typography.headlineMedium,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.primary,
          fontFamily = VazirmatnFontFamily
        )
      }
    },
    text = {
      Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Year controls
          Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = {
              selectedYear -= 1
              val maxDays = getDaysInJalaliMonth(selectedYear, selectedMonth)
              if (selectedDay > maxDays) selectedDay = maxDays
            }) {
              Text("«", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
            Text(
              text = JalaliCalendar.toPersianDigits(selectedYear),
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              fontFamily = VazirmatnFontFamily
            )
            IconButton(onClick = {
              selectedYear += 1
              val maxDays = getDaysInJalaliMonth(selectedYear, selectedMonth)
              if (selectedDay > maxDays) selectedDay = maxDays
            }) {
              Text("»", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
          }

          // Month controls
          val monthNames = listOf(
            "فروردین", "اردیبهشت", "خرداد", "تیر", "مرداد", "شهریور",
            "مهر", "آبان", "آذر", "دی", "بهمن", "اسفند"
          )
          Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = {
              if (selectedMonth > 1) {
                selectedMonth -= 1
              } else {
                selectedYear -= 1
                selectedMonth = 12
              }
              val maxDays = getDaysInJalaliMonth(selectedYear, selectedMonth)
              if (selectedDay > maxDays) selectedDay = maxDays
            }) {
              Text("<", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
            Text(
              text = monthNames[selectedMonth - 1],
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              fontFamily = VazirmatnFontFamily,
              modifier = Modifier.widthIn(min = 60.dp),
              textAlign = TextAlign.Center
            )
            IconButton(onClick = {
              if (selectedMonth < 12) {
                selectedMonth += 1
              } else {
                selectedYear += 1
                selectedMonth = 1
              }
              val maxDays = getDaysInJalaliMonth(selectedYear, selectedMonth)
              if (selectedDay > maxDays) selectedDay = maxDays
            }) {
              Text(">", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Weekdays Header
        val weekDays = listOf("ش", "ی", "د", "س", "چ", "پ", "ج")
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
          weekDays.forEach { day ->
            Text(
              text = day,
              modifier = Modifier.width(32.dp),
              textAlign = TextAlign.Center,
              style = MaterialTheme.typography.bodySmall,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
              fontFamily = VazirmatnFontFamily
            )
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Calendar Grid
        val daysInMonth = getDaysInJalaliMonth(selectedYear, selectedMonth)
        val firstDayOfWeekOffset = getFirstDayOfWeekOffset(selectedYear, selectedMonth)

        val totalGridCells = daysInMonth + firstDayOfWeekOffset
        val rowsCount = (totalGridCells + 6) / 7

        Column(
          verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          for (rowIndex in 0 until rowsCount) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceEvenly
            ) {
              for (colIndex in 0 until 7) {
                val cellIndex = rowIndex * 7 + colIndex
                val dayNumber = cellIndex - firstDayOfWeekOffset + 1

                if (dayNumber in 1..daysInMonth) {
                  val isSelected = dayNumber == selectedDay
                  Box(
                    modifier = Modifier
                      .size(32.dp)
                      .clip(RoundedCornerShape(16.dp))
                      .background(
                        if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
                      )
                      .clickable {
                        selectedDay = dayNumber
                      },
                    contentAlignment = Alignment.Center
                  ) {
                    Text(
                      text = JalaliCalendar.toPersianDigits(dayNumber),
                      color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                      style = MaterialTheme.typography.bodyMedium,
                      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                      fontFamily = VazirmatnFontFamily
                    )
                  }
                } else {
                  Spacer(modifier = Modifier.size(32.dp))
                }
              }
            }
          }
        }
      }
    }
  )
}

fun getDaysInJalaliMonth(year: Int, month: Int): Int {
  return when (month) {
    in 1..6 -> 31
    in 7..11 -> 30
    12 -> if (isJalaliLeapYear(year)) 30 else 29
    else -> 30
  }
}

fun isJalaliLeapYear(year: Int): Boolean {
  val r = (year - 474) % 2820
  val r2 = (r + 474) % 33
  return (r2 * 8 + 21) % 33 < 8
}

fun getFirstDayOfWeekOffset(year: Int, month: Int): Int {
  val pd = PersianDate()
  pd.initJalaliDate(year, month, 1)
  val gy = pd.grgYear
  val gm = pd.grgMonth
  val gd = pd.grgDay
  
  val calendar = Calendar.getInstance()
  calendar.set(gy, gm - 1, gd)
  val calendarDay = calendar.get(Calendar.DAY_OF_WEEK)
  return when (calendarDay) {
    Calendar.SATURDAY -> 0
    Calendar.SUNDAY -> 1
    Calendar.MONDAY -> 2
    Calendar.TUESDAY -> 3
    Calendar.WEDNESDAY -> 4
    Calendar.THURSDAY -> 5
    Calendar.FRIDAY -> 6
    else -> 0
  }
}

@Composable
fun generateAnnouncementAnnotatedText(
  substance: SubstanceTravel?,
  smoking: SmokingTravel?,
  viewModel: TravelViewModel
): androidx.compose.ui.text.AnnotatedString {
  val highlightColor = MaterialTheme.colorScheme.primary
  val boldWeight = FontWeight.Bold

  if (substance == null || substance.name.isBlank()) {
    return buildAnnotatedString {
      append("لطفاً ابتدا مشخصات سفر مواد خود (نام، تاریخ شروع و غیره) را در تب اول وارد کرده و دکمه ذخیره را بزنید تا متن اعلام سفر شما آماده شود.")
    }
  }

  val (subMonths, subDays) = viewModel.calculateDuration(substance.travelStartDate, substance.travelEndDate)
  val subAction = if (substance.isCompleted) "سفر کردم" else "سفر میکنم"

  val name = substance.name.ifBlank { "..." }
  val years = JalaliCalendar.toPersianDigits(substance.yearsOfAddictionDamage.ifBlank { "۰" })
  val substanceName = substance.lastAntiXSubstance.ifBlank { "..." }
  val medicine = substance.treatmentMedicine.ifBlank { "..." }
  val guide = substance.guideName.ifBlank { "..." }
  val legion = substance.legionName.ifBlank { "..." }

  val subMonthsStr = JalaliCalendar.toPersianDigits(subMonths)
  val subDaysStr = JalaliCalendar.toPersianDigits(subDays)

  return buildAnnotatedString {
    append("سلام دوستان من ")
    withStyle(style = SpanStyle(color = highlightColor, fontWeight = boldWeight)) {
      append(name)
    }
    append(" هستم، با ")
    withStyle(style = SpanStyle(color = highlightColor, fontWeight = boldWeight)) {
      append(years)
    }
    append(" سال تخریب وارد کنگره شدم، آخرین آنتی ایکس مصرفی ")
    withStyle(style = SpanStyle(color = highlightColor, fontWeight = boldWeight)) {
      append(substanceName)
    }
    append("، الان هم ")
    withStyle(style = SpanStyle(color = highlightColor, fontWeight = boldWeight)) {
      append("$subMonthsStr ماه و $subDaysStr روز")
    }
    append(" هست که ")
    append(subAction)
    append("، با روش دی اس تی داروی درمان ")
    withStyle(style = SpanStyle(color = highlightColor, fontWeight = boldWeight)) {
      append(medicine)
    }
    append("، با راهنمای خوبم ")
    withStyle(style = SpanStyle(color = highlightColor, fontWeight = boldWeight)) {
      append(guide)
    }
    append(" در لژیون ")
    withStyle(style = SpanStyle(color = highlightColor, fontWeight = boldWeight)) {
      append(legion)
    }
    append(".")

    // If substance travel is completed, append its own independent freedom text immediately here
    if (substance.isCompleted) {
      val (freeMonths, freeDays) = viewModel.calculateFreedomDuration(substance.travelEndDate)
      val freeMonthsStr = JalaliCalendar.toPersianDigits(freeMonths)
      val freeDaysStr = JalaliCalendar.toPersianDigits(freeDays)
      append("\nو الان هم ")
      withStyle(style = SpanStyle(color = highlightColor, fontWeight = boldWeight)) {
        append("$freeMonthsStr ماه و $freeDaysStr روز")
      }
      append(" هست که آزاد و رها هستم.")
    }

    // If smoking travel start date is configured, append the William White text
    if (smoking != null && smoking.travelStartDate != null) {
      val (smkMonths, smkDays) = viewModel.calculateDuration(smoking.travelStartDate, smoking.travelEndDate)
      val smkAction = if (smoking.isCompleted) "سفر کردم" else "سفر میکنم"
      val smkYears = JalaliCalendar.toPersianDigits(smoking.yearsOfSmokingDamage.ifBlank { "۰" })
      val smkMedicine = smoking.treatmentMedicine.ifBlank { "..." }
      val smkGuide = smoking.guideName.ifBlank { "..." }
      val smkMonthsStr = JalaliCalendar.toPersianDigits(smkMonths)
      val smkDaysStr = JalaliCalendar.toPersianDigits(smkDays)

      append("\n\nدر ادامه وارد لژیون ویلیام وایت شدم، تخریب ")
      withStyle(style = SpanStyle(color = highlightColor, fontWeight = boldWeight)) {
        append(smkYears)
      }
      append(" سال، الان هم ")
      withStyle(style = SpanStyle(color = highlightColor, fontWeight = boldWeight)) {
        append("$smkMonthsStr ماه و $smkDaysStr روز")
      }
      append(" هست که ")
      append(smkAction)
      append("، با روش دی اس تی داروی درمان ")
      withStyle(style = SpanStyle(color = highlightColor, fontWeight = boldWeight)) {
        append(smkMedicine)
      }
      append("، با راهنمای خوبم ")
      withStyle(style = SpanStyle(color = highlightColor, fontWeight = boldWeight)) {
        append(smkGuide)
      }
      append(".")

      // If smoking travel is completed, append its own independent freedom text immediately here
      if (smoking.isCompleted) {
        val (freeMonths, freeDays) = viewModel.calculateFreedomDuration(smoking.travelEndDate)
        val freeMonthsStr = JalaliCalendar.toPersianDigits(freeMonths)
        val freeDaysStr = JalaliCalendar.toPersianDigits(freeDays)
        append("\nو الان هم ")
        withStyle(style = SpanStyle(color = highlightColor, fontWeight = boldWeight)) {
          append("$freeMonthsStr ماه و $freeDaysStr روز")
        }
        append(" هست که آزاد و رها هستم.")
      }
    }
  }
}

@Composable
fun DtsTab(viewModel: TravelViewModel) {
  val dtsSteps by viewModel.dtsSteps.collectAsState()
  val context = LocalContext.current

  var showDialog by remember { mutableStateOf(false) }
  var editingStep by remember { mutableStateOf<DtsStep?>(null) }
  var stepToDelete by remember { mutableStateOf<DtsStep?>(null) }

  // Dialog fields
  var selectedDate by remember { mutableStateOf<Long?>(System.currentTimeMillis()) }
  var morningDose by remember { mutableStateOf("") }
  var afternoonDose by remember { mutableStateOf("") }
  var nightDose by remember { mutableStateOf("") }
  var reminderEnabled by remember { mutableStateOf(true) }

  // Set fields when editing starts
  LaunchedEffect(editingStep) {
    if (editingStep != null) {
      selectedDate = editingStep!!.startDate
      morningDose = editingStep!!.morningDose
      afternoonDose = editingStep!!.afternoonDose
      nightDose = editingStep!!.nightDose
      reminderEnabled = editingStep!!.reminderEnabled
    } else {
      selectedDate = System.currentTimeMillis()
      morningDose = ""
      afternoonDose = ""
      nightDose = ""
      reminderEnabled = true
    }
  }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .padding(8.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(bottom = 80.dp), // Space for FAB
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      val latestStep = dtsSteps.lastOrNull()
      if (latestStep != null) {
        Card(
          modifier = Modifier.fillMaxWidth().testTag("current_dts_step_card"),
          colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.12f)
          ),
          shape = RoundedCornerShape(16.dp),
          border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f))
        ) {
          Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween,
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.Timeline,
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.primary,
                  modifier = Modifier.size(20.dp)
                )
                Text(
                  text = "پله فعلی من",
                  style = MaterialTheme.typography.titleMedium,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.primary,
                  fontFamily = VazirmatnFontFamily
                )
              }
              
              val nextStepDate = latestStep.startDate + (21L * 24 * 60 * 60 * 1000)
              val now = System.currentTimeMillis()
              val diffDays = ((nextStepDate - now) / (1000 * 60 * 60 * 24)).coerceAtLeast(0).toInt()
              
              Box(
                modifier = Modifier
                  .background(
                    color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.8f),
                    shape = RoundedCornerShape(8.dp)
                  )
                  .padding(horizontal = 10.dp, vertical = 4.dp)
              ) {
                Text(
                  text = "${JalaliCalendar.toPersianDigits(diffDays)} روز تا پله بعدی",
                  style = MaterialTheme.typography.bodySmall,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.onTertiaryContainer,
                  fontFamily = VazirmatnFontFamily
                )
              }
            }
            
            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
            
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                Text("صبح", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontFamily = VazirmatnFontFamily)
                Text(JalaliCalendar.toPersianDigits(latestStep.morningDose), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontFamily = VazirmatnFontFamily)
              }
              Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                Text("ظهر", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontFamily = VazirmatnFontFamily)
                Text(JalaliCalendar.toPersianDigits(latestStep.afternoonDose), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontFamily = VazirmatnFontFamily)
              }
              Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                Text("شب", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontFamily = VazirmatnFontFamily)
                Text(JalaliCalendar.toPersianDigits(latestStep.nightDose), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontFamily = VazirmatnFontFamily)
              }
            }
            
            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
            
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(
                  text = "تاریخ شروع پله:",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                  fontFamily = VazirmatnFontFamily
                )
                Text(
                  text = JalaliCalendar.formatJalali(latestStep.startDate),
                  style = MaterialTheme.typography.bodyMedium,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.onSurface,
                  fontFamily = VazirmatnFontFamily
                )
              }
              
              Column(horizontalAlignment = Alignment.End) {
                Text(
                  text = "تاریخ پله بعدی:",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                  fontFamily = VazirmatnFontFamily
                )
                val nextStepDate = latestStep.startDate + (21L * 24 * 60 * 60 * 1000)
                Text(
                  text = JalaliCalendar.formatJalali(nextStepDate),
                  style = MaterialTheme.typography.bodyMedium,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.tertiary,
                  fontFamily = VazirmatnFontFamily
                )
              }
            }
          }
        }
      } else {
        Card(
          modifier = Modifier.fillMaxWidth().testTag("current_dts_step_card"),
          colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)
          ),
          shape = RoundedCornerShape(16.dp),
          border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
        ) {
          Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.Start,
              modifier = Modifier.fillMaxWidth()
            ) {
              Icon(
                imageVector = Icons.Default.Timeline,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "پله فعلی من",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                fontFamily = VazirmatnFontFamily
              )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "هنوز هیچ پلهای ثبت نشده است.",
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              fontFamily = VazirmatnFontFamily,
              fontWeight = FontWeight.Medium
            )
          }
        }
      }

      if (dtsSteps.isEmpty()) {
        Card(
          modifier = Modifier.fillMaxWidth(),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          shape = RoundedCornerShape(16.dp),
          border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)),
          elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
          Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Text(
              text = "برنامه‌ریزی پله‌های روش DST",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.primary,
              fontFamily = VazirmatnFontFamily
            )
            Text(
              text = "در این قسمت می‌توانید مقدار دوز مصرفی دارو در هر پله ۲۱ روزه را مدیریت کنید. زمان تنظیم دوز جدید در پایان هر پله یادآوری خواهد شد.",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
              fontFamily = VazirmatnFontFamily,
              lineHeight = 20.sp
            )
          }
        }

        Box(
          modifier = Modifier
            .weight(1f)
            .fillMaxWidth(),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "هیچ پله درمانی ثبت نشده است.\nبرای اضافه کردن پله جدید روی دکمه دوز جدید کلیک کنید.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            fontFamily = VazirmatnFontFamily,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
          )
        }
      } else {
        // Table container with horizontal scroll
        Card(
          modifier = Modifier
            .weight(1f)
            .fillMaxWidth(),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          shape = RoundedCornerShape(16.dp),
          border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)),
          elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
          Box(
            modifier = Modifier
              .fillMaxSize()
              .horizontalScroll(rememberScrollState())
              .padding(12.dp)
          ) {
            Column(
              modifier = Modifier
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
            ) {
              // Table Header
              Row(
                modifier = Modifier
                  .background(
                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(8.dp)
                  )
                  .padding(vertical = 10.dp, horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                TableHeaderCell("دوز DTS", 90)
                TableHeaderCell("هفته اول", 80)
                TableHeaderCell("هفته دوم", 80)
                TableHeaderCell("هفته سوم", 80)
                TableHeaderCell("عملیات", 70)
              }

              Spacer(modifier = Modifier.height(8.dp))

              // Table Rows
              dtsSteps.forEach { step ->
                val week1Millis = step.startDate
                val week2Millis = step.startDate + (7L * 24 * 60 * 60 * 1000)
                val week3Millis = step.startDate + (14L * 24 * 60 * 60 * 1000)

                val week1Str = JalaliCalendar.formatJalali(week1Millis)
                val week2Str = JalaliCalendar.formatJalali(week2Millis)
                val week3Str = JalaliCalendar.formatJalali(week3Millis)

                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 12.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  // Dose cell
                  Column(modifier = Modifier.width(90.dp)) {
                    Text("صبح: ${JalaliCalendar.toPersianDigits(step.morningDose)}", style = MaterialTheme.typography.bodySmall, fontFamily = VazirmatnFontFamily, fontWeight = FontWeight.Medium)
                    Text("ظهر: ${JalaliCalendar.toPersianDigits(step.afternoonDose)}", style = MaterialTheme.typography.bodySmall, fontFamily = VazirmatnFontFamily, fontWeight = FontWeight.Medium)
                    Text("شب: ${JalaliCalendar.toPersianDigits(step.nightDose)}", style = MaterialTheme.typography.bodySmall, fontFamily = VazirmatnFontFamily, fontWeight = FontWeight.Medium)
                  }

                  // Week cells
                  TableCell(week1Str, 80)
                  TableCell(week2Str, 80)
                  TableCell(week3Str, 80)

                  // Operations cell
                  Row(
                    modifier = Modifier.width(70.dp),
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    IconButton(
                      onClick = { editingStep = step; showDialog = true },
                      modifier = Modifier.size(30.dp)
                    ) {
                      Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "ویرایش",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                      )
                    }
                    IconButton(
                      onClick = { stepToDelete = step },
                      modifier = Modifier.size(30.dp)
                    ) {
                      Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "حذف",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(16.dp)
                      )
                    }
                  }
                }
                Box(
                  modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                )
              }
            }
          }
        }
      }
    }

    // FAB Button - Positioning as Floating Action Button bottom right
    FloatingActionButton(
      onClick = { editingStep = null; showDialog = true },
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(16.dp)
        .testTag("add_dts_button"),
      containerColor = MaterialTheme.colorScheme.primary,
      contentColor = MaterialTheme.colorScheme.onPrimary
    ) {
      Row(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Icon(imageVector = Icons.Default.Add, contentDescription = null)
        Text("دوز جدید", fontFamily = VazirmatnFontFamily, fontWeight = FontWeight.Bold)
      }
    }
  }

  // Delete Confirmation Dialog
  if (stepToDelete != null) {
    AlertDialog(
      onDismissRequest = { stepToDelete = null },
      confirmButton = {
        TextButton(
          onClick = {
            val id = stepToDelete!!.id
            viewModel.deleteDtsStep(id)
            ReminderReceiver.cancelReminder(context, id)
            stepToDelete = null
            Toast.makeText(context, "پله درمانی حذف شد.", Toast.LENGTH_SHORT).show()
          }
        ) {
          Text("بله، حذف شود", fontFamily = VazirmatnFontFamily, color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { stepToDelete = null }) {
          Text("انصراف", fontFamily = VazirmatnFontFamily)
        }
      },
      title = {
        Text("حذف پله درمانی", fontFamily = VazirmatnFontFamily, fontWeight = FontWeight.Bold)
      },
      text = {
        Text("آیا از حذف این پله درمانی اطمینان دارید؟ این عمل غیر قابل بازگشت است.", fontFamily = VazirmatnFontFamily)
      }
    )
  }

  // Add/Edit Dialog
  if (showDialog) {
    AlertDialog(
      onDismissRequest = { showDialog = false; editingStep = null },
      confirmButton = {
        Button(
          onClick = {
            if (morningDose.isBlank() || afternoonDose.isBlank() || nightDose.isBlank() || selectedDate == null) {
              Toast.makeText(context, "لطفاً تمام فیلدها را پر کنید.", Toast.LENGTH_SHORT).show()
              return@Button
            }

            viewModel.saveDtsStep(
              id = editingStep?.id ?: 0,
              startDate = selectedDate!!,
              morningDose = morningDose,
              afternoonDose = afternoonDose,
              nightDose = nightDose,
              reminderEnabled = reminderEnabled,
              onSaved = { id ->
                if (reminderEnabled) {
                  ReminderReceiver.scheduleReminder(context, id.toInt(), selectedDate!!)
                } else {
                  ReminderReceiver.cancelReminder(context, id.toInt())
                }
              }
            )

            showDialog = false
            editingStep = null
            Toast.makeText(context, "پله درمانی با موفقیت ذخیره شد.", Toast.LENGTH_SHORT).show()
          }
        ) {
          Text("ذخیره", fontFamily = VazirmatnFontFamily, fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { showDialog = false; editingStep = null }) {
          Text("انصراف", fontFamily = VazirmatnFontFamily)
        }
      },
      title = {
        Text(
          text = if (editingStep != null) "ویرایش پله درمانی" else "ثبت پله درمانی جدید",
          fontFamily = VazirmatnFontFamily,
          fontWeight = FontWeight.Bold
        )
      },
      text = {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          DatePickerField(
            label = "تاریخ شروع پله (تقویم جلالی)",
            currentTimestamp = selectedDate,
            onDateSelected = { selectedDate = it }
          )

          OutlinedTextField(
            value = morningDose,
            onValueChange = { morningDose = it },
            label = { Text("دوز صبح (سی‌سی یا گرم)", fontFamily = VazirmatnFontFamily) },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            shape = RoundedCornerShape(12.dp)
          )

          OutlinedTextField(
            value = afternoonDose,
            onValueChange = { afternoonDose = it },
            label = { Text("دوز ظهر (سی‌سی یا گرم)", fontFamily = VazirmatnFontFamily) },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            shape = RoundedCornerShape(12.dp)
          )

          OutlinedTextField(
            value = nightDose,
            onValueChange = { nightDose = it },
            label = { Text("دوز شب (سی‌سی یا گرم)", fontFamily = VazirmatnFontFamily) },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            shape = RoundedCornerShape(12.dp)
          )

          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clickable { reminderEnabled = !reminderEnabled }
              .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(
              text = "فعال‌سازی یادآور ۲۱ روزه پله",
              fontFamily = VazirmatnFontFamily,
              style = MaterialTheme.typography.bodyMedium
            )
            Checkbox(
              checked = reminderEnabled,
              onCheckedChange = { reminderEnabled = it }
            )
          }
        }
      }
    )
  }
}

@Composable
fun TableHeaderCell(text: String, width: Int) {
  Text(
    text = text,
    modifier = Modifier.width(width.dp),
    style = MaterialTheme.typography.bodyMedium,
    fontWeight = FontWeight.Bold,
    fontFamily = VazirmatnFontFamily,
    color = MaterialTheme.colorScheme.onPrimaryContainer
  )
}

@Composable
fun TableCell(text: String, width: Int) {
  Text(
    text = text,
    modifier = Modifier.width(width.dp),
    style = MaterialTheme.typography.bodySmall,
    fontFamily = VazirmatnFontFamily,
    color = MaterialTheme.colorScheme.onSurface
  )
}
