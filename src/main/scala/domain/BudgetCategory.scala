package domain

import mongo4cats.bson.ObjectId

import java.time.LocalDateTime

case class BudgetCategory(
                           _id: ObjectId,
                           createdAt: LocalDateTime,
                           updatedAt: LocalDateTime,
                           name: String,
                           user: User
                         )
