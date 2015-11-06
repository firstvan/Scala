package controllers

import com.mongodb.casbah.Imports._

object MongoDB {
  val mongoClient = MongoClient()

  def getCollection(name: String) : MongoCollection = {
    return mongoClient("db")(name)
  }

  def getElement(mongoCollection: MongoCollection, where: String,what: String) : Option[DBObject] = {
    return mongoCollection.findOne(MongoDBObject(where -> what))
  }
}
