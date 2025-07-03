@echo off

REM شغل Eureka أولاً
start cmd /k "cd eureka-server && mvn spring-boot:run"
timeout /t 10

REM ثم user-service
start cmd /k "cd user-service && mvn spring-boot:run"
timeout /t 10

REM ثم payment-service
start cmd /k "cd payment-service && mvn spring-boot:run"
timeout /t 10

REM ثم course-service
start cmd /k "cd course-service && mvn spring-boot:run"
timeout /t 10

REM ثم exam-service
start cmd /k "cd exam-service && mvn spring-boot:run"
timeout /t 21

REM ثم api-gateway
start cmd /k "cd api-gateway && mvn spring-boot:run" 