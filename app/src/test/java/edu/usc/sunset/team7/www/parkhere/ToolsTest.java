package edu.usc.sunset.team7.www.parkhere;

import junit.framework.Assert;

import org.junit.Test;

import edu.usc.sunset.team7.www.parkhere.Utils.Tools;


/**
 * Created by kunal on 11/5/16.
 */

public class ToolsTest {

    // white box testing
    @Test
    public void passwordValidated() throws Exception {
        String examplePassword1 = "hello12345";     // invalid
        String examplePassword2 = "hello12345!";    // valid
        String examplePassword3 = "123456789@";     // valid
        String examplePassword4 = null;             // invalid

        Assert.assertEquals(false, Tools.passwordValid(examplePassword1));
        Assert.assertEquals(true, Tools.passwordValid(examplePassword2));
        Assert.assertEquals(true, Tools.passwordValid(examplePassword3));
        Assert.assertEquals(false, Tools.passwordValid(examplePassword4));

    }

    // white box testing
    @Test
    public void emailValid() throws Exception {
        String exampleEmail1 = "kunal2@uw.edu";         // valid
        String exampleEmail2 = "kunal@website.info";    // valid
        String exampleEmail3 = "kunal@m.f";             // invalid but should evaluate to true, filtered out through email link validation
        String exampleEmail4 = "fdjkl";                 // invalid
        String exampleEmail5 = "@@@.39";                // invalid but should evaluate to true, filtered out through email link validation
        String exampleEmail6 = null;

        Assert.assertEquals(true, Tools.emailValid(exampleEmail1));
        Assert.assertEquals(true, Tools.emailValid(exampleEmail2));
        Assert.assertEquals(true, Tools.emailValid(exampleEmail3));
        Assert.assertEquals(false, Tools.emailValid(exampleEmail4));
        Assert.assertEquals(true, Tools.emailValid(exampleEmail5));
        Assert.assertEquals(false, Tools.emailValid(exampleEmail6));
    }

    

}
