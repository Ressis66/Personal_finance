package domain

import mongo4cats.bson.ObjectId

final case class Permissions(id: ObjectId, user:User)
