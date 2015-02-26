int pwm = 128 ;
void setup(){
  Serial.begin(9600);
  pinMode(5,OUTPUT);
}

void loop(){
  pwm += 10;
  analogWrite(5,pwm);
  delay(1000);
  analogWrite(5,0);
  delay(1000);
  Serial.println(pwm);
}


