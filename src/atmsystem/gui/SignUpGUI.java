package atmsystem.gui;

import atmsystem.database.DatabaseHandler;
import atmsystem.models.Customer;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;
import java.util.regex.Pattern;

public class SignUpGUI extends JFrame {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^\\+?[0-9\\-\\s]{7,15}$");

    private final JTextField customerIdField;
    private final JTextField firstNameField;
    private final JTextField lastNameField;
    private final JTextField addressField;
    private final JTextField phoneField;
    private final JTextField emailField;

    public SignUpGUI() {
        setTitle("ATM System - Customer Registration");
        setSize(640, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(Theme.BG_TEAL);
        setLayout(new BorderLayout());

        JPanel header = new JPanel();
        header.setBackground(Theme.BG_TEAL);
        header.setBorder(Theme.panelBorder());
        header.add(Theme.titleLabel("CUSTOMER REGISTRATION"));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Theme.BG_TEAL);
        form.setBorder(Theme.panelBorder());

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(8, 10, 8, 10);
        gc.fill = GridBagConstraints.HORIZONTAL;

        customerIdField = Theme.textField();
        customerIdField.setEditable(false);
        customerIdField.setBackground(new java.awt.Color(220, 220, 220));
        firstNameField = Theme.textField();
        lastNameField = Theme.textField();
        addressField = Theme.textField();
        phoneField = Theme.textField();
        emailField = Theme.textField();

        addRow(form, gc, 0, "Customer ID:", customerIdField);
        addRow(form, gc, 1, "First Name:", firstNameField);
        addRow(form, gc, 2, "Last Name:", lastNameField);
        addRow(form, gc, 3, "Address:", addressField);
        addRow(form, gc, 4, "Phone Number:", phoneField);
        addRow(form, gc, 5, "Email Address:", emailField);

        JButton next = Theme.primaryButton("NEXT");
        JButton back = Theme.secondaryButton("BACK");
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttons.setOpaque(false);
        buttons.add(next);
        buttons.add(back);

        gc.gridx = 0; gc.gridy = 6; gc.gridwidth = 2; gc.anchor = GridBagConstraints.CENTER;
        form.add(buttons, gc);

        add(header, BorderLayout.NORTH);
        add(form, BorderLayout.CENTER);

        next.addActionListener(e -> proceed());
        back.addActionListener(e -> {
            new LoginGUI().setVisible(true);
            dispose();
        });

        autoGenerateCustomerId();
    }

    private void addRow(JPanel form, GridBagConstraints gc, int row, String label, JTextField field) {
        gc.gridx = 0; gc.gridy = row; gc.weightx = 0; gc.anchor = GridBagConstraints.LINE_END;
        form.add(Theme.formLabel(label), gc);
        gc.gridx = 1; gc.gridy = row; gc.weightx = 1.0;
        field.setPreferredSize(new Dimension(280, 30));
        form.add(field, gc);
    }

    private void autoGenerateCustomerId() {
        try {
            customerIdField.setText(DatabaseHandler.generateCustomerId());
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Could not generate customer ID. Check database connection.\n" + ex.getMessage(),
                    "Database error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void proceed() {
        String customerId = customerIdField.getText();
        String firstName  = firstNameField.getText().trim();
        String lastName   = lastNameField.getText().trim();
        String address    = addressField.getText().trim();
        String phone      = phoneField.getText().trim();
        String email      = emailField.getText().trim();

        if (firstName.isEmpty() || lastName.isEmpty() || address.isEmpty()
                || phone.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.",
                    "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            JOptionPane.showMessageDialog(this, "Invalid email format.",
                    "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!PHONE_PATTERN.matcher(phone).matches()) {
            JOptionPane.showMessageDialog(this, "Invalid phone number.",
                    "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Customer customer = new Customer(customerId, firstName, lastName, address, phone, email);
            DatabaseHandler.saveCustomer(customer);
            new AccountCreationGUI(customer).setVisible(true);
            dispose();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Database error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
