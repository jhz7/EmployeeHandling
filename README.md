# EmployeeHandling
Employee handling application in scala play 

# Settings

* Create the sql tables on your preferred data base, using employeesTable.sql and usersTable.sql scripts.
* Set the data base connection parameters in the application.conf file inside "common/conf" folder.
* Set the JDBC dependency for your data base in the build.sbt file.

# To Run

* You must open one bash on project root path for exposed micro projects (3 on this case).
  - In bash 1 run <sbt "project authApi" "run -Dhttp.port=9001">
  - In bash 2 run <sbt "project employeeApi" "run -Dhttp.port=9002">
  - In bash 3 run <sbt "project site" "run"> that runs on 9000 default play port.

Open your browser on localhost:9000