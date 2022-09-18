package me.blackout.hwidgrabber;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.security.MessageDigest;

public class Main {
    private static String hwid;

    public static void main(String[] args) throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        hwid = getHWID();
        JPanel jp = new JPanel();
        jp.setLayout(new GridLayout(1, 2));

        JTextField hwidfield;

        jp.add(hwidfield = new JTextField(hwid));
        System.out.println(hwid);
        hwidfield.setEditable(false);
        JButton button;
        jp.add(button = new JButton("Copy"));
        button.addActionListener(e -> Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(hwidfield.getText()), null));

        JOptionPane.showMessageDialog(null, jp);
    }

    public static String getHWID() {
        try {
            String toEncrypt =  System.getProperty("user.name") + java.net.InetAddress.getLocalHost().getHostName() + "orcrist";
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
