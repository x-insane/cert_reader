package com.xinsane.cert_reader;

import com.xinsane.cert_reader.util.MultiLineRowRenderer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        JFrame frame = new JFrame("证书查看器");
        frame.setBounds(400, 200, 600, 450);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        List<String[]> list = new ArrayList<>();
        try {
            InputStream inputStream;
            if (args.length > 0) {
                inputStream = new FileInputStream(args[0]);
                frame.setTitle("证书查看器 - " + args[0]);
            }
            else {
                inputStream = ClassLoader.getSystemResourceAsStream("google.cer");
                frame.setTitle("证书查看器 - " + "google.cer");
            }
            CertificateFactory x509 = CertificateFactory.getInstance("X509");
            X509Certificate cert = (X509Certificate) x509.generateCertificate(inputStream);
            System.out.println("版本号: V" + cert.getVersion());
            System.out.println("序列号: " + cert.getSerialNumber().toString(16));
            System.out.println("签名算法: " + cert.getSigAlgName());
            System.out.println("颁发者: " + cert.getIssuerDN());
            System.out.println("使用者: " + cert.getSubjectDN());
            System.out.println("有效期: " + sdf.format(cert.getNotBefore()) + " - " + sdf.format(cert.getNotAfter()));
            System.out.println("公钥: " + bytes2Hex(cert.getPublicKey().getEncoded()));
            MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
            System.out.println("指纹: " + bytes2Hex(sha1.digest(cert.getEncoded())));
            System.out.println("签名: " + bytes2Hex(cert.getSignature()));
            list.add(new String[] { "版本号", "V" + cert.getVersion() });
            list.add(new String[] { "序列号",  cert.getSerialNumber().toString(16)});
            list.add(new String[] { "签名算法",  cert.getSigAlgName()});
            list.add(new String[] { "颁发者",  cert.getIssuerDN().toString()});
            list.add(new String[] { "使用者",  cert.getSubjectDN().toString()});
            list.add(new String[] { "有效期",  sdf.format(cert.getNotBefore()) + " - " + sdf.format(cert.getNotAfter())});
            list.add(new String[] { "公钥",  bytes2Hex(cert.getPublicKey().getEncoded())});
            list.add(new String[] { "指纹",  bytes2Hex(sha1.digest(cert.getEncoded()))});
            list.add(new String[] { "签名",  bytes2Hex(cert.getSignature())});
        }
        catch (CertificateException | NoSuchAlgorithmException | FileNotFoundException e) {
            e.printStackTrace();
        }
        String[] titles = { "属性", "值" };
        DefaultTableModel model = new DefaultTableModel(list.toArray(new String[0][]), titles);
        JTable table = new JTable(model) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            @Override
            public boolean isCellSelected(int row, int column) {
                return false;
            }
        };
        table.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        table.getColumnModel().getColumn(0).setMaxWidth(100);
        table.getColumnModel().getColumn(1).setCellRenderer(new MultiLineRowRenderer());
        frame.add(new JScrollPane(table));
        frame.setVisible(true);
    }

    private static String bytes2Hex(byte[] bytes) {
        char[] HEX_CHAR = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        char[] buf = new char[bytes.length * 3];
        int index = 0;
        for (byte b : bytes) {
            buf[index++] = HEX_CHAR[b >>> 4 & 0xf];
            buf[index++] = HEX_CHAR[b & 0xf];
            buf[index++] = ' ';
        }
        return new String(buf);
    }

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
}
