class Navigator{
    public Navigator(){
        
    }
    
    getPath(start, end, map)?
    
    
    
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

