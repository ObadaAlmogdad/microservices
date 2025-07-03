@echo off


REM شغل instance exam-service على المنفذ 8086
start cmd /k "cd exam-service && mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8086"
timeout /t 5

REM شغل instance exam-service على المنفذ 8087
start cmd /k "cd exam-service && mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8087" 