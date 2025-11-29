@file:Suppress("MISSING_DEPENDENCY_SUPERCLASS_IN_TYPE_ARGUMENT")

package com.milen.realtimepricetracker.ui.snapshot

import com.github.takahirom.roborazzi.AndroidComposePreviewTester
import com.github.takahirom.roborazzi.ComposePreviewTester
import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import sergio.sastre.composable.preview.scanner.android.AndroidComposablePreviewScanner
import org.robolectric.annotation.LooperMode

@ExperimentalRoborazziApi
@Suppress("UNUSED")
@LooperMode(LooperMode.Mode.PAUSED)
class RealTimeComposePreviewTester :
    ComposePreviewTester<ComposePreviewTester.TestParameter.JUnit4TestParameter.AndroidPreviewJUnit4TestParameter> by AndroidComposePreviewTester() {
    
    companion object {
        @JvmStatic
        fun testParameters(): AndroidComposablePreviewScanner {
            return AndroidComposablePreviewScanner()
        }
    }
}
