### Credit Module Challenge
https://github.com/egelirli/creditChallenge

A backend Loan API for a bank so that their employees can 
create, list and pay loans for their customers.

### Maven commands to build/test the application (linux)
* mvn install (Create the jar) 
* mvn test    (Run unit tests) 
* mvn clean   (clean )

### Running the application:(linux)
* (After mvn install)
* java -jar target/creditchallenge-0.0.1-SNAPSHOT.jar

### API definitions:
* http://localhost:8080/swagger-ui.html

### h2-console
* http://localhost:8080/h2-console 

### UserId/Passwords & Role
* admin/admin (Admin)
* 111/111     (Customer)
* 222/222     (Customer)

* Admin user is allowed to operate on any customer
* A customer is allowed to operate only for him or her self    

### Customer Table:
* id,name,surname,credit_limit,used_credit_limit
* '111','Ali','Kaya',100000.0,0.0 
* '222','Ahmet','Aydemir',200000.0,0.0

### Integration Test Tool
Carried out integration test with Talend API Tester
