#include <Servo.h>

int servoPin = 11;

Servo servo;

//int angle; //servo position in degrees

//const int ain = A0;

int svalue;

//int angle = svalue / 6;
int sangle;

void setup()
{
  servo.attach(servoPin);
  Serial.begin(9600);
  servo.write(90);
}

void loop()
{
  //sangle=analogRead(ain)/8; //declare values of variables with the least amount of steps
  //svalue = analogRead(ain);
  //Serial.println(svalue);
  if (Serial.available()){
    sangle = Serial.parseInt();

    Serial.println(sangle);
  }


  servo.write(sangle);

  //for(angle = 0; angle <180; angle ++)
  //{
  //servo.write(angle);
  //delay(15);
  //}

  //for(angle = 180; angle >0; angle--)
  //{
  //servo.write(angle);
  //delay(15);
  //}
  //if(angle <= 150)
  //{
  //servo.write(angle);
  //delay(15);
  //}
  //else
  //{
  //digitalWrite(13,HIGH);
  //delay(500);
  //digitalWrite(13, LOW);
  //}
  servo.write(sangle);
}

