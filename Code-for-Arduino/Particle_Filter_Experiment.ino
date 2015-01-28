#include "map1.h"
#include "map2.h"
#include "map3.h"
#include <Servo.h>

//NORTH, EAST, SOUTH, AND WEST are keywords for directions, to avoid confusion with integers.
// These numbers must be 0,1,..., etc...
#define NORTH 0;
#define EAST 1;
#define SOUTH 2;
#define WEST 3;
#define MAPWIDTHHEIGHT 61;
#define robotLength 31;
#define robotWidth 30;
#define numParticles 20 
double measurementNoise = 5;
const double PI = 3.1415926;

// motorControl and sensorInput are structures that store data about motors and sensors.

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

struct Particle {
  int x;
  int y;
  double w; // weight
  double orientation;
  int map;
};

// List of User Functions below:
struct sensorInput getUltrasonicSensorReadings();
void createParticles();
void updateMeasurementProb(struct sensorInput distance);
double normalizeWeights();
void resample();
double Gaussian(double mu, double sigma, double x);
double random_Gaussian(double mu, double sigma);
void moveParticles(struct motorControl m);
struct sensorInput getExpectedMeasurements(double _x, double _y, int map_to_check);
int high2lowResCoordinates(double q);
struct motorControl nextMove(struct sensorInput distance, struct motorControl motorState);
struct motorControl wallFollow(struct sensorInput distance, struct motorControl motorState, int referenceWall, int spacing, double parallelSpeed, double perpindicularSpeed);
int getMotorPower(double percentage);
double getPercentageFromMotorPower(int motorPower);
int completeScan(); // FIGURE OUT STEP VARIABLE. THERE WILL BE PROBLEMS
void smartScan();

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

int servoPin = 11;
int flameSensorPin = 12;

struct motorControl motors;
//Servo // MAKE SURE VARIABLE IS 'servo'! servo;

//struct Particle[numParticles] particles;
struct Particle working_set_particles[numParticles];
struct Particle resampled_Particles[numParticles];

int pmillis = 0; // for the moveParticles function

void setup(){
    pmillis = millis();
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
    servo.attach(servoPin);
    //Serial.begin(9600); // In case we need to println() anything
  
  // Initialize motor state:
    motors.v = getMotorPower(0);
    motors.h = getMotorPower(0);
    
    createParticles();
    updateMeasurementProb(getUltrasonicSensorReadings());
    resample();
}

struct sensorInput getUltrasonicSensorReadings(){
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
    
    return distance;
}

void createParticles(){
    // generate numParticles new particles at random locations on the map.
    // In future, will generate them with higher probability at the given start location
    int i = 0;
    for (i = 0; i < numParticles; i++){
        working_set_particles[i].x = (int) floor(random(244));
        working_set_particles[i].y = (int) floor(random(244));
        working_set_particles[i].orientation = random(2.0*PI);
        working_set_particles[i].w = 1.0f / (double) numParticles; // each particle is equally likely at first.
        working_set_particles[i].map = (int) floor( 1 + random(3) );
        
        // Make an identical set of particles for future resampling.
        resampled_Particles[i].x = working_set_particles[i].x;
        resampled_Particles[i].y = working_set_particles[i].y;
        resampled_Particles[i].orientation = working_set_particles[i].orientation;
        resampled_Particles[i].w = working_set_particles[i].w;
        resampled_Particles[i].map = working_set_particles[i].map;
    }
}

void updateMeasurementProb(struct sensorInput distance){
    int i = 0;
    double max = 0;
    for (i = 0; i < numParticles; i++){
        
        double w = 1.0;
        
        struct sensorInput expectedMeasurements = getExpectedMeasurements(working_set_particles[i].x, working_set_particles[i].y, working_set_particles[i].map);
            
        int dir = 0;
        for (dir = 0; dir < 4; dir++){
            int meas = distance.get[dir];
            int expect = expectedMeasurements.get[dir];
            double change = abs(Gaussian(meas, measurementNoise, expect));
            w *= change;
        }
        if (w > max) max = w;
        
       working_set_particles[i].w = w;
       
    }
    //Serial.println(max);
}

double normalizeWeights(){    
    /* Get the sum of all the particle weights */
    double sum = 0.0;
    double max1 = 0.0;
    int i = 0;
    for (i = 0; i < numParticles; i++){
        sum += abs(working_set_particles[i].w);
        if (working_set_particles[i].w > max1){ max1 = working_set_particles[i].w; }
    }
    
    /* Divide each particle weight by the sum, so their total will add to 1.0 */
    double max2 = 0.0;
    for (i = 0; i < numParticles; i++){
        if (working_set_particles[i].w > max1) { // Double check that each particle, is in fact less than the maximum
            println("GREATER THAN MAX          ", working_set_particles[i].w, ">", max1, i);
            //break;
        } 
        working_set_particles[i].w = working_set_particles[i].w/sum;
        if (working_set_particles[i].w > max2){ max2 = working_set_particles[i].w; } 
    }
    double sum2 = 0.0;
    for (i = 0; i < numParticles; i++){
        sum2 += working_set_particles[i].w;
    }
    return max2;
}

