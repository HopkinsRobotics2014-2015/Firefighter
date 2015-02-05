#include "Streaming.h"

int echoOutF
int echoInF

int echoOutB
int echoInB

int echoOutL
int echoInL

int echoOutR
int echoInR

void setup()
{

Serial.begin(9600);
pinMode(led, OUTPUT);

pinMode(echoInF, INPUT);
pinMode(echoOutF, OUTPUT); 

pinMode(echoInB, INPUT);
pinMode(echoOutB, OUTPUT); 

pinMode(echoInL, INPUT);
pinMode(echoOutL, OUTPUT); 

pinMode(echoInR, INPUT);
pinMode(echoOutR, OUTPUT); 

}

void loop()
}

int north = getUltrasonicF();
int south = getUltrasonicB();
int east = getUltrasonicL();
int west = getUltrasonicR();

Serial << north << " " << south << " " << east << " " << west << endl; 

}

int getUltrasonicF()
{

  digitalWrite(echoOutF, LOW);
  delay(2);
  digitalWrite(echoOutF, HIGH);
  delay(10);
  digitalWrite(echoOutF, LOW);
  int distanceF = pulseIn(echoInF, HIGH)/57.355;
  //float distanceF = distance/57.355; //cm
  
  return distanceF;

}

int getUltrasonicB()
{

  digitalWrite(echoOutB, LOW);
  delay(2);
  digitalWrite(echoOutB, HIGH);
  delay(10);
  digitalWrite(echoOutB, LOW);
  int distanceB = pulseIn(echoInB, HIGH)/57.355;
  //float distanceB = distance/57.355; //cm
  
  return distanceB;

}

int getUltrasonicL()
{

  digitalWrite(echoOutL, LOW);
  delay(2);
  digitalWrite(echoOutL, HIGH);
  delay(10);
  digitalWrite(echoOutL, LOW);
  int distanceL = pulseIn(echoInL, HIGH)/57.355;
 // float distanceE = distance/57.355; //cm
  
  return distanceL;

}

int getUltrasonicR()
{

  digitalWrite(echoOutR, LOW);
  delay(2);
  digitalWrite(echoOutR, HIGH);
  delay(10);
  digitalWrite(echoOutR, LOW);
  int distanceR = pulseIn(echoInR, HIGH)/57.355;
  //float distanceW = distance/57.355; //cm
  
  return distanceR;


}
