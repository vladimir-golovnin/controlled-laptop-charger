package ru.vgolovnin.laptop.charger;


import java.util.concurrent.TimeUnit;

class ChargerController implements Runnable {

    private static final long CONTROL_LOOP_INTERVAL_MILLIS = TimeUnit.MINUTES.toMillis(5);

    private final Battery battery;
    private final Charger charger;

    ChargerController(Battery battery, Charger charger) {
        this.battery = battery;
        this.charger = charger;
    }

    public void run() {
        for (; ; ) {
            if (battery.isDischarging() && battery.getChargePercent() < 20) {
                charger.switchOn();
            } else if (battery.isCharging() && battery.getChargePercent() > 80) {
                charger.switchOff();
            }
            try {
                Thread.sleep(CONTROL_LOOP_INTERVAL_MILLIS);
            } catch (InterruptedException ignored) {
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Thread contolThread = new Thread(new ChargerController(new Battery(), Charger.init()));
        contolThread.start();
        contolThread.join();
    }

}
