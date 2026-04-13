package util;

/**
 * TimerThread - Utility for quiz timing.
 */
public class TimerThread extends Thread {
    private int seconds;

    public TimerThread(int seconds) {
        this.seconds = seconds;
    }

    @Override
    public void run() {
        // Implement timer logic
    }
}
