package sdv.tools.boxes;


import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

public class LidarBox extends StorageBox{

    private volatile int[] scans;

    private volatile boolean isReady;



    public LidarBox() {
        super();
        scans = new int[]{};
    }

    private static final AtomicReferenceFieldUpdater<LidarBox,int[]> updater =
            AtomicReferenceFieldUpdater.newUpdater(
                    LidarBox.class, int[].class, "scans");

    public void setValue(int[] currentScan) {
        updater.compareAndSet(this, this.scans, currentScan);
        this.isReady = true;
    }

    public int[] getValue() {
        isReady = false;
        return scans;
    }

    public boolean isReady() {
        return isReady;
    }

}
