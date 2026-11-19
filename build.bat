@echo off
if not exist out mkdir out
dir /s /b src\*.java > sources.txt
javac -encoding UTF-8 -d out @sources.txt
if %errorlevel% neq 0 (echo BUILD FAILED) else (echo BUILD OK)
