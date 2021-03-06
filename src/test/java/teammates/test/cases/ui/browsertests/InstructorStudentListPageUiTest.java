package teammates.test.cases.ui.browsertests;

import static org.testng.AssertJUnit.assertNotNull;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.util.Const;
import teammates.common.util.ThreadHelper;
import teammates.common.util.Url;
import teammates.test.driver.BackDoor;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;
import teammates.test.pageobjects.InstructorCourseDetailsPage;
import teammates.test.pageobjects.InstructorCourseEnrollPage;
import teammates.test.pageobjects.InstructorCourseStudentDetailsEditPage;
import teammates.test.pageobjects.InstructorCourseStudentDetailsViewPage;
import teammates.test.pageobjects.InstructorStudentListPage;
import teammates.test.pageobjects.InstructorStudentRecordsPage;

/**
 * Covers the 'student list' view for instructors.
 */
public class InstructorStudentListPageUiTest extends BaseUiTestCase {
    private static Browser browser;
    private static InstructorStudentListPage viewPage;
    private static DataBundle testData;

    @BeforeClass
    public static void classSetup() throws Exception {
        printTestClassHeader();
        testData = loadDataBundle("/InstructorStudentListPageUiTest.json");
        restoreTestDataOnServer(testData);
        browser = BrowserPool.getBrowser();
    }
    
    @Test
    public void testAll() throws Exception{
        
        testContent();
        testShowPhoto();
        testLinks();
        testSearch();
        testDeleteAction();
        testSearchScript();
        testDisplayArchive();
    }
    
    private void testSearch() {
        
        InstructorAttributes instructorWith2Courses = testData.instructors.get("instructorOfCourse2");
        String instructorId = instructorWith2Courses.googleId;
        
        Url viewPageUrl = createUrl(Const.ActionURIs.INSTRUCTOR_STUDENT_LIST_PAGE)
            .withUserId(instructorId);
        
        ______TS("content: search no match");
        viewPage = loginAdminToPage(browser, viewPageUrl, InstructorStudentListPage.class);
        viewPage.setSearchKey("noMatch");
        viewPage.verifyHtmlMainContent("/instructorStudentListPageSearchNoMatch.html");

        ______TS("content: search student");
        
        viewPage = loginAdminToPage(browser, viewPageUrl, InstructorStudentListPage.class);
        viewPage.setSearchKey("charlie");
        viewPage.verifyHtmlMainContent("/instructorStudentListPageSearchStudent.html");
        
    }

    private void testContent() {
        String instructorId;
        
        ______TS("content: 2 course with students");
        
        InstructorAttributes instructorWith2Courses = testData.instructors.get("instructorOfCourse2");
        instructorId = instructorWith2Courses.googleId;
        
        Url viewPageUrl = createUrl(Const.ActionURIs.INSTRUCTOR_STUDENT_LIST_PAGE)
            .withUserId(instructorId);
        
        viewPage = loginAdminToPage(browser, viewPageUrl, InstructorStudentListPage.class);
        viewPage.checkCourse(0);
        viewPage.checkCourse(1);
        viewPage.verifyHtmlAjax("/instructorStudentListWithHelperView.html");
        
        // update current instructor privileges
        BackDoor.deleteInstructor(instructorWith2Courses.courseId, instructorWith2Courses.email);
        instructorWith2Courses.privileges = instructorWith2Courses.getInstructorPrivilegesFromText();
        instructorWith2Courses.privileges.setDefaultPrivilegesForCoowner();
        instructorWith2Courses.instructorPrivilegesAsText = instructorWith2Courses.getTextFromInstructorPrivileges();
        BackDoor.createInstructor(instructorWith2Courses);
        
        viewPage = loginAdminToPage(browser, viewPageUrl, InstructorStudentListPage.class);
        viewPage.checkCourse(0);
        viewPage.checkCourse(1);
        viewPage.verifyHtmlAjax("/instructorStudentList.html");
        
        ______TS("content: 1 course with no students");
        
        instructorId = testData.instructors.get("instructorOfCourse1").googleId;
        
        viewPageUrl = createUrl(Const.ActionURIs.INSTRUCTOR_STUDENT_LIST_PAGE)
            .withUserId(instructorId);
        
        viewPage = loginAdminToPage(browser, viewPageUrl, InstructorStudentListPage.class);
        viewPage.checkCourse(0);
        viewPage.verifyHtmlAjax("/instructorStudentListPageNoStudent.html");

        
        ______TS("content: no course");
        
        instructorId = testData.accounts.get("instructorWithoutCourses").googleId;
        
        viewPageUrl = createUrl(Const.ActionURIs.INSTRUCTOR_STUDENT_LIST_PAGE)
                .withUserId(instructorId);
            
        viewPage = loginAdminToPage(browser, viewPageUrl, InstructorStudentListPage.class);
        viewPage.verifyHtmlMainContent("/instructorStudentListPageNoCourse.html");
    }

    private void testShowPhoto() {
        String instructorId = testData.instructors.get("instructorOfCourse2").googleId;
        Url viewPageUrl = createUrl(Const.ActionURIs.INSTRUCTOR_STUDENT_LIST_PAGE)
                    .withUserId(instructorId);
            
        viewPage = loginAdminToPage(browser, viewPageUrl, InstructorStudentListPage.class);
        
        ______TS("default image");
        
        StudentAttributes student = testData.students.get("Student1Course2");
        viewPage.checkCourse(0);
        viewPage.checkCourse(1);
        
        viewPage.clickShowPhoto(student.course, student.name);
        viewPage.verifyProfilePhotoIsDefault(student.course, student.name);
        
        ______TS("student has uploaded an image");
        
        //TODO: implement this method after a backend way to upload to cloud storage
        // has been implemented
        
    }
    
