package IPPSystem.Interfaces;

import java.util.Map;

public interface TabStateful {
    /** Export current UI state (filters, selected IDs, query text, etc.) */
    Map<String, Object> exportState();

    /** Import UI state into a fresh controller (called in new tab) */
    void importState(Map<String, Object> state);
}
