package sdv.tools.boxes;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * StorageBox represents a common field-box
 * for threads. It is abstrated, which means we
 * can expand further on this class to create
 * any concurrency StorageBox we would like.
 * While at it's current state it might be a little thin
 * the idea is to expand this for the navigation algorithm
 * to utilize and thus cohesion and low coupling is
 * paramount.
 *
 * @author Ole-martin Steinnes
 */
public abstract class StorageBox {

    // Boolean field which can be used to
    // determine if the box is active, start,
    // and stop it.
    protected AtomicBoolean run = new AtomicBoolean(false);

    /**
     * Sets the run-flag of the Box to true
     * can be used to "start" the box, which
     * would tell other threads the box is active
     * when prompted by isActive()
     */
    public void start(){
        run.set(true);
    }

    /**
     * Sets te run-flag of the Box to false
     * can be used to "stop" the box, shich
     * would tell other threads the box is inactive
     * when promted by isActive()
     */
    public void stop(){
        run.set(false);
    }

    /**
     * Returns run-status(flag)
     * @return run-status(flag)
     */
    public boolean active(){
        return run.get();
    }

}