    public void testLinks() throws Exception{
        
        ______TS("link: enroll");
        String courseId = testData.courses.get("course2").id;
        InstructorCourseEnrollPage enrollPage = viewPage.clickEnrollStudents(courseId);
        enrollPage.verifyIsCorrectPage(courseId);
        viewPage = enrollPage.goToPreviousPage(InstructorStudentListPage.class);
        
        ______TS("link: view");
        
        StudentAttributes student1 = testData.students.get("Student2Course2");
        viewPage.checkCourse(0);
        viewPage.checkCourse(1);
        ThreadHelper.waitFor(500);
        InstructorCourseStudentDetailsViewPage studentDetailsPage = viewPage.clickViewStudent(student1.course, student1.name);
        studentDetailsPage.verifyIsCorrectPage(student1.email);
        viewPage = studentDetailsPage.goToPreviousPage(InstructorStudentListPage.class);
        
        ______TS("link: edit");
        
        StudentAttributes student2 = testData.students.get("Student3Course3");
        viewPage.checkCourse(0);
        viewPage.checkCourse(1);
        ThreadHelper.waitFor(500);
        InstructorCourseStudentDetailsEditPage studentEditPage = viewPage.clickEditStudent(student2.course, student2.name);
        studentEditPage.verifyIsCorrectPage(student2.email);
        studentEditPage.submitButtonClicked();
        
        InstructorAttributes instructorWith2Courses = testData.instructors.get("instructorOfCourse2");
        String instructorId = instructorWith2Courses.googleId;
        Url viewPageUrl = createUrl(Const.ActionURIs.INSTRUCTOR_STUDENT_LIST_PAGE)
            .withUserId(instructorId);
        viewPage = loginAdminToPage(browser, viewPageUrl, InstructorStudentListPage.class);
        
        ______TS("link: view records");
        
        viewPage.checkCourse(0);
        viewPage.checkCourse(1);
        ThreadHelper.waitFor(500);
        InstructorStudentRecordsPage studentRecordsPage = viewPage.clickViewRecordsStudent(student2.course, student2.name);
        studentRecordsPage.verifyIsCorrectPage(student2.name);
        viewPage = studentRecordsPage.goToPreviousPage(InstructorStudentListPage.class);
    }
    
    private void testDeleteAction() {
        InstructorAttributes instructorWith2Courses = testData.instructors.get("instructorOfCourse2");
        String instructorId = instructorWith2Courses.googleId;
        
        Url viewPageUrl = createUrl(Const.ActionURIs.INSTRUCTOR_STUDENT_LIST_PAGE)
            .withUserId(instructorId);

        ______TS("action: delete");
        
        viewPage = loginAdminToPage(browser, viewPageUrl, InstructorStudentListPage.class);
        viewPage.checkCourse(0);
        viewPage.checkCourse(1);
        ThreadHelper.waitFor(500);
        String studentName = testData.students.get("Student2Course2").name;
        String studentEmail = testData.students.get("Student2Course2").email;
        String courseId = testData.courses.get("course2").id;
        
        viewPage.clickDeleteAndCancel(courseId, studentName);
        assertNotNull(BackDoor.getStudent(courseId, studentEmail));

        String expectedStatus = "The student has been removed from the course";
        viewPage.clickDeleteAndConfirm(courseId, studentName);
        InstructorCourseDetailsPage courseDetailsPage = viewPage.changePageType(InstructorCourseDetailsPage.class);
        courseDetailsPage.verifyStatus(expectedStatus);
    }
    
    private void testSearchScript() {
        // already covered under testContent() ______TS("content: search active")
    }
    
    private void testDisplayArchive() {
        String instructorId = testData.instructors.get("instructorOfCourse4").googleId;
        Url viewPageUrl = createUrl(Const.ActionURIs.INSTRUCTOR_STUDENT_LIST_PAGE)
                .withUserId(instructorId);
        viewPage = loginAdminToPage(browser, viewPageUrl, InstructorStudentListPage.class);
    
        ______TS("action: display archive");
        
        viewPage.clickDisplayArchiveOptions();
        viewPage.checkCourse(0);
        viewPage.checkCourse(1);
        viewPage.checkCourse(2);
        viewPage.verifyHtmlAjax("/instructorStudentListPageDisplayArchivedCourses.html");
        
        ______TS("action: hide archive");
        
        viewPage.clickDisplayArchiveOptions();
        viewPage.checkCourse(0);
        viewPage.checkCourse(1);
        viewPage.verifyHtmlAjax("/instructorStudentListPageHideArchivedCourses.html");
        
        ______TS("action: re-display archive");
        
        viewPage.clickDisplayArchiveOptions();viewPage.checkCourse(0);
        viewPage.checkCourse(1);
        viewPage.checkCourse(2);
        viewPage.verifyHtmlAjax("/instructorStudentListPageDisplayArchivedCourses.html");
    }
    
    @AfterClass
    public static void classTearDown() throws Exception {
        BrowserPool.release(browser);
    }
}