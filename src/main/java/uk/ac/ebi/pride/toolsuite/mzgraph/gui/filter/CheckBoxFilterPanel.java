package uk.ac.ebi.pride.toolsuite.mzgraph.gui.filter;


import uk.ac.ebi.pride.toolsuite.gui.action.ActionListenable;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.event.EventListenerList;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 28-Jun-2010
 * Time: 15:15:06
 */
public class CheckBoxFilterPanel extends JPanel implements ActionListener, ActionListenable {
    
    /** the setting for the display of checkboxes */
    public final static int DEFAULT_NUMBER_OF_ROWS = 0;
    public final static int DEFAULT_NUMBER_OF_COLUMNS = 2;
    /** used as both action commands and display name */
    public final static String HIDE_ALL_ACTION = "Hide All";
    public final static String SHOW_ALL_ACTION = "Show All";
    /** keep track of all the action listeners */
    private EventListenerList listeners;
    /** the name of this panel, normally this should be the category name */
    private String name;
    /** the types, Map<Type Name, Initial Visibility> */
    private Map<Comparable, Boolean> types;
    /** a set of check boxes to control the visibility of the types */
    private Set<JCheckBox> checkboxes;

    public CheckBoxFilterPanel(String name, Map<Comparable, Boolean> types) {
        this.types = types;
        this.name = name;
        this.checkboxes = new HashSet<JCheckBox>();
        this.listeners = new EventListenerList();
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setName(name);
        this.setOpaque(false);
        this.add(createCheckBoxPanel());
        this.add(createOverallControlPanel());
        this.setBorder(BorderFactory.createTitledBorder(name));
        this.setMaximumSize(new Dimension(400, (int)(40*(Math.round((checkboxes.size()/DEFAULT_NUMBER_OF_COLUMNS) + 0.5))+80)));
    }

    public void addActionListener(ActionListener listener) {
        listeners.add(ActionListener.class, listener);
    }

    public void removeActionListener(ActionListener listener) {
        listeners.remove(ActionListener.class, listener);
    }

    /**
     * Create a JPanel which consists of one checkbox for each type.
     *
     * @return JPanel   checked box panel
     */
    private JPanel createCheckBoxPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new GridLayout(DEFAULT_NUMBER_OF_ROWS, DEFAULT_NUMBER_OF_COLUMNS));
        for (Comparable type : types.keySet()) {
            JCheckBox cb = new JCheckBox(type.toString());
            cb.setOpaque(false);
            cb.setSelected(types.get(type));
            cb.setActionCommand(type.toString());
            cb.addActionListener(this);
            checkboxes.add(cb);
            panel.add(cb);
        }
        return panel;
    }

    /**
     * Create a JPanel which consists of select all and deselect all
     * button.
     *
     * @return JPanel   overall control panel.
     */
    private JPanel createOverallControlPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new FlowLayout(FlowLayout.RIGHT));

        // clear all button
        JButton clearButton = new JButton(HIDE_ALL_ACTION);
        clearButton.setActionCommand(HIDE_ALL_ACTION);
        clearButton.addActionListener(this);
        panel.add(clearButton);

        // insert a gap
        Box.Filler filler = new Box.Filler(new Dimension(5, 5), new Dimension(5, 5), new Dimension(5, 5));
        panel.add(filler);

        // select all button
        JButton allButton = new JButton(SHOW_ALL_ACTION);
        allButton.setActionCommand(SHOW_ALL_ACTION);
        allButton.addActionListener(this);
        panel.add(allButton);

        return panel;
    }

    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        if (cmd.equals(HIDE_ALL_ACTION)) {
            for (JCheckBox checkbox : checkboxes) {
                if (checkbox.isSelected()) {
                    checkbox.doClick();
                }
            }
        } else if (cmd.equals(SHOW_ALL_ACTION)) {
            for (JCheckBox checkbox : checkboxes) {
                if (!checkbox.isSelected()) {
                    checkbox.doClick();
                }
            }
        } else {
            ActionListener[] actionListeners = listeners.getListeners(ActionListener.class);
            for (ActionListener actionListener : actionListeners) {
                actionListener.actionPerformed(new FilterActionEvent(e.getSource(), e.getID(), name, e.getActionCommand()));
            }
        }
    }
}
