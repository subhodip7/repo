package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertTrue;
import static org.testng.AssertJUnit.assertEquals;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.util.Const;
import teammates.ui.controller.AjaxResult;
import teammates.ui.controller.InstructorStudentListAjaxPageAction;
import teammates.ui.controller.InstructorStudentListAjaxPageData;

public class InstructorStudentListAjaxPageActionTest extends BaseActionTest {
    
    private final DataBundle dataBundle = getTypicalDataBundle();

    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
        restoreTypicalDataInDatastore();
        uri = Const.ActionURIs.INSTRUCTOR_STUDENT_LIST_AJAX_PAGE;
    }

    @Test
    public void testExecuteAndPostProcess() throws Exception {
        InstructorAttributes instructor = dataBundle.instructors.get("instructor3OfCourse1");
        String instructorId = instructor.googleId;

        gaeSimulation.loginAsInstructor(instructorId);
        ______TS("Unsuccessful case: not enough parameters");

        verifyAssumptionFailure();
        
        String[] submissionParams = new String[]{
        };
        
        verifyAssumptionFailure(submissionParams);
        
        ______TS("typical successful case");
        
        submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, instructor.courseId,
        };
        
        InstructorStudentListAjaxPageAction action = getAction(submissionParams);
        AjaxResult result = (AjaxResult) action.executeAndPostProcess();
        InstructorStudentListAjaxPageData data = 
                (InstructorStudentListAjaxPageData) result.data;
        assertEquals(2, data.courseSectionDetails.size());
        assertTrue(data.hasSection);
        assertEquals(5, data.emailPhotoUrlMapping.values().size());
        assertEquals(instructor.courseId, data.course.id);
    }
    
    private InstructorStudentListAjaxPageAction getAction(String... params) throws Exception {
        return (InstructorStudentListAjaxPageAction) (gaeSimulation.getActionObject(uri, params));
    }
}
