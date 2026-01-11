# Sharp Rewards - Data Relationship Diagram (DRD) for Firestore

**Firestore NoSQL Database Structure**

---

## 📊 Overview

This document defines the **Data Relationship Diagram (DRD)** for Sharp Rewards, implemented using **Firebase Cloud Firestore** (NoSQL document database). Unlike traditional relational databases, Firestore uses collections and documents with denormalization for performance optimization.

---

## 🗄️ Firestore Collections Structure

### Collection Hierarchy

```
/users/{userId}
  ├─ name: string
  ├─ email: string
  ├─ wallet: number
  ├─ streak: number
  └─ /transactions/{transactionId}  [subcollection]
       ├─ points: number
       ├─ type: string
       └─ createdAt: timestamp

/games/{gameId}
  ├─ type: string
  ├─ duration: number
  └─ reward: number

/participations/{participationId}
  ├─ userId: string (reference)
  ├─ gameId: string (reference)
  ├─ score: number
  └─ timestamp: timestamp

/ideas/{ideaId}
  ├─ userId: string (reference)
  ├─ title: string
  ├─ description: string
  └─ votes: number

/leaderboard/{userId}
  ├─ score: number (denormalized from user.wallet)
  └─ name: string (denormalized from user.name)
```

---

## 📋 Collection Definitions

### 1. users/ Collection
**Document ID**: `userId` (Firebase Auth UID)

| Field | Type | Description | Indexed |
|-------|------|-------------|---------|
| name | string | User's display name | No |
| email | string | User's email address | Yes |
| wallet | number | Total points balance | Yes |
| streak | number | Current consecutive day streak | No |
| photoUrl | string | Profile picture URL | No |
| createdAt | timestamp | Account creation time | No |
| updatedAt | timestamp | Last update time | No |

**Example Document** (`users/abc123`):
```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "wallet": 250,
  "streak": 5,
  "photoUrl": "",
  "createdAt": "2024-01-10T10:00:00Z",
  "updatedAt": "2024-01-11T08:30:00Z"
}
```

**Firestore Indexes Required**:
```
Collection: users
Fields: email (Ascending)
Query: .where("email", "==", email)

Collection: users
Fields: wallet (Descending)
Query: .orderBy("wallet", "desc").limit(50)
```

---

### 2. games/ Collection
**Document ID**: `gameId` (Auto-generated)

| Field | Type | Description | Indexed |
|-------|------|-------------|---------|
| type | string | Game type identifier | Yes |
| duration | number | Game duration in seconds (0 = unlimited) | No |
| reward | number | Base points per correct answer | No |
| title | string | Display name of game | No |
| description | string | Game description | No |
| isActive | boolean | Whether game is available | Yes |
| createdAt | timestamp | Game creation time | No |

**Example Document** (`games/game_flash_001`):
```json
{
  "type": "flash_hustle",
  "duration": 300,
  "reward": 10,
  "title": "Flash Hustle Quiz",
  "description": "Answer 5 questions in 5 minutes",
  "isActive": true,
  "createdAt": "2024-01-01T00:00:00Z"
}
```

**Game Types**:
- `flash_hustle` - Timed quiz (5 questions, 5 minutes)
- `skill_streak` - Daily skill challenge
- `quick_quiz` - Fast 3-question quiz
- `challenge_mode` - Advanced difficulty

**Firestore Index Required**:
```
Collection: games
Fields: type (Ascending), isActive (Ascending)
Query: .where("type", "==", "flash_hustle").where("isActive", "==", true)
```

---

### 3. participations/ Collection
**Document ID**: `participationId` (Auto-generated)

| Field | Type | Description | Indexed |
|-------|------|-------------|---------|
| userId | string | Reference to users/{userId} | Yes |
| gameId | string | Reference to games/{gameId} | Yes |
| score | number | Score achieved in participation | No |
| timestamp | timestamp | When participation occurred | Yes |
| questionsAttempted | number | Total questions attempted | No |
| correctAnswers | number | Number of correct answers | No |
| pointsEarned | number | Points earned from participation | No |
| timeTaken | number | Time taken in seconds | No |

