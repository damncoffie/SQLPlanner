import java.util.TimerTask;

/**
 * Расширяет класс TimerTask. Содержит overridden-метод run(), который запускается при каждом истеченном таймере.
 * @author Created by Cooper on 30.11.2016.
 * @version 2.0
 * */
class MyTimerTask extends TimerTask {

    /**
     * Объект класса ModelEvent.
     * */
    private ModelEvent event;

    /**
     * Контроллер журнала.
     * */
    private ControllerJournal cJournal;

    /**
     * Контроллер хранилища таймеров.
     * */
    private ControllerTimers cTimers;


    /**
     * @param event объект класса ModelEvent - задача
     * @param cJournal контроллер журнала
     * @param cTimers контроллер хранилища таймеров
     * */
    MyTimerTask(ModelEvent event, ControllerJournal cJournal, ControllerTimers cTimers) {

        this.event = event;
        this.cJournal = cJournal;
        this.cTimers = cTimers;
    }

    /**
     * Вызывает пользовательский интерфейс по истечении времени таймера и выводит напоминание в консоль.
     * */
    @Override
    public void run() {

        new ViewNotifyUI(cJournal, cTimers).makeGUI(event);
        System.out.println("It's time!" + "\n");
        System.out.println("Type your command and press Enter below:");
    }
}