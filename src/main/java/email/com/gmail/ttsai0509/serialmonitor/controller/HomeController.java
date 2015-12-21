package email.com.gmail.ttsai0509.serialmonitor.controller;

import email.com.gmail.ttsai0509.serialmonitor.codec.BinaryCodec;
import email.com.gmail.ttsai0509.serialmonitor.codec.Codec;
import email.com.gmail.ttsai0509.serialmonitor.codec.DefaultCodec;
import email.com.gmail.ttsai0509.serialmonitor.codec.HexCodec;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

@SuppressWarnings("EmptyCatchBlock")
public class HomeController {

    private static final Logger log = LoggerFactory.getLogger(HomeController.class);

    private static final Codec defaultCodec = new DefaultCodec();
    private static final Codec binaryCodec = new BinaryCodec();
    private static final Codec hexCodec = new HexCodec();
    private static final List<Codec> codecs = Collections.unmodifiableList(
            Arrays.asList(defaultCodec, binaryCodec, hexCodec)
    );

    // Serial Incoming Connection
    @FXML public ComboBox<Baud> inBaud;
    @FXML public ComboBox<DataBits> inData;
    @FXML public ComboBox<StopBits> inStop;
    @FXML public ComboBox<FlowControl> inFlow;
    @FXML public ComboBox<Parity> inParity;
    @FXML public ComboBox<Codec> inFormat;
    @FXML public TextField inPort;
    @FXML public Button inConnect;

    // Serial Outgoing Message
    @FXML public ComboBox<Baud> outBaud;
    @FXML public ComboBox<DataBits> outData;
    @FXML public ComboBox<StopBits> outStop;
    @FXML public ComboBox<FlowControl> outFlow;
    @FXML public ComboBox<Parity> outParity;
    @FXML public ComboBox<Codec> outFormat;
    @FXML public TextField outPort;
    @FXML public TextField outMessage;
    @FXML public Button outSend;

    @FXML public TabPane tabPane;

    private Map<Tab, ComListener> connections = new HashMap<>();

