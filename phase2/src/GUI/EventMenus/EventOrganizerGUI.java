package GUI.EventMenus;

import Controller.EventManagementSystem;
import GUI.Main.PanelStack;
import GUI.PanelBuilder.EventPanelBuilder;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class EventOrganizerGUI {
    private PanelStack panelStack;
    private EventManagementSystem eventSystem;
    private SortGUI eventSortGUI;

    private EventPanelBuilder panelBuilder = new EventPanelBuilder();

    private DefaultListModel<String> listModel = new DefaultListModel<>();


    private JPanel eventPanel = new JPanel();
    private JPanel jListPanel = new JPanel();
    private JPanel buttonPanel = new JPanel();
    private JPanel northButtonPanel = new JPanel();

    private JLabel eventsJLabel = new JLabel("EVENTS MENU");

    private JList eventsJList = new JList(listModel); //TODO pass in list of events of speakers in parameter of JList

    private JScrollPane eventsJScrollPane = new JScrollPane();
    private JButton backButton = new JButton("Back");
    private JButton broadcastButton = new JButton("✉");
    private JButton editButton = new JButton("Edit");
    private JButton deleteButton = new JButton("Delete");
    private JButton sortButton = new JButton("Sort");
    private JButton addEventButton = new JButton("Add Event");


    /**
     * Creates an EventOrganizerGUI and initializes its EventManagementSystem and the panelStack
     *
     * @param eventSystem the EventManagementSystem that the EventOrganizerGUI communicates with
     * @param panelStack The current instance of PanelStack instantiated in MainGUI
     */
    public EventOrganizerGUI(EventManagementSystem eventSystem, PanelStack panelStack) {
        this.eventSystem = eventSystem;
        this.panelStack = panelStack;
        eventSortGUI = new SortGUI(eventSystem, listModel, panelStack);
        broadcastButtonListen();
        backButtonListen();
        sortButtonListener();
        editButtonListen();
        addEventButtonListener();
    }

    /**
     * Builds and loads the Event Page for Organizers
     *
     * @return The Event panel for Organizers
     */
    public JPanel startEventPage() {
        listModel.clear();
        panelBuilder.buildBorderLayoutPanel(eventPanel, 20, 20, 40, 20);
        panelBuilder.buildComponentBorderLayout(eventsJLabel, eventPanel, BorderLayout.NORTH, 48);
        buildListModel();
        eventsJList = new JList(listModel);
        panelBuilder.buildJScrollPanePanel(jListPanel, eventsJList, eventsJScrollPane);
        eventPanel.add(jListPanel, BorderLayout.CENTER);
        buttonPanel.setLayout(new BorderLayout());
        eventPanel.add(buttonPanel, BorderLayout.SOUTH);
        northButtonPanel.setLayout(new FlowLayout());
        buttonPanel.add(northButtonPanel, BorderLayout.NORTH);
        panelBuilder.buildComponent(broadcastButton, northButtonPanel, 14);
        panelBuilder.buildComponentBorderLayout(backButton, buttonPanel, BorderLayout.WEST, 14);
        panelBuilder.buildComponent(editButton, northButtonPanel,14);
        panelBuilder.buildComponent(deleteButton, northButtonPanel,14);
        panelBuilder.buildComponent(sortButton, northButtonPanel,14);
        panelBuilder.buildComponentBorderLayout(addEventButton, buttonPanel, BorderLayout.EAST, 14);
        sortButton.setEnabled(true);
        deleteButtonListen();
        listListener();
        return eventPanel;
    }

    private void buildListModel(){
        eventSystem.makeListsEvents();
        List<String> eventList = eventSystem.getEventLists().get(2);
        if (!eventList.isEmpty()){
            for (String event:eventList) {
                listModel.addElement(event);
            }
        }
        eventsJList.setModel(listModel);
    }

    private void backButtonListen(){
        backButton.addActionListener(e -> {
            JPanel panel = (JPanel) panelStack.pop();
            panel.removeAll();
            panelStack.loadPanel((JPanel) panelStack.pop());
        });
    }

    private void editButtonListen(){
        editButton.addActionListener(e -> {
            String message = JOptionPane.showInputDialog("Enter the new capacity of the event.");
            if (message.matches("\\d+")){
                String event = eventsJList.getSelectedValue().toString();
                if(eventSystem.editEvent(Integer.parseInt(event.substring(0, 1)), Integer.parseInt(message))){
                    JOptionPane.showMessageDialog(eventPanel, "Event capacity changed.");
                }
                else{
                    JOptionPane.showMessageDialog(eventPanel, "Please enter a number greater than the " +
                            "current capacity.");
                }
            }
            else{
                JOptionPane.showMessageDialog(eventPanel, "Please enter a number.");
            }
        });

    }

    private void addEventButtonListener(){
        addEventButton.addActionListener(e -> panelStack.loadPanel(new AddEventGUI(eventSystem, panelStack).addEventPage()));
    }

    private void deleteButtonListen(){
        deleteButton.addActionListener(e -> {
            if (eventsJList.getSelectedIndex() != -1) {
                String event = eventsJList.getSelectedValue().toString();
                if (eventSystem.deleteEvent(Integer.parseInt(event.substring(0, 1)))) {
                    listModel.removeElementAt(eventsJList.getSelectedIndex());
                    eventsJList.setModel(listModel);
                    JOptionPane.showMessageDialog(eventPanel, "You have successfully cancelled this event.");
                }
            }
        });
    }

    private void broadcastButtonListen(){
        broadcastButton.addActionListener(e -> {
            if(!eventsJList.isSelectionEmpty()){
                String message = JOptionPane.showInputDialog("Enter the content of your message: ");
                if (message != null) {
                    String event = eventsJList.getSelectedValue().toString();
                    eventSystem.broadcast(Integer.parseInt(event.substring(0, 1)), message);
                }
            }
        });
    }

    private void sortButtonListener(){
        sortButton.addActionListener(e -> panelStack.loadPanel(eventSortGUI.sortPage()));
    }

    private void listListener(){
        eventsJList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()){
                broadcastButton.setEnabled(true);
                editButton.setEnabled(true);
                deleteButton.setEnabled(true);
            }
        });
    }

}
