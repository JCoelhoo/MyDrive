# MyDrive - es16al_07-project

###Project description:
Domain ([PT] (https://fenix.tecnico.ulisboa.pt/downloadFile/1970943312268892/es16p0.pdf)/
[EN](https://fenix.tecnico.ulisboa.pt/downloadFile/1970943312268893/es16p0en.pdf))

First Delivery ([PT](https://fenix.tecnico.ulisboa.pt/downloadFile/563568428731757/es16p1.pdf)/ 
[EN](https://fenix.tecnico.ulisboa.pt/downloadFile/563568428731758/es16p1en.pdf)) 

Second Delivery ([PT](https://fenix.tecnico.ulisboa.pt/downloadFile/563568428736236/es16p2.pdf)/
[EN](https://fenix.tecnico.ulisboa.pt/downloadFile/563568428736237/es16p2en.pdf))  

Third Delivery ([PT](https://fenix.tecnico.ulisboa.pt/downloadFile/845043405447749/es16p3.pdf)/
[EN](https://fenix.tecnico.ulisboa.pt/downloadFile/845043405447750/es16p3en.pdf))        

###Install commands:

$ mysql -p -u root

Enter password: rootroot

mysql> GRANT ALL PRIVILEGES ON *.* TO 'mydrive'@'localhost' IDENTIFIED BY 'mydriv3' WITH GRANT OPTION;

mysql> CREATE DATABASE mydrivedb;

mysql> \q

$ git clone https://github.com/tecnico-softeng/es16al_07-project.git

$ cd es16al_07-project

$ mvn clean package exec:java -Dexec.args=drive.xml
