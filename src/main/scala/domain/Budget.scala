package domain

import mongo4cats.bson.ObjectId

import java.time.LocalDateTime

case class Budget (
                 _id: ObjectId,
                 createdAt: LocalDateTime,
                 updatedAt: LocalDateTime,
                 title: String,
                 amount: BigDecimal,
                 description: String,
                 lineItems: List[LineItem],
                 user: User
                 )
