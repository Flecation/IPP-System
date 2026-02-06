package IPPSystem.Interfaces;

public interface AddOverlayForm {
    /** True if required fields are filled and values look valid (basic UI validation). */
    boolean isFormValid();

    /** True if the user has typed/selected anything (used to warn before dismiss). */
    boolean hasUnsavedChanges();

    /** Message to show when the user clicks outside but the form isn't ready. */
    String getValidationMessage();
}
