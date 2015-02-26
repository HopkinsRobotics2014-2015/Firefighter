int inputPin = 9;
int outputPin = 8;
int led = 13;

void setup()
{
  Serial.begin(9600);
  pinMode(led, OUTPUT);
  pinMode(inputPin, INPUT);
  pinMode(outputPin, OUTPUT); 
}

void loop()
{
  digitalWrite(outputPin, LOW);
  delay(2);
  digitalWrite(outputPin, HIGH);
  delay(10);
  digitalWrite(outputPin, LOW);
  int distance = pulseIn(inputPin, HIGH);
  float distance1 = distance/57.355; //cm
  Serial.println(distance1);
  delay(100);
}
