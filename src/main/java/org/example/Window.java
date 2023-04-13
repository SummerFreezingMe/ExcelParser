package org.example;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Properties;

class Window extends JFrame implements ActionListener {
    JFrame configureDBConnectionFrame = new JFrame("configure DB Connection");

    static JLabel l;
    Container configureContainer;

    Container mainContainer;
    JLabel configureTitleLabel;
    JLabel urlLabel;
    JTextField urlField;
    JLabel userLabel;
    JTextField userField;
    JLabel passwordLabel;
    JTextField passwordField;
    JButton submitOption;
    JButton refuseOption;
    JLabel sslLabel;
    JRadioButton sslOptionTrue;
    JRadioButton sslOptionFalse;
    ButtonGroup sslSelection;
    String filename;

    Properties dbProperties = new Properties();

    String databaseURL;

    Window() {
    }

    private static void invokeWindow() {
        JFrame mainFrame = new JFrame("Choose files to parse");
        mainFrame.setSize(400, 100);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JButton button2 = new JButton("open");
        JButton button3 = new JButton("parse");
        JButton button4 = new JButton("db connection");
        button2.setSize(200, 30);
        button3.setSize(200, 30);
        button4.setSize(200, 30);
        Window f1 = new Window();
        button2.addActionListener(f1);
        button3.addActionListener(f1);
        button4.addActionListener(f1);
        JPanel mainPanel = new JPanel();
        mainPanel.add(button2);
        mainPanel.add(button3);
        mainPanel.add(button4);
        l = new JLabel("no file selected");
        mainPanel.add(l);
        mainFrame.add(mainPanel);
        mainFrame.setVisible(true);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (ClassNotFoundException | InstantiationException | UnsupportedLookAndFeelException |
                 IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        invokeWindow();
    }

    public void actionPerformed(ActionEvent evt) {
        String com = evt.getActionCommand();
        switch (com) {
            case "parse":
                try {
                    Program.startParsing(filename,databaseURL,dbProperties);
                    l.setText("File successfully parsed!");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                break;
            case "db connection":
                configureDBConnection();

                break;
            case "Submit":
                dbProperties.clear();
                databaseURL = urlField.getText();
                dbProperties.put("user",userField.getText());
                dbProperties.put("password", passwordField.getText());
                dbProperties.put("ssl", String.valueOf(sslOptionTrue.isSelected()));
                configureDBConnectionFrame.setVisible(false);
                break;
            case "Refuse":
                configureDBConnectionFrame.setVisible(false);
                break;
            default:
                JFileChooser j = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
                j.setAcceptAllFileFilterUsed(false);
                j.setDialogTitle("Select a .xlsx file");
                FileNameExtensionFilter restrict = new FileNameExtensionFilter("Only .xlsx files", "xlsx");
                j.addChoosableFileFilter(restrict);
                int r = j.showOpenDialog(null);
                if (r == JFileChooser.APPROVE_OPTION) {
                    filename = j.getSelectedFile().getAbsolutePath();
                    l.setText(filename);
                } else
                    l.setText("the user cancelled the operation");
                break;
        }
    }

    private void configureDBConnection() {



        configureDBConnectionFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        configureDBConnectionFrame.setTitle("Registration Form");
        configureDBConnectionFrame.setSize( 600, 400);
        configureDBConnectionFrame.setLocationRelativeTo(null);
        configureDBConnectionFrame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        configureDBConnectionFrame.setResizable(false);

        configureContainer = getContentPane();
        configureContainer.setLayout(null);

        configureTitleLabel = new JLabel("Database Connection");
        configureTitleLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        configureTitleLabel.setSize(300, 30);
        configureTitleLabel.setLocation(200, 30);
        configureContainer.add(configureTitleLabel);

        urlLabel = new JLabel("URL");
        urlLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        urlLabel.setSize(100, 20);
        urlLabel.setLocation(100, 100);
        configureContainer.add(urlLabel);

        urlField = new JTextField();
        urlField.setFont(new Font("Arial", Font.PLAIN, 15));
        urlField.setSize(225, 20);
        urlField.setLocation(200, 100);
        configureContainer.add(urlField);

        userLabel = new JLabel("User");
        userLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        userLabel.setSize(100, 20);
        userLabel.setLocation(100, 150);
        configureContainer.add(userLabel);

        userField = new JTextField();
        userField.setFont(new Font("Arial", Font.PLAIN, 15));
        userField.setSize(150, 20);
        userField.setLocation(200, 150);
        configureContainer.add(userField);

        passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        passwordLabel.setSize(100, 20);
        passwordLabel.setLocation(100, 200);
        configureContainer.add(passwordLabel);

        passwordField = new JTextField();
        passwordField.setFont(new Font("Arial", Font.PLAIN, 15));
        passwordField.setSize(150, 20);
        passwordField.setLocation(200, 200);
        configureContainer.add(passwordField);

        sslLabel = new JLabel("SSL");
        sslLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        sslLabel.setSize(100, 20);
        sslLabel.setLocation(100, 250);
        configureContainer.add(sslLabel);

        sslOptionTrue = new JRadioButton("True");
        sslOptionTrue.setFont(new Font("Arial", Font.PLAIN, 15));
        sslOptionTrue.setSelected(true);
        sslOptionTrue.setSize(75, 20);
        sslOptionTrue.setLocation(200, 250);
        configureContainer.add(sslOptionTrue);

        sslOptionFalse = new JRadioButton("False");
        sslOptionFalse.setFont(new Font("Arial", Font.PLAIN, 15));
        sslOptionFalse.setSelected(false);
        sslOptionFalse.setSize(80, 20);
        sslOptionFalse.setLocation(275, 250);
        configureContainer.add(sslOptionFalse);

        sslSelection = new ButtonGroup();
        sslSelection.add(sslOptionTrue);
        sslSelection.add(sslOptionFalse);

        submitOption = new JButton("Submit");
        submitOption.setFont(new Font("Arial", Font.PLAIN, 15));
        submitOption.setSize(100, 20);
        submitOption.setLocation(150, 300);
        submitOption.addActionListener(this);
        configureContainer.add(submitOption);

        refuseOption = new JButton("Refuse");
        refuseOption.setFont(new Font("Arial", Font.PLAIN, 15));
        refuseOption.setSize(100, 20);
        refuseOption.setLocation(270, 300);
        refuseOption.addActionListener(this);
        configureContainer.add(refuseOption);

        configureDBConnectionFrame.add(configureContainer);
        configureDBConnectionFrame.setVisible(true);
    }
}