    @FXML
    public void initialize() {

        // ComboBox Items
        inFormat.setItems(FXCollections.observableList(codecs));
        inBaud.setItems(FXCollections.observableList(Arrays.asList(Baud.values())));
        inData.setItems(FXCollections.observableList(Arrays.asList(DataBits.values())));
        inStop.setItems(FXCollections.observableList(Arrays.asList(StopBits.values())));
        inFlow.setItems(FXCollections.observableList(Arrays.asList(FlowControl.values())));
        inParity.setItems(FXCollections.observableList(Arrays.asList(Parity.values())));

        outFormat.setItems(FXCollections.observableList(codecs));
        outBaud.setItems(FXCollections.observableList(Arrays.asList(Baud.values())));
        outData.setItems(FXCollections.observableList(Arrays.asList(DataBits.values())));
        outStop.setItems(FXCollections.observableList(Arrays.asList(StopBits.values())));
        outFlow.setItems(FXCollections.observableList(Arrays.asList(FlowControl.values())));
        outParity.setItems(FXCollections.observableList(Arrays.asList(Parity.values())));

        // Default Settings
        inFormat.getSelectionModel().select(defaultCodec);
        inBaud.getSelectionModel().select(Baud.BAUD_9600);
        inData.getSelectionModel().select(DataBits.DATABITS_8);
        inStop.getSelectionModel().select(StopBits.STOPBITS_1);
        inFlow.getSelectionModel().select(FlowControl.FLOWCONTROL_NONE);
        inParity.getSelectionModel().select(Parity.PARITY_NONE);

        outFormat.getSelectionModel().select(defaultCodec);
        outBaud.getSelectionModel().select(Baud.BAUD_9600);
        outData.getSelectionModel().select(DataBits.DATABITS_8);
        outStop.getSelectionModel().select(StopBits.STOPBITS_1);
        outFlow.getSelectionModel().select(FlowControl.FLOWCONTROL_NONE);
        outParity.getSelectionModel().select(Parity.PARITY_NONE);

        // Event Handling
        FXControlUtils.setSelectAllOnFocus(inPort);
        FXControlUtils.setSelectAllOnFocus(outPort);
        FXControlUtils.setSelectAllOnFocus(outMessage);

        inConnect.setOnAction(event -> {
            if (hasValidSettings(inPort, inBaud, inData, inStop, inFlow, inParity, inFormat))
                readInput(
                        buildConfig(inPort, inBaud, inData, inStop, inFlow, inParity),
                        inFormat.getSelectionModel().getSelectedItem()
                );
            else
                new Alert(Alert.AlertType.ERROR, "All parameters must be set.").show();
        });

        outSend.setOnAction(event -> {
            if (hasValidSettings(outPort, outBaud, outData, outStop, outFlow, outParity, outFormat))
                writeOutput(
                        buildConfig(outPort, outBaud, outData, outStop, outFlow, outParity),
                        outMessage.getText(),
                        outFormat.getSelectionModel().getSelectedItem()
                );
            else
                new Alert(Alert.AlertType.ERROR, "All parameters must be set.").show();
        });

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

    private SerialConfig buildConfig(
            TextField port,
            ComboBox<Baud> baud,
            ComboBox<DataBits> data,
            ComboBox<StopBits> stop,
            ComboBox<FlowControl> flow,
            ComboBox<Parity> parity
    ) {
        String cPort = port.getText();
        Baud cBaud = baud.getSelectionModel().getSelectedItem();
        DataBits cData = data.getSelectionModel().getSelectedItem();
        StopBits cStop = stop.getSelectionModel().getSelectedItem();
        FlowControl cFlow = flow.getSelectionModel().getSelectedItem();
        Parity cParity = parity.getSelectionModel().getSelectedItem();
        return new SerialConfig(cPort, cBaud, cData, cParity, cStop, cFlow);
    }

    private boolean hasValidSettings(
            TextField port,
            ComboBox<Baud> baud,
            ComboBox<DataBits> data,
            ComboBox<StopBits> stop,
            ComboBox<FlowControl> flow,
            ComboBox<Parity> parity,
            ComboBox<Codec> format
    ) {
        return baud.getSelectionModel().getSelectedItem() != null
                && data.getSelectionModel().getSelectedItem() != null
                && stop.getSelectionModel().getSelectedItem() != null
                && flow.getSelectionModel().getSelectedItem() != null
                && parity.getSelectionModel().getSelectedItem() != null
                && format.getSelectionModel().getSelectedItem() != null
                && port.getText() != null && !port.getText().isEmpty();
    }

    private void writeOutput(SerialConfig config, String str, Codec codec) {
        SerialPort com = null;
        OutputStream os = null;

        try {
            com = ComUtils.connectSerialPort(config, 2000);
            os = com.getOutputStream();
            os.write(codec.decode(str));
            os.flush();

        } catch (NoSuchPortException | UnsupportedCommOperationException | IOException e) {

            new Alert(Alert.AlertType.ERROR, "Error sending to " + config.getPort());

        } catch (PortInUseException e) {

            new Alert(Alert.AlertType.ERROR, "Port already in use.");

        } finally {
            if (os != null) try { os.close(); } catch (IOException e) {}
            if (com != null) try { com.close(); } catch (Exception e) {}

        }
    }

    private void readInput(SerialConfig config, Codec codec) {
        ComListener com = new ComListener();
        TextArea text = new TextArea();
        Tab tab = new Tab();

        com.setDataCallback(dat -> Platform.runLater(() -> {
            if (codec == binaryCodec)
                text.setText(text.getText() + codec.encode(dat).replaceAll("(.{8})(?!$)", "$1 ") + " ");
            else if (codec == hexCodec)
                text.setText(text.getText() + codec.encode(dat).replaceAll("(.{2})(?!$)", "$1 ") + " ");
            else
                text.setText(text.getText() + codec.encode(dat));

        }));
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
