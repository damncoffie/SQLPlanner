import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.text.ParseException;

/**
 * Содержит методы для изменения состояния задачи.
 * @author Created by Cooper on 30.11.2016.
 * @version 1.0
 */
class ControllerEvent {

    /**
     * Проверяет заголовок на валидность и добавляет его задаче.
     * @param headline заголовок
     * @param event задача
     * @param journal модель журнала
     * @return булево значение (добавил/не добавил)
     * */
    Boolean addHeadline(String headline, ModelEvent event, Document journal) {

        Boolean isExists = checkEvent(headline, journal);

        if (isExists) {
            return false;
        } else {
            event.setHeadline(headline);
            return true;
        }
    }

    /**
     * Добавляет описание задаче.
     * @param description описание
     * @param event задача
     * */
    void addDescription(String description, ModelEvent event) {

        event.setDescription(description);
    }


    /**
     * Проверяет дату на валидность и добавляет её задаче.
     * @param date дата
     * @param event задача
     * @return булево значение (добавил/не добавил)
     * */
    boolean addDate(String date, ModelEvent event) throws ParseException {

        Boolean isValid = ControllerTimers.checkDate(date);

        if (isValid) {
            event.setDate(ControllerTimers.convertStringToDate(date));
            return true;
        } else {
            return false;
        }
    }

    /**
     * Добавляет контакты задаче.
     * @param contacts контакты
     * @param event задача
     * */
    void addContacts(String contacts, ModelEvent event) {
        event.setContacts(contacts);
    }

    /**
     * Проверяет заголовок задачи на валидность.
     * @param hToDelete входящая строка от пользователя
     * @param journal текущая модель журнала
     * @return булево значение (уже есть/совпадения нет)
     * */
    private Boolean checkEvent(String hToDelete, Document journal) {

        if (journal.hasChildNodes()) {
            NodeList headlines = journal.getElementsByTagName("headline");

            for (int x = 0; x < headlines.getLength(); x++) {
                if(headlines.item(x).getTextContent().equals(hToDelete)) {
                    return true;
                }
            }
        }

        return false;
    }
}