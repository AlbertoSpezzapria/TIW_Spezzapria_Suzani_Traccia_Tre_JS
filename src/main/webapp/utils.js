/**
 * AJAX call management
 */

function makeCall(method, url, formElement, cback, reset = true) {
    var req = new XMLHttpRequest(); 
    req.onreadystatechange = function() {
      cback(req)
    }; 
    req.open(method, url);
    if (formElement == null) {
      req.send();
    } else {
		req.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
		let formData = new FormData(formElement);
		let encodedData = new URLSearchParams(formData).toString();
		req.send(encodedData);    }
    if (formElement !== null && reset === true) {
      formElement.reset();
    }
  }