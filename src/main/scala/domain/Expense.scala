package domain

import mongo4cats.bson.ObjectId

import java.time.LocalDateTime

case class Expense(
                    _id: ObjectId,
                    createdAt: LocalDateTime,
                    updatedAt: LocalDateTime,
                    amount: BigDecimal,
                    description: String,
                    lineItem: LineItem
                  )
