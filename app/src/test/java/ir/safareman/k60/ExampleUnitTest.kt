package ir.safareman.k60

import ir.safareman.k60.viewmodel.JalaliCalendar
import org.junit.Assert.assertEquals
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun testPersianDigits() {
    val input = 1399
    val expected = "۱۳۹۹"
    val actual = JalaliCalendar.toPersianDigits(input)
    assertEquals(expected, actual)
  }
}
