package EMR;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PatientRecordAdd {
    private JFrame frame;
    private Connection conn;
    private PreparedStatement preparedStatement;

    public PatientRecordAdd() {
        // Set dark theme
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }

        frame = new JFrame("Dodaj Zapis Pacijenta");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Database connection for patient records
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:patient_record.db");
            create_patient_record_table();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Headline
        JLabel basicInfoLabel = new JLabel("Osnovno", JLabel.CENTER);
        basicInfoLabel.setFont(new Font("Helvetica", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(basicInfoLabel, gbc);

        // Form fields
        String[] labels = {
                "Ime:", "Srednje Ime:", "Prezime:", "Broj Kartona:", "JMBG:", "LBO:", "Broj Knjižice:",
                "Adresa:", "Broj Telefona:", "Grad:"
        };

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = i + 1;
            gbc.gridwidth = 1;
            mainPanel.add(new JLabel(labels[i]), gbc);

            gbc.gridx = 1;
            mainPanel.add(new JTextField(20), gbc);
        }

        // Personal Information and Medical Info Headline
        JLabel personalInfoLabel = new JLabel("Privatni Podaci", JLabel.CENTER);
        personalInfoLabel.setFont(new Font("Helvetica", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = labels.length + 1;
        gbc.gridwidth = 2;
        mainPanel.add(personalInfoLabel, gbc);

        // Sex Drop-down
        String[] sexOptions = {"empty", "Muško", "Žensko", "Drugo"};
        addLabelAndDropdown(mainPanel, "Pol:", sexOptions, gbc);

        // Blood Type Drop-down
        String[] bloodTypeOptions = {"empty", "A", "B", "AB", "O"};
        addLabelAndDropdown(mainPanel, "Krvna Grupa:", bloodTypeOptions, gbc);

        // RH Factor Drop-down
        String[] rhFactorOptions = {"empty", "Pozitivna", "Negativna"};
        addLabelAndDropdown(mainPanel, "RH Faktor:", rhFactorOptions, gbc);

        // Marital Status Field
        addLabelAndTextField(mainPanel, "Bračno Stanje:", gbc);

        // Risk Factor Headline
        JLabel riskFactorLabel = new JLabel("Faktori Rizika", JLabel.CENTER);
        riskFactorLabel.setFont(new Font("Helvetica", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = labels.length + 6;
        gbc.gridwidth = 2;
        mainPanel.add(riskFactorLabel, gbc);

        // Checkboxes for Risk Factors
        String[] riskFactors = {"Holesterol", "Pušenje", "Gojaznost", "Hipertenzija", "Dijabetes", "Alkoholizam", "Alergije"};
        for (int i = 0; i < riskFactors.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = labels.length + 7 + i;
            gbc.gridwidth = 2;
            mainPanel.add(createCheckbox(riskFactors[i]), gbc);
        }

        // Chosen Doctor Headline
        JLabel chosenDoctorLabel = new JLabel("Izabrani Lekari", JLabel.CENTER);
        chosenDoctorLabel.setFont(new Font("Helvetica", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = labels.length + 14 + riskFactors.length;
        gbc.gridwidth = 2;
        mainPanel.add(chosenDoctorLabel, gbc);

        // Fields for Chosen Doctor
        String[] doctorFields = {"Lekar Opšte Prakse:", "Pedijatar:", "Stomatolog:", "Ginekolog/Urolog:"};
        for (int i = 0; i < doctorFields.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = labels.length + 15 + riskFactors.length + i;
            gbc.gridwidth = 1;
            mainPanel.add(new JLabel(doctorFields[i]), gbc);

            gbc.gridx = 1;
            mainPanel.add(new JTextField(20), gbc);
        }

        // Save Record Button
        JButton saveRecordButton = createButton("Sačuvaj Zapis");
        saveRecordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sacuvajPacijentovZapis();
            }
        });
        gbc.gridx = 0;
        gbc.gridy = labels.length + 19 + riskFactors.length + doctorFields.length;
        gbc.gridwidth = 2;
        mainPanel.add(saveRecordButton, gbc);

        frame.add(mainPanel);
        frame.pack();
        centrirajProzor();
        frame.setVisible(true);
    }

    private void addLabelAndDropdown(JPanel panel, String label, String[] options, GridBagConstraints gbc) {
        gbc.gridx = 0;
        gbc.gridy += 1;
        gbc.gridwidth = 1;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        JComboBox<String> dropdown = new JComboBox<>(options);
        panel.add(dropdown, gbc);
    }

    private void addLabelAndTextField(JPanel panel, String label, GridBagConstraints gbc) {
        gbc.gridx = 0;
        gbc.gridy += 1;
        gbc.gridwidth = 1;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        panel.add(new JTextField(20), gbc);
    }

    private JCheckBox createCheckbox(String label) {
        JCheckBox checkbox = new JCheckBox(label);
        checkbox.setFont(new Font("Helvetica", Font.PLAIN, 14));
        checkbox.setForeground(Color.BLACK); // Changed to black text
        checkbox.setBackground(new Color(36, 47, 65)); // Dark Blue-Grey
        return checkbox;
    }

    private JButton createButton(String label) {
        JButton button = new JButton(label);
        button.setFont(new Font("Helvetica", Font.PLAIN, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(36, 47, 65)); // Dark Blue-Grey
        button.setFocusPainted(false);
        return button;
    }

    private void centrirajProzor() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screenSize.width - frame.getWidth()) / 2;
        int y = (screenSize.height - frame.getHeight()) / 2;
        frame.setLocation(x, y);
    }

    private void create_patient_record_table() {
        try {
            preparedStatement = conn.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS pacijentovi_zapisi (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "ime TEXT, " +
                            "srednje_ime TEXT, " +
                            "prezime TEXT, " +
                            "broj_kartona TEXT, " +
                            "jmbg TEXT, " +
                            "lbo TEXT, " +
                            "broj_knjizice TEXT, " +
                            "adresa TEXT, " +
                            "broj_telefona TEXT, " +
                            "grad TEXT, " +
                            "pol TEXT, " +
                            "krvna_grupa TEXT, " +
                            "rh_faktor TEXT, " +
                            "bracno_stanje TEXT, " +
                            "holesterol BOOLEAN, " +
                            "pusenje BOOLEAN, " +
                            "gojaznost BOOLEAN, " +
                            "hipertenzija BOOLEAN, " +
                            "dijabetes BOOLEAN, " +
                            "alkoholizam BOOLEAN, " +
                            "alergije BOOLEAN, " +
                            "lekar_opste_prakse TEXT, " +
                            "pedijatar TEXT, " +
                            "stomatolog TEXT, " +
                            "ginekolog_urolog TEXT)"
            );
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void sacuvajPacijentovZapis() {
        String ime = getVrednostTekstPolja(frame, "Ime:");
        String srednjeIme = getVrednostTekstPolja(frame, "Srednje Ime:");
        String prezime = getVrednostTekstPolja(frame, "Prezime:");
        String brojKartona = getVrednostTekstPolja(frame, "Broj Kartona:");
        String jmbg = getVrednostTekstPolja(frame, "JMBG:");
        String lbo = getVrednostTekstPolja(frame, "LBO:");
        String brojKnjizice = getVrednostTekstPolja(frame, "Broj Knjižice:");
        String adresa = getVrednostTekstPolja(frame, "Adresa:");
        String brojTelefona = getVrednostTekstPolja(frame, "Broj Telefona:");
        String grad = getVrednostTekstPolja(frame, "Grad:");
        String pol = getVrednostPadajucegMenija(frame, "Pol:");
        String krvnaGrupa = getVrednostPadajucegMenija(frame, "Krvna Grupa:");
        String rhFaktor = getVrednostPadajucegMenija(frame, "RH Faktor:");
        String bracnoStanje = getVrednostTekstPolja(frame, "Bračno Stanje:");
        boolean holesterol = getVrednostCeka(frame, "Holesterol");
        boolean pusenje = getVrednostCeka(frame, "Pušenje");
        boolean gojaznost = getVrednostCeka(frame, "Gojaznost");
        boolean hipertenzija = getVrednostCeka(frame, "Hipertenzija");
        boolean dijabetes = getVrednostCeka(frame, "Dijabetes");
        boolean alkoholizam = getVrednostCeka(frame, "Alkoholizam");
        boolean alergije = getVrednostCeka(frame, "Alergije");
        String lekarOpstePrakse = getVrednostTekstPolja(frame, "Lekar Opšte Prakse:");
        String pedijatar = getVrednostTekstPolja(frame, "Pedijatar:");
        String stomatolog = getVrednostTekstPolja(frame, "Stomatolog:");
        String ginekologUrolog = getVrednostTekstPolja(frame, "Ginekolog/Urolog:");
        System.out.println("Vrednost za ime: " + ime);
    System.out.println("Vrednost za srednjeIme: " + srednjeIme);

        try {
            preparedStatement = conn.prepareStatement(
                    "INSERT INTO pacijentovi_zapisi (ime, srednje_ime, prezime, broj_kartona, jmbg, lbo, broj_knjizice, " +
                            "adresa, broj_telefona, grad, pol, krvna_grupa, rh_faktor, bracno_stanje, holesterol, pusenje, " +
                            "gojaznost, hipertenzija, dijabetes, alkoholizam, alergije, lekar_opste_prakse, pedijatar, " +
                            "stomatolog, ginekolog_urolog) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
            );

            preparedStatement.setString(1, ime);
            preparedStatement.setString(2, srednjeIme);
            preparedStatement.setString(3, prezime);
            preparedStatement.setString(4, brojKartona);
            preparedStatement.setString(5, jmbg);
            preparedStatement.setString(6, lbo);
            preparedStatement.setString(7, brojKnjizice);
            preparedStatement.setString(8, adresa);
            preparedStatement.setString(9, brojTelefona);
            preparedStatement.setString(10, grad);
            preparedStatement.setString(11, pol);
            preparedStatement.setString(12, krvnaGrupa);
            preparedStatement.setString(13, rhFaktor);
            preparedStatement.setString(14, bracnoStanje);
            preparedStatement.setBoolean(15, holesterol);
            preparedStatement.setBoolean(16, pusenje);
            preparedStatement.setBoolean(17, gojaznost);
            preparedStatement.setBoolean(18, hipertenzija);
            preparedStatement.setBoolean(19, dijabetes);
            preparedStatement.setBoolean(20, alkoholizam);
            preparedStatement.setBoolean(21, alergije);
            preparedStatement.setString(22, lekarOpstePrakse);
            preparedStatement.setString(23, pedijatar);
            preparedStatement.setString(24, stomatolog);
            preparedStatement.setString(25, ginekologUrolog);
            
            System.out.println("SQL Upit: " + preparedStatement.toString());

            preparedStatement.execute();

            JOptionPane.showMessageDialog(frame, "Zapis pacijenta uspešno sačuvan.", "Uspeh", JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException e) {
            e.printStackTrace(System.out);
            JOptionPane.showMessageDialog(frame, "Greška prilikom čuvanja zapisa pacijenta.", "Greška", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String getVrednostTekstPolja(JFrame frame, String label) {
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

private String getVrednostPadajucegMenija(JFrame frame, String label) {
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
                                if (fieldComponent instanceof JComboBox) {
                                    return (String) ((JComboBox<?>) fieldComponent).getSelectedItem();
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

private boolean getVrednostCeka(JFrame frame, String label) {
Component[] components = frame.getContentPane().getComponents();
for (Component component : components) {
    if (component instanceof JPanel) {
        Component[] panelComponents = ((JPanel) component).getComponents();
        for (Component panelComponent : panelComponents) {
            if (panelComponent instanceof JPanel) {
                Component[] subPanelComponents = ((JPanel) panelComponent).getComponents();
                for (Component subPanelComponent : subPanelComponents) {
                    if (subPanelComponent instanceof JCheckBox) {
                        if (((JCheckBox) subPanelComponent).getText().equals(label)) {
                            return ((JCheckBox) subPanelComponent).isSelected();
                        }
                    }
                }
            }
        }
    }
}
return false;
}

public static void main(String[] args) {
SwingUtilities.invokeLater(new Runnable() {
    @Override
    public void run() {
        new PatientRecordAdd();
    }
});
}
}

