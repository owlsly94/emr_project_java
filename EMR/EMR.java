package EMR;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EMR {
    private JFrame root;
    private Connection conn;
    private PreparedStatement preparedStatement;

    public EMR() {
        // Set dark theme
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }

        root = new JFrame("Dom Zdravlja EMR");
        root.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Database connection for user authentication
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:user_credentials.db");
            create_user_table();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Headline
        JLabel headlineLabel = new JLabel("Dom Zdravlja EMR", JLabel.CENTER);
        headlineLabel.setFont(new Font("Helvetica", Font.BOLD, 18));
        root.add(headlineLabel, BorderLayout.NORTH);

        // Buttons for Options
        JButton addRecordButton = createButton("Otvori karton");
        addRecordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openAddRecordWindow();
            }
        });

        JButton searchRecordsButton = createButton("Pretrazi kartone");
        searchRecordsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openSearchRecordsWindow();
            }
        });

        JButton doctorsButton = createButton("Doktori");
        doctorsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                authenticateAndOpenDoctors();
            }
        });

        JButton appointmentsButton = createButton("Termini");
        appointmentsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openAppointmentsWindow();
            }
        });

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new GridLayout(4, 1, 10, 10));
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        buttonsPanel.add(addRecordButton);
        buttonsPanel.add(searchRecordsButton);
        buttonsPanel.add(doctorsButton);
        buttonsPanel.add(appointmentsButton);

        root.add(buttonsPanel, BorderLayout.CENTER);

        // Center the window
        centerWindow();
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Helvetica", Font.PLAIN, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(36, 47, 65)); // Dark Blue-Grey
        button.setFocusPainted(false);
        return button;
    }

    private void centerWindow() {
        int width = 300;
        int height = 300;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        int x = (screenSize.width - width) / 2;
        int y = (screenSize.height - height) / 2;

        root.setSize(width, height);
        root.setLocation(x, y);
        root.setVisible(true);
    }

    private void openAddRecordWindow() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new PatientRecordAdd();
            }
        });
    }

    private void openSearchRecordsWindow() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new SearchPatientRecords();
            }
        });
    }

    private void openAppointmentsWindow() {
        // Implement the logic to open the appointments window
    }

    private void create_user_table() {
        try {
            preparedStatement = conn.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS users (username TEXT PRIMARY KEY, password TEXT NOT NULL)");
            preparedStatement.execute();

            // Insert default user (admin) if not exists
            preparedStatement = conn.prepareStatement("SELECT * FROM users WHERE username = 'admin'");
            ResultSet resultSet = preparedStatement.executeQuery();

            if (!resultSet.next()) {
                preparedStatement = conn.prepareStatement("INSERT INTO users (username, password) VALUES ('admin', 'admin')");
                preparedStatement.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void authenticateAndOpenDoctors() {
        String username = JOptionPane.showInputDialog("Enter Username:");
        String password = JOptionPane.showInputDialog("Enter Password:");

        if (authenticateUser(username, password)) {
            // Implement the logic to open the doctors window
        } else {
            JOptionPane.showMessageDialog(root, "Authentication Failed", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean authenticateUser(String username, String password) {
        try {
            preparedStatement = conn.prepareStatement("SELECT * FROM users WHERE username = ? AND password = ?");
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();

            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new EMR();
            }
        });
    }
}
