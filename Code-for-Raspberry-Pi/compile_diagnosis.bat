@echo off

set PATH="C:\Program Files (x86)\Java\jdk1.8.0_25\bin";%PATH%

cmd /k javac -d ./bin/Diagnosis -cp ./type_classes;./bin/core.jar;./bin/RXTXcomm.jar;./bin;. Diagnosis.java

