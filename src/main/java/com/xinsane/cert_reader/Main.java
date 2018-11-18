package com.xinsane.cert_reader;

import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;

public class Main {

    public static void main(String[] args) {
        try {
            InputStream inputStream = ClassLoader.getSystemResourceAsStream("google.cer");
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
        }
        catch (CertificateException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
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
