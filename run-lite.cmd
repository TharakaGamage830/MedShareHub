@echo off
echo 🚀 Starting MedShare Hub in LITE MODE (H2 + In-Memory Cache)...
cd backend
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=local
