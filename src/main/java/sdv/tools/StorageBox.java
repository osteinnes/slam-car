package sdv.tools;

public class StorageBox {
    private volatile String[] str;
    private boolean run;

    public StorageBox() {
        str = new String[4];
    }


    public void setValue(String[] currentString) {
        this.str = currentString;
    }

    public String[] getValue() {
        return str;
    }
}



