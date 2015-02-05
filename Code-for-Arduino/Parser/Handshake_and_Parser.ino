#include "Streaming.h"
int ledPin = 13;

int messageInterval = 100;
double blinkrate = 0;
double period = 1000.0;

void setup(){
  pinMode(ledPin, OUTPUT);
  
  // Blink twice when program starts.  
  digitalWrite(ledPin, LOW);
  delay(200);
  digitalWrite(ledPin, HIGH);
  delay(300);
  digitalWrite(ledPin, LOW);
  delay(150);
  digitalWrite(ledPin, HIGH);
  delay(400);
  digitalWrite(ledPin, LOW);
  
  //Serial.begin(115200);
  Serial.begin(9600);
  while(Serial.available() <= 0){
    ;
  }
  Serial << "ok" << endl; // Connection has been established
}
String input_key = "";
String input_val = "";
void loop(){
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
      Serial << period << " " <<  blinkrate << endl;
      pmillis1 = millis();
    }
    Blink();
}



void process(String key, String val){
  if (key.equals("FREQ")){
    setBlinkRate(val.toInt());
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

void setBlinkRate(int rate){
  blinkrate = rate;
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
