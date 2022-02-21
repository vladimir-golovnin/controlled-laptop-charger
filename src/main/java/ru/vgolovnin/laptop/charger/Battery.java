package ru.vgolovnin.laptop.charger;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class Battery {

    private static final File CAPACITY_INFO_FILE = new File("/sys/class/power_supply/BAT1/capacity");
    private static final File STATUS_FILE = new File("/sys/class/power_supply/BAT1/status");

    public Status getStatus() {
        try (BufferedReader reader = new BufferedReader(new FileReader(STATUS_FILE, StandardCharsets.UTF_8))) {
            String statusString = reader.readLine();
            return parseStatus(statusString);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Status parseStatus(String statusString) {
        switch (statusString) {
            case "Charging":
                return Status.CHARGING;
            case "Discharging":
                return Status.DISCHARGING;
            default:
                throw new IllegalArgumentException(String.format("Unrecognized status string: %s", statusString));
        }
    }

    public boolean isCharging() {
        return getStatus() == Status.CHARGING;
    }

    public boolean isDischarging() {
        return !isCharging();
    }

    public double getChargePercent() {
        try (BufferedReader reader = new BufferedReader(new FileReader(CAPACITY_INFO_FILE, StandardCharsets.UTF_8))) {
            String capacityString = reader.readLine();
            return Double.parseDouble(capacityString);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public enum Status {
        CHARGING, DISCHARGING
    }

}