**Example Document** (`participations/part_xyz789`):
```json
{
  "userId": "abc123",
  "gameId": "game_flash_001",
  "score": 50,
  "timestamp": "2024-01-11T10:00:00Z",
  "questionsAttempted": 5,
  "correctAnswers": 5,
  "pointsEarned": 50,
  "timeTaken": 180
}
```

**Firestore Composite Indexes Required**:
```
Collection: participations
Fields: userId (Ascending), timestamp (Descending)
Query: .where("userId", "==", uid).orderBy("timestamp", "desc")

Collection: participations
Fields: gameId (Ascending), timestamp (Descending)
Query: .where("gameId", "==", gameId).orderBy("timestamp", "desc")

Collection: participations
Fields: userId (Ascending), gameId (Ascending), timestamp (Descending)
Query: .where("userId", "==", uid).where("gameId", "==", gameId).orderBy("timestamp", "desc")
```

---

### 4. ideas/ Collection
**Document ID**: `ideaId` (Auto-generated)

| Field | Type | Description | Indexed |
|-------|------|-------------|---------|
| userId | string | Reference to users/{userId} | Yes |
| title | string | Idea title | No |
| description | string | Detailed idea description | No |
| votes | number | Number of votes/likes | Yes |
| category | string | Idea category | Yes |
| status | string | Approval status | Yes |
| pointsAwarded | number | Points awarded if approved | No |
| submittedAt | timestamp | Submission time | Yes |

**Example Document** (`ideas/idea_001`):
```json
{
  "userId": "abc123",
  "title": "Team Challenge Mode",
  "description": "Allow users to compete in teams of 3-5 players",
  "votes": 25,
  "category": "feature",
  "status": "approved",
  "pointsAwarded": 50,
  "submittedAt": "2024-01-10T15:30:00Z"
}
```

**Status Values**:
- `pending` - Awaiting review
- `approved` - Approved and rewarded
- `rejected` - Not approved

**Firestore Composite Indexes Required**:
```
Collection: ideas
Fields: userId (Ascending), submittedAt (Descending)
Query: .where("userId", "==", uid).orderBy("submittedAt", "desc")

Collection: ideas
Fields: status (Ascending), votes (Descending)
Query: .where("status", "==", "approved").orderBy("votes", "desc")

Collection: ideas
Fields: category (Ascending), submittedAt (Descending)
Query: .where("category", "==", "feature").orderBy("submittedAt", "desc")
```

---

### 5. leaderboard/ Collection
**Document ID**: `userId` (Same as users/{userId})

| Field | Type | Description | Indexed |
|-------|------|-------------|---------|
| score | number | Denormalized wallet balance | Yes |
| name | string | Denormalized user name | No |
| updatedAt | timestamp | Last update time | No |

**Example Document** (`leaderboard/abc123`):
```json
{
  "score": 250,
  "name": "John Doe",
  "updatedAt": "2024-01-11T10:05:00Z"
}
```

**Purpose**: Denormalized collection for fast leaderboard queries without joining users collection.

**Firestore Index Required**:
```
Collection: leaderboard
Fields: score (Descending)
Query: .orderBy("score", "desc").limit(50)
```

**Sync Strategy**:
- When `users/{userId}.wallet` changes → Update `leaderboard/{userId}.score`
- Implemented in AuthRepository, GameRepository, StreakRepository, WalletRepository

---

## 🔗 Data Relationships in Firestore

### Relationship Patterns

#### 1. users → participations (One-to-Many)
```kotlin
// Get all participations for a user
firestore.collection("participations")
    .whereEqualTo("userId", "abc123")
    .orderBy("timestamp", Query.Direction.DESCENDING)
    .get()
```

**Visualization**:
```
users/abc123
    ↓ (1:N)
participations/part_001 { userId: "abc123", gameId: "game_001", score: 50 }
participations/part_002 { userId: "abc123", gameId: "game_001", score: 40 }
participations/part_003 { userId: "abc123", gameId: "game_002", score: 30 }
```

