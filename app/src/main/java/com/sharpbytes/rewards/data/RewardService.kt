package com.sharpbytes.rewards.data




















































































































































































































































































































































































































































































































































































































**Version**: 1.0**Date**: January 11, 2024  **Prepared for**: Sharp Rewards MVP - Smart Rewarding System  ---**Status**: Production-ready gamification system with FOMO mechanics1. ✅ **Inactivity Penalty** - -50% wallet after 7 days inactive### Penalties (1 type):3. ✅ **Rarity Bonus** - 1.5x for rare game participation2. ✅ **Streak Bonus** - 1.2x-2.0x for daily activity1. ✅ **Speed Bonus** - 1.5x-2.0x for fast completion### Smart Bonuses (3 types):7. ✅ **Redeemable Rewards** - Cash/coupons/gift cards6. ✅ **Leaderboard Rank** - Competitive position5. ✅ **Badges** - Achievement tracking (4 rarities)4. ✅ **Levels** - Visual progression (1-50+)3. ✅ **XP** - Level progression (50% of points)2. ✅ **Coins** - Redeemable currency (10% of points)1. ✅ **Wallet Points** - Leaderboard currency### Reward Types (7 total):## 🎯 Summary---6. **Inactivity Penalty Applied**: Count of users penalized monthly5. **Badge Unlock Rate**: % of users with rare/epic/legendary badges4. **Redemption Rate**: Coins redeemed / Coins earned ratio3. **Streak Retention**: % of users with 7+ day streaks2. **Speed Bonus Distribution**: % of users earning speed bonuses1. **Average Reward Multiplier**: Track average bonus multiplier per game### Key Performance Indicators (KPIs)## 📊 Analytics & Metrics---```}  allow write: if false; // Only server-side operations  allow read: if isAuthenticated();match /user_badges/{badgeId} {// Only backend can modify badges}                && userHasEnoughCoins(request.resource.data.coinsRequired);  allow create: if isOwner(request.resource.data.userId)   allow read: if isOwner(resource.data.userId);match /redemptions/{redemptionId} {// Only allow users to redeem their own rewards```javascript## 🔐 Firestore Security Rules---```└───┴──────────────┴────────┴───────┴────────┘│ 5 │ Alex        │  7,200 │  12   │ ⭐      ││ 4 │ Emma        │  8,500 │  15   │ 🔥      ││ 3 │ Mike🥉      │ 10,000 │  18   │ ⭐🎯    ││ 2 │ Sarah🥈     │ 12,500 │  22   │ 🏆🔥    ││ 1 │ John🥇      │ 15,000 │  25   │ 🏆💎⚡  │├───┼──────────────┼────────┼───────┼────────┤│ # │ Player       │ Score  │ Level │ Badges │┌───┬──────────────┬────────┬───────┬────────┐```### Leaderboard with Level & Badges```└─────────────────────────────────────┘│  Badge Unlocked: Speed Demon! 🔥    ││  New Level: 7 → 8! 🎊               ││                                      ││  ⭐ XP:            +75 XP           ││  🪙 Coins:         +15 coins        ││  💰 Wallet:        +150 pts         ││                                      ││  Total Earned:      150 points      │├─────────────────────────────────────┤│  💎 Rarity Bonus:  +25 (1.5x)      ││  🔥 Streak Bonus:  +25 (1.5x)      ││  ⚡ Speed Bonus:    +50 (2.0x)      ││  Base Points:        50             │├─────────────────────────────────────┤│  🎉 Congratulations!                │┌─────────────────────────────────────┐```### Reward Summary Screen## 📱 UI/UX Display Examples---```showRewardSummary(rewardCalc)// 8. Show reward breakdown to usercheckAndAwardBadges(userId)// 7. Check for new badgesrewardService.awardReward(userId, rewardCalc)// 6. Award rewards to usersaveParticipation(participation))    totalRewardMultiplier = rewardCalc.totalMultiplier    rarityBonus = rewardCalc.rarityBonus,    streakBonus = rewardCalc.streakBonus,    speedBonus = rewardCalc.speedBonus,    timeTaken = timeTaken,    xpEarned = rewardCalc.xpEarned,    coinsEarned = rewardCalc.coinsEarned,    pointsEarned = rewardCalc.totalPoints,    questionsAttempted = 5,    correctAnswers = correctAnswers,    score = correctAnswers * 10,    gameId = game.gameId,    userId = userId,val participation = Participation(// 5. Create participation record)    gameId = game.gameId    streakDays = user.streak,    timeTaken = timeTaken,    basePoints = basePoints,val rewardCalc = rewardService.calculateSmartReward(// 4. Calculate smart rewardval user = getUserData(userId)// 3. Get user data for streakval basePoints = game.reward * correctAnswersval correctAnswers = 5val timeTaken = (endTime - startTime) / 1000 // secondsval endTime = System.currentTimeMillis()// 2. User completes gameval startTime = System.currentTimeMillis()val game = getGame("flash_hustle")// 1. User starts game```kotlin### Complete Game Flow with Smart Rewards## 🎮 Integration Example---```}    return Result.success(redemptionId)        checkRedemptionBadges(userId)    // 4. Check for redemption badge        val redemptionId = createRedemption(redemption)    )        status = "pending"        coinsRequired = coinsRequired,        rewardTitle = rewardTitle,        rewardType = rewardType,        userId = userId,    val redemption = Redemption(    // 3. Create redemption record        updateCoins(userId, -coinsRequired)    // 2. Deduct coins        }        return Result.failure(Exception("Insufficient coins"))    if (user.coins < coinsRequired) {    val user = getUserData(userId)    // 1. Check user coins balance): Result<String> {    coinsRequired: Long    rewardTitle: String,    rewardType: String,    userId: String,suspend fun redeemReward(```kotlin### Redemption Process| 50% Off Coupon | 3,000 coins | 30 days || 25% Off Coupon | 1,500 coins | 30 days || 10% Off Coupon | 500 coins | 30 days ||--------|---------------|----------|| Reward | Coins Required | Validity |#### Coupons| $25 Amazon | 22,500 coins | Instant || $15 iTunes | 13,500 coins | Instant || $10 Google Play | 9,000 coins | Instant || $5 Amazon | 4,500 coins | Instant ||--------|---------------|----------|| Reward | Coins Required | Delivery |#### Gift Cards| $50 Bank Transfer | 50,000 coins | 5-7 days || $25 Bank Transfer | 25,000 coins | 5-7 days || $10 PayPal | 10,000 coins | 2-3 days || $5 PayPal | 5,000 coins | 2-3 days ||--------|---------------|-----------------|| Reward | Coins Required | Processing Time |#### Cash Redemptions### Available Rewards## 💵 Redemption System---```}    }        100 -> awardBadge(userId, "quiz_master_badge")        1 -> awardBadge(userId, "first_win_badge")    when (gameCount) {    val gameCount = getGameParticipationCount(userId)    // Game count badges        }        30 -> awardBadge(userId, "streak_30_badge")        7 -> awardBadge(userId, "streak_7_badge")        3 -> awardBadge(userId, "streak_3_badge")    when (user.streak) {    // Streak badges        }        20 -> awardBadge(userId, "level_20_badge")        10 -> awardBadge(userId, "level_10_badge")        5 -> awardBadge(userId, "level_5_badge")    when (user.level) {    val user = getUserData(userId)    // Level badgessuspend fun checkAndAwardBadges(userId: String) {```kotlin### Badge Award Logic| Reward Hunter | 10 redemptions | Epic | +500 XP || Big Spender | Redeem 1000+ coins | Rare | +100 XP || First Redemption | Redeem first reward | Common | +50 XP ||-------|-------------|--------|---------|| Badge | Requirement | Rarity | Rewards |#### 5. **Redemption Badges**| Trending Creator | 100+ votes on idea | Epic | +300 coins || Idea Factory | 10 approved ideas | Rare | +150 coins || Innovator | Submit first idea | Common | +25 coins ||-------|-------------|--------|---------|| Badge | Requirement | Rarity | Rewards |#### 4. **Idea Badges**| Perfect Score | 100% accuracy 10 times | Legendary | +500 coins || Speed Demon | Win with <30s average | Epic | +200 coins || Quiz Master | Complete 100 games | Rare | +100 coins || First Win | Complete first game | Common | +10 coins ||-------|-------------|--------|---------|| Badge | Requirement | Rarity | Rewards |#### 3. **Game Badges**| Year Legend | 365 consecutive days | Legendary | +1000 coins || Month Master | 30 consecutive days | Epic | +150 coins || Week Warrior | 7 consecutive days | Rare | +50 coins || 3-Day Streak | 3 consecutive days | Common | +20 coins ||-------|-------------|--------|---------|| Badge | Requirement | Rarity | Rewards |#### 2. **Streak Badges**| Diamond Star | Reach Level 50 | Legendary | +500 coins, +1000 XP || Gold Star | Reach Level 20 | Epic | +250 coins, +500 XP || Silver Star | Reach Level 10 | Rare | +100 coins, +200 XP || Bronze Star | Reach Level 5 | Common | +50 coins, +100 XP ||-------|-------------|--------|---------|| Badge | Requirement | Rarity | Rewards |#### 1. **Level Badges**### Badge Categories## 🏆 Badge System---```}    return 1    }        }            return level + 1        if (xp >= levels[level]) {    for (level in levels.indices.reversed()) {        val levels = listOf(0L, 100L, 300L, 600L, 1000L, 1500L, ...)fun calculateLevel(xp: Long): Int {```kotlin### Level Calculation Algorithm| 15+ | +5,000 per level | - | - || 14 | 15,000 | 55,500 | - || 13 | 10,000 | 40,500 | - || 12 | 8,000 | 30,500 | - || 11 | 6,000 | 22,500 | - || 10 | 4,500 | 16,500 | 🥈 Level 10 Badge || 9 | 3,600 | 12,000 | - || 8 | 2,800 | 8,400 | - || 7 | 2,100 | 5,600 | - || 6 | 1,500 | 3,500 | - || 5 | 1,000 | 2,000 | 🥉 Level 5 Badge || 4 | 600 | 1,000 | - || 3 | 300 | 400 | - || 2 | 100 | 100 | - || 1 | 0 | 0 | - ||-------|-------------|---------------|----------------|| Level | XP Required | Cumulative XP | Badge Unlocked |### Level Progression Table## 📊 XP & Level System---| Rarity Bonus | **+25** | Rare game participation || Streak Bonus | **+25** | 8-day streak || Speed Bonus | **+50** | Lightning fast completion || XP | **+112** | Progress toward next level || Coins | **+22** | Redeemable for rewards || Wallet Points | **+225** | Added to leaderboard score ||-------------|--------|-------|| Reward Type | Amount | Notes |### Result:   ```   XP: 225 × 0.50 = +112 XP   Coins: 225 × 0.10 = +22 coins   Wallet Points: +225      Total Points = 50 × 4.5 = 225 points   Total Multiplier = 2.0 × 1.5 × 1.5 = 4.5x   ```5. **Total Calculation**:   ```   Rarity Bonus = 50 × 1.5 = 75 points   Rarity Multiplier = 1.5x (< 10 participations)   ```4. **Rarity Bonus** (6 participations):   ```   Streak Bonus = 50 × 1.5 = 75 points   Streak Multiplier = 1.5x (7-29 days)   ```3. **Streak Bonus** (8 days):   ```   Speed Bonus = 50 × 2.0 = 100 points   Speed Multiplier = 2.0x (< 60 seconds)   ```2. **Speed Bonus** (55 seconds):   ```   Base = 10 points/question × 5 correct = 50 points   ```1. **Base Points**:### Step-by-Step Calculation:- **Game Participation Count**: 6 (rare game)- **User Streak**: 8 days- **Time Taken**: 55 seconds- **Correct Answers**: 5/5- **Base Points Per Question**: 10- **Game**: Flash Hustle Quiz### Scenario:## 🎯 Complete Example Calculation---  - Any wallet transaction  - Daily check-in  - Idea submission  - Game participation- User's `lastActiveDate` updated on every:**Prevention**:```}    return Result.success(0)    }        return Result.success(penalty)        updateWallet(userId, -penalty)        val penalty = user.wallet * 0.5    if (daysSinceActive >= 7) {        val daysSinceActive = (currentTime - user.lastActiveDate) / DAY_IN_MILLIS    val user = getUserData(userId)suspend fun applyInactivityPenalty(userId: String): Result<Long> {```kotlin**Implementation**:```New Balance: 500 pointsPenalty: 1000 × 0.5 = -500 pointsDays Inactive: 10 daysWallet Balance: 1000 points```**Example**:- Maintain active user base- Encourage daily engagement- Create FOMO (Fear Of Missing Out)**Purpose**: | < 7 days | No penalty | - || 7+ days | **-50% wallet points** | ⚠️ Inactivity Penalty Applied ||---------------|---------|---------|| Days Inactive | Penalty | Warning |**Penalty for not engaging with the app**## 📉 Inactivity Penalty (FOMO Reinforcement)---```}    return if (count < 10) 1.5f else 1.0f            .get().await().size()        .whereEqualTo("gameId", gameId)    val count = firestore.collection("participations")suspend fun calculateRarityBonus(gameId: String): Float {```kotlin**Implementation**:```Rarity Reward: 50 × 1.5 = 75 pointsRarity Bonus: 1.5xGame Participation Count: 7Base Points: 50```**Example**:**Purpose**: Encourage users to try new/underutilized games| ≥ 10 participations | **1.0x** | Standard || < 10 participations | **1.5x** | 💎 Rare Game ||------------------------------|------------|--------|| Total Participations in Game | Multiplier | Rarity |**Rewards playing less popular games**## 💎 Rarity Bonus (Rare Participation)---```}    }        else -> 1.0f        streakDays >= 3 -> 1.2f        streakDays >= 7 -> 1.5f        streakDays >= 30 -> 2.0f    return when {fun calculateStreakBonus(streakDays: Int): Float {```kotlin**Implementation**:```Streak Reward: 50 × 1.5 = 75 pointsStreak Bonus: 1.5xStreak Days: 10Base Points: 50```**Example**:| 1-2 days | **1.0x** | - || 3-6 days | **1.2x** | ⭐ Starter Streak || 7-29 days | **1.5x** | 🔥 Weekly Warrior || 30+ days | **2.0x** | 🏆 Legendary Streak ||-------------|------------|----------------|| Streak Days | Multiplier | Badge Unlocked |**Rewards longer activity streaks**## 🔥 Streak Bonus (Consistency Multiplier)---```}    }        else -> 1.0f        timeTaken < 180 -> 1.5f        timeTaken < 60 -> 2.0f    return when {fun calculateSpeedBonus(timeTaken: Long): Float {```kotlin**Implementation**:```Speed Reward: 50 × 2.0 = 100 pointsSpeed Bonus: 2.0xTime Taken: 45 secondsBase Points: 50```**Example**:| > 180 seconds | **1.0x** | Standard Pace || 60-180 seconds | **1.5x** | Quick Response ⚡ || < 60 seconds | **2.0x** | Lightning Fast 🔥 ||------------|------------|-------------------|| Time Taken | Multiplier | Bonus Description |**Rewards faster completion**## ⚡ Speed Bonus (Time-Based Multiplier)---```XP = Total Reward × 0.50 (50%)Coins = Total Reward × 0.10 (10%)Total Reward = Base Points × Speed Bonus × Streak Bonus × Rarity Bonus```kotlin### Total Reward Formula```Base Points = Game Points * Correct Answers```kotlin### Base Reward Calculation## 🧠 Smart Reward Logic---- **Storage**: `redemptions/{redemptionId}`- **Status Tracking**: Pending → Approved → Fulfilled- **Requirements**: Spend coins to redeem  - Products (physical merchandise)  - Gift Cards (Amazon, Google Play, iTunes)  - Coupons (discount codes)  - Cash (PayPal, bank transfer)- **Types**: ### 7. **Redeemable Rewards**- **Storage**: `leaderboard/{userId}.score`- **Display**: Top 50 users, user's current rank- **Calculation**: Based on wallet points (denormalized in leaderboard collection)- **Purpose**: Competitive positioning### 6. **Leaderboard Rank**- **Storage**: `users/{userId}.badges[]`, `user_badges/{userId}_{badgeId}`- **Rarity Tiers**: Common, Rare, Epic, Legendary- **Types**: Level badges, milestone badges, special achievement badges- **Purpose**: Achievement recognition### 5. **Badges**- **Storage**: `users/{userId}.level`- **Levels**: 1-50+ (exponentially increasing XP requirements)- **Calculation**: Automatically calculated from XP- **Purpose**: Visual progression indicator### 4. **Levels**- **Storage**: `users/{userId}.xp`- **Usage**: Determine user level, unlock level-based badges- **Earning**: 50% of wallet points earned (automatic conversion)- **Purpose**: Level progression system### 3. **XP (Experience Points)**- **Storage**: `users/{userId}.coins`- **Usage**: Redeem for cash, coupons, gift cards, products- **Earning**: 10% of wallet points earned (automatic conversion)- **Purpose**: Redeemable currency for real-world rewards### 2. **Coins**- **Storage**: `users/{userId}.wallet`- **Usage**: Visible on leaderboard, represents total skill/engagement- **Earning**: Games, streaks, ideas, achievements- **Purpose**: Primary currency for leaderboard ranking### 1. **Wallet Points**## 💰 Reward Types---**Gamification Engine with Multiple Reward Types & Dynamic Bonuses**import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlin.math.floor
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Smart Reward System
 * Calculates rewards based on:
 * - Speed bonuses (faster completion)
 * - Streak bonuses (longer streaks)
 * - Rarity bonuses (rare participation)
 * - Inactivity penalties (FOMO reinforcement)
 */
