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
	
	//Home
	document.getElementById("homebutton").addEventListener("click", () => {
		sessionStorage.setItem("currentSection", "home-page");
		window.location.reload(); 
	});
	
	let homePage = document.getElementById("home-page")
	let examPage = document.getElementById("exam-page");
	let enrolledStudentsPage = document.getElementById("enrolled-students-page");
	let formGradePage = document.getElementById("form-grade-page");
	let reportPage = document.getElementById("report-page");
	let allReportsPage = document.getElementById("allreports-page");
	let multiInsertPage = document.getElementById("multi-insert-page");
	let multiOverlay = document.getElementById("modal-overlay");

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
	  window.location.reload(); 
	});
	document.getElementById("backbutton-grade-form").addEventListener("click", () => {
	  window.location.reload();
	});
	document.getElementById("backbutton-allreports").addEventListener("click", () => {
	  sessionStorage.setItem("currentSection", "home-page");
	  window.location.reload();
  	});	
	
	//allReport buttons
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
		multiOverlay.style.display = "block";
		document.getElementById("modal-overlay").style.display = "block";
		renderMultiInsert();
	});
	document.getElementById("send-multiple-grade-button").addEventListener("click", () =>{
		multiInsertPage.style.display = "none";
		updateMultiGrade();
	});
	document.getElementById("backbutton-multi").addEventListener("click", () => {
		multiInsertPage.style.display = "none";
		multiOverlay.style.display = "none";
		//updateMultiGrade();
	});
	
	//modify grade button
	document.getElementById("update-grade-button").addEventListener('click', (e) => {
	  var form = e.target.closest("form");
	  if (form.checkValidity()) {
	    makeCall("POST", 'ModifyGrade', e.target.closest("form"),
	      function(req) {
	        if (req.readyState == XMLHttpRequest.DONE) {
	          if (req.status === 200) {
				//alert("Voto inserito con successo!");
			  	window.location.reload();
	          }else{
				console.error(`Errore inserimento voto per studente ${req.status} ${req.responseText}`);
			  }
	        }
	      },false);
	  } else {
	  	 form.reportValidity();
	  }
	});

	document.querySelectorAll("th[data-column]").forEach(th => {
		th.addEventListener("click", () => {
	    	const column = th.getAttribute("data-column");
	      	if (currentSortColumn === column) {
	        	sortAscending = !sortAscending;
	      	} else {
	        	currentSortColumn = column;
	        	sortAscending = true;
	      	}
	      	sortStudents(column);
			updateSortArrows();
		});
	});
	
	document.querySelectorAll("th[data-column]").forEach(th => {
	  const column = th.getAttribute("data-column");
	  columnLabels[column] = th.textContent.trim();
	});
	
});

let enrolledStudents = [];
let currentSortColumn = null;
let sortAscending = true;
const columnLabels = {};

const gradeOrder = [0, -1, -2, -3];
for (let i = 18; i <= 31; i++) gradeOrder.push(i);


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
    tr.querySelector(".view-exams-btn").addEventListener("click", (e) => {
	  const courseId = e.target.dataset.id;
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
    tr.querySelector(".view-students-btn").addEventListener("click", (e) => {
		const examinationId = e.target.dataset.id;
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
		idExam = examination.idExam;
		const students = data.students;
		enrolledStudents = data.students;
		document.getElementById("course-exam").textContent = course.name + " : " + examination.date;
		if (students.length === 0) {
		  document.getElementById("show_enrolled").style.display = "none";
		  document.getElementById("no_show_enrolled").style.display = "block";
		} else {
		  document.getElementById("no_show_enrolled").style.display = "none";
		  renderStudents();
		}
		document.getElementById("backbutton-enrolled-students").addEventListener("click", () => {
		  sessionStorage.setItem("currentSection", "exam-page");
		  sessionStorage.setItem("selectedId", course.idCourse);
		  window.location.reload();
	  	});
		document.getElementById("publish-button").addEventListener('click', () => { 
		   	makeCall("GET", "PublishGrade?id=" + examination.idExam, null, (req) => {
		        if (req.readyState == XMLHttpRequest.DONE) {
		          if (req.status === 200) {
					//alert("Voti inseriti con successo!");
				  	window.location.reload();
		          }else{
					console.error(`Errore pubblicazione voto per studente ${req.status} ${req.responseText}`);
				  }
		        }
		      }
		    );
		});
		document.getElementById("report-button").addEventListener('click', () => { 
		   	makeCall("GET", "CreateReport?id=" + examination.idExam, null, (req) => {
		        if (req.readyState == XMLHttpRequest.DONE) {
		          if (req.status === 200) {
					//alert("Voti inseriti con successo!");
					const idReport = JSON.parse(req.responseText);
					sessionStorage.setItem("selectedId", idReport);
					sessionStorage.setItem("currentSection", "report-page");
				  	window.location.reload();
		          }else{
					console.error(`Errore pubblicazione voto per studente ${req.status} ${req.responseText}`);
				  }
		        }
		      });
		});
      } else {
		document.getElementById("error-message").innerHTML = "<p>Errore nel caricamento degli studenti iscritti</p>";
      }
    }
  });
}
	


