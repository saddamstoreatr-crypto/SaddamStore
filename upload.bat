@echo off
cd /d C:\Users\DELL\AndroidStudioProjects\SaddamStore

git config user.name "saddamstoreatr-crypto"
git config user.email "saddamstoreatr@gmail.com"

set /p msg=Enter commit message: 
git add .
git commit -m "%msg%"
git branch -M main
git push -u origin main

pause
