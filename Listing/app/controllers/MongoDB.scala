package controllers

import com.mongodb.casbah.Imports._

import scala.collection.mutable.ListBuffer

object MongoDB {
  val mongoClient = MongoClient()

  def getCollection(name: String) : MongoCollection = {
    return mongoClient("db")(name)
  }

  def getElement(mongoCollection: MongoCollection, where: String,what: String) : Option[DBObject] = {
    return mongoCollection.findOne(MongoDBObject(where -> what))
  }

  def getListOfElement(mongoCollection: MongoCollection, piece: Int) : List[models.Product] = {
    val coll = mongoCollection.find()
    val listOfProducts = new ListBuffer[models.Product]
    var i = 0
    for (x <- coll){
      if(i < piece)
        listOfProducts += new models.Product(Int.unbox(x.get("itemno")), String.valueOf(x.get("cikknev")), Long.unbox(x.get("ean")), Int.unbox(x.get("ar")), Int.unbox(x.get("keszlet")))
      i += 1
    }

    return listOfProducts.toList
  }

  def getListOfElementWithSearch(mongoCollection: MongoCollection, name :String) : List[models.Product] = {
    val nameWithRegex = "^" + name.toLowerCase()+".*"
    val coll = mongoCollection.find(MongoDBObject("lowerCaseName" -> nameWithRegex.r))
    val listOfProducts = new ListBuffer[models.Product]

    for (x <- coll){
        listOfProducts += new models.Product(Int.unbox(x.get("itemno")), String.valueOf(x.get("cikknev")), Long.unbox(x.get("ean")), Int.unbox(x.get("ar")), Int.unbox(x.get("keszlet")))
    }

    return listOfProducts.toList.sortWith((a, b) => if(a.name < b.name) true; else false)
  }
}
