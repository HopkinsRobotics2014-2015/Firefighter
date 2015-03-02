import processing.core.*;
import processing.data.*;
import processing.event.*;
import processing.opengl.*;

import java.util.Map;
import java.util.HashMap;

public class Diagnosis extends PApplet{
  ArduinoSerialConnection asc; 
  Particle_Filter filter; // Determines map, location, and orientation
  Navigator nav; // Determines short-term destination
  Maps susanin;
  
  int margin = 20;
  float factor;
  Map<String, Integer> data; // most recent sensor data from Arduino
  Map<String, Integer> oldData; // previous data
  motorControl motors;
  Control control;
  double x = 20.0;
  double y = 20.0;
  
  
  public void setup(){
    size(800,600);
    background(255);
    factor = min((width-2*margin)/180.0f, (height-2*margin)/78.0f);
    
    motors = new motorControl();
        
    susanin = new Maps();
    
    nav = new Navigator();
    
    
    
    control = new Control();
    
    // Initialize the filter with the expected start position.
    filter = new Particle_Filter((int)x, (int)y);
    sensorInput sense = new sensorInput();
           
    motors.h = 191;
    motors.v = 191;
    oldData = new HashMap<String, Integer>();
    asc = new ArduinoSerialConnection();
    for (String tag : asc.CommunicationTags){
        oldData.put(tag, 0);
    }
    if ( asc.initialize() ) {
      asc.establishConnection();
    } else {
      //exit();
    }
    
    
    
  }
  public void draw(){
    background(255);
    pushMatrix();
    translate(margin,margin);
    
    scale(factor,factor);
    stroke(0);
    fill(0);
    for (Maps.Wall w : susanin.vertical_walls){
      line(w.x1, w.y1, w.x2, w.y2);
    }
    for (Maps.Wall w : susanin.horizontal_walls){
      line(w.x1, w.y1, w.x2, w.y2);
    }
    
    
    //** Main loop goes here **//
    data = asc.getMostRecentData(); 
    
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
    
    println(sensors);
    
    //if (data.get("NORTH") != oldData.get("NORTH") || data.get("WEST") != oldData.get("WEST") || data.get("SOUTH") != oldData.get("SOUTH") || data.get("EAST") != oldData.get("EAST")){            
    
    Particle loc = filter.process(sensors, motors);
    //Particle loc = filter.process(susanin.getExpectedMeasurements((int)x,(int)y,0,0), motors);
    
    stroke(0,255,0);
    fill(0,255,0);
    
    for (Particle p : filter.working_set_particles){
      point(p.x,p.y);
    }
    
    nav.plan(loc);
    stroke(0,0,255);
    fill(0,0,255);
    
    for (Checkpoint cp : nav.checkpoints){
      rect(cp.x,cp.y,2,2);
    }
    
    fill(0);
    stroke(0,255,0);
    ellipse(loc.x,loc.y,5,5);   
    
    
    motors = control.getMotors(loc, nav.prevCheckpoint, nav.nextCheckpoint, sensors);
    System.out.println((int) x + " " + (int) y + " LOC: " + loc.orientation + " " + loc.x + "," + loc.y  + " Nav: " + nav.nextCheckpoint.x + "," + nav.nextCheckpoint.y + " MH: " + motors.h + " MV: " + motors.v);
     // oldData = data;  
    popMatrix();     
  }
  
  public void mousePressed(){
    x = (mouseX-margin)/factor;
    y = (mouseY-margin)/factor;  
    if (mouseButton == RIGHT){
      
      filter = new Particle_Filter((int)x,(int)y);     
    }
  }
  
  public static void main(String[] passedArgs){
    String[] appletArgs = new String[] { "Diagnosis" };
		if (passedArgs != null) {
			PApplet.main(concat(appletArgs, passedArgs));
		} else {
			PApplet.main(appletArgs);
		}
  }
}