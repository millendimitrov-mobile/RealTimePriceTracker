package com.milen.realtimepricetracker.ui

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.milen.realtimepricetracker.MainActivity
import com.milen.realtimepricetracker.R
import com.milen.realtimepricetracker.data.websocket.StockData.INITIAL_STOCKS
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalTestApi::class)
@RunWith(AndroidJUnit4::class)
class FeedToDetailsNavigationTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun testNavigateToTeslaDetailsAndBack() {
        with(composeTestRule) {
            waitForIdle()

            onNodeWithText("Stock Prices")
                .assertIsDisplayed()

            waitForIdle()

            waitUntilAtLeastOneExists(
                matcher = hasText(COMPANY_NAME),
                timeoutMillis = 10_000
            )

            onNodeWithText(COMPANY_NAME)
                .performScrollTo()
                .assertIsDisplayed()
                .performClick()

            waitForIdle()

            onNodeWithText(
                composeTestRule.activity.getString(R.string.symbol_details_title)
            )
                .assertIsDisplayed()

            waitForIdle()

            waitUntilAtLeastOneExists(
                matcher = hasText(COMPANY_NAME),
                timeoutMillis = 10_000
            )
            
            onNodeWithText(
                INITIAL_STOCKS.firstOrNull { it.name == COMPANY_NAME }?.description.orEmpty()
            ).assertIsDisplayed()

            onNodeWithContentDescription(
                composeTestRule.activity.getString(R.string.back)
            )
                .assertIsDisplayed()
                .performClick()

            waitForIdle()

            onNodeWithText("Stock Prices")
                .assertIsDisplayed()
        }
    }

    companion object {
        private const val COMPANY_NAME = "Broadcom"
    }
}

