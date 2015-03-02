import java.util.Arrays;
import java.util.Comparator;
import java.util.Scanner;

class Maps {
    static long startTime = System.nanoTime();
    //NORTH, EAST, SOUTH, AND WEST are keywords for directions, to avoid confusion with integers.
    // These numbers must be 0,1,..., etc...
    final static int NORTH = 0;
    final static int EAST = 1;
    final static int SOUTH = 2;
    final static int WEST = 3;
    final static int[] all_maps = {0,1,2,3,4,5,6,7,8,9,10,11};
    final static int MAXDISTANCE = 345;
    final static int ROBOT_RADIUS = 15;
    
    /*
    
    Maps work like this:
    
    Map#    Layout  Dog Position
    0       1       0
    1       2       0
    2       3       0
    3       1       1
    4       2       1 
    5       3       1 
    6       1       2
    7       2       2 
    8       3       2
    9       1       3
    10      2       3
    11      3       3
    
    If we are sure that there is no dog, we will use only maps 3-11.
   
    */
    
    static int[][] wall_coordinates = {
      {0, 0,0,181,0},
        {0, 0,0,0,78},
          {0, 181,0,181,78},
            {0, 0,78,181,78},
              {1, 161, 29, 161, 49}
           
        /*{0, 0,0,244,0}, // outer wall
        {3, 72,0,72,88}, // room 
        {0, 0,0,0,244}, // outer wall
        {0, 244,0,244,244}, // outer wall
            
        {5, 124,15,124,30,        3, 4, 5   }, // dog1
            
        {4, 122,46,142,46}, // no map specification means allmaps.
            
        {4, 142, 46, 197, 46,     0, 1, 3, 4, 6, 7, 9, 10   }, // map1, map2
            
        {4, 122,46,122,100},
        {4, 197,46,197,100},
            
        {5, 211,68,230,68,        6,7,8}, // dog2
            
        {3, 46,88,72,88},
            
        {4, 122, 100, 164, 100,   2,5,8,11}, // map3
            
        {4, 164,100,197,100},
            
        {5, 122,110,122,142,      9,10,11}, // dog3
            
        {2, 0,140,72,140},
        {2, 72,140,72,197},
        {1, 125,152,197,152},
            
        {1, 125, 152, 125, 198,   1,4,7,10}, // map2
        {1, 125, 198, 125, 244,   0,2,3,5,6,8,9,11}, // map1, map3
            
        {0, 0,244,244,244} // outer wall*/
    };
    
    public Maps(){
        createWalls();
    }
    
    public static class Wall implements Comparable<Wall> {
        // x1 <= x2, y1 <= y2
        int x1;
        int x2;
        int y1;
        int y2;
        int room; // each Wall belongs to a room. Outer walls are room 0. Dog is room 5.
        int thickness;
        int[] maps;
        Wall(){}
        public int compareTo(Wall compareWall) {

            int compareQuantity = compareWall.room;

            //ascending order
            return this.room - compareQuantity;

            //descending order
            //return compareQuantity - this.quantity;

        }
        
        public static Comparator<Wall> Horizontally = new Comparator<Wall>() {

            public int compare(Wall wall1, Wall wall2) {

              int xw1 = wall1.x1; // assumes that for all walls, x1 <= x2
              int xw2 = wall2.x1;

              //ascending order
              return xw1 - xw2;

            }

        };
        
        public static Comparator<Wall> Vertically = new Comparator<Wall>() {

            public int compare(Wall wall1, Wall wall2) {

              int yw1 = wall1.y1; // assumes that for all walls, y1 <= y2
              int yw2 = wall2.y1;

              //ascending order
              return yw1 - yw2;

            }

        };
        public String toString(){
            return ("\n" + this.x1 + " " + this.y1 + " " + this.x2 + " " + this.y2 + " --------Room " + this.room + " ::::: " + Arrays.toString(this.maps));
        }
    }

    static Wall[] all_walls = new Wall[wall_coordinates.length];
    public static Wall[] horizontal_walls = new Wall[2];
    public static Wall[] vertical_walls = new Wall[3];

