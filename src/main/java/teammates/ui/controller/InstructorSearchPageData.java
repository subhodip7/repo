package teammates.ui.controller;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CommentSearchResultBundle;
import teammates.common.datatransfer.FeedbackResponseCommentSearchResultBundle;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.StudentSearchResultBundle;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.common.util.Url;

public class InstructorSearchPageData extends PageData {

    private final String photoUrl = Const.ActionURIs.STUDENT_PROFILE_PICTURE + "?" + 
                Const.ParamsNames.STUDENT_EMAIL+"=%s&" + 
                Const.ParamsNames.COURSE_ID + "=%s";

    public CommentSearchResultBundle commentSearchResultBundle = new CommentSearchResultBundle();
    public FeedbackResponseCommentSearchResultBundle feedbackResponseCommentSearchResultBundle = new FeedbackResponseCommentSearchResultBundle();
    public StudentSearchResultBundle studentSearchResultBundle = new StudentSearchResultBundle();
    public String searchKey = "";
    public int totalResultsSize;
    public boolean isSearchCommentForStudents;
    public boolean isSearchCommentForResponses;
    public boolean isSearchForStudents;
    
    public InstructorSearchPageData(AccountAttributes account) {
        super(account);
    }

    public String getStudentPhotoUrl(StudentAttributes student) {
        return String.format(photoUrl, StringHelper.encrypt(student.email),  StringHelper.encrypt(student.course));
    }

    public String getCourseStudentDetailsLink(String courseId, StudentAttributes student){
        String link = Const.ActionURIs.INSTRUCTOR_COURSE_STUDENT_DETAILS_PAGE;
        link = Url.addParamToUrl(link,Const.ParamsNames.COURSE_ID,courseId);
        link = Url.addParamToUrl(link,Const.ParamsNames.STUDENT_EMAIL,student.email);
        link = addUserIdToUrl(link);
        return link;
    }
    
    public String getCourseStudentEditLink(String courseId, StudentAttributes student){
        String link = Const.ActionURIs.INSTRUCTOR_COURSE_STUDENT_DETAILS_EDIT;
        link = Url.addParamToUrl(link,Const.ParamsNames.COURSE_ID,courseId);
        link = Url.addParamToUrl(link,Const.ParamsNames.STUDENT_EMAIL,student.email);
        link = addUserIdToUrl(link);
        return link;
    }
    
    //TODO: create another delete action which redirects to studentListPage?
    public String getCourseStudentDeleteLink(String courseId, StudentAttributes student){
        String link = Const.ActionURIs.INSTRUCTOR_COURSE_STUDENT_DELETE;
        link = Url.addParamToUrl(link,Const.ParamsNames.COURSE_ID,courseId);
        link = Url.addParamToUrl(link,Const.ParamsNames.STUDENT_EMAIL,student.email);
        link = addUserIdToUrl(link);
        return link;
    }
    
    public String getStudentRecordsLink(String courseId, StudentAttributes student){
        String link = Const.ActionURIs.INSTRUCTOR_STUDENT_RECORDS_PAGE;
        link = Url.addParamToUrl(link,Const.ParamsNames.COURSE_ID,courseId);
        link = Url.addParamToUrl(link,Const.ParamsNames.STUDENT_EMAIL,student.email);
        link = addUserIdToUrl(link);
        return link;
    }
}
