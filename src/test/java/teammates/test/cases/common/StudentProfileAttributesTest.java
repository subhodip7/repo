/**
 * 
 */
package teammates.test.cases.common;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.datastore.Text;

import static org.testng.AssertJUnit.assertTrue;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNull;
import teammates.common.datatransfer.StudentProfileAttributes;
import teammates.common.util.FieldValidator;
import teammates.common.util.Sanitizer;
import teammates.common.util.StringHelper;
import teammates.storage.entity.StudentProfile;
import teammates.test.cases.BaseTestCase;
import teammates.test.util.TestHelper;

public class StudentProfileAttributesTest extends BaseTestCase {

    private static StudentProfileAttributes profile;
    
    @BeforeClass
    public static void setupClass() throws Exception {
        printTestClassHeader();
        profile = new StudentProfileAttributes();
        profile.googleId = "valid.googleId";
        profile.shortName = "shor";
        profile.institute = "institute";
        profile.email = "valid@email.com";
        profile.nationality = "nationality";
        profile.gender = "female";
        profile.moreInfo = "moreInfo can have a lot more than this...";
        profile.pictureKey = "profile Pic Key";
    }
    
    @Test
    public void testGetInvalidityInfo() {
        StudentProfileAttributes validProfile = profile;
        StudentProfileAttributes invalidProfile = getInvalidStudentProfileAttributes();
        
        ______TS("Valid Profile Attributes");
        assertTrue("valid: all valid info", validProfile.isValid());
        assertEquals(new ArrayList<String>(), validProfile.getInvalidityInfo());
        
        ______TS("Valid profile with empty attributes");
        validProfile.shortName = "";
        validProfile.email = "";
        validProfile.nationality = "";
        validProfile.institute = "";
        
        assertTrue("valid: all valid info", validProfile.isValid());
        assertEquals(new ArrayList<String>(), validProfile.getInvalidityInfo());
        
        ______TS("Invalid Profile Attributes");
        assertFalse("valid: all valid info", invalidProfile.isValid());
        List<String> expectedErrorMessages = new ArrayList<String>();

        //tests both the constructor and the invalidity info
        expectedErrorMessages.add(String.format(FieldValidator.GOOGLE_ID_ERROR_MESSAGE, profile.googleId, FieldValidator.REASON_TOO_LONG));
        expectedErrorMessages.add(String.format(FieldValidator.PERSON_NAME_ERROR_MESSAGE, profile.shortName, FieldValidator.REASON_CONTAINS_INVALID_CHAR));
        expectedErrorMessages.add(String.format(FieldValidator.EMAIL_ERROR_MESSAGE, profile.email, FieldValidator.REASON_INCORRECT_FORMAT));
        expectedErrorMessages.add(String.format(FieldValidator.INSTITUTE_NAME_ERROR_MESSAGE, profile.institute, FieldValidator.REASON_TOO_LONG));
        expectedErrorMessages.add(String.format(FieldValidator.NATIONALITY_ERROR_MESSAGE, profile.nationality, FieldValidator.REASON_START_WITH_NON_ALPHANUMERIC_CHAR));
        expectedErrorMessages.add(String.format(FieldValidator.GENDER_ERROR_MESSAGE, profile.gender));
        
        TestHelper.isSameContentIgnoreOrder(expectedErrorMessages, invalidProfile.getInvalidityInfo());
    }
    
    @Test
    public void testeGetIdentificationString() {
        assertEquals(profile.googleId, profile.getIdentificationString());
    }
    
    @Test
    public void testSanitizeForSaving() {
        StudentProfileAttributes profileToSanitize = getStudentProfileAttributesToSanitize();
        StudentProfileAttributes profileToSanitizeExpected = getStudentProfileAttributesToSanitize();
        profileToSanitize.sanitizeForSaving();
        
        assertEquals(Sanitizer.sanitizeGoogleId(profileToSanitizeExpected.googleId), profileToSanitize.googleId);
        assertEquals(Sanitizer.sanitizeForHtml(profileToSanitizeExpected.shortName), profileToSanitize.shortName);
        assertEquals(Sanitizer.sanitizeForHtml(profileToSanitizeExpected.institute), profileToSanitize.institute);
        assertEquals(Sanitizer.sanitizeForHtml(profileToSanitizeExpected.email), profileToSanitize.email);
        assertEquals(Sanitizer.sanitizeForHtml(profileToSanitizeExpected.nationality), profileToSanitize.nationality);
        assertEquals(Sanitizer.sanitizeForHtml(profileToSanitizeExpected.gender), profileToSanitize.gender);
        assertEquals(Sanitizer.sanitizeForHtml(profileToSanitizeExpected.moreInfo), profileToSanitize.moreInfo);
    }
    
    @Test
    public void testToEntity() {
        StudentProfile expectedEntity = new StudentProfile(profile.googleId, profile.shortName, profile.institute, profile.email, 
                profile.nationality, profile.gender, new Text(profile.moreInfo), new BlobKey(profile.pictureKey));
        StudentProfileAttributes testProfile = new StudentProfileAttributes(expectedEntity);
        StudentProfile actualEntity = (StudentProfile) testProfile.toEntity();
        assertEquals(expectedEntity.getShortName(), actualEntity.getShortName());
        assertEquals(expectedEntity.getInstitute(), actualEntity.getInstitute());
        assertEquals(expectedEntity.getEmail(), actualEntity.getEmail());
        assertEquals(expectedEntity.getNationality(), actualEntity.getNationality());
        assertEquals(expectedEntity.getGender(), actualEntity.getGender());
        assertEquals(expectedEntity.getMoreInfo(), actualEntity.getMoreInfo());
        assertEquals(expectedEntity.getModifiedDate(), actualEntity.getModifiedDate());
        assertEquals(expectedEntity.getPictureKey(), actualEntity.getPictureKey());
    }
    
    @Test
    public void testToString() {
        StudentProfileAttributes spa = new StudentProfileAttributes((StudentProfile) profile.toEntity());
        profile.modifiedDate = spa.modifiedDate;
        
        // the toString must be unique to the values in the object
        assertEquals(profile.toString(), spa.toString());
    }
    
    public StudentProfileAttributes getInvalidStudentProfileAttributes() {
        
        String googleId = StringHelper.generateStringOfLength(46);
        String shortName = "%%";
        String email = "invalid@email@com";
        String institute = StringHelper.generateStringOfLength(FieldValidator.INSTITUTE_NAME_MAX_LENGTH+1);
        String nationality = "$invalid nationality ";
        String gender = "invalidGender";
        String moreInfo = "Ooops no validation for this one...";
        String pictureKey = "";
        
        return new StudentProfileAttributes(googleId, shortName, email, institute, 
                nationality, gender, moreInfo, pictureKey);
    }
    
    public StudentProfileAttributes getStudentProfileAttributesToSanitize() {
        String googleId = " test.google@gmail.com ";
        String shortName = "<name>";
        String email = "'toSanitize@email.com'";
        String institute = "institute/\"";
        String nationality = "&\"invalid nationality &";
        String gender = "'\"'invalidGender";
        String moreInfo = "<<script> alert('hi!'); </script>";
        String pictureKey = "testPictureKey";
        
        return new StudentProfileAttributes(googleId, shortName, email, institute, 
                nationality, gender, moreInfo, pictureKey);
    }
    
    @Test
    public void testGetEntityTypeAsString() {
        assertEquals("StudentProfile", profile.getEntityTypeAsString());
    }
    
    @AfterClass
    public static void tearDown() {
        printTestClassFooter();
    }

}
