#include "Streaming.h"

// North
int echoOutF = 22;
int echoInF = 23;

// East
int echoOutR = 28; 
int echoInR = 29;

//South
int echoOutB = 24;
int echoInB = 25;

// West
int echoOutL = 26;
int echoInL = 27;

int motorPortH = 2;
int motorPortV = 3;
int servoPort = 5;

int ledPin = 13;

int messageInterval = 100;

struct motorControl {
  int v;
  int h;
};

struct motorControl motors;

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
    
    motors.h = 191;
    motors.v = 191;
    
    Serial.begin(9600);
    Serial.println("reset;");
    while(true){
      if (Serial.available() > 0){
        if (Serial.read() == 'G'){ break;}
      }
    }
    Serial << "ok" << endl;
}
int DOM = 0;
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
    if (inChar != ' '){
      if (isDigit(inChar)){
        input_val += (char)inChar;
      } else if (inChar == ';' || inChar == '\n'){
        process(input_key, input_val);
        input_key = "";
        input_val = "";
      } else {
        
        input_key += (char)inChar;
        
      }
    }
  } else if (millis() - pmillis1 > messageInterval) {
//      Serial << period << " " <<  blinkrate << endl;
     Serial << "NORTH " << north << "; SOUTH " << south << "; EAST " << east << "; WEST " << west << ";" << " MH " << motors.h << "; MV " << motors.v << endl; 
     // Serial << "ok" << endl;
      pmillis1 = millis();
      //Blink();
  }


/*south = getUltrasonicB();
east = getUltrasonicL();
west = getUltrasonicR();
north = getUltrasonicF();*/

Blink();
 analogWrite(motorPortH, motors.h);
 analogWrite(motorPortV, motors.v);

  
}

void process(String key, String val){
  if (key.equals("MH")){
    motors.h = val.toInt();
  } else if (key.equals("MV")){
    motors.v = val.toInt();
  } else if (key.equals("FREQ")){
    blinkrate = val.toInt();
  } else if (key.equals("PER")){
    period = val.toInt();    
  }
}

void Blink(){
  static unsigned long int pmillis2 = millis();
  if (blinkrate > 0){
    if (millis() - pmillis2 >= (0.5 * period/blinkrate)){
      toggleLed();
      pmillis2 = millis(); 
    }
  } else {
    digitalWrite(ledPin, LOW);
  }
}

void toggleLed(){
  static int i = 0;
  if (i == 0){
    digitalWrite(ledPin, HIGH);
    i = 1;
  } else {
    digitalWrite(ledPin, LOW);
    i = 0;
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
  int distanceB = pulseIn(echoInB, HIGH, 58000)/57.355;
  //float distanceB = distance/57.355; //cm
  
  return distanceB;

}

int getUltrasonicL(){

  digitalWrite(echoOutL, LOW);
  delayMicroseconds(2);
  digitalWrite(echoOutL, HIGH);
  delayMicroseconds(10);
  digitalWrite(echoOutL, LOW);
  int distanceL = pulseIn(echoInL, HIGH, 58000)/57.355;
 // float distanceE = distance/57.355; //cm
  
  return distanceL;

}

int getUltrasonicR(){

  digitalWrite(echoOutR, LOW);
  delayMicroseconds(2);
  digitalWrite(echoOutR, HIGH);
  delayMicroseconds(10);
  digitalWrite(echoOutR, LOW);
  int distanceR = pulseIn(echoInR, HIGH, 48000)/57.355;
  //float distanceW = distance/57.355; //cm
  
  return distanceR;
}
