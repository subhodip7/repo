package teammates.test.cases.ui.browsertests;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;

import org.openqa.selenium.By;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.util.Const;
import teammates.common.util.ThreadHelper;
import teammates.common.util.Url;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;
import teammates.test.pageobjects.InstructorFeedbackEditPage;
import teammates.test.pageobjects.InstructorFeedbackResultsPage;
import teammates.test.util.Priority;

/**
 * Tests 'Feedback Results' view of instructors.
 * SUT: {@link InstructorFeedbackResultsPage}.
 */
@Priority(1)
public class InstructorFeedbackResultsPageUiTest extends BaseUiTestCase {

    private static DataBundle testData;
    private static Browser browser;
    private InstructorFeedbackResultsPage resultsPage;
        
    @BeforeClass
    public static void classSetup() throws Exception {
        printTestClassHeader();
        testData = loadDataBundle("/InstructorFeedbackResultsPageUiTest.json");
        removeAndRestoreTestDataOnServer(testData);
        browser = BrowserPool.getBrowser();
    }
    
    @Test
    public void testAll() throws Exception {
        testContent();
        testAjaxForLargeScaledSession();
        testSortAction();
        testFilterAction();
        testPanelsCollapseExpand();
        testShowStats();
        testSearchScript();
        testFeedbackResponseCommentActions();
        testDownloadAction();
        testLink();
    }

    public void testContent(){
        
        ______TS("standard session results");
        
        resultsPage = loginToInstructorFeedbackResultsPage("CFResultsUiT.instr", "Open Session");
        resultsPage.verifyHtml("/instructorFeedbackResultsPageOpen.html");
        
        ______TS("standard session results with helper view");
        
        resultsPage = loginToInstructorFeedbackResultsPage("CFResultsUiT.helper", "Open Session");
        resultsPage.verifyHtmlMainContent("/instructorFeedbackResultsPageOpenWithHelperView.html");
        
        ______TS("empty session");
        
        resultsPage = loginToInstructorFeedbackResultsPage("CFResultsUiT.instr", "Empty Session");
        resultsPage.verifyHtmlMainContent("/instructorFeedbackResultsPageEmpty.html");
    
        //TODO: test content for results page for different question types, and views.
    }
    
