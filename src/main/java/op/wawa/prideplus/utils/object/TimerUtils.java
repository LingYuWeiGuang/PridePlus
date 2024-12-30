package op.wawa.prideplus.utils.object;

public final class TimerUtils {
    public long lastMS = System.currentTimeMillis();
    public long time;
    private long lastTime;
    public long lastMs;
    public void reset() {
        lastMS = System.currentTimeMillis();
    }
    public long getTimePassed() {
        return System.currentTimeMillis() - this.lastTime;
    }
    public boolean hasTimeElapsed(long time, boolean reset) {
        if (System.currentTimeMillis() - lastMS > time) {
            if (reset) reset();
            return true;
        }

        return false;
    }
    public long time() {
        return System.nanoTime() / 1000000L - time;
    }
    public boolean sleep(final long time) {
        if (time() >= time) {
            reset();
            return true;
        }
        return false;
    }


    public boolean hasReached(final double milliseconds) {
        return this.getCurrentMS() - this.lastMS >= milliseconds;
    }
    public long elapsed() {
        return System.currentTimeMillis() - lastMS;
    }

    public boolean hasTimeElapsed(long time) {
        this.time = time;
        return System.currentTimeMillis() - lastMS > this.time;
    }

    public long getTime() {
        return System.currentTimeMillis() - lastMS;
    }
    public long getCurrentMS() {
        return System.nanoTime() / 1000000L;
    }
    public void setTime(long time) {
        lastMS = time;
    }

    public static final class CPSDelay {
        private final TimerUtils timerUtils = new TimerUtils();

        public boolean shouldAttack(int cps) {
            int aps = 20 / cps;
            return timerUtils.hasTimeElapsed(50 * aps, true);
        }

        public void reset() {
            timerUtils.reset();
        }
    }
    public boolean hasTimePassed(long time) {
        return System.currentTimeMillis() - lastTime >= time;
    }
    public boolean delay(final float milliSec) {
        return this.getCurrentMS() - this.lastMS >= milliSec;
    }
    public boolean delay(float nextDelay, boolean reset) {
        if (System.currentTimeMillis() - lastMs >= nextDelay) {
            if (reset) {
                this.reset();
            }
            return true;
        }
        return false;
    }
    public final long getDifference() {
        return this.getCurrentMS() - this.lastMS;
    }
    public boolean isDelayComplete(double valueState) {
        return System.currentTimeMillis() - lastMs >= valueState;
    }
}
