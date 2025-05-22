function checkSession() {
  if (!sessionStorage.getItem("username")) {
    window.location.href = "login.html";
  }
}