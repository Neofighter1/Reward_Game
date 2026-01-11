# Sharp Rewards - Entity Relationship Diagram (ERD)

**For Technical Viva / Interview Explanation**

---

## 📊 Database Schema Overview

This document explains the database structure for the Sharp Rewards application using standard ERD principles.

---

## 🗂️ Core Entities

### 1. USER Entity
**Primary Key**: `userId`

| Column | Type | Description |
|--------|------|-------------|
| userId | String (PK) | Unique identifier for user |
| name | String | User's display name |
| email | String | User's email address |
| streakCount | Integer | Current consecutive day streak |
| walletBalance | Long | Total points in user's wallet |

**Relations**:
- One USER → Many PARTICIPATIONS
- One USER → Many IDEAS
- One USER → Many REWARDS

---

### 2. GAME Entity
**Primary Key**: `gameId`

| Column | Type | Description |
|--------|------|-------------|
| gameId | String (PK) | Unique identifier for game |
| gameType | String | Type of game (e.g., "flash_hustle") |
| duration | Long | Game duration in seconds (0 = unlimited) |
| rewardPoints | Integer | Base points per game completion |

**Relations**:
- One GAME → Many PARTICIPATIONS

**Game Types**:
- `flash_hustle` - Time-bound quiz game (5 questions)
- `skill_streak` - Daily skill challenge
- `quick_quiz` - Fast 3-question quiz
- `challenge_mode` - Advanced difficulty

---

### 3. PARTICIPATION Entity
**Primary Key**: `participationId`  
**Foreign Keys**: `userId`, `gameId`

| Column | Type | Description |
|--------|------|-------------|
| participationId | String (PK) | Unique participation record |
| userId | String (FK) | Reference to USER |
| gameId | String (FK) | Reference to GAME |
| score | Integer | Score achieved in this game |
| timestamp | DateTime | When participation occurred |

**Relations**:
- Many PARTICIPATIONS → One USER
- Many PARTICIPATIONS → One GAME

**Additional Fields** (for detailed tracking):
- `questionsAttempted` - Number of questions answered
- `correctAnswers` - Number of correct answers
- `pointsEarned` - Total points earned
- `timeTaken` - Time taken to complete (seconds)

---

### 4. IDEA Entity
**Primary Key**: `ideaId`  
**Foreign Key**: `userId`

| Column | Type | Description |
|--------|------|-------------|
| ideaId | String (PK) | Unique identifier for idea |
| userId | String (FK) | Reference to USER who submitted |
| title | String | Idea title |
| description | String | Detailed idea description |
| votes | Integer | Number of votes/likes received |

**Relations**:
- Many IDEAS → One USER

**Status Values**:
- `pending` - Awaiting review
- `approved` - Approved and rewarded
- `rejected` - Not approved

---

### 5. REWARD Entity
**Primary Key**: `rewardId`  
**Foreign Key**: `userId`

| Column | Type | Description |
|--------|------|-------------|
| rewardId | String (PK) | Unique reward transaction ID |
| userId | String (FK) | Reference to USER |
| points | Long | Points earned/deducted |
| createdAt | DateTime | When reward was created |

**Relations**:
- Many REWARDS → One USER

**Reward Types**:
- `earned` - Points earned from games/ideas
- `redeemed` - Points spent/redeemed
- `bonus` - Bonus points from promotions

**Source Types**:
- `game` - Earned from game participation
- `idea` - Earned from approved idea
- `streak` - Earned from daily streak
- `bonus` - Promotional/admin bonus

---

## 🔗 Relationship Diagram

```
┌─────────────┐
│    USER     │
│  (userId)   │
└──────┬──────┘
       │
       │ 1:N
       │
   ┌───┴────────────────────────┬─────────────┬──────────────┐
   │                            │             │              │
   ▼                            ▼             ▼              ▼
┌──────────────┐        ┌────────────┐  ┌─────────┐  ┌──────────┐
│PARTICIPATION │        │    IDEA    │  │ REWARD  │  │ (others) │
│(participationId)│     │  (ideaId)  │  │(rewardId)│  │          │
│                │      │            │  │         │  │          │
│ userId (FK)    │      │ userId (FK)│  │userId(FK)│  │          │
│ gameId (FK)    │      │ title      │  │ points  │  │          │
│ score          │      │ description│  │createdAt│  │          │
│ timestamp      │      │ votes      │  │         │  │          │
└────┬───────────┘      └────────────┘  └─────────┘  └──────────┘
     │
     │ N:1
     │
     ▼
┌──────────────┐
│     GAME     │
│  (gameId)    │
│              │
│ gameType     │
│ duration     │
│ rewardPoints │
└──────────────┘
```

