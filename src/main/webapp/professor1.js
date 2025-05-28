document.addEventListener("DOMContentLoaded", () => {
	
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
	
	let homePage = document.getElementById("home-page")
	let examPage = document.getElementById("exam-page");
	let enrolledStudentsPage = document.getElementById("enrolled-students-page");
	let formGradePage = document.getElementById("form-grade-page");
	let reportPage = document.getElementById("report-page");
	let allReportsPage = document.getElementById("allreports-page");
	let multiInsertPage = document.getElementById("multi-insert-page");
	const currentSection = sessionStorage.getItem("currentSection");
	const id = sessionStorage.getItem("selectedId");
		
  	//Start page
	homePage.style.display = "none";
	examPage.style.display = "none";
	enrolledStudentsPage.style.display = "none";
	formGradePage.style.display = "none";
	reportPage.style.display = "none";
	allReportsPage.style.display = "none";
	multiInsertPage.style.display = "none";
	
	if(currentSection){
		document.getElementById(currentSection).style.display = "block";
		switch(currentSection){
			case "exam-page": 
				loadExaminations(id);
				break;
			case "enrolled-students-page":
				loadEnrolledStudents(id);
			case "allreports-page":
				loadAllReports();
			case "report-page": 
				loadReport(id);
			default:
				loadCourses();
				break;
		}
	}else{
		homePage.style.display = "block";
		loadCourses();
	}
	
	//back buttons
	document.getElementById("backbutton-exam").addEventListener("click", () => {
	  sessionStorage.setItem("currentSection", "home-page");
	  //homePage.style.display = "block";
	  window.location.reload(); // o cambia direttamente visibilità
	});
	document.getElementById("backbutton-enrolled-students").addEventListener("click", () => {
	  sessionStorage.setItem("currentSection", "exam-page");//da sistemare 
	  window.location.reload();
  	});
	document.getElementById("backbutton-grade-form").addEventListener("click", () => {
	  //sessionStorage.setItem("currentSection", "enrolled-students-page");
	  window.location.reload();
	});
	document.getElementById("backbutton-report").addEventListener("click", () => {
	  sessionStorage.setItem("currentSection", "enrolled-students-page");//da sistemare 
	  window.location.reload();
  	});
	document.getElementById("backbutton-allreports").addEventListener("click", () => {
	  sessionStorage.setItem("currentSection", "home-page");
	  window.location.reload();
  	});
		
	document.getElementById("allreport-button-home").addEventListener("click", () =>{
		homePage.style.display = "none";
		allReportsPage.style.display = "block";
		loadAllReports();
	});
	document.getElementById("allreport-button-report").addEventListener("click", () =>{
		reportPage.style.display = "none";
		allReportsPage.style.display = "block";
		loadAllReports();
	});
	document.getElementById("multiinsertbutton").addEventListener("click", () =>{
		multiInsertPage.style.display = "block";
		renderMultiInsert();
	});
	document.getElementById("send-multiple-grade-button").addEventListener("click", () =>{
		multiInsertPage.style.display = "none";
		updateMultiGrade();
	});
});

