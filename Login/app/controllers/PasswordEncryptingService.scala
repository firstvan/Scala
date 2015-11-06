package controllers

class PasswordEncryptingService {
  def encrypt(value: String) : String ={
    val md = java.security.MessageDigest.getInstance("SHA-1")
    md.digest(value.getBytes("UTF-8")).map("%02x".format(_)).mkString
  }
}
