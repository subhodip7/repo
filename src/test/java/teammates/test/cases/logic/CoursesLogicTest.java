package teammates.test.cases.logic;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNull;
import static org.testng.AssertJUnit.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.CourseDetailsBundle;
import teammates.common.datatransfer.CourseSummaryBundle;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.datatransfer.EvaluationAttributes.EvalStatus;
import teammates.common.datatransfer.EvaluationDetailsBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.StudentProfileAttributes;
import teammates.common.datatransfer.SubmissionAttributes;
import teammates.common.datatransfer.TeamDetailsBundle;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.TimeHelper;
import teammates.common.util.Utils;
import teammates.logic.backdoor.BackDoorLogic;
import teammates.logic.core.AccountsLogic;
import teammates.logic.core.CoursesLogic;
import teammates.logic.core.InstructorsLogic;
import teammates.logic.core.StudentsLogic;
import teammates.storage.api.AccountsDb;
import teammates.storage.api.CoursesDb;
import teammates.storage.api.InstructorsDb;
import teammates.test.cases.BaseComponentTestCase;
import teammates.test.driver.AssertHelper;
import teammates.test.util.TestHelper;

public class CoursesLogicTest extends BaseComponentTestCase {
 
    private CoursesLogic coursesLogic = new CoursesLogic();
    private CoursesDb coursesDb = new CoursesDb();
    private AccountsDb accountsDb = new AccountsDb();
    private InstructorsDb instructorsDb = new InstructorsDb();
    
    private static DataBundle dataBundle = getTypicalDataBundle();

    @BeforeClass
    public static void setupClass() throws Exception {
        printTestClassHeader();
        turnLoggingUp(CoursesLogic.class);
        removeTypicalDataInDatastore();
        restoreTypicalDataInDatastore();
    }
    
    @Test
    public void testAll() throws Exception {
        testGetCourse();
        testGetArchivedCoursesForInstructor();
        testGetCoursesForInstructor();
        testIsSampleCourse() ;
        testIsCoursePresent() ;
        testVerifyCourseIsPresent();
        testSetArchiveStatusOfCourse();
        testGetCourseSummary();
        testGetCourseSummaryWithoutStats();
        testGetCourseDetails();
        testGetTeamsForCourse();
        testGetNumberOfSections();
        testGetNumberOfTeams();
        testGetTotalEnrolledInCourse();
        testGetTotalUnregisteredInCourse();
        testGetCoursesForStudentAccount();
        testGetCourseDetailsListForStudent();
        testGetCourseSummariesForInstructor();
        testGetCourseDetailsListForInstructor();
        testGetCoursesSummaryWithoutStatsForInstructor();
        testGetCourseStudentListAsCsv();
        testHasIndicatedSections();
        testCreateCourse();
        testCreateCourseAndInstructor();
        testDeleteCourse() ;
    }


