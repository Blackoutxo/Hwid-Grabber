package me.blackout.hwidgrabber;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.net.InetAddress;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

public class Main {
    private static String hwid, input;

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 1. Create the single main application window
        JFrame frame = new JFrame("HWID Grabber");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 2. Safely attach your image asset to the JFrame
        URL iconURL = Main.class.getResource("/logo.png");
        if (iconURL != null) {
            Image icon = Toolkit.getDefaultToolkit().getImage(iconURL);
            frame.setIconImage(icon);
        } else {
            System.err.println("Icon file not found in resources!");
        }

        // 3. Set up the display panel and fields
        JPanel jp = new JPanel(new GridLayout(1, 2, 20, 5)); // 1 row, 2 columns, 10px spacing 5px vertical gap
        jp.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Padding around elements
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        input = JOptionPane.showInputDialog("Enter a special key for extra encryption:");

        // enum
        JComboBox<encryptionOptions> enumBox = new JComboBox<>(encryptionOptions.values());

        JButton encryptButton = new JButton("Encrypt");

        JTextField hwidfield = new JTextField("                                                                ");

        encryptButton.addActionListener(e -> {
            encryptionOptions selected = (encryptionOptions) enumBox.getSelectedItem();
            hwid = getHWID(selected);

            hwidfield.setText(hwid);
            hwidfield.setEditable(false);
            System.out.println("Generated HWID: " + hwid);
        });

        JButton button = new JButton("Copy");
        button.addActionListener(e -> {
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(hwidfield.getText()), null);
            JOptionPane.showMessageDialog(frame, "HWID copied to clipboard!", "Success", JOptionPane.INFORMATION_MESSAGE);
        });

        // 4. Assemble components and display window
        jp.add(enumBox);
        jp.add(hwidfield);
        bottomPanel.add(button);
        bottomPanel.add(encryptButton);

        frame.add(jp);
        frame.add(bottomPanel, BorderLayout.SOUTH);
        frame.pack(); // Automatically sizes window to fit your text field and button
        frame.setLocationRelativeTo(null); // Centers window on screen
        frame.setResizable(false);
        frame.setVisible(true);
    }

    public static String getHWID(encryptionOptions options) {
        try {
            String toEncrypt =  System.getProperty("os.name") + System.getProperty("user.name") + InetAddress.getLocalHost().getHostName() + input;

            switch (options) {
                case AES256 -> {
                    byte[] keyBytes = new byte[32];
                    byte[] ivBytes = new byte[16];
                    SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");
                    IvParameterSpec ivParam = new IvParameterSpec(ivBytes);

                    Cipher encryptCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                    encryptCipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParam);
                    byte[] encryptedBytes = encryptCipher.doFinal(toEncrypt.getBytes(StandardCharsets.UTF_8));

                    return Base64.getEncoder().encodeToString(encryptedBytes);
                }
                case SHA256 -> {
                    MessageDigest md = MessageDigest.getInstance("SHA-256");
                    md.update(toEncrypt.getBytes());
                    StringBuilder hexString = new StringBuilder();

                    byte[] byteData = md.digest();

                    for (byte data : byteData) {
                        String hex = Integer.toHexString(0xff & data);
                        if (hex.length() == 1) hexString.append('0');
                        hexString.append(hex);
                    }

                    return hexString.toString();
                }
            }

            return "MI BOMBO";
        } catch (Exception exception) {
            exception.printStackTrace();

            return "Error";
        }
    }

    public enum encryptionOptions {
        AES256,
        SHA256
    }
}
