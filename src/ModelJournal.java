import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;

/**
 * Хранит в себе модель журнала планировщика.
 * @author Created by Cooper on 01.12.2016.
 * @version 1.0
 */
class ModelJournal {

    /**
     * Путь к файлу журнала на диске.
     * */
    private File file = new File("C:/Users/Agent_000/Desktop/journal.xml");

    /**
     * Модель будущего xml-журнала.
     * */
    private Document doc;

    /**
     * Конструктор создает новыую модель и добавлет в неё корневой элемент "Journal".
     * */
    ModelJournal() {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // корневой элемент журнала
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("Journal");
            doc.appendChild(rootElement);
            this.doc = doc;
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        }
    }

    /**
     * Возвращет путь к файлу
     * @return File - путь к файлу на диске
     * */
    File getFile() {
        return file;
    }

    /**
     * Возвращает модель журнала
     * @return Document - модель журнала*/
    Document getDoc() {
        return doc;
    }

    /** Заменяет старую версию модели на новую.
     * @param doc Document - новая модель журнала
     * @return собственный объект с обновленной моделью
     * */
    ModelJournal updateDoc(Document doc) {
        this.doc = doc;
        return this;
    }
}


