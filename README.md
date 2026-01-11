# Sharp Rewards - Android MVP

A modern Android app built with Kotlin and Firebase for gamified learning and earning rewards.

## 📱 Features

### Core Features
- **User Authentication**: Firebase Authentication with Email/Password signup and login
- **Flash Hustle Game**: Time-bound quiz game to earn points
- **Skill Streak**: Daily task tracking for consecutive day streaks
- **Wallet**: Track and manage earned points
- **Idea-to-Earn**: Submit ideas for rewards
- **Leaderboard**: Real-time ranking system

## 🛠 Tech Stack

### Frontend
- **Language**: Kotlin
- **UI**: Android Framework with Material Design 3
- **Architecture**: MVVM (Model-View-ViewModel)
- **Navigation**: Jetpack Navigation Component
- **Async**: Kotlin Coroutines

### Backend
- **Authentication**: Firebase Authentication
- **Database**: Cloud Firestore (NoSQL)
- **Storage**: Firebase Cloud Storage
- **Notifications**: Firebase Cloud Messaging (optional)

## 📦 Project Structure

```
app/src/main/
├── java/com/sharpbytes/rewards/
│   ├── MainActivity.kt                 # Main entry point
│   ├── data/                          # Data layer (repositories)
│   │   ├── Models.kt                  # Data classes
│   │   ├── AuthRepository.kt          # Auth operations
│   │   ├── GameRepository.kt          # Game data
│   │   ├── StreakRepository.kt        # Streak tracking
│   │   ├── LeaderboardRepository.kt   # Leaderboard
│   │   ├── IdeaRepository.kt          # Idea submissions
│   │   └── WalletRepository.kt        # Points management
│   ├── ui/                            # UI layer (fragments)
│   │   ├── LoginFragment.kt
│   │   ├── SignupFragment.kt
│   │   ├── DashboardFragment.kt
│   │   ├── GameFragment.kt
│   │   ├── LeaderboardFragment.kt
│   │   ├── WalletFragment.kt
│   │   └── IdeaFragment.kt
│   └── viewmodel/                     # ViewModel layer
│       ├── AuthViewModel.kt
│       ├── GameViewModel.kt
│       ├── StreakViewModel.kt
│       └── LeaderboardViewModel.kt
├── res/
│   ├── layout/                        # XML layouts
│   ├── values/                        # Strings, colors, styles
│   └── drawable/                      # Drawable resources
└── AndroidManifest.xml                # App manifest
```

## 🚀 Getting Started

