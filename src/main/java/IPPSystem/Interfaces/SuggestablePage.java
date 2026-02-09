package IPPSystem.Interfaces;

import java.util.List;
public interface SuggestablePage {
    /** Return suggestion strings for current query (e.g., names, project titles). */
    List<String> getSuggestions(String query);
}
