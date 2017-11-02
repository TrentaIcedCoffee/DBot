# DBot
A database cache with OS

v 1.3.1

### Environment
 - Java 8 runtime
 - MySQL 5.7
 - MySQL J connector

### Download
[DBot v 1.3.1](https://github.com/TrentaIcedCoffee/DBot/raw/master/DBot.jar)

### Configuration

 - C:/Dbot/certificate.json  
 	All information about your MySQL database.  
	host, port, database name, username and password

 - C:/Dbot/config.json  
     All uri information about DBot  
     uri of certificate.json, working directory and status.json

 - working directory  
     The place to store all tables

 - status.json  
     push/pull status, can be auto generated

### Usage

 - push/pull
 - status
 - list
 - field tableName
 - vi tableName fieldName  
   visualize all significant value in a field

 type 'help' to see all usages


### History versions  
 - 1.0.0 alpha version  
 - 1.0.1 added list
 - 1.0.2 added field  
 - 1.1.0 implemented multi-threading  
 - 1.2.0 implemented data visualization  
 - 1.3.0 implemented status, optimized configuration files  
 - 1.3.1 fixed doc and import issue  

### Comming soon...  
 - 1.4.0 brand new parsing algorithm
