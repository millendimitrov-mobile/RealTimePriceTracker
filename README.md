# Real-Time Price Tracker

A modern Android application for tracking real-time stock prices using WebSocket connections. Built
with Jetpack Compose, following MVI (Model-View-Intent) architecture pattern.

## 📱 Features

- **Real-Time Price Updates**: Live stock price updates via WebSocket connection (updates every 2
  seconds)
- **Stock Feed Screen**: Displays 25 stock symbols sorted by price (highest first)
- **Symbol Details Screen**: Detailed view for individual stock symbols with price change indicators
- **Price Change Indicators**: Visual indicators (↑/↓) showing price increases/decreases
- **Price Flash Animations**: Color-coded animations (green for increases, red for decreases) when
  prices change
- **Connection Status**: Real-time connection status indicator (🟢 Connected / 🔴 Disconnected / 🟡
  Connecting)
- **Error Handling**: User-friendly error messages with retry functionality
- **Deep Linking**: Support for deep links to open specific symbol details
- **State Persistence**: Feed state persists across process death using SavedStateHandle
- **Auto-Start Repository**: Automatically starts WebSocket connection when details screen is opened
  via deep link

## 🏗️ Architecture

The app follows **MVI (Model-View-Intent)** architecture pattern:

- **Model**: Domain models (`StockSymbol`, `ConnectionStatus`) and UI state (`FeedState`,
  `SymbolDetailsState`)
- **View**: Jetpack Compose screens (`FeedScreen`, `SymbolDetailsScreen`)
- **Intent**: User actions (`FeedIntent`, `SymbolDetailsIntent`)
- **ViewModel**: Business logic and state management (`FeedViewModel`, `SymbolDetailsViewModel`)

### Layer Separation

```
┌─────────────────────────────────────┐
│         UI Layer (MVI)              │
│  - Intent (User Actions)            │
│  - State (UI State)                 │
│  - ViewModel (State Management)     │
│  - Screen (Composables)             │
└─────────────────────────────────────┘
              ↓
┌─────────────────────────────────────┐
│      Domain Layer                   │
│  - Domain Models (StockSymbol)      │
│  - Uses BigDecimal for prices       │
└─────────────────────────────────────┘
              ↓
┌─────────────────────────────────────┐
│      Data Layer                     │
│  - Network Models (DTOs)            │
│  - WebSocket Repository             │
│  - Mapper (DTO → Domain)            │
└─────────────────────────────────────┘
```

## 🛠️ Tech Stack

- **Language**: Kotlin
- **UI**: Jetpack Compose (Material 3)
- **Architecture**: MVI (Model-View-Intent)
- **Dependency Injection**: Hilt
- **Networking**: Ktor Client (WebSocket)
- **Async Operations**: Kotlin Coroutines & Flow
- **State Management**: StateFlow, SharedFlow, Channel
- **Navigation**: Navigation Compose
- **Serialization**: Kotlinx Serialization (JSON)
- **Testing**:
    - Unit Tests: MockK, Turbine, JUnit 4
    - UI Snapshot Tests: Roborazzi
- **Build System**: Gradle with Version Catalog

## 📦 Dependencies

Key dependencies (see `gradle/libs.versions.toml` for full list):

- **Compose BOM**: `2025.11.01`
- **Kotlin**: `2.2.21`
- **Hilt**: `2.57.2`
- **Ktor**: `3.3.3`
- **Coroutines**: `1.10.2`
- **Navigation Compose**: `2.9.6`
- **Roborazzi**: `1.52.0`

## 🚀 Getting Started

### Prerequisites

- Android Studio Hedgehog or later
- JDK 17 or later
- Android SDK (minSdk: 27, targetSdk: 36)

### Setup

1. Clone the repository:

```bash
git clone https://github.com/millendimitrov-mobile/RealTimePriceTracker.git
cd RealTimePriceTracker
```

2. Open the project in Android Studio

3. Sync Gradle files

4. Run the app on an emulator or physical device

### Build Variants

- **Debug**: Debuggable, no minification, WebSocket URL: `wss://ws.postman-echo.com`
- **Release**: Minified, optimized, WebSocket URL: `wss://ws.postman-echo.com`

## 🔗 Deep Linking

The app supports deep links to open specific symbol details screens.

### Deep Link Format