class RewardService {
    private val firestore = FirebaseFirestore.getInstance()

    companion object {
        // XP thresholds for leveling up
        private val XP_LEVELS = listOf(
            0L,      // Level 1
            100L,    // Level 2
            300L,    // Level 3
            600L,    // Level 4
            1000L,   // Level 5
            1500L,   // Level 6
            2100L,   // Level 7
            2800L,   // Level 8
            3600L,   // Level 9
            4500L,   // Level 10
            6000L,   // Level 11
            8000L,   // Level 12
            10000L,  // Level 13
            15000L   // Level 14+
        )
        
        // Speed bonus thresholds (in seconds)
        const val SPEED_BONUS_THRESHOLD_FAST = 60    // < 1 min = 2x
        const val SPEED_BONUS_THRESHOLD_NORMAL = 180 // < 3 min = 1.5x
        
        // Streak bonus tiers
        const val STREAK_BONUS_THRESHOLD_1 = 3  // 3+ days = 1.2x
        const val STREAK_BONUS_THRESHOLD_2 = 7  // 7+ days = 1.5x
        const val STREAK_BONUS_THRESHOLD_3 = 30 // 30+ days = 2x
        
        // Rarity bonus (participation count thresholds)
        const val RARE_PARTICIPATION_THRESHOLD = 10 // Less than 10 total participations in this game
        const val RARITY_BONUS_MULTIPLIER = 1.5f
        
        // Inactivity penalty
        const val INACTIVITY_DAYS_THRESHOLD = 7 // Days before penalty
        const val INACTIVITY_PENALTY_PERCENT = 0.5f // 50% wallet reduction
    }
    