### Prerequisites
- Android Studio (Flamingo or later)
- JDK 8 or higher
- Firebase Project (create at https://firebase.google.com)

### Setup Instructions

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd mcn3rd
   ```

2. **Create Firebase Project**
   - Go to [Firebase Console](https://console.firebase.google.com)
   - Create a new project named "sharp-rewards-mvp"
   - Enable Firebase Authentication (Email/Password)
   - Enable Firestore Database (Start in Test Mode for development)
   - Download `google-services.json` from Firebase Console

3. **Add Firebase Configuration**
   - Place `google-services.json` in `app/` directory

4. **Build and Run**
   ```bash
   ./gradlew build
   ```
   - Open Android Studio and run on emulator or physical device

## 📊 Database Schema (Firestore)

### Collections

#### `users`
```
{
  "uid": "string",
  "email": "string",
  "displayName": "string",
  "photoUrl": "string",
  "totalPoints": 0,
  "streakDays": 0,
  "lastStreakDate": 0,
  "createdAt": "timestamp",
  "updatedAt": "timestamp"
}
```

#### `quiz_questions`
```
{
  "id": "string",
  "question": "string",
  "options": ["string"],
  "correctAnswer": "string",
  "points": 10,
  "category": "string"
}
```

#### `skill_tasks`
```
{
  "id": "string",
  "title": "string",
  "description": "string",
  "points": 10,
  "difficulty": "easy|medium|hard",
  "category": "string"
}
```

#### `game_results`
```
{
  "id": "string",
  "uid": "string",
  "score": 0,
  "questionsAttempted": 0,
  "correctAnswers": 0,
  "pointsEarned": 0,
  "timeTaken": 0,
  "playedAt": "timestamp"
}
```

#### `streak_records`
```
{
  "uid": "string",
  "date": 0,
  "isCompleted": true,
  "taskId": "string"
}
```

#### `idea_submissions`
```
{
  "id": "string",
  "uid": "string",
  "title": "string",
  "description": "string",
  "category": "string",
  "points": 0,
  "status": "pending|approved|rejected",
  "submittedAt": "timestamp"
}
```

#### `wallet_transactions`
```
{
  "id": "string",
  "uid": "string",
  "points": 0,
  "type": "earned|redeemed|bonus",
  "description": "string",
  "timestamp": "timestamp"
}
```

## 🔐 Firebase Security Rules

```firestore
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    
    // Users can only read/write their own data
    match /users/{userId} {
      allow read, write: if request.auth.uid == userId;
    }
    
    // Everyone can read quiz questions
    match /quiz_questions/{document=**} {
      allow read: if request.auth != null;
    }
    
    // Users can read all game results but only write their own
    match /game_results/{document=**} {
      allow read: if request.auth != null;
      allow create: if request.auth.uid == request.resource.data.uid;
    }
    
    // Users can read leaderboard (all users)
    match /users/{userId} {
      allow read: if request.auth != null;
    }
    
    // Ideas - public read, user write
    match /idea_submissions/{document=**} {
      allow read: if request.auth != null;
      allow create: if request.auth.uid == request.resource.data.uid;
    }
    
    // Wallet transactions - user specific
    match /wallet_transactions/{document=**} {
      allow read, write: if request.auth.uid == resource.data.uid;
    }
  }
}
```

## 🎮 Feature Details

### 1. Flash Hustle Game
- Timed quiz with 5 questions
- 10 points per correct answer
- Instant feedback
- Results saved to `game_results` collection

### 2. Skill Streak
- One task per day
- 10 points per completed task
- Tracks consecutive days
- Daily reset at midnight

### 3. Wallet System
- Displays total earned points
- Transaction history
- Redemption ready (future feature)
- Points earned from games, streaks, and ideas

### 4. Leaderboard
- Top 50 users by total points
- Real-time ranking
- User profile integration

### 5. Idea-to-Earn
- Submit creative ideas
- Earn 50 points per approved idea
- Status tracking (pending, approved, rejected)
- Admin review process

## 🔧 Development Guide

### Adding a New Feature

1. **Create Data Model** in `Models.kt`
2. **Create Repository** in `data/` folder
3. **Create ViewModel** in `viewmodel/` folder
4. **Create Fragment** in `ui/` folder
5. **Create Layout XML** in `res/layout/`
6. **Add Navigation** to `nav_graph.xml` (create if needed)

### Testing

Currently using:
- JUnit for unit tests
- Espresso for UI tests

Run tests:
```bash
./gradlew test                    # Unit tests
./gradlew connectedAndroidTest    # Instrumentation tests
```

## 📱 Supported Versions

- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 34 (Android 14)
- **Compiled SDK**: 34

## 🚧 Future Enhancements

- [ ] Push Notifications (FCM)
- [ ] Social Sharing
- [ ] In-app Redemption
- [ ] Leaderboard Filters
- [ ] Custom Avatars
- [ ] Achievement Badges
- [ ] Offline Support (Room DB)
- [ ] Advanced Analytics

## 📝 API Reference

### AuthRepository
- `signUp(email, password, displayName): Result<String>`
- `login(email, password): Result<String>`
- `logout()`
- `getCurrentUser(): FirebaseUser?`
- `getUserData(uid): Result<User>`
- `updateUserPoints(uid, points): Result<Unit>`

### GameRepository
- `getQuizQuestions(limit): Result<List<QuizQuestion>>`
- `saveGameResult(result): Result<String>`
- `getGameHistory(uid): Result<List<GameResult>>`

### StreakRepository
- `updateStreak(uid, taskId): Result<Int>`
- `getStreakData(uid): Result<StreakRecord?>`

### LeaderboardRepository
- `getLeaderboard(limit): Result<List<LeaderboardEntry>>`
- `getUserRank(uid): Result<Int>`

### IdeaRepository
- `submitIdea(idea): Result<String>`
- `getUserIdeas(uid): Result<List<IdeaSubmission>>`
- `getIdeas(status, limit): Result<List<IdeaSubmission>>`

### WalletRepository
- `getWalletBalance(uid): Result<Long>`
- `addTransaction(transaction): Result<String>`
- `getTransactionHistory(uid, limit): Result<List<WalletTransaction>>`

## 🤝 Contributing

1. Create a feature branch (`git checkout -b feature/amazing-feature`)
2. Commit changes (`git commit -m 'Add amazing feature'`)
3. Push to branch (`git push origin feature/amazing-feature`)
4. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see LICENSE file for details.

## 📞 Support

For issues and questions:
- Open an issue on GitHub
- Contact: support@sharpbytes.com

---

**Built with ❤️ by SharpBytes**
#   R e w a r d _ G a m e  
 