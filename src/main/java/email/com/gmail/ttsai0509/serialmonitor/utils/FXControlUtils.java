package email.com.gmail.ttsai0509.serialmonitor.utils;

import javafx.application.Platform;
import javafx.scene.control.TextField;

public final class FXControlUtils {

    private FXControlUtils() {}

    public static void setSelectAllOnFocus(TextField tf) {
        tf.focusedProperty().addListener((obs, n, o) -> {
            Platform.runLater(() -> {
                if (tf.isFocused() & !tf.getText().isEmpty())
                    tf.selectAll();
            });
        });
    }

}