    public void testGetCourse() throws Exception {

        ______TS("failure: course doesn't exist");

        assertNull(coursesLogic.getCourse("nonexistant-course"));

        ______TS("success: typical case");

        CourseAttributes c = new CourseAttributes();
        c.id = "Computing101-getthis";
        c.name = "Basic Computing Getting";
        coursesDb.createEntity(c);

        assertEquals(c.id, coursesLogic.getCourse(c.id).id);
        assertEquals(c.name, coursesLogic.getCourse(c.id).name);
        
        coursesDb.deleteEntity(c);
        ______TS("Null parameter");
    
        try {
            coursesLogic.getCourse(null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            assertEquals("Supplied parameter was null\n", e.getMessage());
        }
    }
    
    public void testGetArchivedCoursesForInstructor() throws Exception {
        
        ______TS("success: instructor with archive course");
        String instructorId = dataBundle.instructors.get("instructorOfArchivedCourse").googleId;
        
        List<CourseAttributes> archivedCourses = coursesLogic.getArchivedCoursesForInstructor(instructorId);
        
        assertEquals(1, archivedCourses.size());
        assertEquals(true, archivedCourses.get(0).isArchived);
    
        ______TS("boundary: instructor without archive courses");
        instructorId = dataBundle.instructors.get("instructor1OfCourse1").googleId;
        
        archivedCourses = coursesLogic.getArchivedCoursesForInstructor(instructorId);
        
        assertEquals(0, archivedCourses.size());

        ______TS("Null parameter");
    
        try {
            coursesLogic.getArchivedCoursesForInstructor(null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            assertEquals("Supplied parameter was null\n", e.getMessage());
        }
    }
    
    public void testGetCoursesForInstructor() throws Exception {

        ______TS("success: instructor with present courses");
        
        String instructorId = dataBundle.accounts.get("instructor3").googleId;

        List<CourseAttributes> courses = coursesLogic.getCoursesForInstructor(instructorId);

        assertEquals(2, courses.size());

        ______TS("boundary: instructor without any courses");
        
        instructorId = dataBundle.accounts.get("instructorWithoutCourses").googleId;

        courses = coursesLogic.getCoursesForInstructor(instructorId);

        assertEquals(0, courses.size());

        ______TS("Null parameter");
    
        try {
            coursesLogic.getCoursesForInstructor(null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            assertEquals("Supplied parameter was null\n", e.getMessage());
        }
    }

    public void testIsSampleCourse() {
        
        ______TS("typical case: not a sample course");
        
        CourseAttributes c = new CourseAttributes();
        c.id = "course.id";
        
        assertEquals(false, coursesLogic.isSampleCourse(c.id));
        
        ______TS("typical case: is a sample course");
        
        c.id = c.id.concat("-demo3");
        assertEquals(true, coursesLogic.isSampleCourse(c.id));
        
        ______TS("typical case: is a sample course with '-demo' in the middle of its id");
        
        c.id = c.id.concat("-demo33");
        assertEquals(true, coursesLogic.isSampleCourse(c.id));
        
         ______TS("Null parameter");
    
        try {
            coursesLogic.isSampleCourse(null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            assertEquals("Course ID is null", e.getMessage());
        }
    }

    public void testIsCoursePresent() {

        ______TS("typical case: not an existent course");
        
        CourseAttributes c = new CourseAttributes();
        c.id = "non-existent-course";

        assertEquals(false, coursesLogic.isCoursePresent(c.id));

        ______TS("typical case: an existent course");
        
        c.id = "idOfTypicalCourse1";

        assertEquals(true, coursesLogic.isCoursePresent(c.id));

        ______TS("Null parameter");
    
        try {
            coursesLogic.isCoursePresent(null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            assertEquals("Supplied parameter was null\n", e.getMessage());
        }
    }

    public void testVerifyCourseIsPresent() throws Exception {

        ______TS("typical case: verify an inexistent course");
       
        CourseAttributes c = new CourseAttributes();
        c.id = "non-existent-course";

        try{
            coursesLogic.verifyCourseIsPresent(c.id);
            signalFailureToDetectException();
        } catch (EntityDoesNotExistException e) {
            AssertHelper.assertContains("Course does not exist: ", e.getMessage());
        }

        ______TS("typical case: verify an existent course");
       
        c.id = "idOfTypicalCourse1";
        coursesLogic.verifyCourseIsPresent(c.id);
        
        ______TS("Null parameter");
    
        try {
            coursesLogic.verifyCourseIsPresent(null);
            signalFailureToDetectException();
        } catch (AssertionError | EntityDoesNotExistException e) {
            assertEquals("Supplied parameter was null\n", e.getMessage());
        }
    }
    
    public void testSetArchiveStatusOfCourse() throws Exception {
        
        CourseAttributes course = new CourseAttributes("CLogicT.new-course", "New course");
        coursesDb.createEntity(course);
        
        ______TS("success: archive a course");
        
        coursesLogic.setArchiveStatusOfCourse(course.id, true);
        
        CourseAttributes courseRetrieved = coursesLogic.getCourse(course.id);
        assertEquals(true, courseRetrieved.isArchived);
        
        ______TS("success: unarchive a course");
        
        coursesLogic.setArchiveStatusOfCourse(course.id, false);
        
        courseRetrieved = coursesLogic.getCourse(course.id);
        assertEquals(false, courseRetrieved.isArchived);
        
        ______TS("fail: course doesn't exist");
        
        coursesDb.deleteCourse(course.id);
        
        try {
            coursesLogic.setArchiveStatusOfCourse(course.id, true);
            signalFailureToDetectException();
        } catch (EntityDoesNotExistException e) {
            AssertHelper.assertContains("Course does not exist: CLogicT.new-course", e.getMessage());
        }

        ______TS("Null parameter");
    
        try {
            coursesLogic.setArchiveStatusOfCourse(null, true);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            assertEquals("Supplied parameter was null\n", e.getMessage());
        }
    }

    public void testGetCourseSummary() throws Exception {

        ______TS("typical case");

        CourseAttributes course = dataBundle.courses.get("typicalCourse1");
        CourseDetailsBundle courseSummary = coursesLogic.getCourseSummary(course.id);
        assertEquals(course.id, courseSummary.course.id);
        assertEquals(course.name, courseSummary.course.name);
        assertEquals(false, courseSummary.course.isArchived);

        assertEquals(2, courseSummary.stats.teamsTotal);
        assertEquals(5, courseSummary.stats.studentsTotal);
        assertEquals(0, courseSummary.stats.unregisteredTotal);
        
        assertEquals(0, courseSummary.evaluations.size());

        assertEquals(1, courseSummary.sections.get(0).teams.size()); 
        assertEquals("Team 1.1", courseSummary.sections.get(0).teams.get(0).name);

        ______TS("course without students");

        StudentProfileAttributes spa = new StudentProfileAttributes();
        spa.googleId = "instructor1";
        AccountsLogic.inst().createAccount(new AccountAttributes("instructor1", "Instructor 1", true, 
                "instructor@email.com", "National University Of Singapore", spa));
        coursesLogic.createCourseAndInstructor("instructor1", "course1", "course 1");
        courseSummary = coursesLogic.getCourseSummary("course1");
        assertEquals("course1", courseSummary.course.id);
        assertEquals("course 1", courseSummary.course.name);
        
        assertEquals(0, courseSummary.stats.teamsTotal);
        assertEquals(0, courseSummary.stats.studentsTotal);
        assertEquals(0, courseSummary.stats.unregisteredTotal);
        
        assertEquals(0, courseSummary.evaluations.size());
        assertEquals(0, courseSummary.sections.size());
        
        coursesLogic.deleteCourseCascade("course1");
        accountsDb.deleteAccount("instructor1");

        ______TS("non-existent");

        try {
            coursesLogic.getCourseSummary("non-existent-course");
            signalFailureToDetectException();
        } catch (EntityDoesNotExistException e) {
            AssertHelper.assertContains("The course does not exist:", e.getMessage());
        }
        
        ______TS("null parameter");

        try {
            coursesLogic.getCourseSummary(null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            assertEquals("Supplied parameter was null\n", e.getMessage());
        }
    }

    public void testGetCourseSummaryWithoutStats() throws Exception {

        ______TS("typical case");

        CourseAttributes course = dataBundle.courses.get("typicalCourse1");
        CourseSummaryBundle courseSummary = coursesLogic.getCourseSummaryWithoutStats(course.id);
        assertEquals(course.id, courseSummary.course.id);
        assertEquals(course.name, courseSummary.course.name);
        assertEquals(false, courseSummary.course.isArchived);

        assertEquals(0, courseSummary.evaluations.size());
        assertEquals(0, courseSummary.sections.size()); 
       
        ______TS("course without students");
        
        StudentProfileAttributes spa = new StudentProfileAttributes();
        spa.googleId = "instructor1";
        
        AccountsLogic.inst().createAccount(new AccountAttributes("instructor1", "Instructor 1", true,
                "instructor@email.com", "National University Of Singapore", spa));
        coursesLogic.createCourseAndInstructor("instructor1", "course1", "course 1");
        courseSummary = coursesLogic.getCourseSummaryWithoutStats("course1");
        assertEquals("course1", courseSummary.course.id);
        assertEquals("course 1", courseSummary.course.name);
         
        assertEquals(0, courseSummary.evaluations.size());
        assertEquals(0, courseSummary.sections.size());
        
        coursesLogic.deleteCourseCascade("course1");
        accountsDb.deleteAccount("instructor1");

        ______TS("non-existent");

        try {
            coursesLogic.getCourseSummaryWithoutStats("non-existent-course");
            signalFailureToDetectException();
        } catch (EntityDoesNotExistException e) {
            AssertHelper.assertContains("The course does not exist:", e.getMessage());
        }
        
        ______TS("null parameter");

        try {
            coursesLogic.getCourseSummaryWithoutStats(null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            assertEquals("Supplied parameter was null\n", e.getMessage());
        }
    }

    public void testGetCourseDetails() throws Exception {

        ______TS("typical case");

        CourseAttributes course = dataBundle.courses.get("typicalCourse1");
        CourseDetailsBundle courseDetails = coursesLogic.getCourseDetails(course.id);
        assertEquals(course.id, courseDetails.course.id);
        assertEquals(course.name, courseDetails.course.name);
        assertEquals(false, courseDetails.course.isArchived);

        assertEquals(2, courseDetails.stats.teamsTotal);
        assertEquals(5, courseDetails.stats.studentsTotal);
        assertEquals(0, courseDetails.stats.unregisteredTotal);
        
        assertEquals(2, courseDetails.evaluations.size());
        assertEquals("evaluation2 In Course1", courseDetails.evaluations.get(0).evaluation.name);
        assertEquals("evaluation1 In Course1", courseDetails.evaluations.get(1).evaluation.name);

        assertEquals(1, courseDetails.sections.get(0).teams.size()); 
        assertEquals("Team 1.1", courseDetails.sections.get(0).teams.get(0).name);
        
        ______TS("course without students");

        StudentProfileAttributes spa = new StudentProfileAttributes();
        spa.googleId = "instructor1";
        
        AccountsLogic.inst().createAccount(new AccountAttributes("instructor1", "Instructor 1", true, 
                "instructor@email.com", "National University Of Singapore", spa));
        coursesLogic.createCourseAndInstructor("instructor1", "course1", "course 1");
        courseDetails = coursesLogic.getCourseDetails("course1");
        assertEquals("course1", courseDetails.course.id);
        assertEquals("course 1", courseDetails.course.name);
        
        assertEquals(0, courseDetails.stats.teamsTotal);
        assertEquals(0, courseDetails.stats.studentsTotal);
        assertEquals(0, courseDetails.stats.unregisteredTotal);
        
        assertEquals(0, courseDetails.evaluations.size());
        assertEquals(0, courseDetails.sections.size());
        
        coursesLogic.deleteCourseCascade("course1");
        accountsDb.deleteAccount("instructor1");
        
        ______TS("non-existent");

        try {
            coursesLogic.getCourseDetails("non-existent-course");
            signalFailureToDetectException();
        } catch (EntityDoesNotExistException e) {
            AssertHelper.assertContains("The course does not exist:", e.getMessage());
        }
        
        ______TS("null parameter");

        try {
            coursesLogic.getCourseDetails(null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            assertEquals("Supplied parameter was null\n", e.getMessage());
        }
    }

    public void testGetTeamsForCourse() throws Exception {
       
        ______TS("typical case");

        CourseAttributes course = dataBundle.courses.get("typicalCourse1");
        List<TeamDetailsBundle> teams = coursesLogic.getTeamsForCourse(course.id);
        
        assertEquals(2, teams.size()); 
        assertEquals("Team 1.1", teams.get(0).name);
        assertEquals("Team 1.2", teams.get(1).name);


        ______TS("course without students");

        StudentProfileAttributes spa = new StudentProfileAttributes();
        spa.googleId = "instructor1";
        
        AccountsLogic.inst().createAccount(new AccountAttributes("instructor1", "Instructor 1", true, 
                "instructor@email.com", "National University Of Singapore", spa));
        coursesLogic.createCourseAndInstructor("instructor1", "course1", "course 1");
        teams = coursesLogic.getTeamsForCourse("course1");

        assertEquals(0, teams.size());
        
        coursesLogic.deleteCourseCascade("course1");
        accountsDb.deleteAccount("instructor1");
        
        ______TS("non-existent");

        try {
            coursesLogic.getTeamsForCourse("non-existent-course");
            signalFailureToDetectException();
        } catch (EntityDoesNotExistException e) {
            AssertHelper.assertContains("does not exist", e.getMessage());
        }
        
        ______TS("null parameter");

        try {
            coursesLogic.getTeamsForCourse(null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            assertEquals("Supplied parameter was null\n", e.getMessage());
        }
    }
 
    public void testGetNumberOfSections() throws Exception {

        ______TS("Typical case");

        CourseAttributes course = dataBundle.courses.get("typicalCourse1");
        int sectionNum = coursesLogic.getNumberOfSections(course.id);

        assertEquals(2, sectionNum);

        ______TS("Course with no sections");

        course = dataBundle.courses.get("typicalCourse2");
        sectionNum = coursesLogic.getNumberOfSections(course.id);

        assertEquals(0, sectionNum);

         ______TS("non-existent");

        try {
            coursesLogic.getNumberOfSections("non-existent-course");
            signalFailureToDetectException();
        } catch (EntityDoesNotExistException e) {
            AssertHelper.assertContains("does not exist", e.getMessage());
        }
        
        ______TS("null parameter");

        try {
            coursesLogic.getNumberOfSections(null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            assertEquals("Supplied parameter was null\n", e.getMessage());
        }
    }
 
    public void testGetNumberOfTeams() throws Exception {
        
        ______TS("typical case");

        CourseAttributes course = dataBundle.courses.get("typicalCourse1");
        int teamNum = coursesLogic.getNumberOfTeams(course.id);
        
        assertEquals(2, teamNum); 

        ______TS("course without students");

        StudentProfileAttributes spa = new StudentProfileAttributes();
        spa.googleId = "instructor1";
        
        AccountsLogic.inst().createAccount(new AccountAttributes("instructor1", "Instructor 1", true, 
                "instructor@email.com", "National University Of Singapore", spa));
        coursesLogic.createCourseAndInstructor("instructor1", "course1", "course 1");
        teamNum = coursesLogic.getNumberOfTeams("course1");

        assertEquals(0, teamNum);
        
        coursesLogic.deleteCourseCascade("course1");
        accountsDb.deleteAccount("instructor1");
        
        ______TS("non-existent");

        try {
            coursesLogic.getNumberOfTeams("non-existent-course");
            signalFailureToDetectException();
        } catch (EntityDoesNotExistException e) {
            AssertHelper.assertContains("does not exist", e.getMessage());
        }
        
        ______TS("null parameter");

        try {
            coursesLogic.getNumberOfTeams(null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            assertEquals("Supplied parameter was null\n", e.getMessage());
        }
    }

    public void testGetTotalEnrolledInCourse() throws Exception {
        
        ______TS("typical case");

        CourseAttributes course = dataBundle.courses.get("typicalCourse1");
        int enrolledNum = coursesLogic.getTotalEnrolledInCourse(course.id);
        
        assertEquals(5, enrolledNum); 

        ______TS("course without students");

        StudentProfileAttributes spa = new StudentProfileAttributes();
        spa.googleId = "instructor1";
        
        AccountsLogic.inst().createAccount(new AccountAttributes("instructor1", "Instructor 1", true, 
                "instructor@email.com", "National University Of Singapore", spa));
        coursesLogic.createCourseAndInstructor("instructor1", "course1", "course 1");
        enrolledNum = coursesLogic.getTotalEnrolledInCourse("course1");

        assertEquals(0, enrolledNum);
        
        coursesLogic.deleteCourseCascade("course1");
        accountsDb.deleteAccount("instructor1");
        
        ______TS("non-existent");

        try {
            coursesLogic.getTotalEnrolledInCourse("non-existent-course");
            signalFailureToDetectException();
        } catch (EntityDoesNotExistException e) {
            AssertHelper.assertContains("does not exist", e.getMessage());
        }
        
        ______TS("null parameter");

        try {
            coursesLogic.getTotalEnrolledInCourse(null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            assertEquals("Supplied parameter was null\n", e.getMessage());
        }
    }

    public void testGetTotalUnregisteredInCourse() throws Exception {

        ______TS("typical case");

        CourseAttributes course = dataBundle.courses.get("unregisteredCourse");
        int unregisteredNum = coursesLogic.getTotalUnregisteredInCourse(course.id);
        
        assertEquals(2, unregisteredNum); 

        ______TS("course without students");

        StudentProfileAttributes spa = new StudentProfileAttributes();
        spa.googleId = "instructor1";
        
        AccountsLogic.inst().createAccount(new AccountAttributes("instructor1", "Instructor 1", true, 
                "instructor@email.com", "National University Of Singapore", spa));
        coursesLogic.createCourseAndInstructor("instructor1", "course1", "course 1");
        unregisteredNum = coursesLogic.getTotalUnregisteredInCourse("course1");

        assertEquals(0, unregisteredNum);
        
        coursesLogic.deleteCourseCascade("course1");
        accountsDb.deleteAccount("instructor1");
         
        ______TS("non-existent");

        try {
            coursesLogic.getTotalUnregisteredInCourse("non-existent-course");
            signalFailureToDetectException();
        } catch (EntityDoesNotExistException e) {
            AssertHelper.assertContains("does not exist", e.getMessage());
        }
        
        ______TS("null parameter");

        try {
            coursesLogic.getTotalUnregisteredInCourse(null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            assertEquals("Supplied parameter was null\n", e.getMessage());
        }
    }

    public void testGetCoursesForStudentAccount() throws Exception {

        ______TS("student having two courses");

        StudentAttributes studentInTwoCourses = dataBundle.students
                .get("student2InCourse1");
        List<CourseAttributes> courseList = coursesLogic
                .getCoursesForStudentAccount(studentInTwoCourses.googleId);
        assertEquals(2, courseList.size());
        // For some reason, index 0 is Course2 and index 1 is Course1
        // Anyway in DataStore which follows a HashMap structure,
        // there is no guarantee on the order of Entities' storage
        CourseAttributes course1 = dataBundle.courses.get("typicalCourse2");
        assertEquals(course1.id, courseList.get(0).id);
        assertEquals(course1.name, courseList.get(0).name);
    
        CourseAttributes course2 = dataBundle.courses.get("typicalCourse1");
        assertEquals(course2.id, courseList.get(1).id);
        assertEquals(course2.name, courseList.get(1).name);
    
        ______TS("student having one course");
    
        StudentAttributes studentInOneCourse = dataBundle.students
                .get("student1InCourse1");
        courseList = coursesLogic.getCoursesForStudentAccount(studentInOneCourse.googleId);
        assertEquals(1, courseList.size());
        course1 = dataBundle.courses.get("typicalCourse1");
        assertEquals(course1.id, courseList.get(0).id);
        assertEquals(course1.name, courseList.get(0).name);
    
        // Student having zero courses is not applicable
    
        ______TS("non-existent student");
    
        try {
            coursesLogic.getCoursesForStudentAccount("non-existent-student");
            signalFailureToDetectException();
        } catch (EntityDoesNotExistException e) {
            AssertHelper.assertContains("does not exist",
                                         e.getMessage());
        }
    
        ______TS("null parameter");
    
        try {
            coursesLogic.getCoursesForStudentAccount(null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            assertEquals("Supplied parameter was null\n", e.getMessage());
        }
    }

    public void testGetCourseDetailsListForStudent() throws Exception {

        ______TS("student having multiple evaluations in multiple courses");
    
        CourseAttributes expectedCourse1 = dataBundle.courses.get("typicalCourse1");
        CourseAttributes expectedCourse2 = dataBundle.courses.get("typicalCourse2");

        EvaluationAttributes expectedEval1InCourse1 = dataBundle.evaluations
                .get("evaluation1InCourse1");
        EvaluationAttributes expectedEval2InCourse1 = dataBundle.evaluations
                .get("evaluation2InCourse1");

        EvaluationAttributes expectedEval1InCourse2 = dataBundle.evaluations
                .get("evaluation1InCourse2");
    
        // This student is in both course 1 and 2
        StudentAttributes studentInBothCourses = dataBundle.students
                .get("student2InCourse1");
    
        // Make sure all evaluations in course1 are visible (i.e., not AWAITING)
        expectedEval1InCourse1.startTime = TimeHelper.getDateOffsetToCurrentTime(-2);
        expectedEval1InCourse1.endTime = TimeHelper.getDateOffsetToCurrentTime(-1);
        expectedEval1InCourse1.published = false;
        assertEquals(EvalStatus.CLOSED, expectedEval1InCourse1.getStatus());
        BackDoorLogic backDoorLogic = new BackDoorLogic();
        backDoorLogic.updateEvaluation(expectedEval1InCourse1);
    
        expectedEval2InCourse1.startTime = TimeHelper.getDateOffsetToCurrentTime(-1);
        expectedEval2InCourse1.endTime = TimeHelper.getDateOffsetToCurrentTime(1);
        assertEquals(EvalStatus.OPEN, expectedEval2InCourse1.getStatus());
        backDoorLogic.updateEvaluation(expectedEval2InCourse1);
    
        // Make sure all evaluations in course2 are still AWAITING
        expectedEval1InCourse2.startTime = TimeHelper.getDateOffsetToCurrentTime(1);
        expectedEval1InCourse2.endTime = TimeHelper.getDateOffsetToCurrentTime(2);
        expectedEval1InCourse2.activated = false;
        assertEquals(EvalStatus.AWAITING, expectedEval1InCourse2.getStatus());
        backDoorLogic.updateEvaluation(expectedEval1InCourse2);
    
        // Get course details for student
        List<CourseDetailsBundle> courseList = coursesLogic
                .getCourseDetailsListForStudent(studentInBothCourses.googleId);
    
        // Verify number of courses received
        assertEquals(2, courseList.size());
    
        // Verify details of course 1 (note: index of course 1 is not 0)
        CourseDetailsBundle actualCourse1 = courseList.get(1);
        assertEquals(expectedCourse1.id, actualCourse1.course.id);
        assertEquals(expectedCourse1.name, actualCourse1.course.name);
        assertEquals(2, actualCourse1.evaluations.size());
    
        // Verify details of evaluation 1 in course 1
        EvaluationAttributes actualEval1InCourse1 = actualCourse1.evaluations.get(1).evaluation;
        TestHelper.verifySameEvaluationData(expectedEval1InCourse1, actualEval1InCourse1);
    
        // Verify some details of evaluation 2 in course 1
        EvaluationAttributes actualEval2InCourse1 = actualCourse1.evaluations.get(0).evaluation;
        TestHelper.verifySameEvaluationData(expectedEval2InCourse1, actualEval2InCourse1);
    
        // For course 2, verify no evaluations returned (because the evaluation
        // in this course is still AWAITING.
        CourseDetailsBundle actualCourse2 = courseList.get(0);
        assertEquals(expectedCourse2.id, actualCourse2.course.id);
        assertEquals(expectedCourse2.name, actualCourse2.course.name);
        assertEquals(0, actualCourse2.evaluations.size());
    
        ______TS("student in a course with no evaluations");
    
        StudentAttributes studentWithNoEvaluations = dataBundle.students
                .get("student1InCourse2");
        courseList = coursesLogic
                .getCourseDetailsListForStudent(studentWithNoEvaluations.googleId);
        assertEquals(1, courseList.size());
        assertEquals(0, courseList.get(0).evaluations.size());
    
        // student with no courses is not applicable
    
        ______TS("non-existent student");
    
        try {
            coursesLogic.getCourseDetailsListForStudent("non-existent-student");
            signalFailureToDetectException();
        } catch (EntityDoesNotExistException e) {
            AssertHelper.assertContains("does not exist",
                                         e.getMessage());
        }
       
        ______TS("null parameter");
    
        try {
            coursesLogic.getCourseDetailsListForStudent(null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            assertEquals("Supplied parameter was null\n", e.getMessage());
        }
    }

    public void testGetCourseSummariesForInstructor() throws Exception {

        ______TS("Instructor with 2 courses");
    
        InstructorAttributes instructor = dataBundle.instructors.get("instructor3OfCourse1");
        HashMap<String, CourseDetailsBundle> courseList = coursesLogic.getCourseSummariesForInstructor(instructor.googleId);
        assertEquals(2, courseList.size());
        for (CourseDetailsBundle cdd : courseList.values()) {
            // check if course belongs to this instructor
            assertTrue(InstructorsLogic.inst().isGoogleIdOfInstructorOfCourse(instructor.googleId, cdd.course.id));
        }
    
        ______TS("Instructor with 0 courses");
        courseList = coursesLogic.getCourseSummariesForInstructor("instructorWithoutCourses");
        assertEquals(0, courseList.size());
   
        ______TS("Non-existent instructor");
    
        try {
            coursesLogic.getCourseSummariesForInstructor("non-existent-instructor");
            signalFailureToDetectException();
        } catch (EntityDoesNotExistException e) {
            AssertHelper.assertContains("does not exist",
                                         e.getMessage());
        }
       
        ______TS("Null parameter");
    
        try {
            coursesLogic.getCourseSummariesForInstructor(null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            assertEquals("Supplied parameter was null\n", e.getMessage());
        }
       
    }

    public void testGetCourseDetailsListForInstructor() throws Exception {

        ______TS("Typical case");
    
        HashMap<String, CourseDetailsBundle> courseListForInstructor = coursesLogic
                .getCoursesDetailsListForInstructor("idOfInstructor3");
        assertEquals(2, courseListForInstructor.size());
        String course1Id = "idOfTypicalCourse1";
    
        // course with 2 evaluations
        ArrayList<EvaluationDetailsBundle> course1Evals = courseListForInstructor
                .get(course1Id).evaluations;
        String course1EvalDetails = "";
        for (EvaluationDetailsBundle ed : course1Evals) {
            course1EvalDetails = course1EvalDetails
                    + Utils.getTeammatesGson().toJson(ed) + Const.EOL;
        }
        int numberOfEvalsInCourse1 = course1Evals.size();
        assertEquals(course1EvalDetails, 2, numberOfEvalsInCourse1);
        assertEquals(course1Id, course1Evals.get(0).evaluation.courseId);
        TestHelper.verifyEvaluationInfoExistsInList(
                dataBundle.evaluations.get("evaluation1InCourse1"),
                course1Evals);
        TestHelper.verifyEvaluationInfoExistsInList(
                dataBundle.evaluations.get("evaluation2InCourse1"),
                course1Evals);
    
        // course with 1 evaluation
        assertEquals(course1Id, course1Evals.get(1).evaluation.courseId);
        ArrayList<EvaluationDetailsBundle> course2Evals = courseListForInstructor
                .get("idOfTypicalCourse2").evaluations;
        assertEquals(1, course2Evals.size());
        TestHelper.verifyEvaluationInfoExistsInList(
                dataBundle.evaluations.get("evaluation1InCourse2"),
                course2Evals);
    
        ______TS("Instructor has a course with 0 evaluations");

        courseListForInstructor = coursesLogic
                .getCoursesDetailsListForInstructor("idOfInstructor4");
        assertEquals(1, courseListForInstructor.size());
        assertEquals(0,
                courseListForInstructor.get("idOfCourseNoEvals").evaluations
                        .size());
    
        ______TS("Instructor with 0 courses");
        
        courseListForInstructor = coursesLogic.getCoursesDetailsListForInstructor("instructorWithoutCourses");
        assertEquals(0, courseListForInstructor.size());
   
        ______TS("Non-existent instructor");
    
        try {
            coursesLogic.getCoursesDetailsListForInstructor("non-existent-instructor");
            signalFailureToDetectException();
        } catch (EntityDoesNotExistException e) {
            AssertHelper.assertContains("does not exist",
                                         e.getMessage());
        }
       
        ______TS("Null parameter");
    
        try {
            coursesLogic.getCoursesDetailsListForInstructor(null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            assertEquals("Supplied parameter was null\n", e.getMessage());
        }
    }

    public void testGetCoursesSummaryWithoutStatsForInstructor() throws Exception {

        ______TS("Typical case");

        HashMap<String, CourseSummaryBundle> courseListForInstructor = coursesLogic
                .getCoursesSummaryWithoutStatsForInstructor("idOfInstructor3");
        assertEquals(2, courseListForInstructor.size());
        String course1Id = "idOfTypicalCourse1";
    
        // course with 2 evaluations
        ArrayList<EvaluationAttributes> course1Evals = courseListForInstructor
                .get(course1Id).evaluations;
        String course1EvalsAttributes = "";
        for (EvaluationAttributes evalAttr : course1Evals) {
            course1EvalsAttributes = course1EvalsAttributes
                    + Utils.getTeammatesGson().toJson(evalAttr) + Const.EOL;
        }
        int numberOfEvalsInCourse1 = course1Evals.size();
        assertEquals(course1EvalsAttributes, 2, numberOfEvalsInCourse1);
        assertEquals(course1Id, course1Evals.get(0).courseId);
        TestHelper.verifyEvaluationInfoExistsInAttributeList(
                dataBundle.evaluations.get("evaluation1InCourse1"),
                course1Evals);
        TestHelper.verifyEvaluationInfoExistsInAttributeList(
                dataBundle.evaluations.get("evaluation2InCourse1"),
                course1Evals);
    
        // course with 1 evaluation
        assertEquals(course1Id, course1Evals.get(1).courseId);
        ArrayList<EvaluationAttributes> course2Evals = courseListForInstructor
                .get("idOfTypicalCourse2").evaluations;
        assertEquals(1, course2Evals.size());
        TestHelper.verifyEvaluationInfoExistsInAttributeList(
                dataBundle.evaluations.get("evaluation1InCourse2"),
                course2Evals);
    
        ______TS("Instructor has a course with 0 evaluations");

        courseListForInstructor = coursesLogic
                .getCoursesSummaryWithoutStatsForInstructor("idOfInstructor4");
        assertEquals(1, courseListForInstructor.size());
        assertEquals(0,
                courseListForInstructor.get("idOfCourseNoEvals").evaluations
                        .size());
    
        ______TS("Instructor with 0 courses");
        
        courseListForInstructor = coursesLogic.getCoursesSummaryWithoutStatsForInstructor("instructorWithoutCourses");
        assertEquals(0, courseListForInstructor.size());
   
        ______TS("Non-existent instructor");
    
        try {
            coursesLogic.getCoursesSummaryWithoutStatsForInstructor("non-existent-instructor");
            signalFailureToDetectException();
        } catch (EntityDoesNotExistException e) {
            AssertHelper.assertContains("does not exist",
                                         e.getMessage());
        }
       
        ______TS("Null parameter");
    
        try {
            coursesLogic.getCoursesSummaryWithoutStatsForInstructor(null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            assertEquals("Supplied parameter was null\n", e.getMessage());
        }
    }

    public void testGetCourseStudentListAsCsv() throws Exception {

        ______TS("Typical case: course with section");
        
        InstructorAttributes instructor1OfCourse1 = dataBundle.instructors.get("instructor1OfCourse1");
        
        String instructorId = instructor1OfCourse1.googleId;
        String courseId = instructor1OfCourse1.courseId;

        String csvString = coursesLogic.getCourseStudentListAsCsv(courseId, instructorId);
        String expectedCsvString = "Course ID,\"idOfTypicalCourse1\"" + Const.EOL  
                                 + "Course Name,\"Typical Course 1 with 2 Evals\"" + Const.EOL  
                                 + Const.EOL + Const.EOL 
                                 + "Section,Team,First Name,Last Name,Status,Email" + Const.EOL
                                 + "\"Section 1\",\"Team 1.1\",\"student1 In\",\"Course1\",\"Joined\",\"student1InCourse1@gmail.com\"" + Const.EOL
                                 + "\"Section 1\",\"Team 1.1\",\"student2 In\",\"Course1\",\"Joined\",\"student2InCourse1@gmail.com\"" + Const.EOL
                                 + "\"Section 1\",\"Team 1.1\",\"student3 In\",\"Course1\",\"Joined\",\"student3InCourse1@gmail.com\"" + Const.EOL
                                 + "\"Section 1\",\"Team 1.1\",\"student4 In\",\"Course1\",\"Joined\",\"student4InCourse1@gmail.com\"" + Const.EOL
                                 + "\"Section 2\",\"Team 1.2\",\"student5 In\",\"Course1\",\"Joined\",\"student5InCourse1@gmail.com\"" + Const.EOL;

        assertEquals(expectedCsvString, csvString);

        ______TS("Typical case: course without sections");

        InstructorAttributes instructor1OfCourse2 = dataBundle.instructors.get("instructor1OfCourse2");

        instructorId = instructor1OfCourse2.googleId;
        courseId = instructor1OfCourse2.courseId;

        csvString = coursesLogic.getCourseStudentListAsCsv(courseId, instructorId);
        expectedCsvString = "Course ID,\"idOfTypicalCourse1\"" + Const.EOL  
                                 + "Course Name,\"Typical Course 1 with 2 Evals\"" + Const.EOL  
                                 + Const.EOL + Const.EOL 
                                 + "Team,First Name,Last Name,Status,Email" + Const.EOL
                                 + "\"Team 2.1\",\"student1 In\",\"Course2\",\"Joined\",\"student1InCourse2@gmail.com\"" + Const.EOL
                                 + "\"Team 2.1\",\"student2 In\",\"Course2\",\"Joined\",\"student2InCourse2@gmail.com\"" + Const.EOL;

        ______TS("Typical case: course with unregistered student");

        InstructorAttributes instructor5 = dataBundle.instructors.get("instructor5");
        
        instructorId = instructor5.googleId;
        courseId = instructor5.courseId;

        csvString = coursesLogic.getCourseStudentListAsCsv(courseId, instructorId);
        expectedCsvString = "Course ID,\"idOfUnregisteredCourse\"" + Const.EOL  
                                 + "Course Name,\"Unregistered Course\"" + Const.EOL  
                                 + Const.EOL + Const.EOL 
                                 + "Section,Team,First Name,Last Name,Status,Email" + Const.EOL
                                 + "\"Section 1\",\"Team 1\",\"student1 In\",\"unregisteredCourse\",\"Yet to join\",\"student1InUnregisteredCourse@gmail.com\"" + Const.EOL
                                 + "\"Section 2\",\"Team 2\",\"student2 In\",\"unregisteredCourse\",\"Yet to join\",\"student2InUnregisteredCourse@gmail.com\"" + Const.EOL;

        assertEquals(expectedCsvString, csvString);

        ______TS("Failure case: non existent instructor");
        
        try {
            coursesLogic.getCourseStudentListAsCsv(courseId, "non-existent-instructor");
            signalFailureToDetectException();
        } catch (EntityDoesNotExistException e) {
            AssertHelper.assertContains("does not exist",
                                         e.getMessage());
        }

        ______TS("Failure case: non existent course in the list of courses of the instructor");

        try {
            coursesLogic.getCourseStudentListAsCsv("non-existent-course", instructorId);
            signalFailureToDetectException();
        } catch (EntityDoesNotExistException e) {
            AssertHelper.assertContains("does not exist",
                                         e.getMessage());
        }

        ______TS("Failure case: null parameter");

        try {
            coursesLogic.getCourseStudentListAsCsv(courseId, null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            assertEquals("Supplied parameter was null\n", e.getMessage());
        }
    }

    public void testHasIndicatedSections() throws Exception {

        ______TS("Typical case: course with sections");

        CourseAttributes typicalCourse1 = dataBundle.courses.get("typicalCourse1");
        assertTrue(coursesLogic.hasIndicatedSections(typicalCourse1.id));

        ______TS("Typical case: course without sections");

        CourseAttributes typicalCourse2 = dataBundle.courses.get("typicalCourse2");
        assertEquals(false, coursesLogic.hasIndicatedSections(typicalCourse2.id));

        ______TS("Failure case: course does not exists");

        try {
            coursesLogic.hasIndicatedSections("non-existent-course");
            signalFailureToDetectException();
        } catch (EntityDoesNotExistException e){
            AssertHelper.assertContains("does not exist",
                                         e.getMessage());   
        }

        ______TS("Failure case: null parameter");

        try {
            coursesLogic.hasIndicatedSections(null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            assertEquals("Supplied parameter was null\n", e.getMessage());
        }

    }

    public void testCreateCourse() throws Exception {
        
        /*Explanation:
         * The SUT (i.e. CoursesLogic::createCourse) has only 1 path. Therefore, we
         * should typically have 1 test cases here.
         */
        ______TS("typical case");
        
        CourseAttributes c = new CourseAttributes();
        c.id = "Computing101-fresh";
        c.name = "Basic Computing";
        coursesLogic.createCourse(c.id, c.name);
        TestHelper.verifyPresentInDatastore(c);
        coursesLogic.deleteCourseCascade(c.id);
        ______TS("Null parameter");
    
        try {
            coursesLogic.createCourse(null, c.name);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            assertEquals("Non-null value expected", e.getMessage());
        }
    }
    
    public void testCreateCourseAndInstructor() throws Exception {
        
        /* Explanation: SUT has 5 paths. They are,
         * path 1 - exit because the account doesn't' exist.
         * path 2 - exit because the account exists but doesn't have instructor privileges.
         * path 3 - exit because course creation failed.
         * path 4 - exit because instructor creation failed.
         * path 5 - success.
         * Accordingly, we have 5 test cases.
         */
        
        ______TS("fails: account doesn't exist");
        
        CourseAttributes c = new CourseAttributes();
        c.id = "fresh-course-tccai";
        c.name = "Fresh course for tccai";
        
        @SuppressWarnings("deprecation")
        InstructorAttributes i = new InstructorAttributes("instructor-for-tccai", c.id, "Instructor for tccai", "ins.for.iccai@gmail.com");       
        
        try {
            coursesLogic.createCourseAndInstructor(i.googleId, c.id, c.name);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains("for a non-existent instructor", e.getMessage());
        }
        TestHelper.verifyAbsentInDatastore(c);
        TestHelper.verifyAbsentInDatastore(i);
        
        ______TS("fails: account doesn't have instructor privileges");
        
        AccountAttributes a = new AccountAttributes();
        a.googleId = i.googleId;
        a.name = i.name;
        a.email = i.email;
        a.institute = "NUS";
        a.isInstructor = false;
        a.studentProfile = new StudentProfileAttributes();
        a.studentProfile.googleId = i.googleId;
        accountsDb.createAccount(a);
        try {
            coursesLogic.createCourseAndInstructor(i.googleId, c.id, c.name);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains("doesn't have instructor privileges", e.getMessage());
        }
        TestHelper.verifyAbsentInDatastore(c);
        TestHelper.verifyAbsentInDatastore(i);
        
        ______TS("fails: error during course creation");
        
        a.isInstructor = true;
        accountsDb.updateAccount(a);
        
        c.id = "invalid id";
        
        try {
            coursesLogic.createCourseAndInstructor(i.googleId, c.id, c.name);
            signalFailureToDetectException();
        } catch (InvalidParametersException e) {
            AssertHelper.assertContains("not acceptable to TEAMMATES as a Course ID", e.getMessage());
        }
        TestHelper.verifyAbsentInDatastore(c);
        TestHelper.verifyAbsentInDatastore(i);
        
        ______TS("fails: error during instructor creation due to duplicate instructor");
        
        c.id = "fresh-course-tccai";
        instructorsDb.createEntity(i); //create a duplicate instructor
        
        try {
            coursesLogic.createCourseAndInstructor(i.googleId, c.id, c.name);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains("Unexpected exception while trying to create instructor for a new course", e.getMessage());
        }
        TestHelper.verifyAbsentInDatastore(c);

        ______TS("fails: error during instructor creation due to invalid parameters");

        i.email = "ins.for.iccai.gmail.com";

        try {
            coursesLogic.createCourseAndInstructor(i.googleId, c.id, c.name);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains("Unexpected exception while trying to create instructor for a new course", e.getMessage());
        }
        TestHelper.verifyAbsentInDatastore(c);
       
        ______TS("success: typical case");

         i.email = "ins.for.iccai@gmail.com";

        //remove the duplicate instructor object from the datastore.
        instructorsDb.deleteInstructor(i.courseId, i.email);
        
        coursesLogic.createCourseAndInstructor(i.googleId, c.id, c.name);
        TestHelper.verifyPresentInDatastore(c);
        TestHelper.verifyPresentInDatastore(i);
        
        ______TS("Null parameter");
    
        try {
            coursesLogic.createCourseAndInstructor(null, c.id, c.name);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            assertEquals("Supplied parameter was null\n", e.getMessage());
        }
    }

    public void testDeleteCourse() throws Exception {
    
        ______TS("typical case");
    
        CourseAttributes course1OfInstructor = dataBundle.courses.get("typicalCourse1");
        StudentAttributes studentInCourse = dataBundle.students.get("student1InCourse1");
        
        // Ensure there are entities in the datastore under this course
        assertTrue(StudentsLogic.inst().getStudentsForCourse(course1OfInstructor.id).size() != 0);
        
        TestHelper.verifyPresentInDatastore(course1OfInstructor);
        TestHelper.verifyPresentInDatastore(studentInCourse);
        TestHelper.verifyPresentInDatastore(dataBundle.evaluations.get("evaluation1InCourse1"));
        TestHelper.verifyPresentInDatastore(dataBundle.evaluations.get("evaluation2InCourse1"));
        TestHelper.verifyPresentInDatastore(dataBundle.instructors.get("instructor1OfCourse1"));
        TestHelper.verifyPresentInDatastore(dataBundle.instructors.get("instructor3OfCourse1"));
        TestHelper.verifyPresentInDatastore(dataBundle.students.get("student1InCourse1"));
        TestHelper.verifyPresentInDatastore(dataBundle.students.get("student5InCourse1"));
        TestHelper.verifyPresentInDatastore(dataBundle.feedbackSessions.get("session1InCourse1"));
        TestHelper.verifyPresentInDatastore(dataBundle.feedbackSessions.get("session2InCourse1"));
        assertEquals(course1OfInstructor.id, studentInCourse.course);
        
        coursesLogic.deleteCourseCascade(course1OfInstructor.id);
    
        // Ensure the course and related entities are deleted
        TestHelper.verifyAbsentInDatastore(course1OfInstructor);
        TestHelper.verifyAbsentInDatastore(studentInCourse);
        TestHelper.verifyAbsentInDatastore(dataBundle.evaluations.get("evaluation1InCourse1"));
        TestHelper.verifyAbsentInDatastore(dataBundle.evaluations.get("evaluation2InCourse1"));
        TestHelper.verifyAbsentInDatastore(dataBundle.instructors.get("instructor1OfCourse1"));
        TestHelper.verifyAbsentInDatastore(dataBundle.instructors.get("instructor3OfCourse1"));
        TestHelper.verifyAbsentInDatastore(dataBundle.students.get("student1InCourse1"));
        TestHelper.verifyAbsentInDatastore(dataBundle.students.get("student5InCourse1"));
        TestHelper.verifyAbsentInDatastore(dataBundle.feedbackSessions.get("session1InCourse1"));
        TestHelper.verifyAbsentInDatastore(dataBundle.feedbackSessions.get("session2InCourse1"));
        TestHelper.verifyAbsentInDatastore(dataBundle.comments.get("comment1FromI1C1toS1C1"));
        TestHelper.verifyAbsentInDatastore(dataBundle.comments.get("comment2FromI1C1toS1C1"));
        TestHelper.verifyAbsentInDatastore(dataBundle.comments.get("comment1FromI3C1toS2C1"));

        ArrayList<SubmissionAttributes> submissionsOfCourse = new ArrayList<SubmissionAttributes>(dataBundle.submissions.values());
        for (SubmissionAttributes s : submissionsOfCourse) {
            if (s.course.equals(course1OfInstructor.id)) {
                TestHelper.verifyAbsentInDatastore(s);
            }
        }
    
        ______TS("non-existent");
    
        // try to delete again. Should fail silently.
        coursesLogic.deleteCourseCascade(course1OfInstructor.id);
    
        ______TS("null parameter");
    
        try {
            coursesLogic.deleteCourseCascade(null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            assertEquals("Supplied parameter was null\n", e.getMessage());
        }
    }
}
