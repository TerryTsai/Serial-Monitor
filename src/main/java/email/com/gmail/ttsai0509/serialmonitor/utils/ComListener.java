package email.com.gmail.ttsai0509.serialmonitor.utils;


import email.com.gmail.ttsai0509.serialmonitor.config.SerialConfig;
import gnu.io.SerialPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

@SuppressWarnings({"EmptyCatchBlock", "ResultOfMethodCallIgnored"})
public class ComListener implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(ComListener.class);

    private DataCallback dataCallback;
    private ExitCallback exitCallback;

    private SerialConfig config;
    private int timeout;
    private int sleep;
    private byte[] buffer;

    private final AtomicBoolean active;

    public ComListener() {
        this.active = new AtomicBoolean();
    }

    public void setDataCallback(DataCallback dataCallback) {
        this.dataCallback = dataCallback;
    }

    public void setExitCallback(ExitCallback exitCallback) {
        this.exitCallback = exitCallback;
    }

    public void startAsync(SerialConfig config, int timeout, int bufferSize, int sleep) {
        this.config = config;
        this.timeout = timeout;
        this.buffer = new byte[bufferSize];
        this.sleep = sleep;
        active.set(true);
        new Thread(this).start();
    }

    public void stop() {
        active.set(false);
    }

    @Override
    public void run() {

        log.info("Starting " + config.getPort());

        SerialPort port = null;
        InputStream in = null;

        try {

            port = ComUtils.connectSerialPort(config, timeout);
            in = port.getInputStream();

            while (active.get()) {

                try {

                    int available = in.available();
                    if (available > 0) {
                        in.read(buffer, 0, available);
                        if (dataCallback != null) {
                            dataCallback.dataReceived(Arrays.copyOf(buffer, available));
                        }
                    } else {
                        Thread.sleep(sleep);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        } catch (Exception e) {

            e.printStackTrace();

        } finally {

            if (in != null) try { in.close(); } catch (Exception e) {}
            if (port != null) try { port.close(); } catch (Exception e) {}

        }

        log.info("Stopping " + config.getPort());

        if (exitCallback != null)
            exitCallback.onExit();

    }

    /******************************************************************
     *                                                                *
     * Callbacks
     *                                                                *
     ******************************************************************/

    public static interface DataCallback {
        void dataReceived(byte[] data);
    }

    public static interface ExitCallback {
        void onExit();
    }


}