---

## 📋 Cardinality Explanation (For Viva)

### One-to-Many Relationships

#### 1. USER → PARTICIPATION (1:N)
**Explanation**: 
- One user can participate in many games
- Each participation belongs to exactly one user

**Example**:
```
User "John" (userId: user_123)
  ├─ Participation 1: Flash Hustle Game on Jan 5
  ├─ Participation 2: Flash Hustle Game on Jan 6
  └─ Participation 3: Skill Streak on Jan 6
```

#### 2. GAME → PARTICIPATION (1:N)
**Explanation**:
- One game can have many participations (by different users)
- Each participation is for exactly one game

**Example**:
```
Game "Flash Hustle" (gameId: game_001)
  ├─ Participation by User A
  ├─ Participation by User B
  └─ Participation by User C
```

#### 3. USER → IDEA (1:N)
**Explanation**:
- One user can submit many ideas
- Each idea belongs to exactly one user

**Example**:
```
User "Sarah" (userId: user_456)
  ├─ Idea 1: "New game mode suggestion"
  ├─ Idea 2: "Improved leaderboard"
  └─ Idea 3: "Team challenges"
```

#### 4. USER → REWARD (1:N)
**Explanation**:
- One user can receive many rewards
- Each reward transaction belongs to exactly one user

**Example**:
```
User "Mike" (userId: user_789)
  ├─ Reward 1: +50 pts from game (Jan 5)
  ├─ Reward 2: +10 pts from streak (Jan 5)
  └─ Reward 3: +50 pts from approved idea (Jan 6)
```

---

## 🗃️ Collection Structure (Firestore)

In Firebase Firestore, we implement this ERD as collections:

```
/users/{userId}
  - name
  - email
  - streakCount
  - walletBalance

/games/{gameId}
  - gameType
  - duration
  - rewardPoints

/participations/{participationId}
  - userId (reference)
  - gameId (reference)
  - score
  - timestamp

/ideas/{ideaId}
  - userId (reference)
  - title
  - description
  - votes

/rewards/{rewardId}
  - userId (reference)
  - points
  - createdAt
```

---

## 🔍 Query Examples (For Viva)

### Example 1: Get User's Participation History
```kotlin
// Get all participations for a user
firestore.collection("participations")
    .whereEqualTo("userId", "user_123")
    .orderBy("timestamp", Query.Direction.DESCENDING)
    .get()
```

### Example 2: Get Game Statistics
```kotlin
// Get all participations for a specific game
firestore.collection("participations")
    .whereEqualTo("gameId", "game_001")
    .get()
```

### Example 3: Calculate User's Total Points
```kotlin
// Get all rewards for a user
firestore.collection("rewards")
    .whereEqualTo("userId", "user_123")
    .get()
    .let { rewards ->
        rewards.sumOf { it.points }
    }
```

### Example 4: Get User's Ideas
```kotlin
// Get all ideas submitted by a user
firestore.collection("ideas")
    .whereEqualTo("userId", "user_123")
    .orderBy("submittedAt", Query.Direction.DESCENDING)
    .get()
```

---

## 💡 Interview Questions & Answers

### Q1: Why did you use these specific entities?
**A**: These entities represent the core business logic:
- **USER**: Represents players in the system
- **GAME**: Defines available games/challenges
- **PARTICIPATION**: Tracks user engagement and performance
- **IDEA**: Enables user-generated content and community engagement
- **REWARD**: Provides detailed transaction history for gamification

