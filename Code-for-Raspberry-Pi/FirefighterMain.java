import java.util.Scanner;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

class FirefighterMain {   
    static ArduinoSerialConnection asc; 
    static Maps susanin; // Map for testing purposes
    static Particle_Filter filter; // Determines map, location, and orientation
    static Navigator nav; // Determines short-term destination
    //// Motion Control /// Uses location and orientation to move to short-term destination
    
    static Map<String, Integer> data; // most recent sensor data from Arduino    
    
    public static void main(String[] args){
        ArrayList<sensorInput> lastTen = new ArrayList<sensorInput>();
        
        motorControl motors = new motorControl();
        
        susanin = new Maps();
        
        nav = new Navigator();
        
        double x = 93.0;
        double y = 20.0;
        
        Control control = new Control();
        
        // Initialize the filter with the expected start position.
        filter = new Particle_Filter((int)x, (int)y);
        sensorInput sense = new sensorInput();
               
        motors.h = 191;
        motors.v = 191;

        asc = new ArduinoSerialConnection();        
        
        if ( asc.initialize() ) {
            asc.establishConnection();
            while (true) {
            
                //** Main loop goes here **//
                data = asc.getMostRecentData(); 
                  
                asc.setMessage("MH" + motors.h + ";MV" + motors.v + ";");
                
                // BLINK CODE: asc.setMessage("FREQ2;PER1000;");
                
                  sensorInput sensors = new sensorInput();
                  sensors.north = data.get("NORTH");
                  sensors.east = data.get("EAST");
                  sensors.south = data.get("SOUTH");
                  sensors.west = data.get("WEST");
                  sensors.get[0] = sensors.north;
                  sensors.get[1] = sensors.east;
                  sensors.get[2] = sensors.south;
                  sensors.get[3] = sensors.west;                
                
                  lastTen.add(sensors);
                  sensorInput avgSensors = new sensorInput();
                  int sum = 0;
                  
                  if (lastTen.size() > 10){
                    sum = 0;
                    for (int i = lastTen.size() - 10; i < lastTen.size()-1; i++){
                      sum += lastTen.get(i).get[0];
                    }
                    avgSensors.north = (int) Math.round(sum / 10.0);
                    avgSensors.get[0] = (int) Math.round(sum / 10.0);
                    sum = 0;
                    for (int i = lastTen.size() - 10; i < lastTen.size()-1; i++){
                      sum += lastTen.get(i).get[1];
                    }
                    avgSensors.east = (int) Math.round(sum / 10.0);
                    avgSensors.get[1] = (int) Math.round(sum / 10.0);
                    sum = 0;
                    for (int i = lastTen.size() - 10; i < lastTen.size()-1; i++){
                      sum += lastTen.get(i).get[2];
                    }
                    avgSensors.south = (int)Math.round(sum / 10.0);
                    avgSensors.get[2] = (int) Math.round(sum / 10.0);
                    sum = 0;
                    for (int i = lastTen.size() - 10; i < lastTen.size()-1; i++){
                      sum += lastTen.get(i).get[3];
                    }
                    avgSensors.west = (int) Math.round(sum / 10.0);
                    avgSensors.get[3] = (int) Math.round(sum / 10.0);
                    lastTen.remove(0);
                  }
                
                
                  Particle loc = filter.process(sensors, motors);
                  nav.plan(loc);
                  motors = control.getMotors(loc, nav.prevCheckpoint, nav.nextCheckpoint, sensors);
                  System.out.println(sensors);
                  System.out.println(avgSensors);
                  System.out.println(susanin.getExpectedMeasurements((int)x,(int)y,0,0));
                  System.out.println();
                  System.out.println(motors.v + " " + motors.h);

            }
            //asc.close();
        }
    }
}
