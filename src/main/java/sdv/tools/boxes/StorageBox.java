package sdv.tools.boxes;

public abstract class StorageBox {

    protected boolean run;

    public StorageBox() {

    }


    public void start(){
        run = true;
    }

    public void stop(){
        run = false;
    }

    public boolean active(){
        return run;
    }

}
