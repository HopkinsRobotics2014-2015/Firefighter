@echo off

set PATH="C:\Program Files (x86)\Java\jdk1.8.0_25\bin";%PATH%

cmd /k java -Djava.library.path=./bin -cp ./bin/FirefighterMain;./bin/RXTXcomm.jar; FirefighterMain