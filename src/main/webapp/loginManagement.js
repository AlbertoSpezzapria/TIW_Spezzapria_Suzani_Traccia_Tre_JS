/**
 * login management
 */

document.addEventListener("DOMContentLoaded", function() {
	
	document.getElementById("loginbutton").addEventListener('click', (e) => {
	  var form = e.target.closest("form");
	  if (form.checkValidity()) {
	    makeCall("POST", 'CheckLogin', e.target.closest("form"),
	      function(x) {
	        if (x.readyState == XMLHttpRequest.DONE) {
	          var message = x.responseText;
			console.log("Response received:", message);
	          switch (x.status) {
	            case 200:
				const resp = JSON.parse(message);
				console.log("Risposta:", resp);
				
				sessionStorage.setItem('username', resp.username);
				sessionStorage.setItem('role', resp.role);
				
				if(resp.role === 'professor') {
					window.location.href = "HomeProfessor.html";
				} else {
					window.location.href = "HomeStudent.html";					
				}
	              break;
	            case 400: // bad request
	              document.getElementById("errormessage").textContent = message;
	              break;
	            case 401: // unauthorized
	                document.getElementById("errormessage").textContent = message;
	                break;
	            case 500: // server error
	          	document.getElementById("errormessage").textContent = message;
	              break;
	          }
	        }
	      });
	  } else {
	  	 form.reportValidity();
	  }
	});
	
})