    public void testSortAction(){
        
        ______TS("test sort types");
        
        resultsPage = loginToInstructorFeedbackResultsPage("CFResultsUiT.instr", "Open Session");
        resultsPage.displayByGiverRecipientQuestion();
        
        assertEquals("[more]", resultsPage.getQuestionAdditionalInfoButtonText(9,"giver-1-recipient-1"));
        assertEquals(true, resultsPage.clickQuestionAdditionalInfoButton(9,"giver-1-recipient-1"));
        assertEquals("[less]", resultsPage.getQuestionAdditionalInfoButtonText(9,"giver-1-recipient-1"));
        assertEquals(false, resultsPage.clickQuestionAdditionalInfoButton(9,"giver-1-recipient-1"));
        assertEquals("[more]", resultsPage.getQuestionAdditionalInfoButtonText(9,"giver-1-recipient-1"));

        assertEquals(true, resultsPage.clickQuestionAdditionalInfoButton(10,"giver-1-recipient-1"));
        assertEquals(false, resultsPage.clickQuestionAdditionalInfoButton(10,"giver-1-recipient-1"));

        assertEquals(true, resultsPage.clickQuestionAdditionalInfoButton(11,"giver-1-recipient-1"));
        assertEquals(false, resultsPage.clickQuestionAdditionalInfoButton(11,"giver-1-recipient-1"));

        assertEquals(true, resultsPage.clickQuestionAdditionalInfoButton(12,"giver-1-recipient-1"));
        assertEquals(false, resultsPage.clickQuestionAdditionalInfoButton(12,"giver-1-recipient-1"));

        resultsPage.verifyHtmlMainContent("/instructorFeedbackResultsSortGiverRecipientQuestion.html");
        
        resultsPage.displayByRecipientGiverQuestion();
        resultsPage.verifyHtmlMainContent("/instructorFeedbackResultsSortRecipientGiverQuestion.html");

        assertEquals("[more]", resultsPage.getQuestionAdditionalInfoButtonText(9,"giver-1-recipient-1"));
        assertEquals(true, resultsPage.clickQuestionAdditionalInfoButton(9,"giver-1-recipient-1"));
        assertEquals("[less]", resultsPage.getQuestionAdditionalInfoButtonText(9,"giver-1-recipient-1"));
        assertEquals(false, resultsPage.clickQuestionAdditionalInfoButton(9,"giver-1-recipient-1"));
        assertEquals("[more]", resultsPage.getQuestionAdditionalInfoButtonText(9,"giver-1-recipient-1"));

        assertEquals(true, resultsPage.clickQuestionAdditionalInfoButton(10,"giver-1-recipient-1"));
        assertEquals(false, resultsPage.clickQuestionAdditionalInfoButton(10,"giver-1-recipient-1"));

        assertEquals(true, resultsPage.clickQuestionAdditionalInfoButton(11,"giver-1-recipient-1"));
        assertEquals(false, resultsPage.clickQuestionAdditionalInfoButton(11,"giver-1-recipient-1"));

        assertEquals(true, resultsPage.clickQuestionAdditionalInfoButton(12,"giver-1-recipient-1"));
        assertEquals(false, resultsPage.clickQuestionAdditionalInfoButton(12,"giver-1-recipient-1"));
        
        
        resultsPage.displayByGiverQuestionRecipient();
        resultsPage.verifyHtmlMainContent("/instructorFeedbackResultsSortGiverQuestionRecipient.html");
        
        resultsPage.displayByRecipientQuestionGiver();
        resultsPage.verifyHtmlMainContent("/instructorFeedbackResultsSortRecipientQuestionGiver.html");
        
        //Sorted by team
        resultsPage.clickGroupByTeam();
        
        resultsPage.displayByGiverRecipientQuestion();
        resultsPage.verifyHtmlMainContent("/instructorFeedbackResultsSortGiverRecipientQuestionTeam.html");

        resultsPage.displayByRecipientGiverQuestion();
        resultsPage.verifyHtmlMainContent("/instructorFeedbackResultsSortRecipientGiverQuestionTeam.html");
        
        resultsPage.displayByGiverQuestionRecipient();
        resultsPage.verifyHtmlMainContent("/instructorFeedbackResultsSortGiverQuestionRecipientTeam.html");
        
        resultsPage.displayByRecipientQuestionGiver();
        resultsPage.verifyHtmlMainContent("/instructorFeedbackResultsSortRecipientQuestionGiverTeam.html");
        
        
        //By question
        resultsPage.displayByQuestion();
        resultsPage.verifyHtmlMainContent("/instructorFeedbackResultsSortQuestion.html");
        
        assertEquals("[more]", resultsPage.getQuestionAdditionalInfoButtonText(9,""));
        assertEquals(true, resultsPage.clickQuestionAdditionalInfoButton(9,""));
        assertEquals("[less]", resultsPage.getQuestionAdditionalInfoButtonText(9,""));
        assertEquals(false, resultsPage.clickQuestionAdditionalInfoButton(9,""));
        assertEquals("[more]", resultsPage.getQuestionAdditionalInfoButtonText(9,""));
        
        assertEquals(true, resultsPage.clickQuestionAdditionalInfoButton(10,""));
        assertEquals(false, resultsPage.clickQuestionAdditionalInfoButton(10,""));

        assertEquals(true, resultsPage.clickQuestionAdditionalInfoButton(11,""));
        assertEquals(false, resultsPage.clickQuestionAdditionalInfoButton(11,""));

        assertEquals(true, resultsPage.clickQuestionAdditionalInfoButton(12,""));
        assertEquals(false, resultsPage.clickQuestionAdditionalInfoButton(12,""));

        
        ______TS("test in-table sort");
        
        verifySortingOrder(By.id("button_sortFeedback"), 
                "1 Response to Danny.", 
                "2 Response to Benny.", 
                "3 Response to Emily.", 
                "4 Response to Charlie.");

        verifySortingOrder(By.id("button_sortFromName"), 
                "Alice Betsy",
                "Benny Charles",
                "Benny Charles",
                "Charlie Dávis");
        
        verifySortingOrder(By.id("button_sortFromTeam"), 
                "Team 1",
                "Team 1",
                "Team 2",
                "Team 2");

        verifySortingOrder(By.id("button_sortToName"), 
                "Benny Charles", 
                "Charlie Dávis", 
                "Danny Engrid",
                "Emily");

        /*Omitted as unable to check both forward and reverse order in one go
         * //TODO: split up verifySortingOrder to enable this test
        verifySortingOrder(By.id("button_sortToTeam"), 
                "Team 2{*}Team 3",
                "Team 1{*}Team 2",
                "Team 1{*}Team 2",
                "Team 1{*}Team 1");
        */
        
    }

