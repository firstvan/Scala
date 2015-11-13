package controllers

import play.api.data.Forms._
import play.api.data._
import play.api.mvc._

import scala.collection.mutable.ListBuffer


case class SearchName(name: String)

object DefaultValues {
  val Searched :String = ""
  val Size :Int = 5
  val ActualPage :Int = 1
  val MaxPageNumber :Int = 8
}

class Application extends Controller {

  val searchForm = Form(
    mapping(
      "name" -> text
    ) (SearchName.apply) (SearchName.unapply)
  )


  def index = Action {
    val products = getList(DefaultValues.Searched, DefaultValues.Size)
    Ok(views.html.index("Termékek", products._1, DefaultValues.Size, DefaultValues.ActualPage, DefaultValues.MaxPageNumber, products._2)).withCookies(new Cookie("searched", DefaultValues.Searched), new Cookie("listedSize", DefaultValues.Size.toString()))
    //Ok(views.html.index("Termékek")).withCookies(new Cookie("searched", DefaultValues.Searched), new Cookie("listedSize", DefaultValues.Size.toString()))
  }

  def sizedList(elem :Int) = Action { implicit  request =>
    val sname = request.cookies.get("searched").get.value
    var size = elem

    val products = getList(sname, size)

    Ok(views.html.index("Termékek", products._1, size, DefaultValues.ActualPage, DefaultValues.MaxPageNumber, products._2)).withCookies(new Cookie("listedSize", size.toString()))
  }

  def search = Action { implicit request =>
    searchForm.bindFromRequest.fold(
      formWithError => Redirect("Not succes"),
      name => {
        val size = request.cookies.get("listedSize").get.value.toInt
        val products = getList(name.name, size)
        Ok(views.html.index("Termékek", products._1, size, DefaultValues.ActualPage, DefaultValues.MaxPageNumber, products._2)).withCookies(new Cookie("searched", name.name))
      }
    )
  }

  def getList(name :String = DefaultValues.Searched, size :Int = DefaultValues.Size, start :Int = DefaultValues.ActualPage) : (List[models.Product], Int) = {
    val coll = MongoDB.getCollection("termekek")
    val products = MongoDB.getListOfElementWithSearch(coll, name)
    var p2 = new ListBuffer[models.Product]()

    var counter = (start - 1) * size
    var max = counter + size

    if(max > products.size){
      max = products.size
    }

    while(counter < max ){
      p2 += products(counter)
      counter += 1
    }

    return (p2.toList, products.size)
  }

  def paging(page :Int) = Action {implicit request =>
    val sname = request.cookies.get("searched").get.value
    val size = request.cookies.get("listedSize").get.value.toInt
    val products = getList(sname, size, page)
    Ok(views.html.index("Termékek", products._1, size, page, DefaultValues.MaxPageNumber, products._2))
  }
}
