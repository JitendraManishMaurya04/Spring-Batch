# Spring-Batch

Contains below funtionalities using Spring-Batch

##CSV to Database
 => Reads data from CSV file and loads it into PostgresDB
 => Currently the CSV input file is read from src/main/resources folder location
 => The PostgresDB used is launched via below Docker command to connect on local system
	docker run --name postgres-db -p 5432:5432 -e POSTGRES_PASSWORD=password -d postgres:16.1-alpine3.18
 => Mutiple Async threads are created for this process
 