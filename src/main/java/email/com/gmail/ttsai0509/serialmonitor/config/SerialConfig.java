package email.com.gmail.ttsai0509.serialmonitor.config;

public class SerialConfig {

    /******************************************************************
     *                                                                *
     * Common Configurations
     *                                                                *
     ******************************************************************/

    public static SerialConfig create_9600_8N1(String port) {
        return new SerialConfig(port, Baud.BAUD_9600, DataBits.DATABITS_8,
                Parity.PARITY_NONE, StopBits.STOPBITS_1, FlowControl.FLOWCONTROL_NONE);
    }

    /******************************************************************
     *                                                                *
     * Configuration Parameters
     *                                                                *
     ******************************************************************/

    private final String port;
    private final Baud baud;
    private final DataBits dataBits;
    private final StopBits stopBits;
    private final FlowControl flowControl;
    private final Parity parity;


    public SerialConfig(String port, Baud baud, DataBits dataBits,
                        Parity parity, StopBits stopBits, FlowControl flowControl) {
        this.port = port;
        this.baud = baud;
        this.dataBits = dataBits;
        this.stopBits = stopBits;
        this.flowControl = flowControl;
        this.parity = parity;
    }

    public String getPort() {
        return port;
    }

    public Baud getBaud() {
        return baud;
    }

    public DataBits getDataBits() {
        return dataBits;
    }

    public StopBits getStopBits() {
        return stopBits;
    }

    public FlowControl getFlowControl() {
        return flowControl;
    }

    public Parity getParity() {
        return parity;
    }
}
