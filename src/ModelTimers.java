import java.util.HashMap;
import java.util.Timer;

/**
 * Хранит в себе модель хранилища таймеров.
 * @author Created by Cooper on 01.12.2016.
 * @version 1.0
 */
class ModelTimers {
    /**
     * Коллекция, хранящая в себе все новосозданные таймеры.
     * */
    private HashMap<String, Timer> timers = new HashMap<>();

    /**
     * Возвращает коллекцию, хранящую таймеры
     * @return коллекция, хранящая таймеры
     * */
    HashMap<String, Timer> getTimers() {
        return timers;
    }
}