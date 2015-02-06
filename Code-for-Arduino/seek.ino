#include "Streaming.h"

int echoOutF = 31;
int echoInF = 30;

int echoOutB = 0;
int echoInB = 0;

int echoOutL = 0;
int echoInL = 0;

int echoOutR = 0;
int echoInR = 0;

int ledPin = 13;

int messageInterval = 100;


void setup()
{
    pinMode(ledPin, OUTPUT);

    pinMode(echoInF, INPUT);
    pinMode(echoOutF, OUTPUT); 

    pinMode(echoInB, INPUT);
    pinMode(echoOutB, OUTPUT); 

    pinMode(echoInL, INPUT);
    pinMode(echoOutL, OUTPUT); 

    pinMode(echoInR, INPUT);
    pinMode(echoOutR, OUTPUT); 
    
    Serial.begin(9600);
    while(Serial.available() <= 0){
        ;
    }
    Serial << "ok" << endl;
}

double blinkrate = 0;
double period = 1000.0;
String input_key = "";
String input_val = "";
void loop()
{
  static int north = 0;
  static int east = 0;
  static int west = 0;
  static int south = 0;
  
  static unsigned long int pmillis1 = millis();
  if (Serial.available() > 0){
    int inChar = Serial.read();
    if (isDigit(inChar)){
      input_val += (char)inChar;
    } else if (inChar == ';' || inChar == '\n'){
      process(input_key, input_val);
      input_key = "";
      input_val = "";
    } else {
      
      input_key += (char)inChar;
      
    }
  } else if (millis() - pmillis1 > messageInterval) {
//      Serial << period << " " <<  blinkrate << endl;
      Serial << "NORTH" << north << ';' << endl;//" " << south << " " << east << " " << west << endl; 

      pmillis1 = millis();
      //Blink();
  }


//int south = getUltrasonicB();
//int east = getUltrasonicL();
//int west = getUltrasonicR();
north = getUltrasonicF();

}

void process(String key, String val){
  if (key.equals("FREQ")){
    blinkrate = val.toInt();
  } else if (key.equals("PER")){
    period = val.toInt();
  }
}


int getUltrasonicF(){

  digitalWrite(echoOutF, LOW);
  delayMicroseconds(2);
  digitalWrite(echoOutF, HIGH);
  delayMicroseconds(10);
  digitalWrite(echoOutF, LOW);
  int distanceF = pulseIn(echoInF, HIGH, 58000)/57.355;
  //float distanceF = distance/57.355; //cm
  
  return distanceF;

}

int getUltrasonicB(){

  digitalWrite(echoOutB, LOW);
  delayMicroseconds(2);
  digitalWrite(echoOutB, HIGH);
  delayMicroseconds(10);
  digitalWrite(echoOutB, LOW);
  int distanceB = pulseIn(echoInB, HIGH)/57.355;
  //float distanceB = distance/57.355; //cm
  
  return distanceB;

}

int getUltrasonicL(){

  digitalWrite(echoOutL, LOW);
  delayMicroseconds(2);
  digitalWrite(echoOutL, HIGH);
  delayMicroseconds(10);
  digitalWrite(echoOutL, LOW);
  int distanceL = pulseIn(echoInL, HIGH)/57.355;
 // float distanceE = distance/57.355; //cm
  
  return distanceL;

}

int getUltrasonicR(){

  digitalWrite(echoOutR, LOW);
  delayMicroseconds(2);
  digitalWrite(echoOutR, HIGH);
  delayMicroseconds(10);
  digitalWrite(echoOutR, LOW);
  int distanceR = pulseIn(echoInR, HIGH)/57.355;
  //float distanceW = distance/57.355; //cm
  
  return distanceR;
}
