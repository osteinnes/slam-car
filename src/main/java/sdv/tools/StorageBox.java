package sdv.tools;

public class StorageBox {
    private String[] currentResponse;
    private boolean run;
    private volatile boolean read;

    public StorageBox(){
        this.run = false;
        this.read = false;
    }

    public void setValue(String[] currentString) {
        this.currentResponse = currentString;
    }

    public String[] getValue(){
        return currentResponse;
    }
    public void setRun(boolean run){
        this.run = run;
    }
    public boolean getRun(){
        return run;
    }

    public void setRead(boolean read){
        this.read = read;
    }
    public boolean getRead(){
        return read;
    }

}
