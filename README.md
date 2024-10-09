# Spring-Batch
Contains below funtionalities using Spring-Batch

## CSV-to-Database
 => Reads data from CSV file and loads it into PostgresDB                                                                   
 => Currently the CSV input file Students.csv is read from src/main/resources/InputFiles folder location                                        
 => The PostgresDB used is launched via below Docker command to connect on local system                           
 docker run --name postgres-db -p 5432:5432 -e POSTGRES_PASSWORD=password -d postgres:16.1-alpine3.18                                                                 
 => Mutiple Async threads are created for this process
 ### URL to trigger the JOB:
      http://localhost:9090/job/csvToDb/students
    
 
## Database-to-CSV
 => Reads data from Database and creates a CSV file                                                               
 => Currently the CSV Output file StudentExtract.csv is created at src/main/resources/OutputFiles folder location                                       
 => The PostgresDB used is launched via below Docker command to connect on local system
 ### URL to trigger the JOB:
     http://localhost:9090/job/dbToCsv/students
 
## CSV-to-CSV
 => Reads data from CSV file, applies some buissiness logic and creates another CSV file 
 => Currently the CSV input file Students.csv is read from src/main/resources/InputFiles folder location   
 => Currently the CSV Output file StudentExtract.csv is created at src/main/resources/OutputFiles folder location                                       
 => ItemReader and ItemWriter Bean code is re-used from CSV-to-Database and Database-to-CSV config files respectively.
 ### URL to trigger the JOB:
     http://localhost:9090/job/csvToCsv/students
 
## Mulitple_CSV-to-Single_CSV
 => Reads data from Mutiple CSV files, applies some buissiness logic and creates a single CSV file with consolidated data 
 => Currently the CSV input file starts with Students_*.csv is read from src/main/resources/InputFiles folder location   
 => Currently the CSV Output file CombinedStudentExtract.csv is created at src/main/resources/OutputFiles folder location                                       
  ### URL to trigger the JOB:
      http://localhost:9090/job/multipleCsvToSingleCsv/students
  
