package ru.vgolovnin.laptop.charger;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

class Charger {

    private static final String PORT_NAME = "/dev/ttyUSB0";
    private static final String PORT_OWNER_NAME = "laptop-charger-controller";

    private final Writer portWriter;

    public static Charger init() throws Exception {
        CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(PORT_NAME);
        if (portIdentifier.isCurrentlyOwned()) {
            throw new IllegalStateException(String.format("Port %s is currently in use", PORT_NAME));
        } else {
            CommPort commPort = portIdentifier.open(PORT_OWNER_NAME, 2000);
            if (!(commPort instanceof SerialPort))
                throw new IllegalStateException(String.format("Port %s is not serial port", PORT_NAME));
            else {
                SerialPort serialPort = (SerialPort) commPort;
                serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

                Writer portWriter = new OutputStreamWriter(serialPort.getOutputStream());
                return new Charger(portWriter);
            }
        }
    }

    private Charger(Writer portWriter) {
        this.portWriter = portWriter;
    }

    public void switchOn() {
        try {
            portWriter.append(">1\n");
            portWriter.flush();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public void switchOff() {
        try {
            portWriter.append(">0\n");
            portWriter.flush();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

}
