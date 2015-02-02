import java.util.Arrays;
import java.util.ArrayList;
public class Particle_Filter{
    long startTime = System.nanoTime();
    int numParticles = 1024;
    public Particle[] working_set_particles = new Particle[numParticles];
    private Particle[] resampled_Particles = new Particle[numParticles];
    double measurementNoise = 5;
    int current_map = 3;
    double[] long_term_mapCount = {0,0,0,0,0,0,0,0,0,0,0,0};
    int mapCountIterations = 0;
    double[] map_probabilities = {(1.0/12.0), (1.0/12.0), (1.0/12.0), (1.0/12.0), (1.0/12.0), (1.0/12.0), (1.0/12.0), (1.0/12.0), (1.0/12.0), (1.0/12.0), (1.0/12.0), (1.0/12.0)};
    ArrayList<Integer> possibilities; // this will help eliminate options we decide are impossible
    Maps maps = new Maps();
    
    public Particle_Filter(){
        createParticles( new Point (100,25) );
    }
    
    public Particle_Filter(int startx, int starty){
        createParticles( new Point (startx,starty) );
    }
    
    public Particle process(sensorInput distance, motorControl motors){
        moveParticles(motors);
        updateMeasurementProbability(distance);
        return resample();
    }
    
    static class Particle {
      int x;
      int y;
      double w; // weight
      double orientation;
      int map;
      int dog;
      Particle(){}
    };
    
    private int millis(){
        return (int)( ((double)(System.nanoTime() - startTime)) * 0.000001);
    }
    
    double Gaussian(double mu, double sigma, double x){
        
        // calculates the probability of x for 1-dim Gaussian with mean mu and var. sigma
        return (double) Math.exp(- (Math.pow(mu - x, 2)) / Math.pow(sigma, 2) / 2.0f) / Math.sqrt(2.0f * Math.PI * Math.pow(sigma, 2));
    }
    
    static boolean use_last = false;
    double y2;
    double random_Gaussian(double mu, double sigma){
        double x1, x2, w, y1;
        if (use_last)		        // use value from previous call 
        {
            y1 = y2;
            use_last = false;
        }
        else
        { 
            do {
                x1 = 2.0 * Math.random() - 1.0;
                x2 = 2.0 * Math.random() - 1.0;
                w = x1 * x1 + x2 * x2;
            } while ( w >= 1.0 );

            w = Math.sqrt( (-2.0 * Math.log( w ) ) / w );
            y1 = x1 * w;
            y2 = x2 * w;
            use_last = true;
        }

        return( mu + y1 * sigma );   
    }
    
    private void createParticles(Point centerPos){
        possibilities = new ArrayList<Integer>();
        for (int i = 0; i < map_probabilities.length; i++){
            possibilities.add(i);
        }
        for (int i = 0; i < numParticles; i++){
            working_set_particles[i] = new Particle();
            working_set_particles[i].x = (int) Math.floor(random_Gaussian(centerPos.x, 122)); // within half an arena of the expected position
            working_set_particles[i].y = (int) Math.floor(random_Gaussian(centerPos.y, 122));
            working_set_particles[i].orientation = Math.random() * 2 * Math.PI;
            working_set_particles[i].w = 1.0f / (double) numParticles; // each particle is equally likely at first.
            working_set_particles[i].map = (int) Math.floor(Math.random() * 12);
            resampled_Particles[i] = new Particle();
            resampled_Particles[i].x = working_set_particles[i].x;
            resampled_Particles[i].y = working_set_particles[i].y;
            resampled_Particles[i].orientation = working_set_particles[i].orientation;
            resampled_Particles[i].w = working_set_particles[i].w;
            resampled_Particles[i].map = working_set_particles[i].map;
        }
    }

