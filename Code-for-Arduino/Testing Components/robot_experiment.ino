#include "map1.h"
#include "map2.h"
#include "map3.h"

//NORTH, EAST, SOUTH, AND WEST are keywords for directions, to avoid confusion with integers.
// These numbers must be 0,1,..., etc...
#define NORTH 0
#define EAST 1
#define SOUTH 2
#define WEST 3

// motorControl and sensorInput are structures that store data about motors and sensors.
// Do not modify these here. Scroll down.

struct motorControl {
 int v;
 int h;
};

struct sensorInput {
  int north;
  int east;
  int south;
  int west;
  int get[4];
};

// List of User Functions below:
struct motorControl nextMove(struct sensorInput distance, struct motorControl motorState);
struct motorControl wallFollow(struct sensorInput distance, struct motorControl motorState, int referenceWall, int spacing, double parallelSpeed, double perpindicularSpeed);
int getMotorPower(double percentage);

// Actual program starts below:

// Get all the pin numbers below.

int trig1 = 1; // NORTH
int echo1 = 2;
int trig2 = 3; // EAST
int echo2 = 4;
int trig3 = 5; // SOUTH
int echo3 = 6;
int trig4 = 7; // WEST
int echo4 = 8;

int motorPin1 = 9; // horizontal (EAST-WEST)
int motorPin2 = 10; // vertical (NORTH-SOUTH)

struct motorControl motors;

void setup(){
    pinMode(echo1, INPUT);
    pinMode(echo2, INPUT);
    pinMode(echo3, INPUT);
    pinMode(echo4, INPUT);
    pinMode(trig1, OUTPUT);
    pinMode(trig2, OUTPUT);
    pinMode(trig3, OUTPUT);
    pinMode(trig4, OUTPUT);
    pinMode(motorPin1, OUTPUT);
    pinMode(motorPin2, OUTPUT);
    Serial.begin(9600); // In case we need to Serial.println() anything
  
  // Initialize motor state:
    motors.v = 191;
    motors.h = 191;
}

struct motorControl nextMove(struct sensorInput distance, struct motorControl motorState){
  static int stage = 0;
  
  motorState = wallFollow(distance, motorState, NORTH, 30, 0.5, 0.5); // Follow the NORTH wall at 30 cm at half power
  stage++;
  
  return motorState;
}

struct motorControl wallFollow(struct sensorInput distance, struct motorControl motorState, int referenceWall, int spacing, double parallelSpeed){

  // A negative parallel speed is left or down, a positive one is right or up
  
  //Proportional Distance Control
  double error = (distance.get[referenceWall]-spacing));
  switch (referenceWall){
    case NORTH:
        motorState.v = getMotorPower(error);
        motorState.h = getMotorPower(parallelSpeed);
        break;
    case SOUTH:
        motorState.v = getMotorPower(-error);
        motorState.h = getMotorPower(parallelSpeed);
        break;
    case EAST:
        motorState.v = getMotorPower(parallelSpeed);
        motorState.h = getMotorPower(error);
        break;
    case WEST:
        motorState.v = getMotorPower(parallelSpeed);
        motorState.h = getMotorPower(-error);
        break;
  }
	
  return motorState;
}

void loop(){
    struct sensorInput distance;
    
    // get the sensor readings and save them to the distance variable
    
    digitalWrite(trig1, LOW);
    delayMicroseconds(2);
    digitalWrite(trig1, HIGH);
    delayMicroseconds(5);
    digitalWrite(trig1, LOW);
    
    distance.north = pulseIn(echo1, HIGH) / 58; // centimeters
    
    digitalWrite(trig2, LOW);
    delayMicroseconds(2);
    digitalWrite(trig2, HIGH);
    delayMicroseconds(5);
    digitalWrite(trig2, LOW);
    
    distance.east = pulseIn(echo2, HIGH) / 58; // centimeters
    
    digitalWrite(trig3, LOW);
    delayMicroseconds(2);
    digitalWrite(trig3, HIGH);
    delayMicroseconds(5);
    digitalWrite(trig3, LOW);
    
    distance.south = pulseIn(echo3, HIGH) / 58; // centimeters
    
    digitalWrite(trig4, LOW);
    delayMicroseconds(2);
    digitalWrite(trig4, HIGH);
    delayMicroseconds(5);
    digitalWrite(trig4, LOW);
    
    distance.west = pulseIn(echo4, HIGH) / 58; // centimeters
    
    distance.get[NORTH] = distance.north;
    distance.get[EAST] = distance.east;
    distance.get[SOUTH] = distance.south;
    distance.get[WEST] = distance.west;
    
    // get the next move, i.e. the new motor settings
    motors = nextMove(distance, motors);
  
  // Update motor power
    analogWrite(motorPin1, motors.h);
    analogWrite(motorPin2, motors.v);
}

int getMotorPower(double percentage){
	int scaledPower = 191 + 50 *  percentage;
    return floor(max(min(scaledPower, 240), 115));
}
