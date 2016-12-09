import java.io.Serializable;
import java.util.Date;

/**
 * Описывает параметры задачи.
 * @author Created by Cooper on 01.12.2016.
 * @version 1.0
 */
class ModelEvent implements Serializable {

    /**
     * Заголовок задачи
     * */
    private String headline;

    /**
     * Описание задачи
     * */
    private String description;

    /**
     * Дата напоминания о задаче
     * */
    private Date date;

    /**
     * Контакты, связанные с задачей
     * */
    private String contacts;

    /**
     * Устанавливает заголовок
     * */
    void setHeadline(String headline) {
        this.headline = headline;
    }

    /**
     * Устанавливает описание
     * */
    void setDescription(String description) {
        this.description = description;
    }

    /**
     * Устанавливает дату
     * */
    void setDate(Date date) {
        this.date = date;
    }

    /**
     * Устанавливает контакты
     * */
    void setContacts(String contacts) {
        this.contacts = contacts;
    }

    /**
     * Возвращает заголовок
     * */
    String getHeadline() {
        return headline;
    }

    /**
     * Возвращает описание
     * */
    String getDescription() {
        return description;
    }

    /**
     * Возвращает дату
     * */
    Date getDate() {
        return date;
    }

    /**
     * Возвращает контакты
     * */
    String getContacts() {
        return contacts;
    }

    /**
     * Возвращает строковое представление объекта в виде:
     * заголовок + описание + дата + контакты
     * */
    @Override
    public String toString() {
        return headline + " /" + description + " /" + date + " /" + contacts;
    }
}