#### 2. games → participations (One-to-Many)
```kotlin
// Get all participations for a game
firestore.collection("participations")
    .whereEqualTo("gameId", "game_001")
    .orderBy("timestamp", Query.Direction.DESCENDING)
    .get()
```

**Visualization**:
```
games/game_001
    ↓ (1:N)
participations/part_001 { userId: "abc123", gameId: "game_001" }
participations/part_004 { userId: "xyz456", gameId: "game_001" }
participations/part_007 { userId: "def789", gameId: "game_001" }
```

#### 3. users → ideas (One-to-Many)
```kotlin
// Get all ideas submitted by a user
firestore.collection("ideas")
    .whereEqualTo("userId", "abc123")
    .orderBy("submittedAt", Query.Direction.DESCENDING)
    .get()
```

**Visualization**:
```
users/abc123
    ↓ (1:N)
ideas/idea_001 { userId: "abc123", title: "Team Mode" }
ideas/idea_005 { userId: "abc123", title: "Dark Theme" }
```

#### 4. users → leaderboard (One-to-One Denormalized)
```kotlin
// Get leaderboard entry for user (synchronized)
firestore.collection("leaderboard").document("abc123").get()
```

**Visualization**:
```
users/abc123 { wallet: 250 }
    ↓ (denormalized sync)
leaderboard/abc123 { score: 250 }
```

---

## 🔄 Denormalization Strategy

### Why Denormalize?

Firestore is a NoSQL database that doesn't support JOINs. Denormalization improves query performance by duplicating data.

### Denormalized Fields

#### leaderboard Collection
**Source**: `users/{userId}`  
**Denormalized Fields**:
- `score` ← `users/{userId}.wallet`
- `name` ← `users/{userId}.name`

**Update Triggers**:
```kotlin
// When wallet changes, update leaderboard
fun updateWallet(userId: String, points: Long) {
    // Update source
    firestore.collection("users").document(userId)
        .update("wallet", FieldValue.increment(points))
    
    // Update denormalized copy
    firestore.collection("leaderboard").document(userId)
        .update("score", FieldValue.increment(points))
}
```

**Benefits**:
- Fast leaderboard queries (no need to read all users)
- Indexed on `score` for efficient sorting
- Reduced read costs (1 query instead of 50+ reads)

---

## 🗂️ Subcollections (Optional Enhancement)

### users/{userId}/transactions/
**Purpose**: Store wallet transaction history per user

| Field | Type | Description |
|-------|------|-------------|
| points | number | Points earned/deducted |
| type | string | "earned", "redeemed", "bonus" |
| description | string | Transaction description |
| sourceType | string | "game", "idea", "streak" |
| sourceId | string | Reference to source document |
| createdAt | timestamp | Transaction time |

**Example Query**:
```kotlin
// Get transaction history for user
firestore.collection("users").document("abc123")
    .collection("transactions")
    .orderBy("createdAt", Query.Direction.DESCENDING)
    .limit(20)
    .get()
```

