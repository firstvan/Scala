function check(){
    var usr = document.getElementById("usrname");
    var pwd = document.getElementById("pwd");

    if(usr.value.length == 0) {
        alert("Nem adott meg felhasználónevet.");
        return false;
    }

    if(pwd.value.length == 0) {
        alert("Nem adott meg jelszót.");
        return false;
    }

    return true;
}