package sdv.tools.boxes;

public class EncoderBox {
    private volatile String[] str;
    private boolean run;

    public EncoderBox() {
        str = new String[]{"NO RESPONSE", "NO RESPONSE", "NO RESPONSE", "NO RESPONSE"};
    }


    public void setValue(String[] currentString) {
        this.str = currentString;
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



