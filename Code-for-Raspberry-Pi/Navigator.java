import java.util.ArrayList;
class Navigator{
    public  Checkpoint[] checkpoints;
    public Checkpoint prevCheckpoint;
    public Checkpoint nextCheckpoint;
    private int target;
    private int[] targets = {1,6,5};//{0,3,10,8};//{0,3,10,8};
    private int targetIndex = 0;
    public Navigator(){
        target = targets[0];
        
        checkpoints = new Checkpoint[18];
        checkpoints[0] = new Checkpoint (23, 42);
        checkpoints[1] = new Checkpoint (23, 114);
        checkpoints[2] = new Checkpoint (23, 166);
        checkpoints[3] = new Checkpoint (23, 221);
        checkpoints[4] = new Checkpoint (97, 23);
        checkpoints[5] = new Checkpoint (97, 114);
        checkpoints[6] = new Checkpoint (97, 175);
        checkpoints[7] = new Checkpoint (97, 221);
        checkpoints[8] = new Checkpoint (143, 73);
        checkpoints[9] = new Checkpoint (143, 118);
        checkpoints[10] = new Checkpoint (150, 175);
        checkpoints[11] = new Checkpoint (150, 221);
        checkpoints[12] = new Checkpoint (169, 23);
        checkpoints[13] = new Checkpoint (169, 73);
        checkpoints[14] = new Checkpoint (220, 23);
        checkpoints[15] = new Checkpoint (220, 118);
        checkpoints[16] = new Checkpoint (220, 221);
        checkpoints[17] = new Checkpoint (220, 175); // added a checkpoint in Room 1
        prevCheckpoint = checkpoints[4];
        nextCheckpoint = checkpoints[5];
        
        /*
        checkpoints = new Checkpoint[7];
        checkpoints[0] = new Checkpoint(30,40);
        checkpoints[1] = new Checkpoint(40,55);
        checkpoints[2] = new Checkpoint(80,20);
        checkpoints[3] = new Checkpoint(80,39);
        checkpoints[4] = new Checkpoint(80,55);
        checkpoints[5] = new Checkpoint(120,40);
        checkpoints[6] = new Checkpoint(120,55);
        link(1,2);
        link(1,6);
        link(2,7);
        link(6,7);
        link(6,7);
        prevCheckpoint = checkpoints[0];
        nextCheckpoint = checkpoints[targets[0]];*/
    }
    
    public void setTarget(int target, int map){
      this.target = target;
    }
    
    void link(int a, int b){
      checkpoints[a-1].neighbors.add(checkpoints[b-1]);
      checkpoints[b-1].neighbors.add(checkpoints[a-1]);
    }
    
    public Point getNextCheckpoint(){
      return new Point(nextCheckpoint.x, nextCheckpoint.y);
    }
        
