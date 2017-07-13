package com.buildtool;

import org.junit.Before;
import org.junit.Test;
import java.io.File;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Created by abhijeetkedari on 4/22/17.
 */
public class ValidationTest {

    Validation validation;
    @Before
    public void setUp() throws Exception {
        validation = new Validation();
    }

    @Test
    public void getInitialUpdatedDirectoriesTrue(){
        String[] tokens = {"validate_approvals","--approvers", "alovelace", "--changed-files",
                            "src/com/twitter/user/User.java", "src/com/twitter/follow/Follow.java"};

        Set<String> expectedResult = new HashSet<>();
        expectedResult.add("src/com/twitter/user");
        expectedResult.add("src/com/twitter/follow");

        Set<String> result = validation.getInitialUpdatedDirectories(tokens);

        assertEquals(expectedResult,result);
    }

    @Test
    public void getInitialUpdatedDirectoriesFalse(){
        String[] tokens = {"validate_approvals","--approvers", "alovelace", "--changed-files",
                "src/com/twitter/user/User.java", "src/com/twitter/follow/Follow.java"};

        Set<String> expectedResult = new HashSet<>();
        expectedResult.add("src/com/twitter/user");

        Set<String> result = validation.getInitialUpdatedDirectories(tokens);

        assertNotEquals(expectedResult,result);
    }

    @Test
    public void getInitialUpdatedFilesTrue(){
        String[] tokens = {"validate_approvals","--approvers", "alovelace", "--changed-files",
                "src/com/twitter/user/User.java", "src/com/twitter/follow/Follow.java"};

        Set<String> expectedResult = new HashSet<>();
        expectedResult.add("src/com/twitter/user/User.java");
        expectedResult.add("src/com/twitter/follow/Follow.java");

        Set<String> result = validation.getInitialUpdatedFiles(tokens);

        assertEquals(expectedResult,result);
    }

    @Test
    public void getInitialUpdatedFilesFalse(){
        String[] tokens = {"validate_approvals","--approvers", "alovelace", "--changed-files",
                "src/com/twitter/user/User.java", "src/com/twitter/follow/Follow.java"};

        Set<String> expectedResult = new HashSet<>();
        expectedResult.add("src/com/twitter/user/User.java");

        Set<String> result = validation.getInitialUpdatedFiles(tokens);

        assertNotEquals(expectedResult,result);
    }

    @Test
    public void getInitialApproversTrue(){
        String[] tokens = {"validate_approvals","--approvers", "alovelace", "--changed-files",
                "src/com/twitter/user/User.java", "src/com/twitter/follow/Follow.java"};

        Set<String> expectedResult = new HashSet<>();
        expectedResult.add("alovelace");

        Set<String> result = validation.getInitialApprovers(tokens);

        assertEquals(expectedResult,result);
    }

    @Test
    public void getInitialApproversFalse(){
        String[] tokens = {"validate_approvals","--approvers", "alovelace", "--changed-files",
                "src/com/twitter/user/User.java", "src/com/twitter/follow/Follow.java"};

        Set<String> expectedResult = new HashSet<>();
        expectedResult.add("eclarke");

        Set<String> result = validation.getInitialApprovers(tokens);

        assertNotEquals(expectedResult,result);
    }

    @Test
    public void validArgumentsListTrue(){
        String[] tokens = {"validate_approvals","--approvers", "alovelace", "--changed-files",
                "src/com/twitter/user/User.java", "src/com/twitter/follow/Follow.java"};


        boolean result = validation.isValidArgumentsList(tokens);

        assertTrue(result);
    }

    @Test
    public void validArgumentsListFalse(){
        String[] tokens = {"validate_approvals", "alovelace", "--changed-files",
                "src/com/twitter/user/User.java", "src/com/twitter/follow/Follow.java"};

        boolean result = validation.isValidArgumentsList(tokens);

        assertFalse(result);
    }

    @Test
    public void fileExistsTrue(){
        Set<String> input = new HashSet<>();
        input.add("src/com/twitter/user/User.java");
        input.add("src/com/twitter/follow/Follow.java");

        boolean result = validation.isFileExists(input);

        assertTrue(result);
    }

    @Test
    public void fileExistsFalse(){
        Set<String> input = new HashSet<>();
        input.add("src/com/twitter/user/UserTest.java");
        input.add("src/com/twitter/follow/FollowTest.java");

        boolean result = validation.isFileExists(input);

        assertFalse(result);
    }

    @Test
    public void getRelativeAddress() throws Exception {
        String file = "/Users/UserName/Documents/workspace/repo_root/src/com";
        String base = "/Users/UserName/Documents/workspace/repo_root/src";
        String result = validation.getRelativeAddress(new File(file),base);

        String expectedResult = "com";

        assertEquals(expectedResult, result);
    }

    @Test
    public void getRelativeAddressFalse() throws Exception {
        String file = "/Users/UserName/Documents/workspace/repo_root/tests/com";
        String base = "/Users/UserName/Documents/workspace/repo_root/src";
        String result = validation.getRelativeAddress(new File(file),base);

        String expectedResult = "com";

        assertNotEquals(expectedResult,result);
    }

    @Test
    public void getParenttrue() throws Exception {
        String directory = "src/com/twitter/user/User.java";
        String result = validation.getParent(directory);

        String expectedResult = "src/com/twitter/user";

        assertEquals(expectedResult, result);
    }

    @Test
    public void getParentFalse() throws Exception {
        String directory = "tests/com/twitter/user/User.java";
        String result = validation.getParent(directory);

        String expectedResult = "src/com/twitter/user";

        assertNotEquals(expectedResult, result);
    }

    @Test
    public void getApprovers() throws Exception {
        String directory = "src/com/twitter/user";
        Set<String> result = validation.getApprovers(directory);

        Set<String> expectedResult = new HashSet<>();
        expectedResult.add("ghopper");

        assertEquals(expectedResult,result);
    }

    @Test
    public void getApproversFalse() throws Exception {
        String directory = "src/com/twitter/user";
        Set<String> result = validation.getApprovers(directory);

        Set<String> expectedResult = new HashSet<>();
        expectedResult.add("alovelace");

        assertNotEquals(expectedResult,result);
    }

    @Test
    public void testReadFileForOwner(){
        String fileName = "src/com/twitter/tweet/OWNERS";
        Set<String> result = validation.readFile(fileName);

        Set<String> expectedResult = new HashSet<>();
        expectedResult.add("mfox");
        expectedResult.add("alovelace");
        expectedResult.add("ghooper");

        assertEquals(expectedResult,result);
    }

    @Test
    public void testReadFileForDependecies(){
        String fileName = "src/com/twitter/tweet/DEPENDENCIES";
        Set<String> result = validation.readFile(fileName);

        Set<String> expectedResult = new HashSet<>();
        expectedResult.add("src/com/twitter/follow");
        expectedResult.add("src/com/twitter/user");

        assertEquals(expectedResult,result);
    }

    @Test
    public void isValidFileName(){
        String fileName = "src/com/twitter/user/User.java";
        boolean result = validation.isValidFileName(fileName);
        assertTrue(result);
    }

    @Test
    public void isValidFileNameFalse(){
        String fileName = "src/com/twitter/user/User.txt";
        boolean result = validation.isValidFileName(fileName);
        assertFalse(result);
    }

}