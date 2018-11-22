package sdv.tools.boxes;

import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

public class EncoderBox extends StorageBox {
    private volatile String[] str;

    private static final AtomicReferenceFieldUpdater<EncoderBox,String[]> updater =
            AtomicReferenceFieldUpdater.newUpdater(
                    EncoderBox.class, String[].class, "str");


    public EncoderBox() {
        super();
        str = new String[]{"NO RESPONSE", "NO RESPONSE", "NO RESPONSE", "NO RESPONSE"};
    }


    public void setValue(String[] currentString) {
        updater.compareAndSet(this, this.str, currentString);
    }

    public String[] getValue() {
            return str;
    }

}



