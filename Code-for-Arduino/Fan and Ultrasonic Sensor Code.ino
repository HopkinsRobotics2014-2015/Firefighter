int echoOut = 
int echoIn =
int fanPin =

void setup()
{
  Serial.begin(9600);
  pinMode(echoOut, OUTPUT);
  pinMode(echoIn, INPUT);
  pinMode(fanPin, OUTPUT);
}

void loop()
{
  digitalWrite(echoOut, LOW);
  delay(2);
  digitalWrite(echoOut, HIGH);
  delay(10);
  digitalWrite(echoOut, LOW);
  int distance = pulseIn(inputPin, HIGH);
  float distance1 = distance/57.355; //cm
  Serial.println(distance1);
  delay(100);
  if(distance1 < 10)
  {
    digitalWrite(fanPin,HIGH); 
  }
  else
  {
    digitalWrite(fanPin, LOW);
  }
}