### Q2: Why is PARTICIPATION a separate entity?
**A**: PARTICIPATION acts as a **junction table** (associative entity) between USER and GAME, enabling:
- Tracking multiple participations by same user in same game
- Storing participation-specific data (score, timestamp)
- Maintaining historical records
- Supporting analytics and reporting

### Q3: How do you handle wallet balance updates?
**A**: Two-step process:
1. Create REWARD record (transaction log)
2. Update USER.walletBalance (atomic operation)

This ensures:
- Transaction history (REWARD table)
- Current balance (USER table)
- Data integrity with Firestore transactions

### Q4: What's the difference between REWARD and PARTICIPATION?
**A**: 
- **PARTICIPATION**: Records game plays (score, time, questions)
- **REWARD**: Records point transactions (earned, redeemed, bonus)

One participation can generate one reward entry, but rewards can come from other sources (ideas, streaks, bonuses).

### Q5: How do you ensure data integrity?
**A**: 
- Foreign keys maintained through Firestore references
- Atomic updates using Firestore transactions
- Validation in Repository layer before writes
- Cascading operations for related data

### Q6: How do you optimize queries?
**A**: 
- Composite indexes on (userId, timestamp)
- Denormalization: Store walletBalance in USER (avoid SUM queries)
- Limit query results (pagination)
- Use Firestore subcollections for related data

---

## 🎯 Key Points for Viva

### Database Design Principles Applied:
1. ✅ **Normalization**: Each entity has single responsibility
2. ✅ **Primary Keys**: Every entity has unique identifier
3. ✅ **Foreign Keys**: Relationships maintained through references
4. ✅ **Data Integrity**: Constraints and validation enforced
5. ✅ **Scalability**: Designed for growth (millions of records)

### Why This ERD is Interview-Safe:
- ✅ Follows standard ERD notation
- ✅ Clear cardinality (1:N relationships)
- ✅ Proper use of junction tables (PARTICIPATION)
- ✅ Separation of concerns (each entity = one concept)
- ✅ Supports complex queries and analytics

### Real-World Scenarios Supported:
1. User signs up → Creates USER record
2. User plays game → Creates PARTICIPATION record
3. System awards points → Creates REWARD record
4. User submits idea → Creates IDEA record
5. Calculate leaderboard → Query USERS by walletBalance
6. Show game history → Query PARTICIPATIONS by userId

---

## 📊 Sample Data

### USER Table
| userId | name | email | streakCount | walletBalance |
|--------|------|-------|-------------|---------------|
| u001 | John Doe | john@example.com | 5 | 250 |
| u002 | Sarah Smith | sarah@example.com | 3 | 180 |

### GAME Table
| gameId | gameType | duration | rewardPoints |
|--------|----------|----------|--------------|
| g001 | flash_hustle | 300 | 10 |
| g002 | skill_streak | 0 | 10 |

### PARTICIPATION Table
| participationId | userId | gameId | score | timestamp |
|-----------------|--------|--------|-------|-----------|
| p001 | u001 | g001 | 50 | 2024-01-10 10:00 |
| p002 | u001 | g001 | 40 | 2024-01-11 10:00 |
| p003 | u002 | g001 | 30 | 2024-01-11 11:00 |

### IDEA Table
| ideaId | userId | title | description | votes |
|--------|--------|-------|-------------|-------|
| i001 | u001 | Team Mode | Add team challenges | 15 |
| i002 | u002 | Daily Bonus | Double points on weekends | 8 |

### REWARD Table
| rewardId | userId | points | createdAt |
|----------|--------|--------|-----------|
| r001 | u001 | +50 | 2024-01-10 10:05 |
| r002 | u001 | +10 | 2024-01-10 12:00 |
| r003 | u002 | +30 | 2024-01-11 11:05 |

---

## 🎓 Summary

This ERD provides:
- ✅ Clear entity definitions
- ✅ Well-defined relationships (1:N)
- ✅ Proper use of primary and foreign keys
- ✅ Scalable for future growth
- ✅ Interview-ready explanation
- ✅ Real-world implementation with Firestore

**Status**: Ready for technical viva/interview presentation

---

**Prepared for**: Sharp Rewards MVP  
**Date**: January 11, 2024  
**Version**: 1.0