**Benefits**:
- Organized data (transactions belong to user)
- Efficient queries (only read transactions for one user)
- Scalable (doesn't bloat users/ collection)

---

## 📊 Query Examples

### Example 1: Get Top 10 Leaderboard
```kotlin
firestore.collection("leaderboard")
    .orderBy("score", Query.Direction.DESCENDING)
    .limit(10)
    .get()
    .await()
```

**Firestore Index**: `score (Descending)`

---

### Example 2: Get User's Game History
```kotlin
firestore.collection("participations")
    .whereEqualTo("userId", userId)
    .orderBy("timestamp", Query.Direction.DESCENDING)
    .limit(20)
    .get()
    .await()
```

**Firestore Composite Index**: `userId (Ascending), timestamp (Descending)`

---

### Example 3: Get Recent Ideas for Category
```kotlin
firestore.collection("ideas")
    .whereEqualTo("category", "feature")
    .orderBy("submittedAt", Query.Direction.DESCENDING)
    .limit(10)
    .get()
    .await()
```

**Firestore Composite Index**: `category (Ascending), submittedAt (Descending)`

---

### Example 4: Get User's Wallet Balance
```kotlin
firestore.collection("users")
    .document(userId)
    .get()
    .await()
    .getLong("wallet") ?: 0
```

**No index required** (single document read)

---

### Example 5: Get Game Statistics
```kotlin
// Count total participations for a game
firestore.collection("participations")
    .whereEqualTo("gameId", gameId)
    .get()
    .await()
    .size()

// Calculate average score
val snapshot = firestore.collection("participations")
    .whereEqualTo("gameId", gameId)
    .get()
    .await()
    
val avgScore = snapshot.documents
    .mapNotNull { it.getLong("score") }
    .average()
```

**Firestore Index**: `gameId (Ascending)`

---

## 🔐 Firestore Security Rules

### Complete Security Rules Configuration

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    
    // Helper function: Check if user is authenticated
    function isAuthenticated() {
      return request.auth != null;
    }
    
    // Helper function: Check if user owns the document
    function isOwner(userId) {
      return isAuthenticated() && request.auth.uid == userId;
    }
    
    // users/ collection
    match /users/{userId} {
      // Allow read if authenticated
      allow read: if isAuthenticated();
      
      // Allow create only for self during signup
      allow create: if isOwner(userId);
      
      // Allow update only for self (wallet/streak updated by backend)
      allow update: if isOwner(userId);
      
      // Deny delete
      allow delete: if false;
      
      // Subcollection: transactions
      match /transactions/{transactionId} {
        allow read: if isOwner(userId);
        allow write: if isOwner(userId);
      }
    }
    
    // games/ collection
    match /games/{gameId} {
      // Anyone authenticated can read games
      allow read: if isAuthenticated();
      
      // Only admins can write (implement admin check)
      allow write: if false;
    }
    
    // participations/ collection
    match /participations/{participationId} {
      // Users can read their own participations
      allow read: if isAuthenticated() && 
                     resource.data.userId == request.auth.uid;
      
      // Users can create participations for themselves
      allow create: if isOwner(request.resource.data.userId);
      
      // Deny update/delete
      allow update, delete: if false;
    }
    
    // ideas/ collection
    match /ideas/{ideaId} {
      // Anyone authenticated can read ideas
      allow read: if isAuthenticated();
      
      // Users can create ideas for themselves
      allow create: if isOwner(request.resource.data.userId);
      
      // Users can update their own ideas (votes updated by backend)
      allow update: if isOwner(resource.data.userId);
      
      // Users can delete their own ideas
      allow delete: if isOwner(resource.data.userId);
    }
    
    // leaderboard/ collection
    match /leaderboard/{userId} {
      // Anyone authenticated can read leaderboard
      allow read: if isAuthenticated();
      
      // Only backend can write (server-side update)
      allow write: if false;
    }
  }
}
```

---

## 💾 Data Migration Strategy

### Initial Data Population

#### 1. Create Sample Games
```kotlin
val games = listOf(
    Game(
        gameId = "game_flash_001",
        type = "flash_hustle",
        duration = 300,
        reward = 10,
        title = "Flash Hustle Quiz",
        description = "5 questions in 5 minutes"
    ),
    Game(
        gameId = "game_skill_001",
        type = "skill_streak",
        duration = 0,
        reward = 10,
        title = "Daily Skill Challenge",
        description = "Complete daily coding task"
    )
)

