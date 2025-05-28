/**
 * AJAX call management
 */

function makeCall(method, url, body, cback, reset = true) {
	var req = new XMLHttpRequest(); 
	req.onreadystatechange = () => cback(req); 
	req.open(method, url);

	if (body == null) {
	  req.send();
	} else if (body instanceof HTMLFormElement) {
	  req.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
	  let formData = new FormData(body);
	  let encodedData = new URLSearchParams(formData).toString();
	  req.send(encodedData); 
	  if (reset) body.reset();
	} else {
	  // Stai passando un oggetto (es. JSON)
	  req.setRequestHeader("Content-Type", "application/json");
	  req.send(body); 
	}
  }