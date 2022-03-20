package ru.vgolovnin.laptop.charger;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

class ChargerController implements Runnable {

    private static final long CONTROL_LOOP_INTERVAL_MILLIS = TimeUnit.MINUTES.toMillis(1);
    private static final long CHARGER_REINIT_INTERVAL_MILLIS = TimeUnit.SECONDS.toMillis(30);
    private static final int CHARGE_PERCENT_MAX = 83;
    private static final int CHARGE_PERCENT_MIN = 77;

    private static final Logger log = LoggerFactory.getLogger(ChargerController.class);

    private final Battery battery;

    public static void main(String[] args) {
        new ChargerController(new Battery()).run();
    }

    ChargerController(Battery battery) {
        this.battery = battery;
    }

    public void run() {
        for (; ; ) {
            try (Charger charger = Charger.init()) {
                log.info("Charger is connected");
                controlCharge(charger);
            } catch (ChargerControlException e) {
                log.warn(e.getMessage());
                sleep(CHARGER_REINIT_INTERVAL_MILLIS);
            }
        }
    }

    private void controlCharge(Charger charger) throws ChargerControlException {
        for (; ; ) {
            if (battery.isDischarging() && battery.getChargePercent() <= CHARGE_PERCENT_MIN) {
                charger.switchOn();
            } else if (battery.isCharging() && battery.getChargePercent() >= CHARGE_PERCENT_MAX) {
                charger.switchOff();
            }
            sleep(CONTROL_LOOP_INTERVAL_MILLIS);
        }
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
    }

}
