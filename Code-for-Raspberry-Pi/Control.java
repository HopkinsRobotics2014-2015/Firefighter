public class Control {
  motorControl getMotors(Particle loc, Checkpoint prev, Checkpoint next){
    float a = next.y - prev.y;
    float b = -(next.x - prev.x);
    float c = next.x * prev.y - next.y * prev.x; 
        
    float pathX = (b * (b*loc.x - a * loc.y) - a*c) / (a*a + b*b);
    float pathY = (a * (-b * loc.x + a * loc.y) - b * c) / (a*a + b*b);
    
    float deltaX = next.x - loc.x;
    float deltaY = next.y - loc.y;
    double trueAngle = Math.atan2(deltaY, deltaX);
    double newAngle = trueAngle + loc.orientation; // might have to be a +
    Maps mp = new Maps();
    motorControl mc = new motorControl();
    mc.h = 191;
    mc.v = 191;
    if (mp.straightLine(loc, new Point(next.x, next.y))){
        
        mc.h += (int)(Math.cos(newAngle) * 50); 
        mc.v += (int)(Math.sin(newAngle) * 50); // Make this minus if motor is reversed
        //return mc;
    }
    
        
    // reusing variables to do path correction vector
    
    deltaX = pathX - loc.x;
    deltaY = pathY - loc.y;
    trueAngle = Math.atan2(deltaY, deltaX);
    newAngle = trueAngle + loc.orientation; // might have to be a +
    
    mc.h += (int)(Math.cos(newAngle) * 40); 
    mc.v += (int)(Math.sin(newAngle) * 40);
         
    return mc;
    
  }
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
}
