package edu.usc.sunset.team7.www.parkhere;

import junit.framework.Assert;

import org.junit.Test;

import edu.usc.sunset.team7.www.parkhere.Utils.Tools;


/**
 * Created by kunal on 11/5/16.
 */

public class ToolsTest {

    // black box testing
    @Test
    public void passwordValidatedBB() throws Exception {
        String examplePassword1 = "hello12345";
        String examplePassword2 = "hello12345!";
        String examplePassword3 = "123456789@";

        Assert.assertEquals(false, Tools.passwordValid(examplePassword1));
        Assert.assertEquals(true, Tools.passwordValid(examplePassword2));
        Assert.assertEquals(true, Tools.passwordValid(examplePassword3));
    }

    // white box testing
    @Test
    public void passwordValidatedWB() throws Exception {
        String examplePassword1 = null;

        Assert.assertEquals(false, Tools.passwordValid(examplePassword1));
    }



}
