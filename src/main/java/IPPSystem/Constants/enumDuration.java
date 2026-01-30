package IPPSystem.Constants;

public enum enumDuration {

    MONTH("month"),
    DAY("day"),
    YEAR("year");

    private final String duration;

    enumDuration(String displayTitle){
        this.duration = displayTitle;
    }

    @Override
    public String toString(){
        return this.duration;
    }
}
