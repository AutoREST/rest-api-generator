# REST API Generator

## Build

To build the application you will need [Maven](http://maven.apache.org/).

Then execute the build command:
  
    mvn clean package
    
This command will create two `.jar` files, one with the dependencies bundle in and the other with only the compiled code.


## Usage

The current stage can be used with the syntax:

    java -jar rest-api-generator.jar <fileOfAPI_JSON_Schema.json> <optionsJSON_String>
    
Example:  
    
    java -jar rest-api-generator.jar advanced.json "{\"APIName\":\"college\", \"DataBaseName\":\"collegeDB\"}"
