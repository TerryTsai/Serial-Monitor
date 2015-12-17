package email.com.gmail.ttsai0509.serialmonitor.controller;

import email.com.gmail.ttsai0509.serialmonitor.config.*;
import email.com.gmail.ttsai0509.serialmonitor.utils.ComListener;
import email.com.gmail.ttsai0509.serialmonitor.utils.ComUtils;
import email.com.gmail.ttsai0509.serialmonitor.utils.FXControlUtils;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("EmptyCatchBlock")
public class HomeController {

    private static final Logger log = LoggerFactory.getLogger(HomeController.class);

    @FXML public SplitPane root;

    @FXML public VBox leftMenu;
    @FXML public TextField port;
    @FXML public TextField msg;
    @FXML public Button send;
    @FXML public Button connect;
    @FXML public ComboBox<Baud> baud;
    @FXML public ComboBox<DataBits> data;
    @FXML public ComboBox<StopBits> stop;
    @FXML public ComboBox<FlowControl> flow;
    @FXML public ComboBox<Parity> parity;

    @FXML public TabPane tabPane;

    private Map<Tab, ComListener> connections = new HashMap<>();

    @FXML
    public void initialize() {
        FXControlUtils.setSelectAllOnFocus(port);
        FXControlUtils.setSelectAllOnFocus(msg);

        baud.setItems(FXCollections.observableList(Arrays.asList(Baud.values())));
        data.setItems(FXCollections.observableList(Arrays.asList(DataBits.values())));
        stop.setItems(FXCollections.observableList(Arrays.asList(StopBits.values())));
        flow.setItems(FXCollections.observableList(Arrays.asList(FlowControl.values())));
        parity.setItems(FXCollections.observableList(Arrays.asList(Parity.values())));

        connect.setOnAction(event -> {
            if (validArguments())
                readInput(buildConfig());
            else
                new Alert(Alert.AlertType.ERROR, "All parameters must be set.").show();
        });

        send.setOnAction(event -> {
            if (validArguments())
                writeOutput(buildConfig(), msg.getText());
            else
                new Alert(Alert.AlertType.ERROR, "All parameters must be set.").show();
        });

        // Default Settings
        baud.getSelectionModel().select(Baud.BAUD_9600);
        data.getSelectionModel().select(DataBits.DATABITS_8);
        stop.getSelectionModel().select(StopBits.STOPBITS_1);
        flow.getSelectionModel().select(FlowControl.FLOWCONTROL_NONE);
        parity.getSelectionModel().select(Parity.PARITY_NONE);

    }

    public void dispose() {
        for (Map.Entry<Tab, ComListener> entry : connections.entrySet())
            entry.getValue().stop();
        connections.clear();
    }

    /******************************************************************
     *                                                                *
     * Helper Methods
     *                                                                *
     ******************************************************************/

    private SerialConfig buildConfig() {
        String cPort = port.getText();
        Baud cBaud = baud.getSelectionModel().getSelectedItem();
        DataBits cData = data.getSelectionModel().getSelectedItem();
        StopBits cStop = stop.getSelectionModel().getSelectedItem();
        FlowControl cFlow = flow.getSelectionModel().getSelectedItem();
        Parity cParity = parity.getSelectionModel().getSelectedItem();
        return new SerialConfig(cPort, cBaud, cData, cParity, cStop, cFlow);
    }

    private boolean validArguments() {
        return port.getText() != null
                && !port.getText().isEmpty()
                && baud.getSelectionModel().getSelectedItem() != null
                && data.getSelectionModel().getSelectedItem() != null
                && stop.getSelectionModel().getSelectedItem() != null
                && flow.getSelectionModel().getSelectedItem() != null
                && parity.getSelectionModel().getSelectedItem() != null;
    }

    private void writeOutput(SerialConfig config, String message) {
        SerialPort com = null;
        OutputStream os = null;
        OutputStreamWriter osw = null;

        try {
            com = ComUtils.connectSerialPort(config, 2000);
            os = com.getOutputStream();
            osw = new OutputStreamWriter(os);
            osw.write(message);
            osw.flush();

        } catch (NoSuchPortException | UnsupportedCommOperationException | IOException e) {

            new Alert(Alert.AlertType.ERROR, "Error sending to " + config.getPort());

        } catch (PortInUseException e) {

            new Alert(Alert.AlertType.ERROR, "Port already in use.");

        } finally {
            if (osw != null) try { osw.close(); } catch (IOException e) {}
            if (os != null) try { os.close(); } catch (IOException e) {}
            if (com != null) try { com.close(); } catch (Exception e) {}

        }
    }

    private void readInput(SerialConfig config) {
        ComListener com = new ComListener();
        TextArea text = new TextArea();
        Tab tab = new Tab();

        com.setDataCallback(data1 -> Platform.runLater(() -> text.setText(text.getText() + data1)));
        com.setExitCallback(() -> Platform.runLater(() -> tab.getStyleClass().add("tab-exited")));

        text.setEditable(false);
        text.setWrapText(true);
        text.textProperty().addListener((obs, o, n) -> {
            if (!tab.isSelected())
                tab.getStyleClass().add("tab-changed");
        });

        tab.setText(config.getPort());
        tab.setContent(text);
        tab.setClosable(true);
        tab.setOnClosed(e -> {
            com.stop();
            connections.remove(tab);
        });
        tab.selectedProperty().addListener((obs, o, n) -> {
            tab.getStyleClass().remove("tab-changed");
        });

        connections.put(tab, com);
        tabPane.getTabs().add(tab);
        com.startAsync(config, 2000, 2048, 1000);
    }

}
