package teammates.test.cases.ui;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.util.Const;

public class InstructorEvalSubmissionEditPageActionTest extends BaseActionTest {

    // private final DataBundle dataBundle = getTypicalDataBundle();

    
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
		// restoreTypicalDataInDatastore();
        uri = Const.ActionURIs.INSTRUCTOR_EVAL_SUBMISSION_EDIT;
    }
    
    @Test
    public void testExecuteAndPostProcess() throws Exception{
        
        //TODO: implement this
    }
    
    
}
