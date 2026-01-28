package IPPSystem.Constants;

public enum notificationType {

    WARNING("warning"),
    WRONG("wrong"),
    SUCCESS("success"),
    ERROR("error"),
    INFO("info");

    private final String displayTitle;

    notificationType(String displayTitle){
        this.displayTitle = displayTitle;
    }

    @Override
    public String toString(){
        return this.displayTitle;
    }
}

