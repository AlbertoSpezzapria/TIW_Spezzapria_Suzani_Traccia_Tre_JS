document.addEventListener("DOMContentLoaded", () => {
	let homePage = document.getElementById("home-page");
	let coursePage = document.getElementById("course-page");
	let examPage = document.getElementById("exam-page");
	let gradePage = document.getElementById("grade-page");
	const currentSection = sessionStorage.getItem("currentSection");
	const id = sessionStorage.getItem("selectedId");
	
	//Username 
	const username = sessionStorage.getItem("username");
	if (!username) {
	  window.location.href = "login.html";
	} else {
	  document.getElementById("id_username").textContent = username;
	}

	//Logout
	document.getElementById("logoutbutton").addEventListener("click", () => {
	  sessionStorage.clear();
	  window.location.href = "login.html";
	});
	//Home
	document.getElementById("homebutton").addEventListener("click", () => {
		sessionStorage.setItem("currentSection", "home-page");
		window.location.reload(); 
	});
	
	//Start page
	homePage.style.display = "none";
	examPage.style.display = "none";
	gradePage.style.display = "none";
	
	if(currentSection) {
		console.log("Current section: ", currentSection);
		const sectionElement = document.getElementById(currentSection);
		if(sectionElement) {
			sectionElement.style.display = "block";
			switch(currentSection) {
				case "exam-page":
					loadExaminations(id);
					break;
				case "grade-page":
					loadGrade(id);
					break;
				default:
					loadCourses();
					break;
			}
		} else {
			console.error("Elemento non trovato per id:", currentSection);
			homePage.style.display = "block";
			loadCourses();
		}
	}else{
		console.log("Nessuna currentSection, carico Home");
		homePage.style.display = "block";
		loadCourses();
	}
	
	document.getElementById("backbutton-exam").addEventListener("click", () => {
	  sessionStorage.setItem("currentSection", "home-page");
	  window.location.reload(); 
	});

});

let draggedBox = null;
let idEx;

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
		document.getElementById("error-message").innerHTML = "<p>Errore nel caricamento dei corsi</p>";
      }
    }
  });
}

function renderCourses(courses) {
  const tbody = document.getElementById("courses-table-body");
  tbody.innerHTML = "";
  courses.forEach(course => {
    const tr = document.createElement("tr");
    tr.innerHTML = `
      <td>${course.name}</td>
      <td><button class="btn btn-small view-exams-btn" data-id="${course.idCourse}">Visualizza Appelli</button></td>
    `;
    tr.querySelector(".view-exams-btn").addEventListener("click", () => {
	  const courseId = event.target.dataset.id;
	  sessionStorage.setItem("selectedId", courseId);
	  sessionStorage.setItem("currentSection", "exam-page");
	  document.getElementById("exam-page").style.display = "block";
	  document.getElementById("home-page").style.display = "none";
	  loadExaminations(course.idCourse);
    });
    tbody.appendChild(tr);
  });
}

function loadExaminations(id) {
  makeCall("GET", "GetExamination?id=" + id, null, (req) => {
    if (req.readyState === XMLHttpRequest.DONE) {
      if (req.status === 200) {
        const data = JSON.parse(req.responseText);
		const course = data.course;
		const examinations = data.examinations;
		document.getElementById("course-name-exam").textContent = course.name;
		if (examinations.length === 0) {
		  document.getElementById("show_examinations").style.display = "none";
		  document.getElementById("no_show_examinations").style.display = "block";
		} else {
		  document.getElementById("no_show_examinations").style.display = "none";
		  renderExaminations(examinations);
		}
      } else {
		document.getElementById("error-message").innerHTML = "<p>Errore nel caricamento degli appelli</p>";
      }
    }
  });
}

function renderExaminations(examinations) {
  const tbody = document.getElementById("examinations-table-body");
  tbody.innerHTML = "";
  examinations.forEach(examination => {
    const tr = document.createElement("tr");
    tr.innerHTML = `
      <td>${examination.date}</td>
      <td><button class="btn btn-small view-students-btn" data-id="${examination.idExam}">Visualizza Dettagli</button></td>
    `;
    tr.querySelector(".view-students-btn").addEventListener("click", (e) => {
		const examinationId = e.target.dataset.id;
		sessionStorage.setItem("selectedId", examinationId);
		sessionStorage.setItem("currentSection", "exam-page");
		document.getElementById("exam-page").style.display = "none";
		document.getElementById("grade-page").style.display = "block";
		loadGrade(examination.idExam);		
    });
    tbody.appendChild(tr);
  });
}

