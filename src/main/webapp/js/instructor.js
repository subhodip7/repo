/*
 * This Javascript file is included in all instructor pages. Functions here
 * should be common to some/all instructor pages.
 */



//Initial load-up
//-----------------------------------------------------------------------------

window.addEventListener('load', function (){
    if(typeof doPageSpecificOnload !== 'undefined'){
        doPageSpecificOnload();
    };
});


$(function() { 
    $("[data-toggle='tooltip']").tooltip({html: true}); 
});

//-----------------------------------------------------------------------------


/**
 * Function that shows confirmation dialog for deleting a course
 * @param courseID
 * @returns
 */
function toggleDeleteCourseConfirmation(courseID) {
    return confirm("Are you sure you want to delete the course: " + courseID + "? " +
            "This operation will delete all students and evaluations in this course. " +
            "All instructors of this course will not be able to access it hereafter as well.");
}

/**
 * Pops up confirmation dialog whether to delete specified evaluation
 * @param courseID
 * @param name
 * @returns
 */
function toggleDeleteEvaluationConfirmation(courseID, name) {
    return confirm("Are you sure you want to delete the evaluation " + name + " in " + courseID + "?");
}

/**
 * Pops up confirmation dialog whether to delete specified evaluation
 * @param courseID
 * @param name
 * @returns
 */
function toggleDeleteFeedbackSessionConfirmation(courseID, name) {
    return confirm("Are you sure you want to delete the feedback session " + name + " in " + courseID + "?");
}

/**
 * Pops up confirmation dialog whether to publish the specified
 * evaluation
 * @param name
 */
function togglePublishEvaluation(name) {
    return confirm("Are you sure you want to publish the evaluation " + name + "?");
}

/**
 * Pops up confirmation dialog whether to unpublish the specified
 * evaluation
 * @param name
 */
function toggleUnpublishEvaluation(name){
    return confirm("Are you sure you want to unpublish the evaluation " + name + "?");
}

/**
 * Pops up confirmation dialog whether to remind students to fill in a specified
 * evaluation.
 * @param courseID
 * @param evaluationName
 */
function toggleRemindStudents(evaluationName) {
    return confirm("Send e-mails to remind students who have not submitted their evaluations for " + evaluationName + "?");
}




/**
 * Checks whether a team's name is valid
 * Used in instructorCourseEnroll page (through instructorCourseEnroll.js)
 * @param teamName
 * @returns {Boolean}
 */
function isStudentTeamNameValid(teamName) {
    return teamName.length<=TEAMNAME_MAX_LENGTH;
}

/**
 * To check whether a student's name and team name are valid
 * @param editName
 * @param editTeamName
 * @returns {Boolean}
 */
function isStudentInputValid(editName, editTeamName, editEmail) {
    if (editName == "" || editTeamName == "" || editEmail == "") {
        setStatusMessage(DISPLAY_FIELDS_EMPTY,true);
        return false;
    } else if (!isNameValid(editName)) {
        setStatusMessage(DISPLAY_NAME_INVALID,true);
        return false;
    } else if (!isStudentTeamNameValid(editTeamName)) {
        setStatusMessage(DISPLAY_STUDENT_TEAMNAME_INVALID,true);
        return false;
    } else if (!isEmailValid(editEmail)){
        setStatusMessage(DISPLAY_EMAIL_INVALID,true);
        return false;
    }
    return true;
}
