"""
Sharp Rewards MVP - Testing Guide
"""

## Unit Testing

### Test Structure
```
app/src/test/
├── AuthRepositoryTest.kt
├── GameRepositoryTest.kt
├── ViewModelTest.kt
└── ...
```

### Running Tests
```bash
# Run all tests
./gradlew test

# Run specific test
./gradlew test --tests AuthRepositoryTest

# With coverage
./gradlew testDebugUnitTest

# View coverage report
open app/build/reports/coverage/index.html
```

## Instrumentation Testing

### Test Setup
```kotlin
@RunWith(AndroidJUnit4::class)
class LoginFragmentTest {
    
    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)
    
    @Test
    fun testLoginFlow() {
        onView(withId(R.id.email_input)).perform(typeText("test@example.com"))
        onView(withId(R.id.password_input)).perform(typeText("password"))
        onView(withId(R.id.login_button)).perform(click())
        
        onView(withId(R.id.user_name_text)).check(matches(isDisplayed()))
    }
}
```

### Running UI Tests
```bash
# Run all instrumentation tests
./gradlew connectedAndroidTest

# Run specific test
./gradlew connectedAndroidTest --tests LoginFragmentTest
```

## Firebase Emulator Testing

### Setup Emulator
```bash
# Install Firebase CLI
npm install -g firebase-tools

# Initialize project
firebase init emulators

# Start emulator
firebase emulators:start
```

### Connect App to Emulator
```kotlin
// In MainActivity.onCreate()
if (BuildConfig.DEBUG) {
    val settings = FirebaseFirestoreSettings.Builder()
        .setHost("10.0.2.2:8080")
        .setSslEnabled(false)
        .setPersistenceEnabled(false)
        .build()
    FirebaseFirestore.getInstance().firestoreSettings = settings
    
    FirebaseAuth.getInstance().useEmulator("10.0.2.2", 9099)
}
```

## Performance Testing

### Benchmarks to Track
- App startup time: < 2 seconds
- Game load time: < 500ms
- Leaderboard load: < 1 second
- API response: < 200ms

### Using Benchmark Library
```kotlin
@BenchmarkRule
fun testGameLoad() = benchmarkRule.measureRepeated {
    gameViewModel.startGame()
}
```

## Security Testing

### Firestore Rules Testing
```bash
# Test security rules with Firebase CLI
firebase emulators:start
firebase test:firestore
```

### Authentication Testing
- Test signup validation
- Test password strength
- Test session persistence
- Test logout clearing data

## Load Testing

### Using Apache JMeter
```bash
# Download JMeter
# Create test plan for:
# - Concurrent user signups
# - Game submissions
# - Leaderboard queries
# - Firestore writes
```

## Manual Testing Checklist

### Authentication
- [ ] Signup with new email
- [ ] Signup validation (empty fields)
- [ ] Signup with existing email
- [ ] Login with correct credentials
- [ ] Login with wrong password
- [ ] Logout and verify

### Game Features
- [ ] Start game loads questions
- [ ] Answer all questions
- [ ] Score calculation correct
- [ ] Results saved to Firestore
- [ ] Points added to user

### Wallet
- [ ] Initial balance correct
- [ ] Points update after game
- [ ] Transaction history shows
- [ ] History ordered by date

### Leaderboard
- [ ] Loads top 50 users
- [ ] Sorted by points (descending)
- [ ] User rank displayed
- [ ] Real-time updates

### Idea Submission
- [ ] Submit idea with all fields
- [ ] Validation on empty fields
- [ ] Confirmation message
- [ ] Points awarded

### Streak System
- [ ] Complete one task per day
- [ ] Task cannot be repeated same day
- [ ] Points added correctly
- [ ] Streak increments

## Debugging Tips

### Logcat Filtering
```bash
# Filter by app
adb logcat | grep com.sharpbytes.rewards

# Filter by level
adb logcat *:E (errors only)

# Filter by tag
adb logcat AuthViewModel:V
```

### Firebase Console Debugging
- Check Firestore collections for data
- Review Authentication users
- Check Cloud Function logs
- Monitor usage analytics

### Memory Profiler
1. Open Android Studio
2. Tools > Profiler
3. Select Memory
4. Monitor for leaks

### Network Profiler
1. Open Android Studio
2. Tools > Profiler
3. Select Network
4. Monitor API calls

## Common Test Scenarios

### Scenario 1: New User Journey
1. Launch app
2. Signup with email
3. Auto-login after signup
4. View dashboard
5. Play game
6. Check leaderboard
7. Submit idea

### Scenario 2: Existing User
1. Launch app
2. Login with email/password
3. View dashboard
4. Check daily streak
5. Play game multiple times
6. Check wallet history

### Scenario 3: Offline & Online
1. Offline: Try actions, verify error
2. Online: Auto-sync data
3. Verify no data loss

## Test Coverage Goals

- Repository layer: 90%
- ViewModel layer: 85%
- UI layer: 70%
- Overall: 80%

## Continuous Integration

### GitHub Actions
```yaml
# .github/workflows/android.yml
name: Android Tests
on: [push, pull_request]
jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - uses: actions/setup-java@v2
      - run: ./gradlew test
      - run: ./gradlew connectedAndroidTest
```

## Reporting Bugs

Use this template:
```
**Title**: [Bug] Description

**Steps to Reproduce**:
1. Step 1
2. Step 2

**Expected**: What should happen

**Actual**: What actually happened

**Device**: Android version, device model

**Logs**: Attach logcat output
```

---

**Testing is continuous, not just at the end!**
