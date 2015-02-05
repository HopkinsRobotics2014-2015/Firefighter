class Navigator{
    public Navigator(){
        
    }
    
    private class Node {
        int x,y;
        float f,g,h;
        Node next;
        ArrayList<Node> neighbors;
        public Node(int _x, int _y){
            this.x = _x;
            this.y = _y;
            this.f = Float.POSITIVE_INFINITY;
            this.g = 0;
            this.h = 0;
            this.neighbors = new ArrayList<Node>();
        }
        
    }
    
    getPath(start, end, map)?
    
    void ASTAR(Node startNode, Node endNode){
      ArrayList<Node> open = new ArrayList<Node>();
      ArrayList<Node> closed = new ArrayList<Node>();
      
      boolean foundPath = false;
      
      Node q = endNode;
      q.f = 0;
      q.g = 0;
      
      open.add(q);
      
      while (!open.isEmpty()){
        
        float minf = Float.POSITIVE_INFINITY;
        for (Node n : open){
          if (n.f < minf){
            q = n;
            minf = n.f;
          }
        }
        
        if (q == startNode){
          foundPath = true;
          break;
        }
        
        open.remove(q);
        
        for (Node neighbor : q.neighbors){
          float g = q.g + q.getCost(neighbor);
          float h = neighbor.getStraightLineDistance(startNode);
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

