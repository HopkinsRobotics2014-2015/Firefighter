int xmotorPin = x; //x direction motor
int ymotorPin = y; //y direction motor
int echoPin1 = e1; //input for the Ultrasonic
int soundPin1 = s1; //output for the Ultrasonic
int echoPin2 = e2; //input for the Ultrasonic
int soundPin2 = s2; //output for the Ultrasonic
int echoPin3 = e3; //input for the Ultrasonic
int soundPin3 = s3; //output for the Ultrasonic
int echoPin4 = e4; //input for the Ultrasonic
int soundPin4 = s4; //output for the Ultrasonic

void setup()
{
    Serial.begin(9600);
    pinMode(xmotorPin,OUTPUT); //declaring pin mode for x motor
    pinMode(ymotorPin, OUTPUT); // declaring pin mode for y motor
    pinMode(echoPin1, INPUT);//declaring ultrasonic input
    pinMode(soundPin1, OUTPUT);//declaring ultrasonic output
    pinMode(echoPin2, INPUT);//declaring ultrasonic input
    pinMode(soundPin2, OUTPUT);//declaring ultrasonic output
    pinMode(echoPin3, INPUT);//declaring ultrasonic input
    pinMode(soundPin3, OUTPUT);//declaring ultrasonic output
    pinMode(echoPin4, INPUT);//declaring ultrasonic input
    pinMode(soundPin4, OUTPUT);//declaring ultrasonic output
}

void loop()
{
    digitalWrite(soundPin1,LOW);//send out signals simultaneously
    digitalWrite(soundPin2,LOW);
    digitalWrite(soundPin3,LOW);
    digitalWrite(soundPin4,LOW);
    
    delay(2);
    
    digitalWrite(soundPin1, HIGH); // send an echo, and send it back
    digitalWrite(soundPin2, HIGH);
    digitalWrite(soundPin3, HIGH);
    digitalWrite(soundPin4, HIGH);
    
    delay(10);
    
    digitalWrite(soundPin1, LOW);
    digitalWrite(soundPin2, LOW);
    digitalWrite(soundPin3, LOW);
    digitalWrite(soundPin4, LOW);
    
    int distance1 = pulseIn(echoPin1, HIGH);
    distance1 = distance1/29/2;
    int distance2 = pulseIn(echoPin1, HIGH);
    distance2 = distance2/29/2;
    int distance3 = pulseIn(echoPin1, HIGH);
    distance3 = distance3/29/2;
    int distance4 = pulseIn(echoPin1, HIGH);
    distance4 = distance4/29/2;
    
    delay(10);
    
    if(distance1<50 || distance2<50 || distance3<50 || distance4<50) //set up obstacles MORE than 50 cm away for now, see how robot reacts
    {
      analogWrite(mp, 0);
      delay(20);
      while(distance1<50)
        {
          analogWrite(xmotorPin,50); // sensor on one side reads back a value, correct until within parameters
        }
      while(distance2<50);
        {
          analogWrite(xmotorPin,-50);
        }
      while(distance3<50);
        {
          analogWrite(ymotorPin,50);
        }
      while(distance4<50);
        {
          analogWrite(ymotorPin,-50);
        }
}
}


