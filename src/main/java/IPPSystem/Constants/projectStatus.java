package IPPSystem.Constants;

public enum projectStatus {
    PLANNING("planning"),
    PROGRESSING("inProgress"),
    DELAY("delay"),
    FINISH("finished"),
    CANCEL("cancel");

    private final String status;

    projectStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return this.status;
    }

    public static projectStatus fromString(String value) {
        if (value == null) return null;
        String v = value.trim();
        for (projectStatus ps : values()) {
            if (ps.status.equalsIgnoreCase(v)) return ps;
        }
        return null; // unknown status in DB
    }

    public boolean isActive() {
        return this == PROGRESSING || this == DELAY;
    }

    public boolean isCompleted() {
        return this == FINISH;
    }
}