```
stocks://symbol/{symbol}
```

### Examples

**Open Apple (AAPL) details:**

```bash
adb shell am start \
  -a android.intent.action.VIEW \
  -d "stocks://symbol/AAPL" \
  com.milen.realtimepricetracker
```

**Open Google (GOOG) details:**

```bash
adb shell am start \
  -a android.intent.action.VIEW \
  -d "stocks://symbol/GOOG" \
  com.milen.realtimepricetracker
```

**Open Microsoft (MSFT) details:**

```bash
adb shell am start \
  -a android.intent.action.VIEW \
  -d "stocks://symbol/MSFT" \
  com.milen.realtimepricetracker
```

### Supported Symbols

The app tracks 25 stock symbols including:

- AAPL (Apple)
- GOOG (Google)
- MSFT (Microsoft)
- AMZN (Amazon)
- TSLA (Tesla)
- NVDA (NVIDIA)
- META (Meta Platforms)
- And 18 more...

See `app/src/main/java/com/milen/realtimepricetracker/data/websocket/StockData.kt` for the complete
list.

## 🧪 Testing

### Unit Tests

Run all unit tests:

```bash
./gradlew :app:testDebugUnitTest
```

Run specific test class:

```bash
./gradlew :app:testDebugUnitTest --tests "com.milen.realtimepricetracker.ui.feature.feed.FeedViewModelTest"
```

### UI Instrumentation Tests (Espresso)

The project includes UI instrumentation tests using Jetpack Compose Testing and Espresso to verify user interactions and navigation flows.

#### Running UI Tests

Run all instrumentation tests:

```bash
./gradlew :app:connectedDebugAndroidTest
```

Run a specific test class:

```bash
./gradlew :app:connectedDebugAndroidTest --tests "com.milen.realtimepricetracker.ui.FeedToDetailsNavigationTest"
```

**Note**: UI instrumentation tests require a connected device or emulator. Make sure you have a device connected before running these tests.

#### Test Scenarios

The project includes the following UI test scenarios:

- **Feed to Details Navigation Test** (`FeedToDetailsNavigationTest`):
  - Verifies navigation from the feed screen to symbol details
  - Tests scrolling to find a specific stock (Tesla)
  - Validates that the details screen displays correct information
  - Confirms navigation back to the feed screen

### UI Snapshot Testing with Roborazzi

