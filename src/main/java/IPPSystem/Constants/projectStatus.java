package IPPSystem.Constants;

public enum projectStatus {
    PLANNING("planning"),
    PROGRESSING("inProgress"),
    DELAY("delay"),
    FINISH("finished"),
    CANCEL("cancel");

    private final String status;
    projectStatus(String status){this.status = status;}
    @Override
    public String toString(){return this.status;}
}
