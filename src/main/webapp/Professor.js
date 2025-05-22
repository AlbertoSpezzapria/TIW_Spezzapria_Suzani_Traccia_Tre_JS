document.addEventListener("DOMContentLoaded", () => {
	const username = sessionStorage.getItem("username");
	if (!username) {
	  window.location.href = "login.html";
	} else {
	  document.getElementById("id_username").textContent = username;
	}

	// 2. Logout
	document.getElementById("logoutbutton").addEventListener("click", () => {
	  sessionStorage.clear();
	  window.location.href = "login.html";
	});
  
	loadCourses();
});

function loadCourses() {
  makeCall("GET", "GetCourses", null, (req) => {
    if (req.readyState === XMLHttpRequest.DONE) {
      if (req.status === 200) {
        const courses = JSON.parse(req.responseText);
		if (courses.length === 0) {
		  document.getElementById("show_courses").style.display = "none";
		  document.getElementById("no_show_courses").style.display = "block";
		} else {
		  document.getElementById("no_show_courses").style.display = "none";
		  renderCourses(courses);
		}
      } else {
		document.getElementById("show_courses").innerHTML = "<p>Errore nel caricamento dei corsi.</p>";
      }
    }
  });
}

function renderCourses(courses) {
  const tbody = document.getElementById("courses-table-body");
  tbody.innerHTML = ""; // pulisci
  courses.forEach(course => {
    const tr = document.createElement("tr");
    tr.innerHTML = `
      <td>${course.name}</td>
      <td><button class="btn btn-small view-exams-btn" data-id="${course.idCourse}">Visualizza Appelli</button></td>
    `;
    tr.querySelector(".view-exams-btn").addEventListener("click", () => {
      window.location.href = "esami.html?id=" + course.idCourse;
    });
    tbody.appendChild(tr);
  });
}