    void ASTAR(Checkpoint startCheckpoint, Checkpoint endCheckpoint){
      ArrayList<Checkpoint> open = new ArrayList<Checkpoint>();
      ArrayList<Checkpoint> closed = new ArrayList<Checkpoint>();
      
      boolean foundPath = false;
      
      Checkpoint q = endCheckpoint;
      q.f = 0f;
      q.g = 0f;
      q.next = q;
      
      open.add(q);
      
      while (!open.isEmpty()){
        
        float minf = Float.POSITIVE_INFINITY;
        for (Checkpoint n : open){
          if (n.f < minf){
            q = n;
            minf = n.f;
          }
        }
        
        if (q == startCheckpoint){
          foundPath = true;
          break;
        }
        
        open.remove(q);
        
        for (Checkpoint neighbor : q.neighbors){
          float g = q.g + q.getCost(neighbor);
          float h = neighbor.getStraightLineDistance(startCheckpoint);
          float f = g + h;
          
          if (f < neighbor.f){
            neighbor.f = f;
            neighbor.g = g;
            neighbor.next = q;
          }
          
          if (!open.contains(neighbor) && !closed.contains(neighbor)){
            open.add(neighbor);
          }
      
        }
        
        closed.add(q);
      }
}

void plan(Particle loc){
    setNeighbors(loc.map);
    ASTAR(prevCheckpoint, checkpoints[target]);
    //this.nextCheckpoint = prevCheckpoint.next;
    if ((loc.x - nextCheckpoint.x)*(loc.x - nextCheckpoint.x) + (loc.y - nextCheckpoint.y)*(loc.y - nextCheckpoint.y) < 50){
      System.out.println("REACHED CHECKPOINT " + nextCheckpoint.x + " " + nextCheckpoint.y);
      prevCheckpoint = nextCheckpoint;
      if (nextCheckpoint != checkpoints[target]){  
        nextCheckpoint = nextCheckpoint.next;
      } else {
        if (targetIndex != targets.length - 1){
          target = targets[++targetIndex];
          System.out.println("New Target: " + targetIndex);
        }
       // setNeighbors(loc.map);
        ASTAR(prevCheckpoint, checkpoints[target]);
        this.nextCheckpoint = checkpoints[target];//prevCheckpoint.next;
        System.out.println("NEXT CHECKPOINT " + nextCheckpoint.x + " " + nextCheckpoint.y);
      }
    } 
}
void setNeighbors(int map) {

  for (Checkpoint cp : checkpoints) {
    cp.neighbors.clear();
    cp.next = null;
    cp.f = Float.POSITIVE_INFINITY;
    cp.g = 0;
    cp.h = 0;
  }
  link(11,18);
  link(16,18);
  link(17,18);

  switch (map) {
    // Map 1 No Dog
  case 0:
    link(1, 2);
    link(2, 6);
    link(3, 4);
    link(4, 8);
    link(5, 6);
    link(5, 13);
    link(6, 7);
    link(6, 10);
    link(7, 8);
    link(7, 11);
    link(9, 10);
    link(9, 14);
    link(10, 16);
    link(11, 12);
    link(12, 17);
    link(13, 15);
    link(15, 16);
    link(16, 17);
    break;
  case 1: // Map Two no dog
    link(1, 2);
    link(2, 6);
    link(3, 4);
    link(4, 8);
    link(5, 6);
    link(5, 13);
    link(6, 7);
    link(6, 10);
    link(7, 8);
    link(8, 12);
    link(9, 10);
    link(9, 14);
    link(10, 16);
    link(11, 12);
    link(12, 17);
    link(13, 15);
    link(15, 16);
    link(16, 17);
    break;
  case 2: // Map 3 no dog
    link(1, 2);
    link(2, 6);
    link(3, 4);
    link(4, 8);
    link(5, 6);
    link(5, 13);
    link(6, 7);
    link(6, 10);
    link(7, 8);
    link(7, 11);
    link(9, 14);
    link(10, 16);
    link(11, 12);
    link(12, 17);
    link(13, 14);
    link(13, 15);
    link(15, 16);
    link(16, 17);
    break;
  // Map One Dog 1
  case 3:
    link(1, 2);
    link(2, 6);
    link(3, 4);
    link(4, 8);
    link(5, 6);
    link(6, 7);
    link(6, 10);
    link(7, 8);
    link(7, 11);
    link(9, 10);
    link(9, 14);
    link(10, 16);
    link(11, 12);
    link(12, 17);
    link(13, 15);
    link(15, 16);
    link(16, 17);
    break;
    // Map Two Dog 1
  case 4:
    link(1, 2);
    link(2, 6);
    link(3, 4);
    link(4, 8);
    link(5, 6);
    link(6, 7);
    link(6, 10);
    link(7, 8);
    link(8, 12);
    link(9, 10);
    link(9, 14);
    link(10, 16);
    link(11, 12);
    link(12, 17);
    link(13, 15);
    link(15, 16);
    link(16, 17);
    break;
  case 5:
    // Map 3 dog 1
    link(1, 2);
    link(2, 6);
    link(3, 4);
    link(4, 8);
    link(5, 6);
    link(6, 7);
    link(6, 10);
    link(7, 8);
    link(7, 11);
    link(9, 14);
    link(10, 16);
    link(11, 12);
    link(12, 17);
    link(13, 14);
    link(13, 15);
    link(15, 16);
    link(16, 17);
    break;
    //Map One Dog 2
  case 6:
    link(1, 2);
    link(2, 6);
    link(3, 4);
    link(4, 8);
    link(5, 13);
    link(5, 6);
    link(6, 7);
    link(6, 10);
    link(7, 8);
    link(7, 11);
    link(9, 10);
    link(9, 14);
    link(10, 16);
    link(11, 12);
    link(12, 17);
    link(13, 15);
    link(16, 17);
    break;
  case 7:
    //Map Two Dog 2

    link(1, 2);
    link(2, 6);
    link(3, 4);
    link(4, 8);
    link(5, 13);
    link(5, 6);
    link(6, 7);
    link(6, 10);
    link(7, 8);
    link(8, 12);
    link(9, 10);
    link(9, 14);
    link(10, 16);
    link(11, 12);
    link(12, 17);
    link(13, 15);
    link(16, 17);
    break;
  case 8: // Map 3 dog 2
    link(1, 2);
    link(2, 6);
    link(3, 4);
    link(4, 8);
    link(5, 13);
    link(5, 6);
    link(6, 7);
    link(6, 10);
    link(7, 8);
    link(7, 11);
    link(9, 14);
    link(10, 16);
    link(11, 12);
    link(12, 17);
    link(13, 14);
    link(13, 15);
    link(16, 17);
    break;
    //Map One Dog 3
  case 9:
    link(1, 2);
    link(2, 6);
    link(3, 4);
    link(4, 8);
    link(5, 13);
    link(5, 6);
    link(6, 7);
    link(7, 8);
    link(7, 11);
    link(9, 10);
    link(9, 14);
    link(10, 16);
    link(11, 12);
    link(12, 17);
    link(13, 15);
    link(15, 16);
    link(16, 17);
    break;
  case 10:
    // Map 2 Dog 3
    link(1, 2);
    link(2, 6);
    link(3, 4);
    link(4, 8);
    link(5, 13);
    link(5, 6);
    link(6, 7);
    link(7, 8);
    link(8, 12);
    link(9, 10);
    link(9, 14);
    link(10, 16);
    link(11, 12);
    link(12, 17);
    link(13, 15);
    link(15, 16);
    link(16, 17);
    break;
  case 11:
    link(1, 2);
    link(2, 6);
    link(3, 4);
    link(4, 8);
    link(5, 13);
    link(5, 6);
    link(6, 7);
    link(7, 8);
    link(7, 11);
    link(9, 14);
    link(10, 16);
    link(11, 12);
    link(12, 17);
    link(13, 14);
    link(13, 15);
    link(15, 16);
    link(16, 17);
    break;
  }
}
}


//A path is a set of nodes in which each node has a predecessor and a successor.
//The robot must always have a successor node in mind to which it is travelling
// We will ignore path smoothing in this application, since it is unlikely to
// dramatically improve results.

// We will need to know the direction from the robot's current position to the 
// successor (target) node with respect to the robot's frame of reference. 
// This will require:
//                      The robot's coordinates (x, y)
//                      The robot's orientation (radians)
//                      The location of the target node (x_target, y_target)
//                      A decision that we have reached the target node
//                      Either continue to next node or extinguish
//
//                      Error check: make sure there are no walls between
//                      the robot and the target node