int current_map = 1;
double long_term_mapCount[] = {0,0,0};
int mapCountIterations = 0;
double[] map_probabilities[] = {(1.0/3.0), (1.0/3.0), (1.0/3.0)};

void resample(){
//println("resampling...");
    double wmax = normalizeWeights();
    int index = floor(random(numParticles));
    double beta = 0;
    
    int mapCount[] = {0,0,0};
    
    int i = 0;
    for (i = 0; i < numParticles; i++){
        beta += random(2 * wmax); 
        while (working_set_particles[index].w < beta){
            beta -= working_set_particles[index].w;
            index++;
            index = index % numParticles;
        }
        
        // Copy the selected particle's information into the resampled particles array.
        resampled_Particles[i].x = working_set_particles[index].x;
        resampled_Particles[i].y = working_set_particles[index].y;
        resampled_Particles[i].w = 1.0 / (double) numParticles;
        resampled_Particles[i].orientation = working_set_particles[index].orientation;
        //resampled_Particles[i].map = 1 + ((int) random_Gaussian(current_map + 2, 0.5)) % 3; // favor current map
        resampled_Particles[i].map = (int) floor(random(3) + 1);
        mapCount[working_set_particles[index].map-1]++;
    }

    // Swap the resampled set with the working set.
    struct Particle c[numParticles] = working_set_particles; // don't know if numParticles is necessary here.
    working_set_particles = resampled_Particles;
    resampled_Particles = c;
    
    for (i = 0; i < 3; i++){
        long_term_mapCount[i] += mapCount[i];
    }
    mapCountIterations += numParticles;
    
    //println(maxInd + 1, maxVal);
    
    int maxInd = 0;
    double maxVal = 0;
    for (i = 0; i < 3; i++){
        map_probabilities[i] = long_term_mapCount[i] / (double) mapCountIterations;
        if (map_probabilities[i] > maxVal){
            maxInd = i;
            maxVal = map_probabilities[i];
        }
    }
    
    current_map = maxInd + 1;
    //Serial.println(1, floor(map_probabilities[0] * 1000)/10.0 + "%   ", 2, floor(map_probabilities[1] * 1000)/10.0 + "%   ", 3, floor(map_probabilities[2] * 1000)/10.0 + "%     ", maxInd+1);
}

double Gaussian(double mu, double sigma, double x){
        
        // calculates the probability of x for 1-dim Gaussian with mean mu and var. sigma
        return (double) exp(- (pow(mu - x, 2)) / pow(sigma, 2) / 2.0f) / sqrt(2.0f * PI * pow(sigma, 2));
}

double random_Gaussian(double mu, double sigma){
    double x1, x2, w, y1;
    static double y2;
    static int use_last = 0;

    if (use_last)		        // use value from previous call 
    {
        y1 = y2;
        use_last = 0;
    }
    else
    { 
        do {
            x1 = random(2) - 1.0;
            x2 = random(2) - 1.0;
            w = x1 * x1 + x2 * x2;
        } while ( w >= 1.0 );

        w = Math.sqrt( (-2.0 * log( w ) ) / w );
        y1 = x1 * w;
        y2 = x2 * w;
        use_last = 1;
    }

    return( mu + y1 * sigma );   
}

void moveParticles(struct motorControl m){
    //static int pmillis = millis();
    double cmps = 11.0; // centimeters per second, should be approx 11.
    double fullPowerDistanceX = (millis()-pmillis)*(cmps/1000.0); // average full power speed of 11cm per second
    double fullPowerDistanceY = (millis()-pmillis)*(cmps/1000.0);  // average full power speed of 11 cm per second.
    double motorNoise = 6;
    int i = 0;
    for (i = 0; i < numParticles; i++){
        int x_change = (int) floor(random_Gaussian(getPercentageFromMotorPower(m.h) * fullPowerDistanceX, motorNoise));
        int y_change = (int) floor(random_Gaussian(getPercentageFromMotorPower(m.v) * fullPowerDistanceY, motorNoise));
        working_set_particles[i].x += x_change;
        working_set_particles[i].y += y_change;
    }
    pmillis = millis();
}