The project uses [Roborazzi](https://github.com/takahirom/roborazzi) for UI snapshot testing of
Compose previews.

#### Recording Base Images

To record base snapshot images from Compose previews:

```bash
./gradlew :app:recordRoborazziDebug
```

This command will:

- Scan all `@Preview` composables (including private ones)
- Generate snapshot images in `app/src/test/snapshottests/`
- Use the custom tester: `RealTimeComposePreviewTester`
- Include private previews automatically

**When to run**:

- Initially to create baseline snapshots
- After making intentional UI changes
- When adding new preview composables

#### Comparing Against Base Images

To compare current UI state against recorded base images:

```bash
./gradlew :app:compareRoborazziDebug
```

This command will:

- Generate new snapshots from current previews
- Compare them against the base images in `app/src/test/snapshottests/`
- Report any visual differences
- Fail the build if differences are detected

**When to run**:

- Before committing code changes
- In CI/CD pipelines to catch visual regressions
- When refactoring UI components

#### Other Roborazzi Commands

- **Verify snapshots** (without recording):
  ```bash
  ./gradlew :app:verifyRoborazziDebug
  ```

- **Clear snapshots**:
  ```bash
  ./gradlew :app:clearRoborazziDebug
  ```

- **Verify and record** (if verification fails, record new images):
  ```bash
  ./gradlew :app:verifyAndRecordRoborazziDebug
  ```

#### Snapshot Configuration

Roborazzi is configured in `app/build.gradle.kts`:

```kotlin
roborazzi {
    generateComposePreviewRobolectricTests {
        outputDir.set(file("src/test/snapshottests"))
        enable.set(true)
        testerQualifiedClassName.set("com.milen.realtimepricetracker.ui.snapshot.RealTimeComposePreviewTester")
        useScanOptionParametersInTester.set(true)
        includePrivatePreviews = true
        packages.set(listOf("com.milen.realtimepricetracker"))
    }
}
```

**Note**: When UI changes are intentional, you should:

1. Run `recordRoborazziDebug` to update the base images
2. Commit the updated snapshot images to version control

## 📁 Project Structure

```
app/src/main/java/com/milen/realtimepricetracker/
├── data/                          # Data layer
│   ├── config/                    # Configuration (WebSocket URLs)
│   ├── mapper/                    # DTO to Domain mappers
│   ├── network/model/             # Network DTOs
│   └── websocket/                 # WebSocket infrastructure
│       ├── PriceFeedService.kt    # WebSocket connection manager
│       ├── PriceGenerator.kt      # Simulated price updates
│       ├── WebSocketRepository.kt # Repository for WebSocket data
│       └── StockData.kt           # Initial stock data (25 symbols)
├── domain/                        # Domain layer
│   ├── model/                     # Domain models
│   ├── logger/                    # Logging interface
│   └── dispatchers/               # Coroutine dispatchers
├── di/                            # Dependency injection modules
├── ui/                            # UI layer
│   ├── feature/                   # Feature modules
│   │   ├── feed/                  # Feed screen (MVI)
│   │   └── details/               # Details screen (MVI)
│   ├── navigation/                # Navigation setup
│   ├── theme/                     # Material 3 theme
│   └── components/                # Reusable components
└── Utils.kt                       # Utility functions (price formatting)
```

## 💡 Key Features Explained

### Price Updates

- Price updates are generated every 2 seconds
- Each update varies prices by ±5% randomly
- Prices are guaranteed to never go below $0.01
- All prices use `BigDecimal` for precision (never `Double`)

### Price Change Detection

- Tracks previous price for each symbol
- Computes price change direction: `INCREASED`, `DECREASED`, `NO_CHANGE`, `UNKNOWN`
- Visual indicators show price direction with icons and colors

### Error Handling

- Connection failures are detected and displayed to users
- JSON parsing errors are caught and shown with user-friendly messages
- Retry functionality allows users to attempt reconnection
- Error state can be dismissed with a close button

### State Persistence

- Feed running state persists across process death using `SavedStateHandle`
- Connection status is maintained during app lifecycle
- Stock data is preserved in ViewModel state

## 🎨 UI Components

### Feed Screen

- **FeedTopBar**: Connection status indicator and start/stop toggle
- **StockRowItem**: Individual stock row with price, change indicator, and flash animation
- **ShowError**: Error display with retry and close buttons
- **ShowLoading**: Loading indicator

### Details Screen

- **SymbolDetailsTopBar**: Back navigation
- **SymbolDetailsBody**: Symbol name, price, change indicator, and description
- **PriceChangeIndicator**: Visual indicator for price direction

## 🔧 Configuration

### WebSocket URL

The WebSocket URL is configured in `app/build.gradle.kts`:

```kotlin
buildConfigField("String", "WEBSOCKET_BASE_URL", "\"wss://ws.postman-echo.com\"")
```

Access it via `BuildConfig.WEBSOCKET_BASE_URL` or `WebSocketConfig.RAW_URL`.

### Price Update Interval

Price updates are generated every 2 seconds (configurable in `PriceGenerator.kt`):

```kotlin
private const val PRICE_UPDATE_INTERVAL_MS = 2_000L
```

## 📝 Code Quality

- **Architecture**: MVI pattern consistently applied
- **Type Safety**: BigDecimal used for all financial calculations
- **Error Handling**: Comprehensive error handling with user feedback
- **Testing**: Unit tests for ViewModels, Mappers, and utilities
- **UI Testing**: Roborazzi snapshot tests for Compose previews
- **Code Style**: Follows Kotlin coding conventions

## 🐛 Known Limitations

- Uses simulated price data (not real stock market data)
- WebSocket connection uses Postman Echo service (for testing)
- No offline caching (data is lost when connection is lost)
- No price history chart (only current price displayed)

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

```
MIT License

Copyright (c) 2025 Real-Time Price Tracker

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

## 👥 Contributors

[Add contributors here]

## 🙏 Acknowledgments

- Built with [Jetpack Compose](https://developer.android.com/jetpack/compose)
- WebSocket testing
  with [Postman Echo](https://www.postman.com/postman/workspace/pstmn-echo-service/)
- UI snapshot testing with [Roborazzi](https://github.com/takahirom/roborazzi)

