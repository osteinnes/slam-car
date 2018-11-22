package sdv.tools.boxes;

public abstract class StorageBox {

    protected boolean run;

    public StorageBox() {

    }

    protected abstract void setValue();

    protected abstract Object getValue();

}