    public void testFilterAction() {

        ______TS("filter by section A");

        resultsPage.filterResponsesForSection("Section A");
        resultsPage.verifyHtmlMainContent("/instructorFeedbackResultsFilteredBySectionA.html");
        
        ______TS("filter by section B, no responses");
        
        resultsPage.filterResponsesForSection("Section B");
        resultsPage.verifyHtmlMainContent("/instructorFeedbackResultsFilteredBySectionB.html");
        
        resultsPage.filterResponsesForAllSections();
        
    }
    
    public void testPanelsCollapseExpand(){
        ______TS("panels expand/collapse");
        
        assertEquals(resultsPage.collapseExpandButton.getText(),"Collapse Questions");
        assertTrue(resultsPage.verifyAllResultsPanelBodyVisibility(true));
        
        resultsPage.clickCollapseExpand();
        ThreadHelper.waitFor(2500);
        assertEquals(resultsPage.collapseExpandButton.getText(),"Expand Questions");
        assertTrue(resultsPage.verifyAllResultsPanelBodyVisibility(false));
        

        resultsPage.clickCollapseExpand();
        ThreadHelper.waitFor(2500);
        assertEquals(resultsPage.collapseExpandButton.getText(),"Collapse Questions");
        assertTrue(resultsPage.verifyAllResultsPanelBodyVisibility(true));
        
    }
    
    public void testShowStats(){
        ______TS("show stats");
        
        assertEquals(resultsPage.showStatsCheckbox.getAttribute("checked"),"true");
        assertTrue(resultsPage.verifyAllStatsVisibility());
        
        resultsPage.clickShowStats();
        assertEquals(resultsPage.showStatsCheckbox.getAttribute("checked"),null);
        assertFalse(resultsPage.verifyAllStatsVisibility());
        

        resultsPage.clickShowStats();
        assertEquals(resultsPage.showStatsCheckbox.getAttribute("checked"),"true");
        assertTrue(resultsPage.verifyAllStatsVisibility());
        
    }
    
    public void testSearchScript(){
        ______TS("test search/filter script");
        
        resultsPage.fillSearchBox("question 1");
        ThreadHelper.waitFor(3000);
        resultsPage.verifyHtmlMainContent("/instructorFeedbackResultsSortQuestionSearch.html");
    }
    
