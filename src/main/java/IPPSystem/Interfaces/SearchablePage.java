package IPPSystem.Interfaces;

public interface SearchablePage {
    /** Called whenever the sidebar search text changes (or user selects a suggestion). */
    void onSearch(String query);

    /** Optional: called when user clears the search */
    default void onSearchCleared() {
        onSearch("");
    }
}