function loadGrade(id) {
	makeCall("Get", "GetGrade?id=" + id, null, (req) => {
		if (req.readyState === XMLHttpRequest.DONE) {
		  if (req.status === 200) {
		    const data = JSON.parse(req.responseText);
			const course = data.course;
			const examination = data.examination;
			const grade = data.student;
			const enrolled = data.enrolled;
			idEx = examination.idExam;
			document.getElementById("course-name-grade").textContent = course.name;
			if (!enrolled) {
			  // Mostra messaggio "non iscritto"
			  document.getElementById("show-grade").style.display = "none";
			  document.getElementById("no-show-grade").style.display = "block";
			  document.getElementById("not-enrolled").style.display = "block";
			  document.getElementById("enrolled-waiting").style.display = "none";
			  document.getElementById("trash-bin").style.display = "none";
			} else if (!grade || grade.grade === null) {
			  // Mostra messaggio "voto non ancora definito"
			  document.getElementById("show-grade").style.display = "none";
			  document.getElementById("no-show-grade").style.display = "block";
			  document.getElementById("not-enrolled").style.display = "none";
			  document.getElementById("enrolled-waiting").style.display = "block";
			  document.getElementById("trash-bin").style.display = "none";
			} else {
			  // Mostra i dati del voto
			  document.getElementById("show-grade").style.display = "block";
			  document.getElementById("no-show-grade").style.display = "none";
			  renderGrade(grade, id);
			}
			document.getElementById("backbutton-grade").addEventListener("click", () => {
			  sessionStorage.setItem("currentSection", "exam-page");
			  sessionStorage.setItem("selectedId", course.idCourse);
			  window.location.reload(); 
			});
		  } else {
			document.getElementById("error-message").innerHTML = "<p>Errore nel caricamento dei voti</p>";
		  }
		}
		
	})
}

function renderGrade(grade, idExam) {
	sessionStorage.setItem("currentSection", "grade-page");
	
	document.getElementById("student-id").textContent = grade.idStudent;
	document.getElementById("student-surname").textContent = grade.surname;
	document.getElementById("student-name").textContent = grade.name;

	document.getElementById("grade-valid").style.display = "none";
	document.getElementById("grade-failed").style.display = "none";
	document.getElementById("grade-rejected").style.display = "none";

	if (grade.evaluationState === "rifiutato") {
	  document.getElementById("grade-rejected").style.display = "block";
	  document.getElementById("trash-bin").style.display = "none";
	} else if (grade.grade < 18) {
	  document.getElementById("grade-failed").style.display = "block";
	  document.getElementById("trash-bin").style.display = "none";
	} else {
	  const gradeValue = grade.grade === 31 ? "30L" : grade.grade;
	  const rejectButton = document.getElementById("reject-grade-button");

	  document.getElementById("grade-value").textContent = gradeValue;
	  document.getElementById("grade-valid").style.display = "block";
	  document.getElementById("reject-grade-button").style.display = "block";
	  document.getElementById("trash-bin").style.display = "block";


	  rejectButton.onclick = () => {
		const url = "RejectGrade?id="+encodeURIComponent(idExam);
		makeCall("GET", url, null, (req) => {
			if(req.readyState === XMLHttpRequest.DONE) {
				if(req.status === 200) {
					loadGrade(idExam);
				} else {
					alert("Errore nel rifiuto del voto");
				}
			}
		});
	  };
	}
}

const gradeBox = document.getElementById("show-grade");
gradeBox.addEventListener("dragstart", function (e) {
	draggedBox = e.target;
});

const trashBin = document.getElementById("trash-bin");
trashBin.addEventListener("dragover", function(e) {
	e.preventDefault();
});
trashBin.addEventListener("drop", function(e){
	e.preventDefault();
	document.getElementById("confirm-reject").style.display = "block";
});

document.getElementById("cancel-btn").addEventListener("click", () => {
	document.getElementById("confirm-reject").style.display = "none";
	draggedBox = null;
});

document.getElementById("confirm-btn").addEventListener("click", () => {
	document.getElementById("confirm-reject").style.display = "none";
	
	const url = "RejectGrade?id=" + encodeURIComponent(idEx);
	makeCall("GET", url, null, (req) => {
		if (req.readyState === XMLHttpRequest.DONE) {
			if (req.status === 200) {
				loadGrade(idEx);
			} else {
				alert("Errore nel rifiuto del voto");
			}
		}
	});
	draggedBox = null;
});