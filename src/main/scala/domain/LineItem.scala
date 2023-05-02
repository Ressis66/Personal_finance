package domain

import mongo4cats.bson.ObjectId

import java.time.LocalDateTime

case class LineItem(
                     _id: ObjectId,
                     createdAt: LocalDateTime,
                     updatedAt: LocalDateTime,
                     budgetCategory: BudgetCategory,
                     projectedAmount: BigDecimal = BigDecimal(0),
                     totalAmountSpent: BigDecimal= BigDecimal(0),
                     expenses: List[Expense],
                     budget: Budget
                   )
