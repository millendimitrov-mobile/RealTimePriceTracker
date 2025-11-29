import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.milen.realtimepricetracker.ui.feature.details.SymbolDetailsViewModel
import com.milen.realtimepricetracker.ui.feature.details.screen.SymbolDetailsContent

@Composable
internal fun SymbolDetailsScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: SymbolDetailsViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()

    // Back should be handled ONLY once for this screen instance
    var backHandled by remember { mutableStateOf(false) }

    fun handleBackOnce() {
        if (backHandled) return
        backHandled = true
        navController.popBackStack()
    }

    // System / gesture back -> same as UI back
    BackHandler(enabled = !backHandled) {
        handleBackOnce()
    }

    SymbolDetailsContent(
        state = state,
        modifier = modifier,
        onBackClick = { handleBackOnce() }
    )
}
