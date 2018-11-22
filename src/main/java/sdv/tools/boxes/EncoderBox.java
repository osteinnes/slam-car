package sdv.tools.boxes;

public class EncoderBox {
    private volatile String[] str;
    private boolean run;

    public EncoderBox() {
        str = new String[4];
    }


    public void setValue(String[] currentString) {
        this.str = currentString;
    }

    public String[] getValue() {
        return str;
    }
}



