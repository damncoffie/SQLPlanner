import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Включает в себя методы для операций с моделью журнала.
 * @author Created by Cooper on 30.11.2016.
 * @version 2.0
 */
class ControllerJournal {

    /**
     * Модель журнала.
     * */
    private Document document;


    /**
     * @param modelJournal объект, хранящий модель журнала
     * */
    ControllerJournal(ModelJournal modelJournal) {

        this.document = modelJournal.getDoc();
    }


    /**
     * @return модель журнала
     * */
    Document getDocument() {
        return document;
    }

    /**
     * Добавляет новую задачу в журнал.
     * */
    void addToJournal(ModelEvent event, Document doc) {
        // новая задача
        Element eventElem = doc.createElement("Event");
        doc.getFirstChild().appendChild(eventElem);

        // заголовок задачи
        Element headline = doc.createElement("headline");
        headline.appendChild(doc.createTextNode(event.getHeadline()));
        eventElem.appendChild(headline);

        // описание задачи
        Element description = doc.createElement("description");
        description.appendChild(doc.createTextNode(event.getDescription()));
        eventElem.appendChild(description);

        // дата задачи
        Element date = doc.createElement("date");
        DateFormat df = new SimpleDateFormat("HH:mm dd/MM/yyyy");
        String stringDate = df.format(event.getDate());
        date.appendChild(doc.createTextNode(stringDate));
        eventElem.appendChild(date);

        // контакты задачи
        Element contacts = doc.createElement("contacts");
        contacts.appendChild(doc.createTextNode(event.getContacts()));
        eventElem.appendChild(contacts);
        updateJournal(doc);
    }

    /**
     * Удаляет задачу из журнала.
     * @param deleteByHeadline строка-заголовок, по которой ищется задача в журнале
     * @param doc модель журнала
     * @throws NullPointerException если файла-журнала не существует
     * */
    Boolean deleteEvent(String deleteByHeadline, Document doc) throws NullPointerException {

        // проверка на наличие задач в журнале
        if (doc.hasChildNodes()) {
            Boolean isExists = checkEvent(deleteByHeadline, doc);

            // проверка на наличие задачи с таким заголовком
            if(isExists) {
                NodeList eventList = doc.getElementsByTagName("Event");

                // поиск задачи по заголовку и её удаление
                for (int x = 0; x < eventList.getLength(); x++) {
                    Node event = eventList.item(x);

                    if (event.getFirstChild().getTextContent().equals(deleteByHeadline)) {
                        event.getParentNode().removeChild(event);
                        deleteEvent(event.getFirstChild().getTextContent(), doc);
                        updateJournal(doc);
                        return true;
                    }
                }
            } else {
                return false;
            }
        } else {
            throw new NullPointerException();
        }
        return false;
    }

    /**
     * Возвращает все задачи из журнала.
     * @return все элементы с тегом "Event"
     * */
   NodeList getEvents() {

       return document.getElementsByTagName("Event");
   }

    /**
     * Обновляет журнал.
     * Перезаписывает XML-файл с учетом существующей модели журнала.
     * @param doc текущая модель журнала
     * */
    void updateJournal(Document doc) {
        try {
            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File("C:/Users/agent_000/Desktop/journal.xml"));
            transformer.transform(source, result);

        } catch (TransformerException tfe) {
            tfe.printStackTrace();
        }
    }


    /**
     * Проверяет существование задачи по заголовку.
     *  @param headlineToCheck строка-заголовок
     *  @param doc текущая модель журнала
     *  @return булево значение (сущ/несущ)
     * */
    private boolean checkEvent(String headlineToCheck, Document doc) {

        Boolean isExists = false;
        NodeList headlineList = doc.getElementsByTagName("headline");

        for (int x = 0; headlineList.item(x) != null; x++) {
            if (headlineList.item(x).getTextContent().equals(headlineToCheck)) {
                isExists = true;
            }
        }
        return isExists;
    }

    /**
     * Загружает из файла, хранящегося на диске, предыдущий журнал (если таковой имеется).
     * Если журнал имеет актуальные задачи (время которых не истекло), запускает их заново.
     * @return новый контроллер с новым журналом; или контроллер со старым, если в нем были актуальные задачи
     * */
    static ControllerJournal checkPreviousEvents() {
        try {
            // парсит старый XML-журнал
            File fXmlFile = new ModelJournal().getFile();
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document oldDoc = dBuilder.parse(fXmlFile);

            // проверяет наличие задач в нём
            if (oldDoc.getFirstChild().hasChildNodes()) {
                // собирает все элементы, содержащие даты
                ControllerJournal oldCJournal = new ControllerJournal(new ModelJournal().updateDoc(oldDoc));
                NodeList previousDates = oldDoc.getElementsByTagName("date");

                for (int x = 0; x < previousDates.getLength(); x++) {
                    Node oldDate = previousDates.item(x);
                    Boolean isActual = ControllerTimers.checkDate(oldDate.getTextContent());

                    // проверяет их на актуальность (истекли или нет)
                    if (isActual) {
                        // если актуально - восстанавливает задачу-объект и запускает таймер
                        ModelEvent event = new ModelEvent();
                        Node oldActualEvent = oldDate.getParentNode();
                        NodeList attrs = oldActualEvent.getChildNodes();

                        event.setHeadline(attrs.item(0).getTextContent());
                        event.setDescription(attrs.item(1).getTextContent());
                        DateFormat df = new SimpleDateFormat("HH:mm dd/MM/yyyy");
                        Date date = df.parse(attrs.item(2).getTextContent());
                        event.setDate(date);
                        event.setContacts(attrs.item(3).getTextContent());
                        ControllerTimers cTimers = new ControllerTimers(oldCJournal, new ModelTimers());
                        cTimers.startCountdown(event);
                    } else {
                        // если нет - удаляет эту задачу
                        Node oldActualEvent = oldDate.getParentNode();
                        NodeList attrs = oldActualEvent.getChildNodes();
                        oldCJournal.deleteEvent(attrs.item(0).getTextContent(), oldDoc);
                    }
                }
                // обновляет журнал и возвращает его
                oldCJournal.updateJournal(oldDoc);
                return oldCJournal;
            }
        } catch (NullPointerException ex) {
            System.out.println("There is no journal.");
            ex.printStackTrace();
        } catch (ParseException ex) {
            System.out.println("Cannot parse.");
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        // если задач нет - возвращает новый журнал
        return new ControllerJournal(new ModelJournal());
    }
}