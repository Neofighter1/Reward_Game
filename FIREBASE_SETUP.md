# FIREBASE SETUP INSTRUCTIONS

## Step 1: Create Firebase Project

1. Go to [Firebase Console](https://console.firebase.google.com)
2. Click "Add project"
3. Name it "sharp-rewards-mvp"
4. Disable Google Analytics (optional for MVP)
5. Click "Create project"

## Step 2: Register Android App

1. In Firebase console, click the Android icon
2. Bundle ID: `com.sharpbytes.rewards`
3. App name: `Sharp Rewards`
4. Debug signing certificate SHA-1: Get from Android Studio (Build > Signing Report)
5. Register app
6. Download `google-services.json`
7. Place it in `app/` directory

## Step 3: Enable Authentication

1. Go to Authentication in Firebase Console
2. Click "Get started"
3. Enable "Email/Password" provider
4. Save

## Step 4: Create Firestore Database

1. Go to Firestore Database
2. Click "Create Database"
3. Select "Start in test mode" (for development)
4. Choose region (e.g., us-central1)
5. Click "Create"

## Step 5: Create Database Collections

In Firestore Console, create these collections:

### users
```javascript
// Create with auto ID
{
  "uid": "user-id",
  "email": "user@example.com",
  "displayName": "User Name",
  "photoUrl": "",
  "totalPoints": 0,
  "streakDays": 0,
  "lastStreakDate": 0,
  "createdAt": "2024-01-11T00:00:00Z",
  "updatedAt": "2024-01-11T00:00:00Z"
}
```

### quiz_questions
```javascript
// Add sample questions
{
  "question": "What is Kotlin?",
  "options": ["Java", "Language", "Framework", "Tool"],
  "correctAnswer": "Language",
  "points": 10,
  "category": "programming"
}
```

### skill_tasks
```javascript
{
  "title": "Daily Coding Challenge",
  "description": "Complete a coding challenge",
  "points": 10,
  "difficulty": "easy",
  "category": "programming"
}
```

## Step 6: Set Security Rules

In Firestore Console, go to Rules tab and replace with:

```firestore
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    
    // Users can only read/write their own data
    match /users/{userId} {
      allow read, write: if request.auth.uid == userId;
      allow read: if request.auth != null;
    }
    
    // Everyone can read quiz questions
    match /quiz_questions/{document=**} {
      allow read: if request.auth != null;
    }
    
    // Game results
    match /game_results/{document=**} {
      allow read: if request.auth != null;
      allow create: if request.auth.uid == request.resource.data.uid;
    }
    
    // Ideas
    match /idea_submissions/{document=**} {
      allow read: if request.auth != null;
      allow create: if request.auth.uid == request.resource.data.uid;
    }
    
    // Wallet
    match /wallet_transactions/{document=**} {
      allow read, write: if request.auth.uid == resource.data.uid;
    }
    
    // Streaks
    match /streak_records/{document=**} {
      allow read, write: if request.auth.uid == resource.data.uid;
    }
  }
}
```

Publish rules.

## Step 7: (Optional) Enable Cloud Messaging

For notifications (future feature):
1. Go to Cloud Messaging
2. Click "Enable"

## Testing

To test locally:

1. Create a test user:
   ```
   Email: test@example.com
   Password: TestPass123!
   ```

2. Upload sample data:
   - Go to quiz_questions collection
   - Add 5-10 sample questions
   - Go to skill_tasks collection
   - Add 3-5 sample tasks

## Environment-Specific Configuration

### Development
- Firestore: Test Mode (full read/write)
- Auth: Email/Password enabled
- Emulator: Use Firebase Emulator Suite for local testing

### Production
- Firestore: Production Mode with proper security rules
- Auth: Email/Password + additional methods
- Enable Cloud Messaging for notifications

## Troubleshooting

### "google-services.json not found"
- Ensure file is in `app/` directory (not `app/src/main/`)

### Authentication fails
- Check Firebase Authentication is enabled
- Verify email/password provider is enabled

### Firestore access denied
- Check security rules are properly published
- Ensure user UID matches in rules

### Emulator connection issues
- Stop Android emulator
- Run `firebase emulators:start`
- Ensure emulator connects to localhost
