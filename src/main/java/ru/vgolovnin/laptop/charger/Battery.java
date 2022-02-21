package ru.vgolovnin.laptop.charger;

public class Battery {
    public boolean isCharging() {
        return false;
    }

    public boolean isDischarging() {
        return !isCharging();
    }

    public double getChargePercent() {
        return 0;
    }

}
