public class sensorInput {
  int north;
  int east;
  int south;
  int west;
  int[] get = new int[4];
  sensorInput(){}
  public String toString(){
    return ("NORTH: " + north + " EAST: " + east + " SOUTH: " + south + " WEST: " + west);
  }
}