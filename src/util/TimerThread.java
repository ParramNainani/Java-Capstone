package util;

public class TimerThread extends Thread {
    private int timeLimitSeconds;
    private volatile boolean isTimeUp = false;

    public TimerThread(int timeLimitSeconds) {
        this.timeLimitSeconds = timeLimitSeconds;
    }

    @Override
    public void run() {
        try {
            for (int i = timeLimitSeconds; i > 0; i--) {
                Thread.sleep(1000); // 1 second
            }
            isTimeUp = true;
            System.out.println("\n\n[Timer] Time is up! Press Enter to see your results.");
        } catch (InterruptedException e) {
            // Timer interrupted early, meaning the quiz ended before time limit
        }
    }

    public boolean isTimeUp() {
        return isTimeUp;
    }
}
