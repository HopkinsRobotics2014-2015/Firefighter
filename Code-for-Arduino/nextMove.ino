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
};

// Actual program starts below:

int input1 = 1;
int input2 = 2;
int input3 = 3;
int input4 = 4;

int motorPin1; // horizontal
int motorPin2; // vertical

struct motorControl motors;

void setup(){
    pinMode(input1, INPUT);
    pinMode(input2, INPUT);
    pinMode(input3, INPUT);
    pinMode(input4, INPUT);
    pinMode(motorPin1, OUTPUT);
    pinMode(motorPin2, OUTPUT);
    Serial.begin(9600);
  
  // Initialize motor state:
    motors.v = 191;
    motors.h = 191;
}

struct motorControl nextMove(struct sensorInput distance, struct motorControl motorState){

 //distance.north; distance.south; etc...

  motorState.h++;
  
  return motorState;
}

void loop(){
    struct sensorInput distance;
    
    // get the sensor readings and save them to the distance variable (sensorInput)
    distance.north = 30;
    distance.west = 30;
    distance.south = 30;
    distance.east = 30;
    
    // get the next move, i.e. the new motor settings
    motors = nextMove(distance, motors);
  
  // Update motor power
    analogWrite(motorPin1, motors.h);
    analogWrite(motorPin2, motors.v);
}
