package Constants;

public enum role {
    MANAGER ("manager"),
    SUPERVISOR("supervisor");


    private final String displayRole;

    role(String displayTitle){
        this.displayRole = displayTitle;
    }

    @Override
    public String toString(){
        return this.displayRole;
    }
}
