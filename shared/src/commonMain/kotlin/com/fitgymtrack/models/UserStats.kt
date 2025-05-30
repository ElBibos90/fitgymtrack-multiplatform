package com.fitgymtrack.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Modello per le statistiche dell'utente
 */
@Serializable
data class UserStats(
    @SerialName("total_workouts")
    val totalWorkouts: Int = 0,

    @SerialName("total_hours")
    val totalHours: Int = 0,

    @SerialName("current_streak")
    val currentStreak: Int = 0,

    @SerialName("longest_streak")
    val longestStreak: Int = 0,

    @SerialName("weekly_average")
    val weeklyAverage: Double = 0.0,

    @SerialName("monthly_average")
    val monthlyAverage: Double = 0.0,

    @SerialName("favorite_exercise")
    val favoriteExercise: String? = null,

    @SerialName("total_exercises_performed")
    val totalExercisesPerformed: Int = 0,

    @SerialName("total_sets_completed")
    val totalSetsCompleted: Int = 0,

    @SerialName("total_reps_completed")
    val totalRepsCompleted: Int = 0,

    @SerialName("weight_progress")
    val weightProgress: Float? = null,

    @SerialName("heaviest_lift")
    val heaviestLift: WeightRecord? = null,

    @SerialName("average_workout_duration")
    val averageWorkoutDuration: Int = 0,

    @SerialName("best_workout_time")
    val bestWorkoutTime: String? = null,

    @SerialName("most_active_day")
    val mostActiveDay: String? = null,

    @SerialName("goals_achieved")
    val goalsAchieved: Int = 0,

    @SerialName("personal_records")
    val personalRecords: List<PersonalRecord> = emptyList(),

    @SerialName("recent_workouts")
    val recentWorkouts: Int = 0,

    @SerialName("recent_improvements")
    val recentImprovements: Int = 0,

    @SerialName("first_workout_date")
    val firstWorkoutDate: String? = null,

    @SerialName("last_workout_date")
    val lastWorkoutDate: String? = null,

    @SerialName("consistency_score")
    val consistencyScore: Float = 0.0f,

    @SerialName("workout_frequency")
    val workoutFrequency: WorkoutFrequency? = null
)

@Serializable
data class WeightRecord(
    @SerialName("exercise_name")
    val exerciseName: String,

    @SerialName("weight")
    val weight: Double,

    @SerialName("reps")
    val reps: Int,

    @SerialName("date")
    val date: String
)

@Serializable
data class PersonalRecord(
    @SerialName("exercise_name")
    val exerciseName: String,

    @SerialName("type")
    val type: String,

    @SerialName("value")
    val value: Double,

    @SerialName("date_achieved")
    val dateAchieved: String,

    @SerialName("previous_record")
    val previousRecord: Double? = null
)

@Serializable
data class WorkoutFrequency(
    @SerialName("weekly_days")
    val weeklyDays: Map<String, Int> = emptyMap(),

    @SerialName("monthly_weeks")
    val monthlyWeeks: Map<String, Int> = emptyMap(),

    @SerialName("hourly_distribution")
    val hourlyDistribution: Map<Int, Int> = emptyMap()
)

@Serializable
data class UserStatsResponse(
    val success: Boolean,
    val stats: UserStats?,
    val message: String? = null
)

@Serializable
data class PeriodStats(
    @SerialName("period_type")
    val periodType: String,

    @SerialName("period_label")
    val periodLabel: String,

    @SerialName("workouts_count")
    val workoutsCount: Int,

    @SerialName("total_duration")
    val totalDuration: Int,

    @SerialName("average_duration")
    val averageDuration: Int,

    @SerialName("most_trained_muscle_group")
    val mostTrainedMuscleGroup: String? = null,

    @SerialName("improvement_percentage")
    val improvementPercentage: Float? = null,

    @SerialName("new_records")
    val newRecords: Int = 0
)

@Serializable
data class StatsComparison(
    @SerialName("current_period")
    val currentPeriod: PeriodStats,

    @SerialName("previous_period")
    val previousPeriod: PeriodStats,

    @SerialName("improvement_percentage")
    val improvementPercentage: Float,

    @SerialName("trend")
    val trend: String
)

@Serializable
data class Achievement(
    val id: Int,
    val name: String,
    val description: String,
    @SerialName("icon_name")
    val iconName: String,
    @SerialName("is_unlocked")
    val isUnlocked: Boolean,
    @SerialName("unlock_date")
    val unlockDate: String? = null,
    @SerialName("progress_current")
    val progressCurrent: Int = 0,
    @SerialName("progress_target")
    val progressTarget: Int = 100
)