    /**
     * Calculate level from XP
     */
    fun calculateLevel(xp: Long): Int {
        for (level in XP_LEVELS.indices.reversed()) {
            if (xp >= XP_LEVELS[level]) {
                return level + 1
            }
        }
        return 1
    }
    
    /**
     * Calculate XP required for next level
     */
    fun getXpForNextLevel(currentLevel: Int): Long {
        return if (currentLevel < XP_LEVELS.size) {
            XP_LEVELS[currentLevel]
        } else {
            XP_LEVELS.last() + (currentLevel - XP_LEVELS.size) * 5000L
        }
    }
    
    /**
     * Calculate speed bonus multiplier
     */
    fun calculateSpeedBonus(timeTaken: Long): Float {
        return when {
            timeTaken < SPEED_BONUS_THRESHOLD_FAST -> 2.0f
            timeTaken < SPEED_BONUS_THRESHOLD_NORMAL -> 1.5f
            else -> 1.0f
        }
    }
    
    /**
     * Calculate streak bonus multiplier
     */
    fun calculateStreakBonus(streakDays: Int): Float {
        return when {
            streakDays >= STREAK_BONUS_THRESHOLD_3 -> 2.0f
            streakDays >= STREAK_BONUS_THRESHOLD_2 -> 1.5f
            streakDays >= STREAK_BONUS_THRESHOLD_1 -> 1.2f
            else -> 1.0f
        }
    }
    
