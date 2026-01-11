# Sharp Rewards MVP - Quick Start Guide

## 🚀 Getting Started in 5 Minutes

### Prerequisites
- Android Studio (Flamingo or later)
- A Firebase project

### 1. Clone & Setup
```bash
cd mcn3rd
# Copy google-services.json to app/ directory
# (Get it from Firebase Console)
```

### 2. Build & Run
```bash
# In Android Studio:
# - Open project
# - Sync Gradle (File > Sync Now)
# - Run on emulator or device (Shift + F10)
```

### 3. Test the App
- **Signup**: Create new account
- **Login**: Use credentials
- **Dashboard**: View all features
- **Flash Hustle**: Play quiz game
- **Leaderboard**: See rankings
- **Wallet**: Check points
- **Submit Idea**: Add ideas for rewards

## 📊 Sample Data

To populate with test data in Firebase Console:

### Sample Quiz Question
```json
{
  "category": "programming",
  "correctAnswer": "Language",
  "options": ["Java", "Language", "Framework", "Tool"],
  "points": 10,
  "question": "What is Kotlin?"
}
```

### Sample Task
```json
{
  "category": "learning",
  "description": "Learn about Kotlin basics",
  "difficulty": "easy",
  "points": 10,
  "title": "Kotlin Fundamentals"
}
```

## 🔑 Key Files

| File | Purpose |
|------|---------|
| `MainActivity.kt` | App entry point |
| `Models.kt` | Data classes |
| `*Repository.kt` | Firebase operations |
| `*ViewModel.kt` | Business logic |
| `*Fragment.kt` | UI screens |
| `nav_graph.xml` | App navigation |

## 🐛 Common Issues

| Issue | Solution |
|-------|----------|
| Build fails | Run: `./gradlew clean build` |
| Firebase errors | Check `google-services.json` location |
| Auth issues | Enable Email/Password in Firebase Console |
| Firestore denied | Check security rules in Firebase |

## 📱 Features Overview

### Authentication
- Email/Password signup
- Login with persistence
- Logout functionality

### Games & Tasks
- Flash Hustle: 5-question quiz (10 pts each)
- Skill Streak: Daily tasks (10 pts each)
- Track performance

### Rewards
- Wallet: Track total points
- Idea submission: 50 pts per approved idea
- Transaction history

### Social
- Real-time leaderboard
- User rankings
- Profile display

## 🔐 Default User (for testing)
```
Email: test@example.com
Password: Test@1234
```

(Create this user manually in Firebase Auth)

## 📞 Support

For issues:
1. Check `README.md` for detailed docs
2. Check `FIREBASE_SETUP.md` for setup help
3. Review error logs in Android Studio logcat

## Next Steps

1. ✅ Setup complete
2. ⏭️ Customize UI colors/fonts
3. ⏭️ Add more quiz questions
4. ⏭️ Implement notifications
5. ⏭️ Add analytics
6. ⏭️ Deploy to Google Play

---

**Happy coding! 🎉**
