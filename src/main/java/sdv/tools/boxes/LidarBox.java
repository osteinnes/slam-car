package sdv.tools.boxes;


import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

/**
 * LidarBox represents a StorageBox which is used
 * between LidarScan- and Slam-threads. By use of AtomicRef
 * it will make sure that the communication between threads
 * is thread-safe. However, it is not necessary when only
 * one thread modifies the field. Though this implementation is made
 * so that expanding on this class is no problem for thread-safety.
 *
 * @author Ole-martin Steinnes
 */
public class LidarBox extends StorageBox{

    // Holds scans from Lidar.
    private volatile int[] scans;

    // Holds isReady-flag
    private volatile boolean isReady;

    /**
     * Creates empty scan-array when instance is created.
     */
    public LidarBox() {
        scans = new int[]{};
    }

    // For thread safety upon expansion
    private static final AtomicReferenceFieldUpdater<LidarBox,int[]> updater =
            AtomicReferenceFieldUpdater.newUpdater(
                    LidarBox.class, int[].class, "scans");

    /**
     * Sets scan values for the last revolution.
     * @param currentScan current scan values by lidar (last whole revolution)
     */
    public void setValue(int[] currentScan) {
        updater.compareAndSet(this, this.scans, currentScan);
        this.isReady = true;
    }

    /**
     * Returns scan values for last revolution
     * @return scan values for last revolution
     */
    public int[] getValue() {
        isReady = false;
        return scans;
    }

    /**
     * Returns true if scans is ready to be read. (prev values was changed)
     * @return true if scans is ready to be read. (prev values was changed)
     */
    public boolean isReady() {
        return isReady;
    }

}
