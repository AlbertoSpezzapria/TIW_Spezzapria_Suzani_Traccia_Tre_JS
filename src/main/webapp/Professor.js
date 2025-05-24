document.addEventListener("DOMContentLoaded", () => {
	let examPage = document.getElementById("exam-page");
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
	examPage.style.display = "none";
	document.getElementById("view-exams-btn").addEventListener("click", () => {
		examPage.style.display = "block";
		loadExaminations();
	});
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
      <td><button id="view-exams-btn" class="btn btn-small" data-id="${course.idCourse}">Visualizza Appelli</button></td>
    `;
    tr.querySelector(".view-exams-btn").addEventListener("click", () => {
      window.location.href = "/GetExamination?id=" + course.idCourse;
    });
    tbody.appendChild(tr);
  });
}

function loadExaminations() {
  makeCall("GET", "GetExamination", null, (req) => {
    if (req.readyState === XMLHttpRequest.DONE) {
      if (req.status === 200) {
        const course = JSON.parse(req.responseText);
		const examinations = JSSON.parse(req.responseText);
		document.getElementById("course_name").textContent = course;
		if (examinations.length === 0) {
		  document.getElementById("show_examinations").style.display = "none";
		  document.getElementById("no_show_examinations").style.display = "block";
		} else {
		  document.getElementById("no_show_examinations").style.display = "none";
		  renderExaminations(examinations);
		}
      } else {
		document.getElementById("show_courses").innerHTML = "<p>Errore nel caricamento degli appelli.</p>";
      }
    }
  });
}

function renderExaminations(examinations) {
  const tbody = document.getElementById("examinations-table-body");
  tbody.innerHTML = ""; // pulisci
  examinations.forEach(examination => {
    const tr = document.createElement("tr");
    tr.innerHTML = `
      <td>${examination.date}</td>
      <td><button class="btn btn-small view-students-btn" data-id="${examination.idExamination}">Visualizza Appelli</button></td>
    `;
    tr.querySelector(".view-students-btn").addEventListener("click", () => {
      window.location.href = "/GetExamination?id=" + course.idCourse;
    });
    tbody.appendChild(tr);
  });
}