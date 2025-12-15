# Project Summary

## Overview
This project is a Java-based application that utilizes the Spring framework for building a web service. It appears to focus on chat functionalities, likely involving real-time communication features. The project is structured with a clear separation of concerns, utilizing various packages for controllers, services, mappers, and utility classes.

### Languages, Frameworks, and Main Libraries Used
- **Programming Language**: Java
- **Framework**: Spring Framework
- **Build Tool**: Maven (indicated by the presence of `pom.xml`)
- **Configuration Files**: YAML for application configuration

## Purpose of the Project
The purpose of this project seems to be the development of a chat application, potentially with support for different environments (development and production) as indicated by the configuration files. The presence of controllers and services suggests that it handles requests and business logic for chat functionalities.

## Build and Configuration Files
- **Build File**: 
  - `/pom.xml`
  
## Source Files Directory
- Source files can be found in the following directories:
  - `/src/main/java/com/itzixi`
  - `/src/main/java/com/itzixi/bean`
  - `/src/main/java/com/itzixi/controller`
  - `/src/main/java/com/itzixi/mapper`
  - `/src/main/java/com/itzixi/service`
  - `/src/main/java/com/itzixi/service/impl`
  - `/src/main/java/com/itzixi/utils`
  
## Documentation Files Location
- Documentation and configuration files are located in:
  - `/src/main/resources`
    - `application-dev.yml`
    - `application-prod.yml`
    - `application.yml`
    - `docker-install-steps`
    - `docker-mysql8`
    - `jdk-profile`
  - `/src/main/resources/mappers`
    - `ChatRecordMapper.xml`
    - `my_doctor_0_1`
    - `my_doctor_0_2`
    - `nginx-config`
    - `run-jar-in-java`
  
## Test Files Directory
- Test files can be found in:
  - `/src/test/java/com/itzixi`