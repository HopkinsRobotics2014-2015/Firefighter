class motorControl {
    int v;
    int h;
    static double cmpsV = 11.0; // centimeters per second, should be approx 11.
    static double cmpsH = 11.0; // centimeters per second, should be approx 11.
    static double motorNoise = 6;
    motorControl(){}
    static int getMotorPower(double percentage){
        int scaledPower = (int) Math.floor(191 + 50 * percentage);
        return (int) Math.floor(Math.max(Math.min(scaledPower, 240), 115));
    }

    static double getPercentageFromMotorPower(int motorPower){
        double percentage = 0.02 * (motorPower - 191);
        return percentage;
    }
}