# EmployeeHandling
Employee handling application developed on scala play 

# To Run

* You have to open one bash on root project path for exposed micro projects (3 on this case).
* In bash 1 run -> sbt "project authApi" "run -Dhttp.port=9001".
* In bash 2 run -> sbt "project employeeApi" "run -Dhttp.port=9002".
* In bash 3 run -> sbt "project site" "run" that runs on 9000 default play port.

Open your browser on localhost:9000