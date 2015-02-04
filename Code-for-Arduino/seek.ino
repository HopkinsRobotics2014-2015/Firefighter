int echoOut1
int echoIn1

void setup()
{

Serial.begin(9600);
pinMode(led, OUTPUT);
pinMode(inputPin, INPUT);
pinMode(outputPin, OUTPUT); 

}

void loop()
}

int north = getUltrasonicN();
int south = getUltrasonicS();
int east = getUltrasonicE();
int west = getUltrasonicW();

}

int getUltrasonicN()
{

  digitalWrite(echoOut, LOW);
  delay(2);
  digitalWrite(echoOut, HIGH);
  delay(10);
  digitalWrite(echoOut, LOW);
  int distance = pulseIn(echoIn, HIGH);
  float distanceN = distance/57.355; //cm
  
  return distanceN;

}

int getUltrasonicS()
{

  digitalWrite(echoOut, LOW);
  delay(2);
  digitalWrite(echoOut, HIGH);
  delay(10);
  digitalWrite(echoOut, LOW);
  int distance = pulseIn(echoIn, HIGH);
  float distanceS = distance/57.355; //cm
  
  return distanceS;

}

int getUltrasonicE()
{

  digitalWrite(echoOut, LOW);
  delay(2);
  digitalWrite(echoOut, HIGH);
  delay(10);
  digitalWrite(echoOut, LOW);
  int distance = pulseIn(echoIn, HIGH);
  float distanceE = distance/57.355; //cm
  
  return distanceE;

}

int getUltrasonicW()
{

  digitalWrite(echoOut, LOW);
  delay(2);
  digitalWrite(echoOut, HIGH);
  delay(10);
  digitalWrite(echoOut, LOW);
  int distance = pulseIn(echoIn, HIGH);
  float distanceW = distance/57.355; //cm
  
  return distanceW;


}
