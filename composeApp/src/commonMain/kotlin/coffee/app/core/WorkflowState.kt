package coffee.app.core

/**
 * Sealed interface representing simple form workflow states.
 */
sealed class WorkflowState {
    /** No form interaction. */
    object Idle : WorkflowState()
    /** User is editing the form. */
    object Editing : WorkflowState()
    /** Validation in läout. */
    object Validating : WorkflowState()
    /** Failed validation with message. */
    data class Error(val message: String) : WorkflowState()
    /** Validated successfully – ready to persist. */
    object Success : WorkflowState()
}