    private static void createWalls(){
        int i = 0;
        int h_count = 0;
        int v_count = 0;
        for (i = 0; i < wall_coordinates.length; i++){
            if (wall_coordinates[i][2] == wall_coordinates[i][4]){
                horizontal_walls[h_count] = new Wall();
                horizontal_walls[h_count].room = wall_coordinates[i][0];
                horizontal_walls[h_count].x1 = wall_coordinates[i][1];
                horizontal_walls[h_count].y1 = wall_coordinates[i][2];
                horizontal_walls[h_count].x2 = wall_coordinates[i][3];
                horizontal_walls[h_count].y2 = wall_coordinates[i][4];
                
                if (horizontal_walls[h_count].room == 5){
                    horizontal_walls[h_count].thickness = 4;
                } else {
                    horizontal_walls[h_count].thickness = 2;
                }
                
                if (wall_coordinates[i].length > 5) {
                    horizontal_walls[h_count].maps = new int[wall_coordinates[i].length - 5];
                    for (int j = 5; j < wall_coordinates[i].length; j++){
                        horizontal_walls[h_count].maps[j - 5] = wall_coordinates[i][j];
                    }
                } else {
                    horizontal_walls[h_count].maps = all_maps;
                }
                
                h_count++;
                
            } else {
                vertical_walls[v_count] = new Wall();
                vertical_walls[v_count].room = wall_coordinates[i][0];
                vertical_walls[v_count].x1 = wall_coordinates[i][1];
                vertical_walls[v_count].y1 = wall_coordinates[i][2];
                vertical_walls[v_count].x2 = wall_coordinates[i][3];
                vertical_walls[v_count].y2 = wall_coordinates[i][4];
                
                if (vertical_walls[v_count].room == 5){
                    vertical_walls[v_count].thickness = 4;
                } else {
                    vertical_walls[v_count].thickness = 2;
                }
                
                if (wall_coordinates[i].length > 5) {
                    vertical_walls[v_count].maps = new int[wall_coordinates[i].length - 5];
                    for (int j = 5; j < wall_coordinates[i].length; j++){
                        vertical_walls[v_count].maps[j - 5] = wall_coordinates[i][j];
                    }
                } else {
                    vertical_walls[v_count].maps = all_maps;
                }
                
                v_count++;
            }
            
        }
        
        Arrays.sort(vertical_walls, Wall.Horizontally);
        Arrays.sort(horizontal_walls, Wall.Vertically);
    }
    
    public static sensorInput getExpectedMeasurements(double _x, double _y, double heading_offset, int map){
        sensorInput distance = new sensorInput();
                
        double heading = 0.5 * Math.PI + heading_offset; // up relative to the robot
        distance.north = (int) Math.round(getNearestWallDistance(_x, _y, heading, map)) - ROBOT_RADIUS;
                
        heading = heading_offset; // right relative to the robot
        distance.east = (int) Math.round(getNearestWallDistance(_x, _y, heading, map)) - ROBOT_RADIUS;
                
        heading = 1.5 * Math.PI + heading_offset;
        distance.south = (int) Math.round(getNearestWallDistance(_x, _y, heading, map)) - ROBOT_RADIUS;
                
        heading = Math.PI + heading_offset;
        distance.west = (int) Math.round(getNearestWallDistance(_x, _y, heading, map)) - ROBOT_RADIUS;
        
        distance.get[NORTH] = distance.north;
        distance.get[EAST] = distance.east;
        distance.get[SOUTH] = distance.south;
        distance.get[WEST] = distance.west;
        
        return distance;
    }
    
    private static double getNearestWallDistance(double _x, double _y, double heading, int map){
        double cos = Math.cos(heading);
        double sin = -Math.sin(heading);
        
        Wall mySensor = new Wall();
        mySensor.x1 = (int) Math.round(_x);
        mySensor.y1 = (int) Math.round(_y);
        mySensor.x2 = (int) Math.round(_x + MAXDISTANCE * cos);
        mySensor.y2 = (int) Math.round(_y + MAXDISTANCE * sin);
        
        if (mySensor.x1 <= 0 || mySensor.x1 >= 244 || mySensor.y1 <= 0 || mySensor.y1 >= 244){
            return MAXDISTANCE;
        }
        
        double horizontal_distance = MAXDISTANCE;
        double dist = 0.0;
        for (Wall w : horizontal_walls){
            if (visible(w, map)){
                dist = getDistanceToIntersection(mySensor, w);
                if (dist > 0 && dist < horizontal_distance){
                    horizontal_distance = dist;
                }    
            }
        }
        
        double vertical_distance = MAXDISTANCE;
        dist = 0.0;
        for (Wall w : vertical_walls){
            if (visible(w, map)){
                dist = getDistanceToIntersection(mySensor, w);
                if (dist > 0 && dist < vertical_distance){
                    vertical_distance = dist;
                }    
            }
        }
        
        return Math.min(vertical_distance, horizontal_distance);
    }
    
    private static boolean visible(Wall wall, int map){
        
        for (int m : wall.maps){
            if (m == map){
                return true;
            }
        }
        return false;
    }
                                                
    private static Wall shiftWall(double x, double y, Wall wall){
        double deltaX = (x > 0.5 * (wall.x1 + wall.x2)) ? 0.5 * wall.thickness : -0.5 * wall.thickness;
        double deltaY = (y > 0.5 * (wall.y1 + wall.y2)) ? 0.5 * wall.thickness : -0.5 * wall.thickness;
        Wall newWall = new Wall();
        newWall.x1 = (int) Math.round(wall.x1 + deltaX);
        newWall.x2 = (int) Math.round(wall.x2 + deltaX);
        newWall.y1 = (int) Math.round(wall.y1 + deltaY);
        newWall.y2 = (int) Math.round(wall.y2 + deltaY);
        return newWall;
    }
    
