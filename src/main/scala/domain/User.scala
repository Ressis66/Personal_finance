package domain

import mongo4cats.bson.ObjectId

import java.time.LocalDateTime

final case class User (
                  _id: ObjectId,
                  createdAt: LocalDateTime,
                  updatedAt: LocalDateTime,
                  name: String
            /*      categories: List[BudgetCategory],
                  budgets: List[Budget]*/
                )