    public void testFeedbackResponseCommentActions() {
        
        resultsPage = loginToInstructorFeedbackResultsPage("CFResultsUiT.instr", "Open Session");
        resultsPage.displayByRecipientGiverQuestion();
        
        ______TS("failure: add empty feedback response comment");
        
        resultsPage.addFeedbackResponseComment("");
        resultsPage.verifyCommentFormErrorMessage("-0-1-1", Const.StatusMessages.FEEDBACK_RESPONSE_COMMENT_EMPTY);
        
        ______TS("action: add new feedback response comments");
        
        resultsPage.displayByRecipientGiverQuestion();
        resultsPage.addFeedbackResponseComment("test comment 1");
        resultsPage.addFeedbackResponseComment("test comment 2");
        resultsPage.verifyCommentRowContent("-0",
                "test comment 1", "CFResultsUiT.instr@gmail.com");
        resultsPage.verifyCommentRowContent("-1",
                "test comment 2", "CFResultsUiT.instr@gmail.com");
        
        resultsPage = loginToInstructorFeedbackResultsPage("CFResultsUiT.instr", "Open Session");
        resultsPage.displayByRecipientGiverQuestion();
        resultsPage.verifyCommentRowContent("-0-1-1-1",
                "test comment 1", "CFResultsUiT.instr@gmail.com");
        resultsPage.verifyCommentRowContent("-0-1-1-2",
                "test comment 2", "CFResultsUiT.instr@gmail.com");
        
        ______TS("action: edit existing feedback response comment");

        resultsPage.editFeedbackResponseComment("-0-1-1-1",
                "edited test comment");
        resultsPage.verifyCommentRowContent("-0-1-1-1",
                "edited test comment", "CFResultsUiT.instr@gmail.com");
        
        ______TS("action: delete existing feedback response comment");

        resultsPage.deleteFeedbackResponseComment("-0-1-1-1");
        resultsPage.verifyRowMissing("-0-1-1-1");
        
        resultsPage = loginToInstructorFeedbackResultsPage("CFResultsUiT.instr", "Open Session");
        resultsPage.displayByRecipientGiverQuestion();
        resultsPage.verifyCommentRowContent("-0-1-1-1",
                "test comment 2", "CFResultsUiT.instr@gmail.com");
        
        ______TS("action: add edit and delete successively");
        
        resultsPage.displayByRecipientGiverQuestion();
        resultsPage.addFeedbackResponseComment("successive action comment");
        resultsPage.verifyCommentRowContent("-0",
                "successive action comment", "CFResultsUiT.instr@gmail.com");
        
        resultsPage.editFeedbackResponseComment("-0",
                "edited successive action comment");
        resultsPage.verifyCommentRowContent("-0",
                "edited successive action comment", "CFResultsUiT.instr@gmail.com");
        
        resultsPage.deleteFeedbackResponseComment("-0");
        resultsPage.verifyRowMissing("-0");
        
        resultsPage = loginToInstructorFeedbackResultsPage("CFResultsUiT.instr", "Open Session");
        resultsPage.displayByRecipientGiverQuestion();
        resultsPage.verifyCommentRowContent("-0-1-1-1",
                "test comment 2", "CFResultsUiT.instr@gmail.com");
        resultsPage.verifyRowMissing("-0-1-1-2");
        
    }
    
    private void testDownloadAction() {
        ______TS("action: download report");
        
        Url reportUrl = createUrl(Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESULTS_DOWNLOAD)
            .withUserId("CFResultsUiT.instr")
            .withCourseId("CFResultsUiT.CS2104")
            .withSessionName("First Session");
        
        resultsPage.verifyDownloadLink(reportUrl);
        
        ______TS("action: download report unsuccessfully");
        
        reportUrl = createUrl(Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESULTS_DOWNLOAD)
            .withUserId("CFResultsUiT.instr");
        browser.driver.get(reportUrl.toString());
        String afterReportDownloadUrl = browser.driver.getCurrentUrl();
        assertFalse(reportUrl.equals(afterReportDownloadUrl));
        
        //return to the previous page
        loginToInstructorFeedbackResultsPage("CFResultsUiT.instr", "Open Session");
    }
    
    public void testLink() {
        ______TS("action: edit");
        InstructorFeedbackEditPage editPage = resultsPage.clickEditLink();
        editPage.verifyContains("Edit Feedback Session");
        editPage.verifyContains("CFResultsUiT.CS2104");
        editPage.verifyContains("First Session");
    }
    
