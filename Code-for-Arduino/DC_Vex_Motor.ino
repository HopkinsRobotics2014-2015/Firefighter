int mp = 3;
int s = 0;

void setup()
{
  Serial.begin(9600);
  pinMode(mp, OUTPUT);
  Serial.println("Speed 0 to 255");
}

void loop()
{
  if(Serial.available())
  {
    s = Serial.parseInt();
    //if(s >= 0 && s <= 255)
    //{
      //s = s - 127;
      analogWrite(mp,s);
      Serial.println(s);
    //}
  }
}
