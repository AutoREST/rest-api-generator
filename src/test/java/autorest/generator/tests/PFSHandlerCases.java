package autorest.generator.tests;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import java.io.StringReader;
import autorest.generator.PFISCompiler;
import autorest.generator.PFSHandler;

/**
 * Unit test for same cases of the PFSHandler.
 */
public class PFSHandlerCases extends TestCase {
	/**
	 * Create the test case
	 *
	 * @param testName name of the test case
	 */
	public PFSHandlerCases(String testName) {
		super(testName);
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite() {
		return new TestSuite(PFSHandlerCases.class);
	}

	/**
	 * Test a JSON Schema that has definitions and accepts an object with a referenced subschema
	 */
	public void test_SchemaWithDefinitionsAndReferences() {
		String sample = "{"+
							"\"definitions\" : {"+
								"\"name\" : {"+
									"\"type\" : \"string\""+
								"}"+
							"},"+
							"\"type\" : \"object\","+
							"\"properties\": {" +
								"\"firstName\": {"+
									"\"$ref\": \"#/definitions/name\"" +
								"}"+
							"},"+
							"\"additionalProperties\": false" +
						"}";
		StringReader reader = new StringReader(sample);
		PFISCompiler parser = new PFISCompiler(reader);
		parser.setPrintException(false);
		assertTrue(parser.parse());
		PFSHandler pfsHandler = parser.getPFSHandler();
		assertTrue(pfsHandler != null);
		assertTrue(pfsHandler.canResolveRefs());
	}

	/**
	 * Test a JSON Schema that has definitions and accepts an object with a invalid subschema reference
	 */
	public void test_SchemaWithDefinitionsAndInvalidReference() {
		String sample = "{"+
							"\"definitions\" : {"+
								"\"name\" : {"+
									"\"type\" : \"string\""+
								"}"+
							"},"+
							"\"type\" : \"object\","+
							"\"properties\": {" +
								"\"firstName\": {"+
									"\"$ref\": \"#/definitions/names\"" +
								"}"+
							"},"+
							"\"additionalProperties\": false" +
						"}";
		StringReader reader = new StringReader(sample);
		PFISCompiler parser = new PFISCompiler(reader);
		parser.setPrintException(false);
		assertTrue(parser.parse());
		PFSHandler pfsHandler = parser.getPFSHandler();
		assertTrue(pfsHandler != null);
		assertTrue(!pfsHandler.canResolveRefs());
	}
}