    public void testAjaxForLargeScaledSession() {
        
        ______TS("Ajax for view by questions");
        
        resultsPage = loginToInstructorFeedbackResultsPageWithViewType("CFResultsUiT.instr", "Open Session", true, "question");
        
        resultsPage.clickAjaxPanel(0);
       
        resultsPage.verifyHtmlAjax("/instructorFeedbackResultsAjaxByQuestion.html");
        
        
        ______TS("Ajax for view by giver > recipient > question");
        
        resultsPage = loginToInstructorFeedbackResultsPageWithViewType("CFResultsUiT.instr", "Open Session", true, "giver-recipient-question");
        
        resultsPage.clickAjaxPanel(0);
        resultsPage.verifyHtmlAjax("/instructorFeedbackResultsAjaxByGRQ.html");
        
        ______TS("Ajax for view by giver > question > recipient");
        
        resultsPage = loginToInstructorFeedbackResultsPageWithViewType("CFResultsUiT.instr", "Open Session", true, "giver-question-recipient");
        
        resultsPage.clickAjaxPanel(0);
        resultsPage.verifyHtmlAjax("/instructorFeedbackResultsAjaxByGQR.html");
        
        ______TS("Ajax for view by recipient > question > giver");
        
        resultsPage = loginToInstructorFeedbackResultsPageWithViewType("CFResultsUiT.instr", "Open Session", true, "recipient-question-giver");
        
        resultsPage.clickAjaxPanel(0);
        resultsPage.verifyHtmlAjax("/instructorFeedbackResultsAjaxByRQG.html");
        
        ______TS("Ajax for view by recipient > giver > question");
        
        resultsPage = loginToInstructorFeedbackResultsPageWithViewType("CFResultsUiT.instr", "Open Session", true, "recipient-giver-question");
        
        resultsPage.clickAjaxPanel(0);
        resultsPage.verifyHtmlAjax("/instructorFeedbackResultsAjaxByRGQ.html");
        
    }


    @AfterClass
    public static void classTearDown() throws Exception {
        BrowserPool.release(browser);
    }
    
    private InstructorFeedbackResultsPage loginToInstructorFeedbackResultsPage(String instructorName, String fsName) {
        Url editUrl = createUrl(Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESULTS_PAGE)
                    .withUserId(testData.instructors.get(instructorName).googleId)
                    .withCourseId(testData.feedbackSessions.get(fsName).courseId)
                    .withSessionName(testData.feedbackSessions.get(fsName).feedbackSessionName);
        
        return loginAdminToPage(browser, editUrl, InstructorFeedbackResultsPage.class);
    }
    
    private InstructorFeedbackResultsPage loginToInstructorFeedbackResultsPageWithViewType(
            String instructorName, String fsName, boolean needAjax, String viewType) {
        Url editUrl = createUrl(Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESULTS_PAGE)
                    .withUserId(testData.instructors.get(instructorName).googleId)
                    .withCourseId(testData.feedbackSessions.get(fsName).courseId)
                    .withSessionName(testData.feedbackSessions.get(fsName).feedbackSessionName);
        
        if(needAjax){
            editUrl = editUrl.withParam(Const.ParamsNames.FEEDBACK_RESULTS_NEED_AJAX, String.valueOf(needAjax));
        }
        
        if(viewType != null){
            editUrl = editUrl.withParam(Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE, viewType);
        }
        
        return loginAdminToPage(browser, editUrl,
                InstructorFeedbackResultsPage.class);
    }
    
    private void verifySortingOrder(By sortIcon, String... values) {
        //check if the rows match the given order of values
        resultsPage.click(sortIcon);
        String searchString = "";
        for (int i = 0; i < values.length; i++) {
            searchString += values[i]+"{*}";
        }
        resultsPage.verifyContains(searchString);
        
        //click the sort icon again and check for the reverse order
        resultsPage.click(sortIcon);
        searchString = "";
        for (int i = values.length; i > 0; i--) {
            searchString += values[i-1]+"{*}";
        }
        resultsPage.verifyContains(searchString);
    }

}