package beans;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/*
 * The class CourseManager acts as a "mock-up" of the the database. 
 * As you advance in the course you will learn to create classes to write and read from the database 
 * and that will be the standard for the course (you will deliver the project using the database).
 * 
 * */

public class CoursesManager {

	private static CoursesManager instance = null;
	private Map<Integer, Course> courses;

	private CoursesManager() {
		super();
		this.courses = new HashMap<Integer, Course>();
		this.courses.put(1, new Course(1, "Tecnologie informatiche per il web", 1));
		this.courses.put(2, new Course(2, "Base di Dati 2", 2));
		this.courses.put(3, new Course(3, "Advanced Web Technologies", 3));
	}

	public static CoursesManager getCoursesManagerInstance() {
		if (instance == null) {
			instance = new CoursesManager();
		}
		return instance;
	}
	
	public Collection<Course> getCourses() {
		return  courses.values();
	}
	
	public Course getCourseById(int id) {
		return courses.get(id);
	}

	public boolean existCourseWithId(Integer id) {
		return courses.containsKey(id);
	}
	
	
	public Integer getNextId() {
		// DUMMY IMPLEMENTATION -- THIS WOULD BE AN AUTOINCREMENTAL FIELD IN THE DATABASE --
		return courses.size() + 1;
	}

}
