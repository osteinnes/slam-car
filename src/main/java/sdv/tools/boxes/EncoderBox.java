package sdv.tools.boxes;

import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

public class EncoderBox {
    private volatile String[] str;
    private boolean run;

    public EncoderBox() {
        str = new String[]{"NO RESPONSE", "NO RESPONSE", "NO RESPONSE", "NO RESPONSE"};
    }

    private static final AtomicReferenceFieldUpdater<EncoderBox,String[]> updater =
            AtomicReferenceFieldUpdater.newUpdater(
                    EncoderBox.class, String[].class, "str");


    /*public void setValue(String[] currentString) {
        synchronized (this) {
            this.str = currentString;
        }
    }*/

    public void setValue(String[] currentString) {
        updater.compareAndSet(this, this.str, currentString);
    }

    public String[] getValue() {
            return str;
    }

    public void start() {
            run = true;
    }

    public void stop() {
            run = false;
    }

    public boolean active() {
            return run;
    }
}



