// motorControl and sensorInput are structures that store data about motors and sensors.
// Do not modify these here. Scroll down.

struct motorControl {
 int v;
 int h;
};

struct sensorInput {
  int up;
  int left;
  int down;
  int right;
};

// Actual program starts below:

int sensor1 = 1;
int sensor2 = 2;
int sensor3 = 3;
int sensor4 = 4;

int motorPin1; // horizontal
int motorPin2; // vertical

struct motorControl motors;

void setup(){
    pinMode(sensor1, INPUT);
    pinMode(sensor2, INPUT);
    pinMode(sensor3, INPUT);
    pinMode(sensor4, INPUT);
    Serial.begin(9600);
  
  // Initialize motor state:
    motors.v = 191;
    motors.h = 191;
}

struct motorControl nextMove(struct sensorInput distance, struct motorControl motorState){

//distance.up; distance.down; etc...
  // This is the only place where the motorControl should be changed.
  
  motorState.h++;
  
  return motorState;
}

void loop(){
    struct sensorInput distance;
    
    // get the sensor readings and save them to si (sensorInput)
    distance.up = 30;
    distance.left = 30;
    distance.down = 30;
    distance.right = 30;
    
    // get the next move, i.e. the new motor settings
    motors = nextMove(distance, motors);
  
  // Update motor power
    analogWrite(motorPin1, motors.h);
    analogWrite(motorPin2, motors.v);
}
