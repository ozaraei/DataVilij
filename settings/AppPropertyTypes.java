package settings;

/**
 * This enumerable type lists the various application-specific property types listed in the initial set of properties to
 * be loaded from the workspace properties <code>xml</code> file specified by the initialization parameters.
 *
 * @author Ritwik Banerjee
 * @see vilij.settings.InitializationParams
 */
public enum AppPropertyTypes {

    /* resource files and folders */
    //DATA_RESOURCE_PATH,

    /* user interface icon file names */
    SCREENSHOT_ICON,

    /* tooltips for user interface buttons */
    SCREENSHOT_TOOLTIP,

    DISPLAY_VALUE,

    DATA_VISUALIZATION,

    SAVE_FILE,

    SAVE_FILE_ERROR,

    TO_SAVE_THE_FILE_YOU_MUST_CLICK_ON_SAVE,

    /* error messages */
    //RESOURCE_SUBDIR_NOT_FOUND,

    /* application-specific message titles */
    //SAVE_UNSAVED_WORK_TITLE,

    /* application-specific messages */
    SAVE_UNSAVED_WORK,

    /* application-specific parameters */
    GUI_RESOURCE_PATHS,
    CSS_RESOURCE_PATHS,
    CSS_RESOURCE_FILENAMES,

    DATA_FILE_EXT,
    DATA_FILE_EXT_DESC,
    //TEXT_AREA,
    //SPECIFIED_FILE,
    DATA_FILE,
    SETTINGS_ICON,
}
