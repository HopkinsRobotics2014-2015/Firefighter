//package arduinoSerialConnection;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Map;
import java.util.HashMap;

/**
 * @author ericjbruno @author mattdailis
 */
public class ArduinoSerialConnection implements SerialPortEventListener {
    SerialPort serialPort = null;

    private String message = "ok\n";
    public String[] CommunicationTags = {"NORTH", "SOUTH", "EAST", "WEST"};
    // Most-recent-data will be updated whenever we receive data from the Arduino
    private Map<String, Integer> most_recent_data = new HashMap<String, Integer>();
    private boolean connected = false;
    
    private static final String PORT_NAMES[] = { 
//        "/dev/tty.usbmodem", // Mac OS X
//        "/dev/usbdev", // Linux
        "/dev/ttyACM0",
        "/dev/ttyACM1",
         "/dev/ttyACM2",
         "/dev/ttyACM3"// Linux
//        "/dev/serial", // Linux
//        "COM4" // Windows
    };
    
    private String appName;
    private BufferedReader input;
    private OutputStream output;
    
    private static final int TIME_OUT = 1000; // Port open timeout
    private static final int DATA_RATE = 9600;//115200;//9600; // Arduino serial port

    public boolean initialize() {
        try {
            CommPortIdentifier portId = null;
            Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();

            // Enumerate system ports and try connecting to Arduino over each
            //
            System.out.println( "Trying:");
            while (portId == null && portEnum.hasMoreElements()) {
                // Iterate through your host computer's serial port IDs
                //
                CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
                System.out.println( "   port" + currPortId.getName() );
                for (String portName : PORT_NAMES) {
                    if ( currPortId.getName().equals(portName) 
                      || currPortId.getName().startsWith(portName)) {

                        // Try to connect to the Arduino on this port
                        //
                        // Open serial port
                        serialPort = (SerialPort)currPortId.open(appName, TIME_OUT);
                        portId = currPortId;
                        System.out.println( "Connected on port" + currPortId.getName() );
                        break;
                    }
                }
            }
        
            if (portId == null || serialPort == null) {
                System.out.println("Oops... Could not connect to Arduino");
                return false;
            }
        
            // set port parameters
            serialPort.setSerialPortParams(DATA_RATE,
                            SerialPort.DATABITS_8,
                            SerialPort.STOPBITS_1,
                            SerialPort.PARITY_NONE);

            // add event listeners
            serialPort.addEventListener(this);
            serialPort.notifyOnDataAvailable(true);

            // Give the Arduino some time
            try { Thread.sleep(2000); } catch (InterruptedException ie) {}
            
            return true;
        }
        catch ( Exception e ) { 
            e.printStackTrace();
        }
        return false;
    }
    
    public void setMessage(String newMessage){
        this.message = newMessage;
    }
    
    public String getMessage(){
        return this.message;
    }
    
    public void appendToMessage(String additionalMessage){
        this.message += additionalMessage;
    }
    
    public void sendData(String data) {
        try {
            System.out.println("Sending data: '" + data +"'");
            
            // open the streams and send the "y" character
            output = serialPort.getOutputStream();
            output.write( data.getBytes() );
        } 
        catch (Exception e) {
            System.err.println(e.toString());
            System.exit(0);
        }
    }

    //
    // This should be called when you stop using the port
    //
    public synchronized void close() {
        if ( serialPort != null ) {
            serialPort.removeEventListener();
            serialPort.close();
        }
    }

    //
    // Handle serial port event
    public synchronized void serialEvent(SerialPortEvent oEvent) {
        //System.out.println("Event received: " + oEvent.toString());
        try {
            switch (oEvent.getEventType() ) {
                case SerialPortEvent.DATA_AVAILABLE: 
                    if ( input == null ) {
                        input = new BufferedReader(
                            new InputStreamReader(
                                    serialPort.getInputStream()));
                    }
                    String inputLine = input.readLine();
                    connected = true;
                    //System.out.println("Arduino Sent: " + inputLine);
                    
                    String[] chunks = inputLine.split(";");
                    
                    for (String chunk : chunks){
                        String[] tokens = chunk.trim().split(" ");
                        if (tokens.length == 2){
                            String key = tokens[0];
                            int val = Integer.parseInt(tokens[1]);
                            if (val > 0){
                              most_recent_data.put(key, val); 
                            }
                        } else {
                            System.out.println("Bad Token" + tokens[0]);
                        }
                    }
                    
                    this.sendData(this.message);
                    break;

                default:
                    break;
            }
        } 
        catch (Exception e) {
            System.err.println(e.toString());
        }
    }
        
    public Map getMostRecentData(){
        return most_recent_data;
    }

    /** Constructor **/
    public ArduinoSerialConnection() {
        appName = getClass().getName();
        
        // Initialize most_recent_data HashMap
        for (String tag : CommunicationTags){
            most_recent_data.put(tag, null);
        }
        
    }
    
    public void establishConnection(){
        while (!connected){
            sendData("hello;");
            try { Thread.sleep(300); } catch (InterruptedException ie) {}
        }
    }
}



