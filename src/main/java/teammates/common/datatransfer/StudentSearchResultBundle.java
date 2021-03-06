package teammates.common.datatransfer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import teammates.common.util.Const;
import teammates.logic.core.InstructorsLogic;
import teammates.logic.core.StudentsLogic;

import com.google.appengine.api.search.Cursor;
import com.google.appengine.api.search.Results;
import com.google.appengine.api.search.ScoredDocument;
import com.google.gson.Gson;

public class StudentSearchResultBundle extends SearchResultBundle {

    public List<StudentAttributes> studentList = new ArrayList<StudentAttributes>();
    public Map<String, InstructorAttributes> instructors = new HashMap<String, InstructorAttributes>();
    public Cursor cursor = null;
    private int numberOfResults = 0;
    private StudentsLogic studentsLogic = StudentsLogic.inst();
    
    public StudentSearchResultBundle(){}
    
    public StudentSearchResultBundle fromResults(Results<ScoredDocument> results, String googleId){
        if(results == null) 
            return this;
        
        cursor = results.getCursor();
        List<InstructorAttributes> instructorRoles = InstructorsLogic.inst().getInstructorsForGoogleId(googleId);
        List<String> giverEmailList = new ArrayList<String>();
        for(InstructorAttributes ins:instructorRoles){
            giverEmailList.add(ins.email);
            instructors.put(ins.courseId, ins);
        }
        
        for(ScoredDocument doc:results){
            StudentAttributes student = new Gson().fromJson(
                    doc.getOnlyField(Const.SearchDocumentField.STUDENT_ATTRIBUTE).getText(), 
                    StudentAttributes.class);
            if(studentsLogic.getStudentForRegistrationKey(student.key) == null){
                studentsLogic.deleteDocument(student);
                continue;
            }
            
            studentList.add(student);
            numberOfResults++;
        }
        
        Collections.sort(studentList, new Comparator<StudentAttributes>(){
            @Override
            public int compare(StudentAttributes s1, StudentAttributes s2){
                int compareResult = s1.course.compareTo(s2.course);
                if(compareResult != 0){
                    return compareResult;
                }
                
                compareResult = s1.section.compareTo(s2.section);
                if(compareResult != 0){
                    return compareResult;
                }
                
                compareResult = s1.team.compareTo(s2.team);
                if(compareResult != 0){
                    return compareResult;
                }
                
                compareResult = s1.name.compareTo(s2.name);
                if(compareResult != 0){
                    return compareResult;
                }
                
                return s1.email.compareTo(s2.email);
            }
        });
        
        return this;
    }

    @Override
    public int getResultSize() {
        return numberOfResults;
    }
}