    int pmillis = millis();
    void moveParticles(motorControl m){        
        int i = 0;
        double fullPowerDistanceX = (millis()-pmillis)*(motorControl.cmpsH/1000.0); // average full power speed of 11cm per second
        double fullPowerDistanceY = (millis()-pmillis)*(motorControl.cmpsV/1000.0);  // average full power speed of 11 cm per second.
        for (i = 0; i < numParticles; i++){
            int x_change = (int) Math.floor(random_Gaussian(motorControl.getPercentageFromMotorPower(m.h) * fullPowerDistanceX, motorControl.motorNoise));
            int y_change = (int) Math.floor(random_Gaussian(motorControl.getPercentageFromMotorPower(m.v) * fullPowerDistanceY, motorControl.motorNoise));
            working_set_particles[i].x += x_change;
            working_set_particles[i].y += y_change;
            double x_prob = Gaussian(motorControl.getPercentageFromMotorPower(m.h) * fullPowerDistanceX, motorControl.motorNoise, x_change);
            double y_prob = Gaussian(motorControl.getPercentageFromMotorPower(m.v) * fullPowerDistanceY, motorControl.motorNoise, y_change);
            working_set_particles[i].w *= 0.5 * (x_prob + y_prob);
        }
        pmillis = millis();
    }
    
    
    private void updateMeasurementProbability(sensorInput distance){
        double max = 0;
        for (int i = 0; i < numParticles; i++){

            double w = 1.0;
            sensorInput expectedMeasurements = maps.getExpectedMeasurements(working_set_particles[i].x, working_set_particles[i].y, working_set_particles[i].orientation, working_set_particles[i].map);
            
            int dir = 0;
            for (dir = 0; dir < 4; dir++){
                int meas = distance.get[dir];
                int expect = expectedMeasurements.get[dir];
                double change = Math.abs(Gaussian(meas, measurementNoise, expect));
                w *= change;
            }
            if (w > max) max = w;
            working_set_particles[i].w = w; // changed to *= to include conditional probability, but it works better with = for some reason.
        }
    }

    private Particle normalizeWeights(){    
        /* Get the sum of all the particle weights */
        double sum = 0.0;
        double max1 = 0.0;
        int i = 0;
        for (i = 0; i < numParticles; i++){
            sum += Math.abs(working_set_particles[i].w);
            if (working_set_particles[i].w > max1){ max1 = working_set_particles[i].w; }
        }

        /* Divide each particle weight by the sum, so their total will add to 1.0 */
        double max2 = 0.0;
        int maxInd2 = 0;
        for (i = 0; i < numParticles; i++){
            if (working_set_particles[i].w > max1) { // Double check that each particle, is in fact less than the maximum
                System.out.println("GREATER THAN MAX          " + working_set_particles[i].w + " > " + max1 + " " + i);
                //break;
            } 
            working_set_particles[i].w = working_set_particles[i].w/sum;
            if (working_set_particles[i].w > max2){ max2 = working_set_particles[i].w; maxInd2 = i;} 
        }
        double sum2 = 0.0;
        for (i = 0; i < numParticles; i++){
            sum2 += working_set_particles[i].w;
        }
        return working_set_particles[maxInd2];
    }

    private Particle resample(){
    //System.out.println("resampling...");
        Particle maxParticle = normalizeWeights(); // the most likely position of the robot.
        double wmax = maxParticle.w;
        int index = (int) Math.floor(Math.random() * numParticles);
        double beta = 0;

        int[] mapCount = {0,0,0,0,0,0,0,0,0,0,0,0};
        double sumX = 0;
        double sumY = 0;
        for (int i = 0; i < numParticles; i++){
            beta += Math.random() * 2 * wmax;
            while (working_set_particles[index].w < beta){
                beta -= working_set_particles[index].w;
                index++;
                index = index % numParticles;
            }

            // Copy the selected particle's information into the resampled particles array.
            resampled_Particles[i].x = working_set_particles[index].x;
            resampled_Particles[i].y = working_set_particles[index].y;
            resampled_Particles[i].w = 1.0 / (double) numParticles;
            resampled_Particles[i].orientation = working_set_particles[index].orientation;
            //resampled_Particles[i].map = 1 + ((int) random_Gaussian(current_map + 2, 0.5)) % 3; // favor current map
            resampled_Particles[i].map = possibilities.get((int) Math.floor(Math.random() * possibilities.size())); // pick a random map out of those that are still possibilities
            mapCount[working_set_particles[index].map]++;
            sumX += resampled_Particles[i].x;
            sumY += resampled_Particles[i].y;
        }

        // Swap the resampled set with the working set.
        Particle[] c = working_set_particles;
        working_set_particles = resampled_Particles;
        resampled_Particles = c;

        for (int i = 0; i < mapCount.length; i++){
            long_term_mapCount[i] += mapCount[i];
        }
        mapCountIterations += numParticles;

        int maxInd = 0;
        double maxVal = 0;
        for (int i = 0; i < map_probabilities.length; i++){
            map_probabilities[i] = long_term_mapCount[i] / (double) mapCountIterations;
            if (map_probabilities[i] > maxVal){
                maxInd = i;
                maxVal = map_probabilities[i];
            }
        }
        
        current_map = maxInd;
        maxParticle.map = current_map; // maybe we should be more explicit in our assumptions.
        // i.e. favor the maps without dogs until we run into one, or something like that.
        System.out.print(current_map + " ");
        for (double prob : map_probabilities){
            System.out.print(Math.floor(10000*prob)/100.0 + " ");
        }
        return maxParticle;
    }
}