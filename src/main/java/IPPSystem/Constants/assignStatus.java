package IPPSystem.Constants;

public enum assignStatus {
    CUSTOM("customAssign"),
    AUTO("autoAssign"),
    EXTRA("extraAssign"),
    ACTUAL("actualResult");

    private final String status;
    assignStatus(String assignStatus){this.status = assignStatus;}

    @Override
    public String toString(){return this.status;}
}
