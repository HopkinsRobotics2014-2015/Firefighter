import java.util.Scanner;
import java.util.Map;
class FirefighterMain {   
    static ArduinoSerialConnection asc; 
    static Maps susanin; // Map for testing purposes
    static Particle_Filter filter; // Determines map, location, and orientation
    static Navigator nav; // Determines short-term destination
    //// Motion Control /// Uses location and orientation to move to short-term destination
    
    static Map data; // most recent sensor data from Arduino
    
    public static void main(String[] args){
    
        // Scanner takes keyboard input, just for testing. Will delete later.
        Scanner scan = new Scanner(System.in);
        System.out.print("\nMap (0 through 11): ");
        int map = scan.nextInt();
        
        System.out.print("x: ");
        double x = scan.nextDouble();
        
        System.out.print("y: ");
        double y = scan.nextDouble();
        
        System.out.print("orientation (0-360 degrees) ");
        double heading_offset = scan.nextDouble() * Math.PI / 180.0;
        
        motorControl motors = new motorControl();
        
        susanin = new Maps();
        
        // Initialize the filter with the expected start position.
        filter = new Particle_Filter((int) x + Math.random*20, (int) y + (Math.random() * 20 - 10);
        sensorInput sense = new sensorInput();
       
        asc = new ArduinoSerialConnection();
        if ( asc.initialize() ) {
            asc.establishConnection();
            while (true) {
            
                //** Main loop goes here **//
                data = asc.getMostRecentData(); 
                if (data.get("NORTH") != null){
                  
                }
            }
            asc.close();
        }       
    }
}