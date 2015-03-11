@echo off

set PATH="C:\Program Files (x86)\Java\jdk1.8.0_25\bin";%PATH%

cmd /k javac -d ./bin/FirefighterMain -cp ./type_classes;./bin/RXTXcomm.jar;./bin;. FirefighterMain.java

