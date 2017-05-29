package autorest.generator.tests;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import java.io.StringReader;
import autorest.generator.JSchParser;

/**
 * Unit test for same basic cases.
 */
public class BasicCase extends TestCase {
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public BasicCase(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(BasicCase.class);
    }

    /**
     * Test a basic JSON Schema that accept a string
     */
    public void test_BasicStringSchema() {
        String sample = "{\"type\" : \"string\"}";
        StringReader reader = new StringReader(sample);
        JSchParser parser = new JSchParser(reader);
        parser.setPrintException(false);
        assertTrue(parser.parse());
    }

    /**
     * Test if an empty JSON Schema will not parse
     */
    public void test_EmptySchema() {
        String sample = "{}";
        StringReader reader = new StringReader(sample);
        JSchParser parser = new JSchParser(reader);
        parser.setPrintException(false);
        assertTrue(!parser.parse());
    }

    /**
     * Test a JSON Schema that has definitions and accepts an object
     */
    public void test_SchemaWithDefinitions() {
        String sample = "{"+
                        	"\"definitions\" : {"+
                        		"\"name\" : {"+
                        			"\"type\" : \"string\""+
                        		"}"+
                        	"},"+
                        	"\"type\" : \"object\""+
                        "}";
        StringReader reader = new StringReader(sample);
        JSchParser parser = new JSchParser(reader);
        parser.setPrintException(false);
        assertTrue(parser.parse());
    }
}
