# New Order Permitting - RHPAM

This project has been developed on jBPM 7.22.0 Final and it was not tested in other versions.

# Deployment
Please follow below steps to deploy this project to jBPM 7.22.0 Final:
- Log into jBPM as admin
- Create a space named 'SolarVillage'
- From the new space, import the project from github: https://github.com/sssanwar/NewOrderPermitting.git
- Also upload the dependency JAR file to the jBPM artifact repository:
``custom-event-listener-0.0.1-SNAPSHOT.jar``
- Build and deploy the project in jBPM

The following describes the steps on how to run the Goverment Permit mock service:
- Download the included Spring Boot JAR file:
``gov-residential-permitting-0.0.1-SNAPSHOT.jar``
- Assuming JDK/JRE 8 is already installed, run the command:
``java -jar gov-residential-permitting-0.0.1-SNAPSHOT.jar``
The Spring Boot app will run on port 8686, therefore make sure no other app is listening on the same port.
- If the jBPM installation is not running from Docker, please edit the ``etc/hosts`` file and add the following line so that the Business Process know the which of the Spring Boot REST API's service is running from - that is the localhost.
```127.0.0.1    host.docker.internal```

# Business Process Execution
- Once the project is deployed, create a new Process Instance by invoking the curl command:
``curl -X POST "http://localhost:8080/kie-server/services/rest/server/containers/new-order-permitting_2.0.0-SNAPSHOT/processes/NewOrderPermitting.NewOrderPermitting/instances" -H "accept: application/json" -H "content-type: application/json" -d "{ \"salesRep\" : { \"com.solarvillage.SalesRep\": { \"name\" : \"Joe Carioca\", \"email\": \"joe@joe\" } }, \"property\" : { \"com.solarvillage.Property\": { \"customer\" : { \"com.solarvillage.Customer\": { \"name\": \"Donald Duck\", \"email\": \"donald@duck\" } }, \"address\": \"123 Disney St, Anaheim\", \"postcode\": \"12345\" } }}" -u "wbadmin:wbadmin"``
- Write down the return process instance ID, or alternatively query below.
- Query the process instance details:
``curl -X GET "http://localhost:8080/kie-server/services/rest/server/containers/new-order-permitting_2.0.0-SNAPSHOT/processes/instances?status=1&page=0&pageSize=10&sortOrder=true" -H "accept: application/json" -u "wbadmin:wbadmin"``
- Looks for process instance with ID: ``NewOrderPermitting.NewOrderPermitting`` and take note of the ``process-instance-id``
- Once submitted the Business Process (BP) will execute two REST requests to the ``gov-residential-permitting`` REST APIs and be returned the permit information.
- Notice the rolling logs from the gov-residential-permitting Spring Boot application.

# Permit Approval Scenarios
## APPROVED Scenarios
For the scenario where both permits are APPROVED, the BP will send two requests for permit creation to the mock service. The BP will then poll the status of the permits every 10 seconds to check the status. Once both permits are approved, a HOA approval task is created. In the case where HOA approval is given, the BP will continue and finish with NewOrder status set to COMPLETED.

Follow below steps for this scenario:
- Notice the Spring Boot logs and take note of the permit numbers returned by the REST API's
- Run the following curl command to set the status to approved:
``curl --request PATCH
  --url http://localhost:8686/permits/42
  --header 'content-type: application/json'
  --data '{
	"status": "approved"
}'``
The number **42** above is the permit number - please adjust accordingly.
- Run the curl command above twice for both electrical and structural permit numbers.
- Notice both the Spring Boot logs and jBPM logs.
- At this point, a user task will be created and assigned to the ``sales`` group.
- Notice the logs in jBPM showing that the TaskEventListener's afterTaskAddedEvent display the Input variables of the user task.
- The actual email sending functionality is not implemented. However this should demonstrate that we can expand the functionality further by writing a custom task event listener that may invoke another REST API for email notification.
- jBPM 7.22.0 Final has the Notification functionality which is not used in this project.
- Log into Workbench in another browser and claim the task and Tick the HOA Approved checkbox.

To use curl-based commands:
- Alternatively, run the curl command and replace {process-instance-id} with the id noted previously.
``curl -X GET "http://localhost:8080/kie-server/services/rest/server/queries/tasks/instances/process/{process-instance-id}?status=Ready" -H "accept: application/json"``
- Note the Task ID and use it to invoke the curl command:
``curl -X PUT "http://localhost:8080/kie-server/services/rest/server/containers/new-order-permitting_2.0.0-SNAPSHOT/tasks/48?user=sales%3Drep" -H "accept: application/json" -H "content-type: application/json" -d "{ \"task-status\" : \"Reserved\", \"task-actual-owner\" : \"sales-rep\"}"``
Replace the id **48** with the actual task id
- Invoke curl command to claim the task:
``curl -X PUT "http://localhost:8080/kie-server/services/rest/server/containers/new-order-permitting_2.0.0-SNAPSHOT/tasks/48/states/claimed?user=sales-rep" -H "accept: application/json"``
- Invoke the curl command to start the task:
``curl -X PUT "http://localhost:8080/kie-server/services/rest/server/containers/new-order-permitting_2.0.0-SNAPSHOT/tasks/48/states/started?user=sales-rep" -H "accept: application/json"``
- Invoke the curl command to set the value isHoaApproved:
``curl -X PUT "http://localhost:8080/kie-server/services/rest/server/admin/containers/new-order-permitting_2.0.0-SNAPSHOT/tasks/48/contents/input" -H "accept: application/json" -H "content-type: application/json" -d "{ \"isHoaApproved\": true}"``
- Invoke curl command to complete the task:
``curl -X PUT "http://localhost:8080/kie-server/services/rest/server/containers/new-order-permitting_2.0.0-SNAPSHOT/tasks/48/states/completed" -H "accept: application/json" -H "content-type: application/json" -d "{}"``
- Once HOA Approved task is completed, the BP should finish and display the results in the jBPM logs with the status and other details.

## DENIED Scenario
For the scenario where one of the permits is DENIED:
- Create a new process instance:
``curl -X POST "http://localhost:8080/kie-server/services/rest/server/containers/new-order-permitting_2.0.0-SNAPSHOT/processes/NewOrderPermitting.NewOrderPermitting/instances" -H "accept: application/json" -H "content-type: application/json" -d "{ \"salesRep\" : { \"com.solarvillage.SalesRep\": { \"name\" : \"Joe Carioca\", \"email\": \"joe@joe\" } }, \"property\" : { \"com.solarvillage.Property\": { \"customer\" : { \"com.solarvillage.Customer\": { \"name\": \"Donald Duck\", \"email\": \"donald@duck\" } }, \"address\": \"123 Disney St, Anaheim\", \"postcode\": \"12345\" } }}" -u "wbadmin:wbadmin"``
- Invoke the curl command as shown above but with the status set to 'denied'
``curl --request PATCH
  --url http://localhost:8686/permits/43
  --header 'content-type: application/json'
  --data '{
	"status": "denied"
}'``
- Make sure the correct permit number is used - see the Spring Boot logs.
- Notice both logs where it is shown another permit that is in progress will be rescinded, and the BP will finish with status set to CANCELLED
