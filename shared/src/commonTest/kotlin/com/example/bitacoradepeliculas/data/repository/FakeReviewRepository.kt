package com.example.bitacoradepeliculas.data.repository

import com.example.bitacoradepeliculas.data.model.MovieLog
import com.example.bitacoradepeliculas.domain.model.DataError

class FakeReviewRepository : ReviewRepository {

    val reviews = mutableListOf<MovieLog>()
    var shouldFail = false
    var errorToReturn: DataError = DataError.Unknown("Error de prueba")
    var getMyReviewsCallCount = 0

    override suspend fun getMyReviews(): Result<List<MovieLog>> {
        getMyReviewsCallCount++
        return if (shouldFail) {
            Result.failure(errorToReturn)
        } else {
            // Sort by log_date desc, id desc (mimicking Supabase order query)
            val sorted = reviews.sortedWith(
                compareByDescending<MovieLog> { it.reviewDate }
                    .thenByDescending { it.id }
            )
            Result.success(sorted)
        }
    }

    override suspend fun createReview(review: MovieLog): Result<Unit> {
        reviews.add(review)
        return Result.success(Unit)
    }

    override suspend fun getReviewById(id: Long): Result<MovieLog?> {
        return Result.success(reviews.find { it.id == id })
    }

    override suspend fun updateReview(review: MovieLog): Result<Unit> {
        val index = reviews.indexOfFirst { it.id == review.id }
        if (index != -1) {
            reviews[index] = review
        }
        return Result.success(Unit)
    }

    override suspend fun deleteReview(id: Long): Result<Unit> {
        reviews.removeAll { it.id == id }
        return Result.success(Unit)
    }
}
