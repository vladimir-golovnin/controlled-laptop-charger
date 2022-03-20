package ru.vgolovnin.laptop.charger;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

class Charger implements AutoCloseable {

    private static final String PORT_NAME = "/dev/ttyUSB0";
    private static final int PORT_BAUDRATE = 9600;
    private static final String PORT_OWNER_NAME = "laptop-charger-controller";
    private static final String SWITCH_ON_CONTROL_SEQUENCE = ">1\n";
    private static final String SWITCH_OFF_CONTROL_SEQUENCE = ">0\n";

    private final SerialPort port;
    private final Writer portWriter;

    public static Charger init() throws ChargerControlException {
        return new Charger(configurePort(getPortIdentifier()));
    }

    private Charger(SerialPort port) {
        this.port = port;
        this.portWriter = obtainPortWriter(port);
    }

    private static CommPortIdentifier getPortIdentifier() throws ChargerControlException {
        CommPortIdentifier portIdentifier;
        File portFile = new File(PORT_NAME);
        if (!portFile.exists()) throw new ChargerControlException(String.format("Port %s was not found", PORT_NAME));
        try {
            portIdentifier = CommPortIdentifier.getPortIdentifier(PORT_NAME);
        } catch (NoSuchPortException e) {
            throw new ChargerControlException(String.format("Port %s was not found", PORT_NAME));
        }
        if (portIdentifier.isCurrentlyOwned()) throw new ChargerControlException(String.format("Port %s is currently in use", PORT_NAME));
        return portIdentifier;
    }

    private static SerialPort configurePort(CommPortIdentifier portIdentifier) throws ChargerControlException {
        try {
            CommPort commPort = portIdentifier.open(PORT_OWNER_NAME, 2000);
            if (!(commPort instanceof SerialPort)) throw new IllegalStateException(String.format("Port %s is not serial port", PORT_NAME));
            SerialPort port = (SerialPort) commPort;
            port.setSerialPortParams(PORT_BAUDRATE, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
            return port;
        } catch (PortInUseException e) {
            throw new ChargerControlException("Cannot open port " + PORT_NAME);
        } catch (UnsupportedCommOperationException e) {
            throw new IllegalStateException(e);
        }
    }

    private Writer obtainPortWriter(SerialPort port) {
        try {
            return new OutputStreamWriter(port.getOutputStream());
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public void close() {
        try {
            portWriter.close();
        } catch (IOException ignored) {
        }
        port.close();
    }

    public void switchOn() throws ChargerControlException {
        writeToPort(SWITCH_ON_CONTROL_SEQUENCE);
    }

    public void switchOff() throws ChargerControlException {
        writeToPort(SWITCH_OFF_CONTROL_SEQUENCE);
    }

    private void writeToPort(String controlSequence) throws ChargerControlException {
        try {
            portWriter.append(controlSequence);
            portWriter.flush();
        } catch (IOException e) {
            throw new ChargerControlException("Not able to write to port " + PORT_NAME);
        }
    }

}
