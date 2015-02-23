import java.util.Scanner;
import java.util.Map;
class FirefighterMain {   
    static ArduinoSerialConnection asc; 
    static Maps susanin; // Map for testing purposes
    static Particle_Filter filter; // Determines map, location, and orientation
    static Navigator nav; // Determines short-term destination
    //// Motion Control /// Uses location and orientation to move to short-term destination
    
    static Map<String, Integer> data; // most recent sensor data from Arduino
    
    public static void main(String[] args){
    
        // Scanner takes keyboard input, just for testing. Will delete later.
       /* Scanner scan = new Scanner(System.in);
        System.out.print("\nMap (0 through 11): ");
        int map = scan.nextInt();
        
        System.out.print("x: ");
        double x = scan.nextDouble();
        
        System.out.print("y: ");
        double y = scan.nextDouble();
        
        System.out.print("orientation (0-360 degrees) ");
        double heading_offset = scan.nextDouble() * Math.PI / 180.0; */
        
        motorControl motors = new motorControl();
        
        susanin = new Maps();
        
        Control control = new Control();
        
        // Initialize the filter with the expected start position.
        //filter = new Particle_Filter((int)( x + Math.random()*20), (int) (y + (Math.random() * 20 - 10)));
        sensorInput sense = new sensorInput();
       
        String[] dirs = {"NORTH", "EAST", "SOUTH", "WEST"};
        
        int dir = 0;
        motors.h = 191;
        motors.v = 191;
        int targeth = 191;
        int targetv = 191;
        
        asc = new ArduinoSerialConnection();
        if ( asc.initialize() ) {
            asc.establishConnection();
            while (true) {
            
                //** Main loop goes here **//
                data = asc.getMostRecentData(); 
               // if (data.get(dirs[dir]) != null && data.get(dirs[dir]) > 30 && data.get(dirs[dir]) > 0){
                  /*if (targeth > motors.h){
                    motors.h += 1;
                  } else if (targeth < motors.h){
                    motors.h -= 1;
                  }
                  
                  if (targetv > motors.v){
                    motors.v += 1;
                  } else if (targetv < motors.v){
                    motors.v -= 1;
                  }
                  motors.h = Math.max(Math.min(240,motors.h), 115);
                  motors.v = Math.max(Math.min(240,motors.v), 115);
                } else {
                  if (data.get(dirs[(dir + 1) %4]) != null && data.get(dirs[(dir + 1) %4]) > 30){
                      dir = (dir + 1) % 4;
                  } else if (data.get(dirs[(4 + dir - 1) %4]) != null && data.get(dirs[(4 + dir - 1) % 4]) > 30 ){
                      dir = (dir - 1) % 4;
                  } else {
                      dir = (dir + 1) % 4;
                      motors.h = 191;
                      motors.v = 191;
                  }
                }
                  
                switch (dir) {
                  case 0: // NORTH
                    targeth = 191;
                    targetv = 115;
                    break;
                  case 1: // EAST
                    targeth = 240;
                    targetv = 191;
                    break;
                  case 2: // SOUTH
                    targeth = 191;
                    targetv = 240;
                    break;
                  case 3: // WEST
                    targeth = 115;
                    targetv = 240;
                    break;
                } 
                asc.setMessage("MH" + motors.h + ";MV" + motors.v + ";");*/
                asc.setMessage("FREQ2;PER1000;");
            }
            //asc.close();
        }       
    }
}