    /**
     * Calculate rarity bonus
     */
    suspend fun calculateRarityBonus(gameId: String): Float {
        return try {
            val participationCount = firestore.collection("participations")
                .whereEqualTo("gameId", gameId)
                .get()
                .await()
                .size()
            
            if (participationCount < RARE_PARTICIPATION_THRESHOLD) {
                RARITY_BONUS_MULTIPLIER
            } else {
                1.0f
            }
        } catch (e: Exception) {
            1.0f
        }
    }
    
    /**
     * Calculate total reward with all bonuses
     */
    suspend fun calculateSmartReward(
        basePoints: Int,
        timeTaken: Long,
        streakDays: Int,
        gameId: String
    ): RewardCalculation {
        val speedMultiplier = calculateSpeedBonus(timeTaken)
        val streakMultiplier = calculateStreakBonus(streakDays)
        val rarityMultiplier = calculateRarityBonus(gameId)
        
        val totalMultiplier = speedMultiplier * streakMultiplier * rarityMultiplier
        
        val finalPoints = (basePoints * totalMultiplier).toInt()
        val coinsEarned = (finalPoints * 0.1f).toInt() // 10% of points as coins
        val xpEarned = (finalPoints * 0.5f).toInt()    // 50% of points as XP
        
        return RewardCalculation(
            basePoints = basePoints,
            speedBonus = ((speedMultiplier - 1.0f) * basePoints).toInt(),
            streakBonus = ((streakMultiplier - 1.0f) * basePoints).toInt(),
            rarityBonus = ((rarityMultiplier - 1.0f) * basePoints).toInt(),
            totalPoints = finalPoints,
            coinsEarned = coinsEarned,
            xpEarned = xpEarned,
            totalMultiplier = totalMultiplier
        )
    }
    
