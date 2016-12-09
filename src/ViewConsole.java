import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Timer;

/**
 * Содержит класс main и методы для проверки данных из предыдущей сессии и запуска новой.
 * @author Created by Cooper on 30.11.2016.
 * @version 1.0
 */
public class ViewConsole {

    /**
     * Контроллер журнала.
     * */
    private ControllerJournal controllerJournal;

    /**
     * Контроллер таймеров.
     * */
    private ControllerTimers controllerTimers;

    /**
     * Контроллер задач.
     * */
    private ControllerEvent controllerEvent;

    /**
     * Вызывает метод для проверки предыдущих сессий. Затем вызывает метод для работы с пользователем.
     * */
    public static void main(String[] args) {

        ViewConsole planner = new ViewConsole();

        try {
            planner.check();
            planner.makeChoice();
        } catch (Exception ex) {ex.printStackTrace();}

        System.exit(0);
    }

    /**
     * Выводит в консоль приветствие, список команд, доступных пользователю, с пояснением.
     * Затем, в зависимости от ввода пользователя, вызывается соответсвующий метод.
     * */
    private void makeChoice() {

        System.out.println("Hello! This is your Planner!");
        System.out.println("You can use the following commands:");
        System.out.println("new - to create and add new event");
        System.out.println("show - to look at all your actual events");
        System.out.println("delete - to delete your event");
        System.out.println("help - to see available commands");
        System.out.println("stop - to finish work with Planner" + "\n");

        while (true) {

            System.out.println("Type your command and press Enter below:");
            String input = getUserInput();

            if (input.equals("new")) {
                addEvent();
            }

            if (input.equals("show")) {
                showEvents();
            }

            if (input.equals("delete")) {
                deleteEvent();
            }

            if (input.equals("help")) {
                System.out.println("new - to create and add new event");
                System.out.println("show - to look at all your actual events");
                System.out.println("delete - to delete your event");
                System.out.println("help - to see available commands");
                System.out.println("stop - to finish work with ViewConsole" + "\n");
            }

            if (input.equals("stop")) {
                stopPlanner();
                break;
            }
        }
    }

    /**
     * Вызывает методы для добавления задачи.
     * */
    private void addEvent() {
        ModelEvent event = new ModelEvent();

        System.out.println("Please, inter headline:");
        String headLine = getUserInput();
        try {
            Boolean isHeadlineOK = controllerEvent.addHeadline(headLine, event, controllerJournal.getDocument());

            if (isHeadlineOK) {
                System.out.println("Please, inter description");
                String description = getUserInput();
                controllerEvent.addDescription(description, event);

                System.out.println("Please, insert date:");
                System.out.println("(your date must be of following format: HH:MM DD:MM:YYYY)");
                System.out.println("(where HH - hours, MM - minutes, DD - day, MM - month, YYYY - year)");
                System.out.println("(your date must be less than 23:59 31/12/2070 and not less than current date)");
                String date = getUserInput();
                try {
                    Boolean isDateOK = controllerEvent.addDate(date, event);

                    if (isDateOK) {
                        System.out.println("Please, add contacts:");
                        String contacts = getUserInput();
                        controllerEvent.addContacts(contacts, event);

                        controllerJournal.addToJournal(event, controllerJournal.getDocument());
                        controllerTimers.startCountdown(event);
                        System.out.println("Your event was successfully added!" + "\n");
                    } else {
                        System.out.println("Wrong date format. Please, check." + "\n");
                    }
                } catch (ParseException ex) {
                    System.out.println("Wrong date format. Please, check." + "\n");
                }

            } else {
                System.out.println("You already have such event. Please, check." + "\n");
            }

        } catch (NullPointerException ex) {
            ex.printStackTrace();
            System.out.println("There is no events yet." + "\n");
        }
    }

    /**
     * Отображает все текущие задачи.
     * */
    private void showEvents() {

        if (controllerJournal.getDocument().getFirstChild().hasChildNodes()) {

            NodeList events = controllerJournal.getEvents();
            ArrayList<Node> eventsList = new ArrayList<>();

            for (int x = 0; x < events.getLength(); x++) {
                eventsList.add(events.item(x));
            }

            for (Node event : eventsList) {
                StringBuilder strEvent = new StringBuilder();
                ArrayList<Node> attrList = new ArrayList<>();
                NodeList attrs = event.getChildNodes();

                for (int y = 0; y < attrs.getLength(); y++) {
                    attrList.add(attrs.item(y));
                }

                for (Node at : attrList) {
                    strEvent.append(at.getTextContent()).append(" |");
                }
                System.out.println(strEvent + "\n");
            }
        } else {
            System.out.println("There are no events yet." + "\n");
        }
    }

    /**
     * Вызывает методы для удаления задачи.
     * */
    private void deleteEvent() {

        System.out.println("Please, insert headline of event to delete:");
        String headlineToDelete = getUserInput();
        try {
            Boolean isDeleted = controllerJournal.deleteEvent(headlineToDelete, controllerJournal.getDocument());
            Boolean isStopped = controllerTimers.stopCountdown(headlineToDelete);
            if (isDeleted & isStopped) {
                System.out.println("Your event was deleted successfully." + "\n");
            } else {
                System.out.println("There is no such headline." + "\n");
            }
        } catch (NullPointerException ex) {
            System.out.println("There is no events yet." + "\n");
        }
    }

    /**
     * Вызывает методы для остановки всех текущих таймеров и сохранения журнала.
     * */
    private void stopPlanner() {

        Collection<Timer> currentTimers = controllerTimers.getTimers().values();
        for (Timer timer: currentTimers) {
            timer.cancel();
        }

        controllerJournal.updateJournal(controllerJournal.getDocument());
    }

    /**
     * Принимает ввод с консоли от пользователя.
     * @return ввод в виде строки
     * */
    private static String getUserInput() {

        String userInput = null;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            userInput = reader.readLine();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return userInput;
    }

    /**
     * инициализирует контроллеры.
     * Вызывает методы для проверки предыдущих сессий работы с журналом.
     * */
    private void check() {
        controllerJournal = ControllerJournal.checkPreviousEvents();
        controllerTimers = new ControllerTimers(controllerJournal, new ModelTimers());
        controllerEvent = new ControllerEvent();
    }
}