games.forEach { game ->
    firestore.collection("games").document(game.gameId).set(game).await()
}
```

#### 2. Sync User to Leaderboard
```kotlin
// Run once for existing users
fun syncLeaderboard(userId: String) {
    val user = firestore.collection("users").document(userId).get().await()
        .toObject(User::class.java)
    
    if (user != null) {
        val leaderboard = Leaderboard(
            userId = userId,
            score = user.wallet,
            name = user.name
        )
        firestore.collection("leaderboard").document(userId).set(leaderboard).await()
    }
}
```

---

## 🎯 Key Differences: DRD vs ERD

| Aspect | ERD (Relational) | DRD (Firestore) |
|--------|------------------|-----------------|
| **Structure** | Tables with rows | Collections with documents |
| **Relationships** | Foreign keys with JOINs | String references, no JOINs |
| **Normalization** | Highly normalized | Denormalized for performance |
| **Queries** | SQL with JOINs | NoSQL with filters |
| **Transactions** | ACID transactions | Limited multi-document transactions |
| **Scaling** | Vertical scaling | Horizontal scaling |
| **Leaderboard** | Query users ordered by points | Separate denormalized collection |
| **Field Names** | totalPoints, streakDays | wallet, streak (shorter) |

---

## 🎓 Interview Talking Points

### Q1: Why use Firestore instead of MySQL?
**A**: 
- **Real-time updates**: Firestore provides live data synchronization
- **Scalability**: Auto-scales horizontally without sharding
- **Firebase ecosystem**: Integrated auth, storage, cloud functions
- **Offline support**: Built-in offline caching for mobile apps
- **No server management**: Serverless architecture

### Q2: Why denormalize the leaderboard?
**A**: Firestore doesn't support JOINs. Reading all users and sorting client-side is expensive (50+ document reads). Denormalized `leaderboard/` collection allows:
- Single query for top 50 users
- Indexed on `score` for fast sorting
- Reduced read costs (1 query vs 50+ reads)

### Q3: How do you keep leaderboard synced?
**A**: Every operation that updates `users/{userId}.wallet` also updates `leaderboard/{userId}.score`:
- Game participation → Update wallet + leaderboard
- Idea approval → Update wallet + leaderboard
- Streak completion → Update wallet + leaderboard

### Q4: What about data consistency?
**A**: Use Firestore batch writes or transactions:
```kotlin
firestore.runTransaction { transaction ->
    transaction.update(userRef, "wallet", FieldValue.increment(points))
    transaction.update(leaderboardRef, "score", FieldValue.increment(points))
}
```

### Q5: How do you handle high-frequency writes?
**A**: 
- Use `FieldValue.increment()` for atomic updates
- Implement exponential backoff for retries
- Consider aggregation functions for analytics
- Use Cloud Functions for heavy processing

---

## 📈 Performance Optimization

### Indexing Strategy
1. **Single-field indexes** (automatic):
   - `users.email`
   - `users.wallet`
   - `leaderboard.score`

2. **Composite indexes** (manual creation):
   - `participations: userId (ASC) + timestamp (DESC)`
   - `participations: gameId (ASC) + timestamp (DESC)`
   - `ideas: userId (ASC) + submittedAt (DESC)`
   - `ideas: status (ASC) + votes (DESC)`

### Query Optimization
- **Limit results**: Use `.limit(50)` to avoid reading thousands of documents
- **Pagination**: Use `.startAfter(lastDoc)` for infinite scroll
- **Cached reads**: Enable offline persistence for mobile apps
- **Batch operations**: Update multiple documents in one transaction

---

## 📊 Summary

### DRD Collections (5 total)
1. ✅ `users/` - User profiles (name, email, wallet, streak)
2. ✅ `games/` - Available games (type, duration, reward)
3. ✅ `participations/` - Game plays (userId, gameId, score, timestamp)
4. ✅ `ideas/` - User submissions (userId, title, description, votes)
5. ✅ `leaderboard/` - Denormalized rankings (userId, score)

### Key Firestore Features Used
- ✅ Collections and documents
- ✅ Subcollections (`users/{userId}/transactions`)
- ✅ References (userId, gameId strings)
- ✅ Timestamps (ServerTimestamp)
- ✅ Denormalization (leaderboard)
- ✅ Atomic updates (FieldValue.increment)
- ✅ Composite indexes
- ✅ Security rules

**Status**: Production-ready Firestore schema for Sharp Rewards MVP

---

**Prepared for**: Sharp Rewards MVP - Firestore Implementation  
**Date**: January 11, 2024  
**Version**: 1.0