struct sensorInput getExpectedMeasurements(double _x, double _y, int map_to_check){
    //int[61][61] map;
    int[][] map = new int[61][61];
    if (map_to_check == 1){
        map = map1;
    }
    if (map_to_check == 2){
        map = map2;
    }
    if (map_to_check == 3){
        map = map3;
    }
    
    int r_x = high2lowResCoordinates(_x);
    int r_y = high2lowResCoordinates(_y);
        
    //struct
    struct sensorInput distance;
    
    if (r_x < 0 || r_y < 0 || r_x + 1 > MAPWIDTHHEIGHT || r_y + 1 > MAPWIDTHHEIGHT){
        return distance; // return all zeros. hopefully this is unlikely enough that the particle will be discarded.
    }
    
    int cell = map[r_y][r_x];
    
    //NORTH
    int x = 0;
    int y = 0;
    while (cell != 1 && r_y - y >= 0){
        cell = map[r_y - y][r_x + x];
        y++;
    }
    distance.north = (int) (y * 4 + floor(_y - 4 * r_y) + random_Gaussian(0.0f, measurementNoise) - 0.5 * robotLength);
    distance.get[NORTH] = distance.north;
    
    //SOUTH
   
    cell = map[r_y][r_x];
    x = 0;
    y = 0;
    while (cell != 1 && r_y + y < MAPWIDTHHEIGHT){
        cell = map[r_y + y][r_x + x];
        y++;
    }
    distance.south = (int) (y * 4 + floor(4 * r_y - _y) + random_Gaussian(0.0f, measurementNoise) - 0.5 * robotLength);
    distance.get[SOUTH] = distance.south;
    
    // EAST
    cell = map[r_y][r_x];
    x = 0;
    y = 0;
    while (cell != 1 && r_x + x < MAPWIDTHHEIGHT){
        cell = map[r_y + y][r_x + x];
        x++;  
    }
    distance.east = (int) (x * 4 + floor(4 * r_x - _x) + random_Gaussian(0.0f, measurementNoise) - 0.5 * robotWidth);
    distance.get[EAST] = distance.east;
    
    // WEST
    cell = map[r_y][r_x];
    x = 0;
    y = 0;
    while (cell != 1 && r_x - x >= 0){
        cell = map3[r_y + y][r_x - x];
        x++;
    }
    distance.west = (int) (x * 4 + floor(_x - 4 * r_x) + random_Gaussian(0.0f, measurementNoise) - 0.5 * robotWidth);
    distance.get[WEST] = distance.west;
        
    return distance;
}

int high2lowResCoordinates(double q){
    return ((int) floor(q * 0.25));
}


int stage = 0;
int threshold = 60;
int referenceDistance = 0;
int wallSpacing = 30;
int clearance = 25;
int step = 5;
int angle = 45;
int prevLight = 1024;
struct motorControl nextMove(struct sensorInput distance, struct motorControl motorState){
  //static int stage = 0;
  motorState.h = getMotorPower(1);
  //moveParticles(motorState);
  //println(stage);
  return motorState;
}

struct motorControl wallFollow(struct sensorInput distance, struct motorControl motorState, int referenceWall, int spacing, double parallelSpeed){

  // A negative parallel speed is left or down, a positive one is right or up
  
  //Proportional Distance Control
  double error = (distance.get[referenceWall]-spacing);
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
    sensorInput distance = getUltrasonicSensorReadings();
    moveParticles(motors);
    updateMeasurementProb(distance);
    resample();
    
    // get the next move, i.e. the new motor settings
    motors = nextMove(distance, motors);
  // Update motor power
    analogWrite(motorPin1, motors.h);
    analogWrite(motorPin2, motors.v);
}

int getMotorPower(double percentage){
    int scaledPower = (int) floor(191 + 50 * percentage);
    return (int) floor(max(min(scaledPower, 240), 115));
}

double getPercentageFromMotorPower(int motorPower){
    double percentage = 0.02 * (motorPower - 191);
    return percentage;
}

int completeScan(){ // FIGURE OUT STEP VARIABLE. THERE WILL BE PROBLEMS
    int bestLight = 1024;
    int bestAngle = 0;
    int angle = 38;
servo.write(angle);
delay(1000);
    for (angle = 38; angle <= 147; angle += step){
    servo.write(angle);
    println(angle);
        delay((int)map(step, 0, 20, 100, 500));
        int light = analogRead(1);
      if (light < bestLight){
        bestAngle = angle;
        bestLight = light;
      }
    }
    return bestLight;
}

void smartScan(){
    angle += step;
    if (angle < 30) {
        angle = 140;
    }
    if (angle > 150) {
        angle = 55;
    }
servo.write(angle);
delay(100);
    int light = analogRead(1);
    if (light > prevLight) step *= -1;
    prevLight = light;
}
