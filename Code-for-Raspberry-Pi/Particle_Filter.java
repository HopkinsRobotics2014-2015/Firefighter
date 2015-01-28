import java.util.Arrays;
public class Particle_Filter{
    long startTime = System.nanoTime();
    int numParticles = 1024;
    public Particle[] working_set_particles = new Particle[numParticles];
    private Particle[] resampled_Particles = new Particle[numParticles];
    double measurementNoise = 5;
    int current_map = 1;
    double[] long_term_mapCount = {0,0,0,0,0,0,0}; // no dog, map1, map2, map3, dog1, dog2, dog3
    int mapCountIterations = 0;
    double[] map_probabilities = {(1.0/3.0), (1.0/3.0), (1.0/3.0)};
    double[] dog_probabilities = {0.25, 0.25, 0.25, 0.25};
    Maps maps = new Maps();
    
    public Particle_Filter(){
        createParticles();
    }
    
    public void process(sensorInput distance, motorControl motors){
        moveParticles(motors);
        updateMeasurementProbability(distance);
        resample();
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
    
    private void createParticles(){
        for (int i = 0; i < numParticles; i++){
            working_set_particles[i] = new Particle();
            working_set_particles[i].x = (int) Math.floor(Math.random() * 244);
            working_set_particles[i].y = (int) Math.floor(Math.random() * 244);
            working_set_particles[i].orientation = 0;//Math.random() * 2 * Math.PI;
            working_set_particles[i].w = 1.0f / (double) numParticles; // each particle is equally likely at first.
            working_set_particles[i].map = (int) Math.floor( 1 + Math.random() * 3 );
            working_set_particles[i].dog = (4 + (int) Math.floor(Math.random() * 4)) % 7;
            resampled_Particles[i] = new Particle();
            resampled_Particles[i].x = working_set_particles[i].x;
            resampled_Particles[i].y = working_set_particles[i].y;
            resampled_Particles[i].orientation = working_set_particles[i].orientation;
            resampled_Particles[i].w = working_set_particles[i].w;
            resampled_Particles[i].map = working_set_particles[i].map;
            resampled_Particles[i].dog = working_set_particles[i].dog;
        }
    }

    int pmillis = millis();
    void moveParticles(motorControl m){        
        int i = 0;
        double fullPowerDistanceX = 0;//(millis()-pmillis)*(motorControl.cmpsH/1000.0); // average full power speed of 11cm per second
        double fullPowerDistanceY = 0;//(millis()-pmillis)*(motorControl.cmpsV/1000.0);  // average full power speed of 11 cm per second.
        for (i = 0; i < numParticles; i++){
            int x_change = (int) Math.floor(random_Gaussian(motorControl.getPercentageFromMotorPower(m.h) * fullPowerDistanceX, motorControl.motorNoise));
            int y_change = (int) Math.floor(random_Gaussian(motorControl.getPercentageFromMotorPower(m.v) * fullPowerDistanceY, motorControl.motorNoise));
            working_set_particles[i].x += x_change;
            working_set_particles[i].y += y_change;
        }
        pmillis = millis();
    }
    
    
    private void updateMeasurementProbability(sensorInput distance){
        double max = 0;
        for (int i = 0; i < numParticles; i++){

            double w = 1.0;
            sensorInput expectedMeasurements = maps.getExpectedMeasurements(working_set_particles[i].x, working_set_particles[i].y, working_set_particles[i].orientation, working_set_particles[i].map, working_set_particles[i].dog);
            //System.out.println(working_set_particles[i].x + " " + working_set_particles[i].y + " " + working_set_particles[i].orientation + " " + working_set_particles[i].map + " " + working_set_particles[i].dog);
            //System.out.println(expectedMeasurements);
            int dir = 0;
            for (dir = 0; dir < 4; dir++){
                int meas = distance.get[dir];
                int expect = expectedMeasurements.get[dir];
                double change = Math.abs(Gaussian(meas, measurementNoise, expect));
                w *= change;
            }
            if (w > max) max = w;
            working_set_particles[i].w = w; // should be *=
        }
    }

    private double normalizeWeights(){    
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
        for (i = 0; i < numParticles; i++){
            if (working_set_particles[i].w > max1) { // Double check that each particle, is in fact less than the maximum
                System.out.println("GREATER THAN MAX          " + working_set_particles[i].w + " > " + max1 + " " + i);
                //break;
            } 
            working_set_particles[i].w = working_set_particles[i].w/sum;
            if (working_set_particles[i].w > max2){ max2 = working_set_particles[i].w; } 
        }
        double sum2 = 0.0;
        for (i = 0; i < numParticles; i++){
            sum2 += working_set_particles[i].w;
        }
        return max2;
    }

    private void resample(){
    //System.out.println("resampling...");
        double wmax = normalizeWeights();
        int index = (int) Math.floor(Math.random() * numParticles);
        double beta = 0;

        int[] mapCount = {0,0,0,0,0,0,0};
        double sumX = 0;
        double sumY = 0;
        int i = 0;
        for (i = 0; i < numParticles; i++){
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
            resampled_Particles[i].map = (int) Math.floor(Math.random() * 3 + 1);
            //resampled_Particles[i].dog = should this randomize?
            mapCount[working_set_particles[index].map]++;
            mapCount[working_set_particles[index].dog]++;
            sumX += resampled_Particles[i].x;
            sumY += resampled_Particles[i].y;
        }

        // Swap the resampled set with the working set.
        Particle[] c = working_set_particles;
        working_set_particles = resampled_Particles;
        resampled_Particles = c;

        for (i = 0; i < mapCount.length; i++){
            long_term_mapCount[i] += mapCount[i];
        }
        mapCountIterations += numParticles;

        //System.out.println(maxInd + 1 + " " + maxVal);

        int maxInd = 0;
        double maxVal = 0;
        for (i = 0; i < map_probabilities.length; i++){
            map_probabilities[i] = long_term_mapCount[i+1] / (double) mapCountIterations;
            if (map_probabilities[i] > maxVal){
                maxInd = i;
                maxVal = map_probabilities[i];
            }
        }
        dog_probabilities[0] = long_term_mapCount[0] / (double) mapCountIterations;
        dog_probabilities[1] = long_term_mapCount[4] / (double) mapCountIterations;
        dog_probabilities[2] = long_term_mapCount[5] / (double) mapCountIterations;
        dog_probabilities[3] = long_term_mapCount[6] / (double) mapCountIterations;
        

        current_map = maxInd + 1;
        System.out.println(1 + " " + Math.floor(map_probabilities[0] * 1000)/10.0 + "%   " + 2 + " " + Math.floor(map_probabilities[1] * 1000)/10.0 + "%   " + 3 + " " + Math.floor(map_probabilities[2] * 1000)/10.0 + "%     " + (maxInd+1) + " dog: " + Arrays.toString(dog_probabilities) + "    avgX: " + (int) (sumX / numParticles) + "     avgY: " + (int) (sumY / numParticles));
    }
}
