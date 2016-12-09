import javax.swing.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;
import java.util.Timer;

/**
 * Включает в себя методы для операций с таймерами.
 * @author Created by Cooper on 30.11.2016.
 * @version 1.0
 */
class ControllerTimers {

    /**
     * Контроллер журнала
     * */
    private ControllerJournal cJournal;

    /**
     * Модель хранилища таймеров
     * */
    private ModelTimers mTimers;


    /**
     * @param cJournal контроллер журнала
     * @param mTimers модель хранилища таймеров
     * */
    ControllerTimers(ControllerJournal cJournal, ModelTimers mTimers) {

        this.cJournal = cJournal;
        this.mTimers = mTimers;
    }

    /**
     * Возвращает коллекцию таймеров
     * @return HashMap коллекция
     *
     * */
    HashMap<String, Timer> getTimers() {
        return mTimers.getTimers();
    }

    /**
     * Конвертирует входящую строку из класса String в Date
     * @param userDate строка для конвертации
     * @throws ParseException если строка не соответсвует заданному формату (HH:mm dd/MM/yyyy)
     * @return переменная класса Date
     */
    static Date convertStringToDate(String userDate) throws ParseException {
        Date date;
        DateFormat df = new SimpleDateFormat("HH:mm dd/MM/yyyy");
        date = df.parse(userDate);

        return date;
    }

    /**
     * Создает и запускает новый таймер.
     * Добавляет таймер в HashMap-хранилище таймеров по ключу-headline поля объекта ModelEvent.
     * @param event объект класса ModelEvent
     * */
    void startCountdown(ModelEvent event) {

        Timer timer = new Timer();
        timer.schedule(new MyTimerTask(event, cJournal, this), event.getDate());
        mTimers.getTimers().put(event.getHeadline(), timer);
    }

    /**
     * Останавливает выбранный таймер.
     * Находит по ключу (входящая строка-headline) таймер соответствующей задачи и останавливает его. Затем удаляет
     * из HashMap-хранилищ таймеров и задач. Обновляет журнал.
     * @param stopByHeadline строка-ключ, по которой ищется нужный таймер
     * @return булево значение (остановлено/не остановлено)
     * */
    Boolean stopCountdown(String stopByHeadline) {

        Boolean isExists = checkTimer(stopByHeadline);

        if(isExists) {
            Timer timerToStop = mTimers.getTimers().get(stopByHeadline);
            timerToStop.cancel();
            mTimers.getTimers().remove(stopByHeadline);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Проверяет дату на валидность.
     * Сначала конвертирует строку в дату. Затем сравнивает её с текущей датой и максимальной.
     * (т.е. минимум - текущая дата, максимум - 23:59 31/12/2070)
     * @param stringDate входящая строка-дата
     * @return булево значение (соответсвует границам/ не соответсвует)
     * */
    static Boolean checkDate(String stringDate) throws ParseException {

        Date now = new Date();
        DateFormat df = new SimpleDateFormat("HH:mm dd/MM/yyyy");
        String maxDateString = "23:59 31/12/2070";
        Date maxDate;

        maxDate = convertStringToDate(maxDateString);
        Date userDate = df.parse(stringDate);

        return !(userDate.compareTo(now) < 0 || userDate.compareTo(maxDate) > 0);
    }

    /**
     * Проверяет, существует ли уже таймер задачи с таким заголовком.
     * @param hToDelete заголовок задачи
     * @return булево значение (существует/не существует)
     * */
    private Boolean checkTimer (String hToDelete) {

        Set<String> headlines = mTimers.getTimers().keySet();

        for (String headline : headlines) {
            if (headline.equals(hToDelete)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Переносит дату для задачи.
     * @param spinner UI-элемент, с которого берется новое значение даты
     * @param event задача
     * */
    void setNewDate(JSpinner spinner, ModelEvent event) {

        Date newDate = (Date) spinner.getValue();
        cJournal.deleteEvent(event.getHeadline(), cJournal.getDocument());
        event.setDate(newDate);
        cJournal.addToJournal(event, cJournal.getDocument());
        cJournal.updateJournal(cJournal.getDocument());

        Timer timer = mTimers.getTimers().get(event.getHeadline());
        System.out.println();
        timer.schedule(new MyTimerTask(event, cJournal, this), newDate);
    }
}