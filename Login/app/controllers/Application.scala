package controllers

import play.api._
import play.api.data._
import play.api.data.Forms._
import play.api.mvc._

import play.api.mvc.Cookie


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
      user => if(Authenticate(user)) Redirect("mainpage").withCookies(Cookie("user", user._1, Option(3600))) else Ok(views.html.index("Bejelentkezés", errMsg))
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
    val usr =  request.cookies.get("user")

    if(usr != None && usr.get.value.length() != 0) {
      Ok(views.html.mainpage("Főoldal", usr.get.value))
    }
    else {
      errMsg = "Bejelentkezés szükséges."
      BadRequest(views.html.index("Bejelentkezés", errMsg))
    }
  }

}
