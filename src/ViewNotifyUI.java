import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Calendar;
import java.util.Date;

/**
 * Включает в себя методы для создания пользовательского интерфейса для взаимодействия
 * с функциями планировщика "отложить" и "остановить" таймер.
 * @author Created by Cooper on 30.11.2016.
 * @version 2.0
 */
class ViewNotifyUI {

    /**
     * Контроллер хранилища таймеров
     * */
    private ControllerTimers cTimers;

    /**
     * Контроллер журнала
     * */
    private ControllerJournal cJournal;

    private ModelEvent event;
    private JFrame choiceFrame;
    private JFrame delayFrame;
    private JSpinner spinner;
    private PlaySound sound;

    /**
     * @param cJournal контроллер журнала
     * @param cTimers контроллер хранилища таймеров
     * */
    ViewNotifyUI(ControllerJournal cJournal, ControllerTimers cTimers) {
        this.cJournal = cJournal;
        this.cTimers = cTimers;
    }


    /**
     * Отображает интерфейс. Включает в себя:
     * 1) окно с кнопками Отложить и Остановить + панели с описанием и контактами задачи
     * 2) окно с настройкой даты переноса задачи и кнопкой её установки
     * 3) запуск/отключение звукового оповещения
     * @param event объект класса ModelEvent
     */
    void makeGUI(ModelEvent event) {
        this.event = event;

        choiceFrame = new JFrame("Planner");
        delayFrame = new JFrame("Planner");

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

        choiceFrame.setLocation(dim.width / 2 - choiceFrame.getSize().width / 2,
                dim.height / 2 - choiceFrame.getSize().height / 2);
        choiceFrame.toFront();

        delayFrame.setLocation(dim.width / 2 - choiceFrame.getSize().width / 2,
                dim.height / 2 - choiceFrame.getSize().height / 2);
        delayFrame.toFront();

        choiceFrame.setSize(500, 180);
        delayFrame.setSize(500, 180);

        JPanel descPanel = new JPanel();
        JPanel contPanel = new JPanel();
        String description = event.getDescription();
        String contacts = event.getContacts();
        JLabel desc = new JLabel(description);
        JLabel cont = new JLabel(contacts);
        desc.setFont(new Font(Font.SERIF, Font.PLAIN, 20));
        cont.setFont(new Font(Font.SERIF, Font.PLAIN, 18));
        descPanel.add(desc);
        contPanel.add(cont);

        JPanel buttonPanel = new JPanel();
        JButton stopButton = new JButton("Stop");
        stopButton.addActionListener(new StopListener());
        JButton delayButton = new JButton("Delay");
        delayButton.addActionListener(new DelayListener());
        buttonPanel.add(stopButton);
        buttonPanel.add(delayButton);

        choiceFrame.getContentPane().add(BorderLayout.SOUTH, buttonPanel);
        choiceFrame.getContentPane().add(BorderLayout.NORTH, descPanel);
        choiceFrame.getContentPane().add(BorderLayout.CENTER, contPanel);

        JPanel delayPanel = new JPanel();
        delayFrame.getContentPane().add(delayPanel);


        // делаем объект Spinner с заданными min и max границами даты
        String maxDateString = "23:59 31/12/2070";
        Date maxDate = null;
        try {
            maxDate = ControllerTimers.convertStringToDate(maxDateString);
        } catch (Exception ex) {
            System.out.println("Cannot parse" + "\n");
        }
        Date now = new Date();
        SpinnerDateModel model = new SpinnerDateModel(now, now, maxDate, Calendar.MINUTE);
        spinner = new JSpinner(model);

        JComponent editor = new JSpinner.DateEditor(spinner, "HH:mm dd/MM/yyyy");
        spinner.setEditor(editor);
        delayPanel.add(spinner);
        JLabel setLabel = new JLabel("Set new date");
        delayPanel.add(setLabel);

        JButton setButton = new JButton("Set");
        setButton.addActionListener(new SetListener());
        delayPanel.add(spinner);
        delayPanel.add(setButton);

        choiceFrame.setAlwaysOnTop(true);
        choiceFrame.addWindowListener(new MyWindowsListener());

        choiceFrame.setVisible(true);
        choiceFrame.toFront();

        sound = new PlaySound("C:/Users/agent_000/Desktop/fc.wav");
        sound.loop();
    }

    /**
     * Класс-слушатель для кнопки Стоп.
     * Останавливает мелодию. Затем вызывает метод для удаления задачи и обновления журнала.
     * */
    private class StopListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                sound.stop();
                PlaySound.sound.close();
            } catch (Exception ex) {
                System.out.println("Can't stop the music!" + "\n");
                ex.printStackTrace();
            }
            Boolean isDeleted = cJournal.deleteEvent(event.getHeadline(), cJournal.getDocument());
            Boolean isStopped = cTimers.stopCountdown(event.getHeadline());
            if (isDeleted & isStopped) {
                System.out.println("Event was deleted successfully!" + "\n");
                System.out.println("Type your command and press Enter below:");
                choiceFrame.setVisible(false);
            } else {
                System.out.println("Cannot delete event" + "\n");
            }
        }
    }

    /**
     * Класс-слушатель для кнопки Установить.
     * Считывает установленную на спиннере дату, затем перезаписывает поле Date data у соответствующего
     * объекта класса ModelEvent, добавляет его в журнал, обновляет его. Затем перезапускает таймер этого
     * объекта с новым временем.
     * */
    private class SetListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            cTimers.setNewDate(spinner, event);
            System.out.println("Date was delayed successfully!" + "\n");
            System.out.println("Type your command and press Enter below:");
            delayFrame.setVisible(false);
        }
    }

    /**
     * Класс-слушатель для кнопки Отложить.
     * Останавливает мелодию. Активизирует окно с настройкой новой даты.
     * */
    private class DelayListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                sound.stop();
                PlaySound.sound.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            choiceFrame.setVisible(false);
            delayFrame.setVisible(true);
        }
    }

    /**
     * Класс-слушатель для Windows-окна.
     * Заоверрайжен только один метод - на закрытие окна - делает то же самое, что и при нажатии кнопки Стоп
     * */
    private class MyWindowsListener implements WindowListener {

        @Override
        public void windowOpened(WindowEvent e) {

        }

        @Override
        public void windowClosing(WindowEvent e) {
            try {
                sound.stop();
                PlaySound.sound.close();
            } catch (Exception ex) {
                System.out.println("Can't stop the music!");
                ex.printStackTrace();
            }
            System.out.println(event.getHeadline());
            System.out.println(cJournal.getDocument());
            Boolean isDeleted = cJournal.deleteEvent(event.getHeadline(), cJournal.getDocument());
            Boolean isStopped = cTimers.stopCountdown(event.getHeadline());
            if (isDeleted & isStopped) {
                System.out.println("Event was deleted successfully!" + "\n");
                System.out.println("Type your command and press Enter below:");
                choiceFrame.setVisible(false);
            } else {
                System.out.println("Cannot delete event" + "\n");
            }
        }

        @Override
        public void windowClosed(WindowEvent e) {

        }

        @Override
        public void windowIconified(WindowEvent e) {

        }

        @Override
        public void windowDeiconified(WindowEvent e) {

        }

        @Override
        public void windowActivated(WindowEvent e) {

        }

        @Override
        public void windowDeactivated(WindowEvent e) {

        }
    }
}