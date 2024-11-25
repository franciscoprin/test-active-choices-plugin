import hudson.model.StringParameterValue
import jenkins.model.Jenkins

/**
 * Class to handle parameter reconstruction based on previous build data.
 */
class StringRebuildParameter {
    String jobName
    int jobBuildNumber
    boolean rebuildValueImmutable
    String parameterName
    String parameterDefaultValue
    String parameterDescription
    boolean parameterTrimTheString
    boolean isReadOnly
    String currentValue
    String errorMessage

    /**
     * Constructor that initializes the parameter based on the previous build's value.
     *
     * @param jobName                The name of the Jenkins job.
     * @param jobBuildNumber         The previous Jenkins build job number to retrieve the previous value.
     * @param rebuildValueImmutable  Whether the input should be immutable if it was previously set.
     * @param parameterName          The name of the parameter.
     * @param parameterDefaultValue  The default value if no previous value is available.
     * @param parameterDescription   The description of the parameter.
     * @param parameterTrimTheString Whether to trim whitespace from the beginning and end of the value.
     */
    StringRebuildParameter(String jobName, int jobBuildNumber, boolean rebuildValueImmutable, String parameterName,
                           String parameterDefaultValue, String parameterDescription, boolean parameterTrimTheString) {
        this.jobName = jobName
        this.jobBuildNumber = jobBuildNumber
        this.rebuildValueImmutable = rebuildValueImmutable
        this.parameterName = parameterName
        this.parameterDefaultValue = parameterDefaultValue
        this.parameterDescription = parameterDescription
        this.parameterTrimTheString = parameterTrimTheString
        this.isReadOnly = false
        this.errorMessage = null

        // Fetch the previous value during initialization
        fetchPreviousValue()
    }

    /**
     * Fetches the parameter value from the previous build and sets the current value.
     */
    private void fetchPreviousValue() {

        // Check if the build number was selected.
        // If the build number is not selected, it defaults to -1.
        if (this.jobBuildNumber < 0) {
            this.currentValue = this.parameterDefaultValue
            return
        }

        // Retrieve the Jenkins job
        def job = Jenkins.instance.getItemByFullName(this.jobName)
        if (job == null) {
            this.errorMessage = "Job '${this.jobName}' not found."
            this.currentValue = this.parameterDefaultValue
            return
        }

        // Get the specified previous build
        def previousBuild = job.getBuildByNumber(this.jobBuildNumber)
        if (previousBuild == null) {
            this.errorMessage = "Build #${this.jobBuildNumber} not found for job '${this.jobName}'."
            this.currentValue = this.parameterDefaultValue
            return
        }

        // Retrieve parameters from the previous build
        def paramsAction = previousBuild.getAction(hudson.model.ParametersAction)
        def previousValue = null
        if (paramsAction != null) {
            def previousParam = paramsAction.getParameter(this.parameterName)
            previousValue = previousParam?.getValue()
        }

        // Use the previous value if available, otherwise use the default
        this.currentValue = previousValue != null && previousValue != '' ? previousValue.toString() : this.parameterDefaultValue
        if (this.parameterTrimTheString) {
            this.currentValue = this.currentValue.trim()
        }

        // Determine if the parameter should be read-only
        if (this.rebuildValueImmutable && previousValue != null && previousValue != '') {
            this.isReadOnly = true
        }
    }

    /**
     * Generates an HTML-like representation of the parameter.
     *
     * @return An HTML string representation of the parameter.
     */
    def render() {
        def readOnlyAttribute = this.isReadOnly ? "readonly" : ""
        return """
<div class="jenkins-form-item tr ">
    <div class="jenkins-form-description">
        ${this.parameterDescription}
    </div>
    <div class="setting-main">
        <div name="parameter">
            <input name="value" placeholder="" type="text" class="setting-input" value="${this.currentValue}" ${readOnlyAttribute}>
        </div>
    </div>
    <div class="validation-error-area">
        ${this.errorMessage != null ? "<p style='color:red;'>${this.errorMessage}</p>" : ""}
    </div>
</div>
"""
    }
}

// Handle JobBuildNumber conversion, using -1 if empty
def jobBuildNumber = JobBuildNumber?.trim() ? JobBuildNumber.toInteger() : -1 // 6

def jobName = JobName // env.JOB_NAME
def rebuildValueImmutable = RebuildValueImmutable.toBoolean() // true
def parameterName = ParameterName // 'date-my-parameter'
def parameterDefaultValue = ParameterDefaultValue // "Default Value"
def parameterDescription = ParameterDescription // "This is a test parameter"
def parameterTrimTheString = ParameterTrimTheString.toBoolean() // true

// Initialize the parameter object
def stringRebuildParam = new StringRebuildParameter(jobName, jobBuildNumber, rebuildValueImmutable, parameterName, parameterDefaultValue, parameterDescription, parameterTrimTheString)

// Render the parameter value
def renderedValue = stringRebuildParam.render()

return renderedValue
