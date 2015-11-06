package controllers

import java.util

import play.api._
import play.api.data._
import play.api.data.Forms._
import play.api.mvc._

import play.api.mvc.Cookie

import scala.collection.mutable.ArrayBuffer

object HashTable {
  val list :ArrayBuffer[String] = new ArrayBuffer[String]()
}

class Application extends Controller {

  val loginForm = Form(
    tuple(
      "usrname" -> text,
      "pwd" -> text
    )
  )

  var errMsg: String = ""

  def index = Action {
    errMsg = ""
    Ok(views.html.index("Bejelentkezés", errMsg))
  }

  def Login = Action { implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors => Redirect("Not succes"),
      user => if(Authenticate(user)) {
        Redirect("mainpage").withSession("user" -> user._1)
      } else Ok(views.html.index("Bejelentkezés", errMsg))
    )
  }

  def Authenticate(user: (String, String)) : Boolean = {
    val usersCollection = MongoDB.getCollection("users")
    val findUser = MongoDB.getElement(usersCollection, "username", user._1)

    if(findUser != None) {
      val encryptor = new PasswordEncryptingService
      val passSHA1 = encryptor.encrypt(user._2)
      val usr = findUser.get
      if(usr.get("password") == passSHA1){

        return true;
      }
      errMsg = "Hibás jelszó"
    } else {
      errMsg = "A felhasznóló nem található."
    }

    return false;
  }

  def LoadMainPage = Action { implicit request =>
    var name :String = null
    request.session.get("user").map {
      user => name = user
    }.getOrElse {
      name = null
    }

    if(name != null) {
      Ok(views.html.mainpage("Főoldal", name))
    }
    else {
      errMsg = "Bejelentkezés szükséges."
      BadRequest(views.html.index("Bejelentkezés", errMsg))
    }
  }

}