    public static double getDistanceToIntersection(Wall sensor, Wall target) {
        Point p = new Point(sensor.x1, sensor.y1);
        Point p2 = new Point(sensor.x2, sensor.y2);
        Point q = new Point(target.x1, target.y1);
        Point q2 = new Point(target.x2, target.y2);
        
        Point r = subtractPoints(p2, p);
        Point s = subtractPoints(q2, q);

        
        double uNumerator = (double) crossProduct(subtractPoints(q, p), r);
        double denominator = (double) crossProduct(r, s);

        if (uNumerator == 0 && denominator == 0) {
            // They are colinear

            // Do they overlap? (Are all the point differences in either direction the same sign)
            // Using != as exclusive or
            if ((q.x - p.x < 0) != (q.x - p2.x < 0) != (q2.x - p.x < 0) != (q2.x - p2.x < 0) || ((q.y - p.y < 0) != (q.y - p2.y < 0) != (q2.y - p.y < 0) != (q2.y - p2.y < 0))){
                return MAXDISTANCE; 
            }
        }

        if (denominator == 0) {
            // lines are parallel
            return MAXDISTANCE;
        }

        double u = uNumerator / denominator;
        double t = crossProduct(subtractPoints(q, p), s) / denominator;

        if ((u >= 0) && (u <= 1)) {
            return t * Math.sqrt(r.x * r.x + r.y * r.y);
        } else {
            return MAXDISTANCE;
        }
    }


    private static int crossProduct(Point point1, Point point2) {
        return point1.x * point2.y - point1.y * point2.x;
    }

    private static Point subtractPoints(Point point1, Point point2) {
        Point result = new Point(point1.x - point2.x, point1.y - point2.y);

        return result;
    }

    private static boolean equalPoints(Point point1, Point point2) {
        return (point1.x == point2.x) && (point1.y == point2.y);
    }
boolean straightLine(Particle loc, Point next){
    boolean lineOfSight = true;
    Point beelineStart = new Point(loc.x, loc.y);
    Point beelineEnd = new Point(next.x, next.y);
    for (Wall w : vertical_walls) {
      if (visible(w, loc.map) && doIntersect(beelineStart, beelineEnd, new Point(w.x1, w.y1), new Point(w.x2, w.y2))) {
        lineOfSight = false;
        break;
      }
    }
    for (Wall w : horizontal_walls) {
      if (visible(w, loc.map) && doIntersect(beelineStart, beelineEnd, new Point(w.x1, w.y1), new Point(w.x2, w.y2))) {
        lineOfSight = false;
        break;
      }
    }
    return lineOfSight;
  }
  // Given three colinear points p, q, r, the function checks if
  // point q lies on line segment 'pr'
  boolean onSegment(Point p, Point q, Point r)
  {
    if (q.x <= Math.max(p.x, r.x) && q.x >= Math.min(p.x, r.x) &&
      q.y <= Math.max(p.y, r.y) && q.y >= Math.min(p.y, r.y))
      return true;

    return false;
  }

  // To find orientation of ordered triplet (p, q, r).
  // The function returns following values
  // 0 --> p, q and r are colinear
  // 1 --> Clockwise
  // 2 --> Counterclockwise


    // Source:  http://www.geeksforgeeks.org/check-if-two-given-line-segments-intersect/
  int orientation(Point p, Point q, Point r)
  {
    // See 10th slides from following link for derivation of the formula
    // http://www.dcs.gla.ac.uk/~pat/52233/slides/Geometry1x1.pdf
    int val = (int)((q.y - p.y) * (r.x - q.x) - (q.x - p.x) * (r.y - q.y));

    if (val == 0) return 0;  // colinear

    return (val > 0)? 1: 2; // clock or counterclock wise
  }

  // The main function that returns true if line segment 'p1q1'
  // and 'p2q2' intersect.
  boolean doIntersect(Point p1, Point q1, Point p2, Point q2)
  {
    // Find the four orientations needed for general and
    // special cases
    int o1 = orientation(p1, q1, p2);
    int o2 = orientation(p1, q1, q2);
    int o3 = orientation(p2, q2, p1);
    int o4 = orientation(p2, q2, q1);

    // General case
    if (o1 != o2 && o3 != o4)
      return true;

    // Special Cases
    // p1, q1 and p2 are colinear and p2 lies on segment p1q1
    if (o1 == 0 && onSegment(p1, p2, q1)) return true;

    // p1, q1 and p2 are colinear and q2 lies on segment p1q1
    if (o2 == 0 && onSegment(p1, q2, q1)) return true;

    // p2, q2 and p1 are colinear and p1 lies on segment p2q2
    if (o3 == 0 && onSegment(p2, p1, q2)) return true;

    // p2, q2 and q1 are colinear and q1 lies on segment p2q2
    if (o4 == 0 && onSegment(p2, q1, q2)) return true;

    return false; // Doesn't fall in any of the above cases
  }
}