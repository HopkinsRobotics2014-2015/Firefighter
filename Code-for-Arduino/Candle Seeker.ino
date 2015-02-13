#include <Servo.h>
int servoPin = 11;          // servo pin
Servo servo;                //declaring servo

const int sensorMin = 0;     // sensor minimum
const int sensorMax = 1024;  // sensor maximum

void setup()
{
  Serial.begin(9600);
  servo.attach(servoPin);
  Serial.begin(9600);
  servo.write(1);
}

void loop()
{

}
