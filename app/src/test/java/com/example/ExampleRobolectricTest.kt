package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.NflRepository
import com.example.model.ProductionRecord
import com.example.model.QualityRecord
import com.example.model.DefectItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("NFL Production & Quality", appName)
  }

  @Test
  fun `test repository initialization and default seed data`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repository = NflRepository(context)

    assertTrue(repository.defaultLines.isNotEmpty())
    val lineJ = repository.defaultLines.find { it.id == "J" }
    assertTrue(lineJ != null)
    assertTrue(lineJ!!.matches("J1")) // J and J1 confirmed same line
    assertTrue(!lineJ.matches("J2")) // J2 not merged
  }
}
