int ledPin = 13;

// Motor Pins:
int motorPin1 = 3;
int motorPin2 = 11;

// Ultrasonic Range Finder Pins:
int inputPin = 9; 
int outputPin = 8;

// Other Variables:
int distance;
int pwm1 = 191;
int pwm2 = 191;

void setup(){
  pinMode(ledPin, OUTPUT);
  pinMode(motorPin1, OUTPUT);
  pinMode(motorPin2, OUTPUT);
  pinMode(inputPin, INPUT);
  pinMode(outputPin, OUTPUT);
  Serial.begin(9600);
}

void loop(){
  // Get the distance in centimeters by sending an ultrasonic pulse.
  digitalWrite(outputPin, LOW);
  delay(2);
  digitalWrite(outputPin, HIGH);
  delay(10);
  digitalWrite(outputPin, LOW);
  distance = pulseIn(inputPin, HIGH)/57.355; //cm 
  Serial.println(distance);
  delay(100);
  
  // Adjust power based on the distance.
  if (distance > 30){
    pwm1 = 115;
    pwm2 = 191;
  } else {
    // If we are within 30 of the wall, begin lateral movement.
    pwm1 += 5; // The reason this is +5 and not -5 is that the speed is becoming "less negative"
    pwm1 = min(191, pwm1);
    
    pwm2 += 10; // lateral
    pwm2 = min(240, pwm2);
  }
  analogWrite(motorPin1, pwm1); // Set the forward-backward motor.
  analogWrite(motorPin2, pwm2); // Set the left-right motor.
}

