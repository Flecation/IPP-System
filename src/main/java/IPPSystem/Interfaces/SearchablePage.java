package IPPSystem.Interfaces;

public interface SearchablePage {
    void onSearch(String keyword);

    default void onSearchClear() {
        onSearch("");
    }
}
