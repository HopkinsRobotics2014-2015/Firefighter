import java.util.ArrayList;

public class Checkpoint {
    int x,y;
    float f,g,h;
    Checkpoint next;
    ArrayList<Checkpoint> neighbors;
    public Checkpoint(int _x, int _y){
        this.x = _x;
        this.y = _y;
        this.f = Float.POSITIVE_INFINITY;
        this.g = 0;
        this.h = 0;
        this.neighbors = new ArrayList<Checkpoint>();
    }
    
    // must be a neighbor. can take into account carpets
    float getCost(Checkpoint n){
      if (neighbors.contains(n)){
        return (n.x - x) * (n.x - x) + (n.y - y) * (n.y - y);
      } else {
        System.out.println("Cannot get cost: no connection between nodes");
      }
      return 0.0f;
    }
    
    // does not require it to be a neighbor
    float getStraightLineDistance(Checkpoint n){
      return (n.x - x) * (n.x - x) + (n.y - y) * (n.y - y);
    }
}