package app.assidua.assidua_android.util;

public interface BackPressable {
    /**
     * Handle a back press action in lieu of it being handled normally by the activity
     * @return true if the calling class should handle the back press normally, or false
     * if the implementing class had already "handled" the press.
     */
    boolean onBackPressed();
}