let enrolledStudents = [];

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
		document.getElementById("course_name").textContent = course.name;
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
      <td><button class="btn btn-small view-students-btn" data-id="${examination.idExam}">Visualizza Iscritti</button></td>
    `;
    tr.querySelector(".view-students-btn").addEventListener("click", () => {
		const examinationId = event.target.dataset.id;
		sessionStorage.setItem("selectedId", examinationId);
		sessionStorage.setItem("currentSection", "enrolled-students-page");
		document.getElementById("enrolled-students-page").style.display = "block";
		document.getElementById("exam-page").style.display = "none";
		loadEnrolledStudents(examination.idExam);		
    });
    tbody.appendChild(tr);
  });
}

function loadEnrolledStudents(id) {
  makeCall("GET", "GetEnrolledStudent?id=" + id, null, (req) => {
    if (req.readyState === XMLHttpRequest.DONE) {
      if (req.status === 200) {
        const data = JSON.parse(req.responseText);
		const course = data.course;
		const examination = data.examination;
		const students = data.students;
		enrolledStudents = data.students;
		document.getElementById("course-exam").textContent = course.name + " : " + examination.date;
		if (students.length === 0) {
		  document.getElementById("show_enrolled").style.display = "none";
		  document.getElementById("no_show_enrolled").style.display = "block";
		} else {
		  document.getElementById("no_show_enrolled").style.display = "none";
		  renderStudents(students);
		}
      } else {
		document.getElementById("error-message").innerHTML = "<p>Errore nel caricamento degli studenti iscritti</p>";
      }
    }
  });
}

function renderStudents(students) {
  const tbody = document.getElementById("students-table-body");
  tbody.innerHTML = "";
  students.forEach(student => {
    const tr = document.createElement("tr");
    tr.innerHTML = `
      <td>${student.idStudent}</td>
	  <td>${student.surname}</td>
	  <td>${student.name}</td>
	  <td>${student.email}</td>
	  <td>${student.degree}</td>
	  <td>${student.grade}</td>
	  <td>${student.evaluationState}</td>
      <td><button class="btn btn-small view-students-btn" data-id="${student.idStudent}">Modifica</button></td>
    `;
    tr.querySelector(".view-students-btn").addEventListener("click", () => {
		const studentId = event.target.dataset.id;
		const examinationId = sessionStorage.getItem("selectedId");
		//sessionStorage.setItem("selectedId", studentId);
		//sessionStorage.setItem("currentSection", "enrolled-students-page");
		document.getElementById("form-grade-page").style.display = "block";
		document.getElementById("enrolled-students-page").style.display = "none";
		loadGradeForm(examinationId, studentId);
    });
    tbody.appendChild(tr);
  });
}

function loadAllReports() {
  makeCall("GET", "GetAllReports", null, (req) => {
    if (req.readyState === XMLHttpRequest.DONE) {
      if (req.status === 200) {
        const reports = JSON.parse(req.responseText);
		if (reports.length === 0) {
		  document.getElementById("show_reports").style.display = "none";
		  document.getElementById("no_show_reports").style.display = "block";
		} else {
		  document.getElementById("no_show_reports").style.display = "none";
		  renderAllReports(reports);
		}
      } else {
		document.getElementById("error-message").innerHTML = "<p>Errore nel caricamento degli studenti iscritti</p>";
      }
    }
  });
}

function renderAllReports(reports) {
  const tbody = document.getElementById("reports-table-body");
  tbody.innerHTML = "";
  reports.forEach(report => {
    const tr = document.createElement("tr");
    tr.innerHTML = `
      <td>${report.courseName}</td>
	  <td>${report.examinationDate}</td>
	  <td>${report.timestamp}</td>
      <td><button class="btn btn-small view-report-btn" data-id="${report.idReport}">Vai al verbale</button></td>
    `;
    tr.querySelector(".view-report-btn").addEventListener("click", () => {
		const reportId = event.target.dataset.id;
		sessionStorage.setItem("selectedId", reportId);
		sessionStorage.setItem("currentSection", "report-page");
		document.getElementById("report-page").style.display = "block";
		document.getElementById("allreports-page").style.display = "none";
		loadReport(reportId);
		
    });
    tbody.appendChild(tr);
  });
}

function loadReport(id) {
  makeCall("GET", "GetReport?id=" + id, null, (req) => {
    if (req.readyState === XMLHttpRequest.DONE) {
      if (req.status === 200) {
        const data = JSON.parse(req.responseText);
		const report = data.report;
		const reportRaws = data.reportRaws;
		document.getElementById("report-courseName").textContent = report.courseName;
		document.getElementById("report-examinationDate").textContent = report.examinationDate;
		document.getElementById("report-timestamp").textContent = report.timestamp;
		/*if (reports.length === 0) {
		  document.getElementById("show_reports").style.display = "none";
		  document.getElementById("no_show_reports").style.display = "block";
		} else {
		  document.getElementById("no_show_reports").style.display = "none";
		  renderAllReports(reports);
		}*/
		renderReport(reportRaws);
      } else {
		document.getElementById("error-message").innerHTML = "<p>Errore nel caricamento degli studenti iscritti</p>";
      }
    }
  });
}

function renderReport(reportRaws) {
  const tbody = document.getElementById("report-table-body");
  tbody.innerHTML = "";
  reportRaws.forEach(student => {
    const tr = document.createElement("tr");
    tr.innerHTML = `
      <td>${student.idStudent}</td>
	  <td>${student.name}</td>
	  <td>${student.surname}</td>
	  <td>${student.grade}</td>
    `;
    tbody.appendChild(tr);
  });
}

function loadGradeForm(idE,idS) {
  makeCall("GET", "GoToGradeForm?idExamination=" + idE + "&idStudent=" + idS, null, (req) => {
    if (req.readyState === XMLHttpRequest.DONE) {
      if (req.status === 200) {
        const data = JSON.parse(req.responseText);
		const idExamination = data.idExamination;
		const student = data.student;
		document.getElementById("student-id").textContent = student.idStudent;
		document.getElementById("student-surname").textContent = student.surname;
		document.getElementById("student-name").textContent = student.name;
      } else {
		document.getElementById("error-message").innerHTML = "<p>Errore nel caricamento degli studenti iscritti</p>";
      }
    }
  });
}

function renderMultiInsert() {
	const tbody = document.getElementById("multi-insert-table-body");
	const notInsertGrade = enrolledStudents.filter(s => s.evaluationState === "non-inserito");
	tbody.innerHTML = "";
	notInsertGrade.forEach(student => {
		const tr = document.createElement("tr");
		tr.innerHTML = `
		  <td>${student.idStudent}</td>
		  <td>${student.surname}</td>
		  <td>${student.name}</td>
		  <td>${student.email}</td>
		  <td>${student.degree}</td>
		  <td>
		    <input type="hidden" name="idStudent" value="${student.idStudent}" data-idexamination="${student.idExamination}">
		    <select name="grade" required data-idstudent="${student.idStudent}" data-idexamination="${student.idExamination}">
		      <option value="">-- Seleziona voto --</option>
		      <option value="-1">Assente</option>
		      <option value="-2">Rimandato</option>
		      <option value="-3">Riprovato</option>
		      ${[...Array(13)].map((_, i) => `<option value="${18 + i}">${18 + i}</option>`).join("")}
		      <option value="31">30L</option>
		    </select>
		  </td>
		`;
		tbody.appendChild(tr);
	});
}

/*
function updateMultiGrade() {
	const selects = document.querySelectorAll(".grade-select");
	let insert = 0;
	let total = 0;
		
	selects.forEach(select => {
		  const idStudent = select.dataset.idstudent;
		  const idExamination = select.dataset.idexamination;
		  const grade = select.value;
		
		  if (grade !== "") {
			total++;
			const form = document.createElement("form");
			const fields = [
				{ name: "idStudent", value: idStudent },
				{ name: "idExamination", value: idExamination },
				{ name: "grade", value: grade }
			];
			  
			  fields.forEach(f => {
			    const input = document.createElement("input");
			    input.type = "hidden";
			    input.name = f.name;
			    input.value = f.value;
			    form.appendChild(input);
			  });

			makeCall("POST", "ModifyGrade", form, (req) => {
				if (req.readyState === XMLHttpRequest.DONE) {
			    	if (req.status === 200) {
						insert++;
			    	} else {
			      		alert("Errore nell'inserimento dei voti.");
			    	}
					if(insert === total) {
						alert("Voti inseriti con successo!");
						loadEnrolledStudents(idExamination); 
					}
				}		
			}, true); 
		  }
	});
	
	if (total === 0) {
	  alert("Inserisci almeno un voto.");
	}  	
}
*/

function updateMultiGrade() {
	  const selects = document.querySelectorAll("select[name='grade']");
	  const multiGrade = [];

	  selects.forEach(select => {
	    const grade = select.value;
	    if (grade !== "") {
	      const idStudent = select.dataset.idstudent;
	      const idExamination = select.dataset.idexamination;

	      multiGrade.push({
	        idStudent,
	        idExamination,
	        grade
	      });
	    }

	  if (multiGrade.length === 0) {
	    alert("Inserisci almeno un voto.");
	    return;
	  }

	  let completed = 0;
	  multiGrade.forEach(item => {
	    const params = new URLSearchParams(item).toString();
	    makeCall("POST", "ModifyGrade", params, (req) => {
	      if (req.readyState === XMLHttpRequest.DONE) {
	        completed++;
	        if (req.status !== 200) {
	          console.error(`Errore inserimento voto per studente ${item.idStudent}`);
	        }
	        if (completed === multiGrade.length) {
	          alert("Voti inseriti con successo!");
	          loadEnrolledStudents(item.idExamination);
	        }
	      }
	    }, false);
	  });
	});
}
