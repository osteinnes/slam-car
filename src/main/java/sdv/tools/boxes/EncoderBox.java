package sdv.tools.boxes;

import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

/**
 * EncoderBox represents a StorageBox which is used
 * between Encoder-thread and Slam-thread. By use of AtomicRef
 * it will make sure that the communication between threads
 * is thread-safe. Encoder values will be stored at at interval in the box
 * where the slam-thread can read the latest value.
**/

public class EncoderBox extends StorageBox {
    //String array for storing encoder values
    private volatile String[] str;

    //Updates the str array only on change
    private static final AtomicReferenceFieldUpdater<EncoderBox,String[]> updater =
            AtomicReferenceFieldUpdater.newUpdater(
                    EncoderBox.class, String[].class, "str");

    //Sets a placeholder value for the string array to avoid nullpointer
    //if the slam thread tries to read values before they have been set.
    public EncoderBox() {
        str = new String[]{"NO RESPONSE", "NO RESPONSE", "NO RESPONSE", "NO RESPONSE"};
    }

    /**
     * Sets the latest encoder value fetched in a string array.
     * @param currentString latest encoder values ex: "enc1:2000:enc2:3000"
     */
    public void setValue(String[] currentString) {
        updater.compareAndSet(this, this.str, currentString);
    }

    /**
     * Lastest encoder readings
     * @return latest encoder readings
     */
    public String[] getValue() {
            return str;
    }

}



