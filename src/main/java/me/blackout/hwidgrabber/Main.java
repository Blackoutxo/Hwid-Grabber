package me.blackout.hwidgrabber;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.net.URL;
import java.security.MessageDigest;

public class Main {
    private static String hwid;

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        hwid = getHWID();

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
        JPanel jp = new JPanel(new GridLayout(1, 2, 10, 0)); // 1 row, 2 columns, 10px spacing
        jp.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Padding around elements

        JTextField hwidfield = new JTextField(hwid);
        hwidfield.setEditable(false);
        System.out.println("Generated HWID: " + hwid);

        JButton button = new JButton("Copy");
        button.addActionListener(e -> {
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(hwidfield.getText()), null);
            JOptionPane.showMessageDialog(frame, "HWID copied to clipboard!", "Success", JOptionPane.INFORMATION_MESSAGE);
        });

        // 4. Assemble components and display window
        jp.add(hwidfield);
        jp.add(button);

        frame.add(jp);
        frame.pack(); // Automatically sizes window to fit your text field and button
        frame.setLocationRelativeTo(null); // Centers window on screen
        frame.setResizable(false);
        frame.setVisible(true);
    }

    public static String getHWID() {
        try {
            String toEncrypt =  System.getProperty("user.name") + java.net.InetAddress.getLocalHost().getHostName() + " insert something here so it gives it a little unique touch ";
            MessageDigest md = MessageDigest.getInstance("SHA256");
            md.update(toEncrypt.getBytes());
            StringBuffer hexString = new StringBuffer();

            byte byteData[] = md.digest();

            for (byte data : byteData) {
                String hex = Integer.toHexString(0xff & data);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (Exception exception) {
            exception.printStackTrace();

            return "Error";
        }
    }
}