function renderStudents() {
  const tbody = document.getElementById("students-table-body");
  tbody.innerHTML = "";
  enrolledStudents.forEach(student => {
    const tr = document.createElement("tr");
	let grade;
	switch(student.grade){
		case -1:
			grade = "Assente";
			break;
		case -2:
			grade = "Rimandato";
			break;
		case -3:
			grade = "Riprovato";
			break;
		case 0:
			grade = "-";
			break;
		case 31:
			grade = "30L";
			break;
		default:
			grade = student.grade;
			break;
	}
	if(student.evaluationState === "non-inserito" || student.evaluationState === "inserito"){
		tr.innerHTML = `
	      <td>${student.idStudent}</td>
		  <td>${student.surname}</td>
		  <td>${student.name}</td>
		  <td>${student.email}</td>
		  <td>${student.degree}</td>
		  <td>${grade}</td>
		  <td>${student.evaluationState}</td>
	      <td><button class="btn btn-small view-students-btn" data-id="${student.idStudent}">Modifica</button></td>
	    `;
	    tr.querySelector(".view-students-btn").addEventListener("click", (e) => {
			const studentId = e.target.dataset.id;
			const examinationId = sessionStorage.getItem("selectedId");
			document.getElementById("form-grade-page").style.display = "block";
			document.getElementById("enrolled-students-page").style.display = "none";
			loadGradeForm(examinationId, studentId);
	    });
	}else{
		tr.innerHTML = `
	      <td>${student.idStudent}</td>
		  <td>${student.surname}</td>
		  <td>${student.name}</td>
		  <td>${student.email}</td>
		  <td>${student.degree}</td>
		  <td>${grade}</td>
		  <td>${student.evaluationState}</td>
		  <td> <td>	      
	    `;
	}
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
    tr.querySelector(".view-report-btn").addEventListener("click", (e) => {
		const reportId = e.target.dataset.id;
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
		document.getElementById("backbutton-report").addEventListener("click", () => {
		  sessionStorage.setItem("currentSection", "enrolled-students-page");
		  sessionStorage.setItem("selectedId", report.idExam);
		  window.location.reload();
	  	});
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
	let grade;
	switch(student.grade){
		case -1:
			grade = "Assente";
			break;
		case -2:
			grade = "Rimandato";
			break;
		case -3:
			grade = "Riprovato";
			break;
		case 0:
			grade = "-";
			break;
		case 31:
			grade = "30L";
			break;
		default:
			grade = student.grade;
			break;
	}
    tr.innerHTML = `
      <td>${student.idStudent}</td>
	  <td>${student.name}</td>
	  <td>${student.surname}</td>
	  <td>${grade}</td>
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
		document.getElementById("idStudent").value = student.idStudent;
		document.getElementById("idExamination").value = idExamination;
		document.getElementById("grade").value = student.grade;
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
		    <select name="grade" required data-idstudent="${student.idStudent}" data-idexamination="${idExam}">
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
  });

  if (multiGrade.length === 0) {
    alert("Inserisci almeno un voto.");
	document.getElementById("multi-insert-page").style.display = "block";
	//document.getElementById("modal-overlay").style.display = "none";

    return;
  }

  let completed = 0;
  let errors = 0;

  multiGrade.forEach(item => {
	document.getElementById("idStudent-mg").value = item.idStudent;
	document.getElementById("idExamination-mg").value = item.idExamination;
	document.getElementById("grade-mg").value = item.grade;
	var form = document.getElementById("multi-grade-form");
    makeCall("POST", "ModifyGrade", form, (req) => {
      if (req.readyState === XMLHttpRequest.DONE) {
        completed++;
        if (req.status !== 200) {
          console.error(`Errore inserimento voto per studente ${item.idStudent}`);
          errors++;
        }

        if (completed === multiGrade.length) {
          if (errors === 0) {
            //alert("Voti inseriti con successo!");
            window.location.reload();
          } else {
            alert("Alcuni voti non sono stati inseriti correttamente. Controlla la console.");
          }
        }
      }
    }, false); 
  });
}

function sortStudents(column) {
  enrolledStudents.sort((a, b) => {
    let valA = (a[column] !== undefined && a[column] !== null) ? a[column] : "";
    let valB = (b[column] !== undefined && b[column] !== null) ? b[column] : "";

	
    if (column === "grade") {
      const indexA = gradeOrder.indexOf(valA);
      const indexB = gradeOrder.indexOf(valB);
      return sortAscending ? indexA - indexB : indexB - indexA;
    }


	if (column === "idStudent") {
	  valA = parseInt(valA);
	  valB = parseInt(valB);
	  return sortAscending ? valA - valB : valB - valA;
	}

    valA = typeof valA === "string" ? valA.toLowerCase() : "";
    valB = typeof valB === "string" ? valB.toLowerCase() : "";

    if (valA < valB) return sortAscending ? -1 : 1;
    if (valA > valB) return sortAscending ? 1 : -1;
    return 0;
  });

  renderStudents();
}

function updateSortArrows() {
  document.querySelectorAll("th[data-column]").forEach(th => {
	const column = th.getAttribute("data-column");
	const baseLabel = columnLabels[column]; 
	if (column === currentSortColumn) {
	  const arrow = sortAscending ? " ▲" : " ▼";
	  th.textContent = baseLabel + arrow;
	} else {
	  th.textContent = baseLabel;
	}
  });
}