    /**
     * Apply inactivity penalty
     */
    suspend fun applyInactivityPenalty(userId: String): Result<Long> {
        return try {
            val userDoc = firestore.collection("users").document(userId).get().await()
            val user = userDoc.toObject(User::class.java) ?: return Result.failure(Exception("User not found"))
            
            val lastActiveDate = user.lastActiveDate
            val currentDate = System.currentTimeMillis()
            val daysSinceActive = (currentDate - lastActiveDate) / (1000 * 60 * 60 * 24)
            
            if (daysSinceActive >= INACTIVITY_DAYS_THRESHOLD) {
                val penaltyAmount = (user.wallet * INACTIVITY_PENALTY_PERCENT).toLong()
                
                // Deduct wallet
                firestore.collection("users").document(userId)
                    .update(mapOf(
                        "wallet" to FieldValue.increment(-penaltyAmount),
                        "lastActiveDate" to currentDate
                    ))
                    .await()
                
                // Update leaderboard
                firestore.collection("leaderboard").document(userId)
                    .update("score", FieldValue.increment(-penaltyAmount))
                    .await()
                
                Result.success(penaltyAmount)
            } else {
                Result.success(0L) // No penalty
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Award reward to user
     */
    suspend fun awardReward(userId: String, reward: RewardCalculation): Result<Unit> {
        return try {
            val userRef = firestore.collection("users").document(userId)
            
            // Update user stats
            userRef.update(mapOf(
                "wallet" to FieldValue.increment(reward.totalPoints.toLong()),
                "coins" to FieldValue.increment(reward.coinsEarned.toLong()),
                "xp" to FieldValue.increment(reward.xpEarned.toLong()),
                "lastActiveDate" to System.currentTimeMillis()
            )).await()
            
            // Check for level up
            val userDoc = userRef.get().await()
            val user = userDoc.toObject(User::class.java)
            if (user != null) {
                val newLevel = calculateLevel(user.xp)
                if (newLevel > user.level) {
                    userRef.update("level", newLevel).await()
                    // Check for level-up badges
                    checkAndAwardLevelBadge(userId, newLevel)
                }
            }
            
            // Update leaderboard
            firestore.collection("leaderboard").document(userId)
                .update("score", FieldValue.increment(reward.totalPoints.toLong()))
                .await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Check and award badges based on achievements
     */
    private suspend fun checkAndAwardLevelBadge(userId: String, level: Int) {
        val levelBadges = mapOf(
            5 to "level_5_badge",
            10 to "level_10_badge",
            20 to "level_20_badge",
            50 to "level_50_badge"
        )
        
        levelBadges[level]?.let { badgeId ->
            awardBadge(userId, badgeId)
        }
    }
    
    /**
     * Award badge to user
     */
    suspend fun awardBadge(userId: String, badgeId: String): Result<Unit> {
        return try {
            val userRef = firestore.collection("users").document(userId)
            val userDoc = userRef.get().await()
            val user = userDoc.toObject(User::class.java) ?: return Result.failure(Exception("User not found"))
            
            // Check if badge already earned
            if (user.badges.contains(badgeId)) {
                return Result.success(Unit)
            }
            
            // Get badge details
            val badgeDoc = firestore.collection("badges").document(badgeId).get().await()
            val badge = badgeDoc.toObject(Badge::class.java) ?: return Result.failure(Exception("Badge not found"))
            
            // Add badge to user
            userRef.update("badges", FieldValue.arrayUnion(badgeId)).await()
            
            // Create user_badge record
            val userBadge = UserBadge(
                id = "${userId}_${badgeId}",
                userId = userId,
                badgeId = badgeId,
                badgeName = badge.name,
                badgeDescription = badge.description,
                badgeRarity = badge.rarity
            )
            firestore.collection("user_badges").document(userBadge.id).set(userBadge).await()
            
            // Award badge rewards
            if (badge.coinsReward > 0 || badge.xpReward > 0) {
                userRef.update(mapOf(
                    "coins" to FieldValue.increment(badge.coinsReward.toLong()),
                    "xp" to FieldValue.increment(badge.xpReward.toLong())
                )).await()
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Redeem coins for rewards
     */
    suspend fun redeemReward(
        userId: String,
        rewardType: String,
        rewardTitle: String,
        coinsRequired: Long
    ): Result<String> {
        return try {
            val userDoc = firestore.collection("users").document(userId).get().await()
            val user = userDoc.toObject(User::class.java) ?: return Result.failure(Exception("User not found"))
            
            // Check if user has enough coins
            if (user.coins < coinsRequired) {
                return Result.failure(Exception("Insufficient coins"))
            }
            
            // Deduct coins
            firestore.collection("users").document(userId)
                .update("coins", FieldValue.increment(-coinsRequired))
                .await()
            
            // Create redemption record
            val redemption = Redemption(
                userId = userId,
                rewardType = rewardType,
                rewardTitle = rewardTitle,
                coinsRequired = coinsRequired,
                status = "pending"
            )
            val redemptionRef = firestore.collection("redemptions").add(redemption).await()
            
            Result.success(redemptionRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

/**
 * Reward calculation result
 */
data class RewardCalculation(
    val basePoints: Int,
    val speedBonus: Int,
    val streakBonus: Int,
    val rarityBonus: Int,
    val totalPoints: Int,
    val coinsEarned: Int,
    val xpEarned: Int,
    val totalMultiplier: Float
)