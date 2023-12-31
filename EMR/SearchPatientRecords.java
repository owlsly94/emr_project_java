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

public class SearchPatientRecords {
    private JFrame frame;
    private Connection conn;
    private PreparedStatement preparedStatement;

    public SearchPatientRecords() {
        // Set dark theme
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }

        frame = new JFrame("Search Patient Records");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Database connection for patient records
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:patient_record.db");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Search Fields
        String[] searchLabels = {"Ime:", "Prezime:", "JMBG:", "LBO:"};

        for (int i = 0; i < searchLabels.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = i;
            gbc.gridwidth = 1;
            mainPanel.add(new JLabel(searchLabels[i]), gbc);

            gbc.gridx = 1;
            mainPanel.add(new JTextField(20), gbc);
        }

        // Search Button
        JButton searchButton = new JButton("Pretraži bazu");
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchPatientRecords();
            }
        });
        gbc.gridx = 0;
        gbc.gridy = searchLabels.length;
        gbc.gridwidth = 2;
        mainPanel.add(searchButton, gbc);

        // Patient Information Headline
        JLabel patientInfoLabel = new JLabel("Informacije o pacijentu", JLabel.CENTER);
        patientInfoLabel.setFont(new Font("Helvetica", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = searchLabels.length + 1;
        gbc.gridwidth = 2;
        mainPanel.add(patientInfoLabel, gbc);

        // Display Area for Patient Information
        JTextArea patientInfoArea = new JTextArea();
        patientInfoArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(patientInfoArea);
        scrollPane.setPreferredSize(new Dimension(300, 200));
        gbc.gridx = 0;
        gbc.gridy = searchLabels.length + 2;
        gbc.gridwidth = 2;
        mainPanel.add(scrollPane, gbc);

        frame.add(mainPanel);
        frame.pack();
        centerFrame();
        frame.setVisible(true);
    }

    private void searchPatientRecords() {
        String ime = getTextFieldValue(frame, "Ime:");
        String prezime = getTextFieldValue(frame, "Prezime:");
        String jmbg = getTextFieldValue(frame, "JMBG:");
        String lbo = getTextFieldValue(frame, "LBO:");

        try {
            preparedStatement = conn.prepareStatement(
                    "SELECT * FROM pacijentovi_zapisi WHERE ime = ? AND prezime = ? AND jmbg = ? AND lbo = ?");
            preparedStatement.setString(1, ime);
            preparedStatement.setString(2, prezime);
            preparedStatement.setString(3, jmbg);
            preparedStatement.setString(4, lbo);

            ResultSet resultSet = preparedStatement.executeQuery();

            // Display patient information
            if (resultSet.next()) {
                displayPatientInformation(resultSet);
            } else {
                JOptionPane.showMessageDialog(frame, "Karton ne postoji", "Ne postoji", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Greska u pretrazivanju.", "Greska", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void displayPatientInformation(ResultSet resultSet) throws SQLException {
        StringBuilder patientInfo = new StringBuilder();

        // Retrieve patient information from the result set
        String[] fields = {"ime", "srednje_ime", "prezime", "broj_kartona", "jmbg", "lbo", "broj_knjizice",
                "adresa", "broj_telefona", "grad", "pol", "krvna_grupa", "rh_faktor", "bracno_stanje", "holesterol",
                "pusenje", "gojaznost", "hipertenzija", "dijabetes", "alkoholizam", "alergije", "lekar_opste_prakse",
                "pedijatar", "stomatolog", "ginekolog_urolog"};

        for (String field : fields) {
            patientInfo.append(field).append(": ").append(resultSet.getString(field)).append("\n");
        }

        JTextArea textArea = new JTextArea(patientInfo.toString());
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(300, 200));

        JOptionPane.showMessageDialog(frame, scrollPane, "Informacije o pacijentu", JOptionPane.PLAIN_MESSAGE);
    }

    private void centerFrame() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screenSize.width - frame.getWidth()) / 2;
        int y = (screenSize.height - frame.getHeight()) / 2;
        frame.setLocation(x, y);
    }

    private String getTextFieldValue(JFrame frame, String label) {
        Component[] components = frame.getContentPane().getComponents();
        for (Component component : components) {
            if (component instanceof JPanel) {
                Component[] panelComponents = ((JPanel) component).getComponents();
                for (Component panelComponent : panelComponents) {
                    if (panelComponent instanceof JPanel) {
                        Component[] subPanelComponents = ((JPanel) panelComponent).getComponents();
                        for (Component subPanelComponent : subPanelComponents) {
                            if (subPanelComponent instanceof JLabel) {
                                if (((JLabel) subPanelComponent).getText().equals(label)) {
                                    Component[] fieldComponents = ((JPanel) panelComponent).getComponents();
                                    for (Component fieldComponent : fieldComponents) {
                                        if (fieldComponent instanceof JTextField) {
                                            return ((JTextField) fieldComponent).getText();
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new SearchPatientRecords();
            }
        });
    }
}
