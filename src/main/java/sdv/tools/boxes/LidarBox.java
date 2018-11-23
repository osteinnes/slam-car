package sdv.tools.boxes;


import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

/**
 * LidarBox represents a StorageBox which is used
 * between LidarThread- and Slam-threads. By use of AtomicRef
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

    // Atomic bool to make box thread-safe
    private AtomicBoolean atomicBoolean;

    /**
     * Creates empty scan-array when instance is created.
     */
    public LidarBox() {
        scans = new int[]{};
        atomicBoolean = new AtomicBoolean(false);
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
        isReady = updater.compareAndSet(this, this.scans, currentScan);
        atomicBoolean.set(isReady);
    }

    /**
     * Returns scan values for last revolution
     * @return scan values for last revolution
     */
    public int[] getValue() {
        atomicBoolean.set(false);
        return scans;
    }

    /**
     * Returns true if scans is ready to be read. (prev values was changed)
     * @return true if scans is ready to be read. (prev values was changed)
     */
    public boolean isReady() {
        return atomicBoolean.get();
    }

}
