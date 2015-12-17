package email.com.gmail.ttsai0509.serialmonitor.utils;


import email.com.gmail.ttsai0509.serialmonitor.config.SerialConfig;
import gnu.io.*;
import org.slf4j.Logger;

import java.util.Enumeration;

public final class ComUtils {

    private ComUtils() {}

    public static String parsePortType(int portType) {
        switch (portType) {
            case CommPortIdentifier.PORT_I2C:
                return "I2C";
            case CommPortIdentifier.PORT_PARALLEL:
                return "Parallel";
            case CommPortIdentifier.PORT_RAW:
                return "Raw";
            case CommPortIdentifier.PORT_RS485:
                return "RS485";
            case CommPortIdentifier.PORT_SERIAL:
                return "Serial";
            default:
                return "Unknown";
        }
    }

    @SuppressWarnings("unchecked")
    public static String listPorts() {
        String ports = "";
        Enumeration<CommPortIdentifier> portEnum = CommPortIdentifier.getPortIdentifiers();
        while (portEnum.hasMoreElements()) {
            CommPortIdentifier portIdentifier = portEnum.nextElement();
            ports += portIdentifier.getName() + " - " + parsePortType(portIdentifier.getPortType()) + "\n";
        }
        return ports;
    }

    public static void logIdentifier(Logger log, CommPortIdentifier commPortId) {
        log.info("Name  : " + commPortId.getName());
        log.info("Type  : " + ComUtils.parsePortType(commPortId.getPortType()));
        log.info("Owner : " + commPortId.getCurrentOwner());
    }

    public static SerialPort connectSerialPort(SerialConfig config, int timeOut) throws
            NoSuchPortException,
            PortInUseException,
            UnsupportedCommOperationException {

        CommPortIdentifier commPortId = CommPortIdentifier.getPortIdentifier(config.getPort());

        CommPort commPort = commPortId.open(ComUtils.class.getSimpleName(), timeOut);

        SerialPort serialPort = (SerialPort) commPort;
        serialPort.setSerialPortParams(config.getBaud().val, config.getDataBits().val,
                config.getStopBits().val, config.getParity().val);

        return serialPort;

    }

}
