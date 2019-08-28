package work.packageroom;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.sun.mail.smtp.SMTPTransport;

public class SendMail {
    
    // https://wseit.engineering.jhu.edu/get-help/jhem-to-exchange-user-instructions/
    private static transient String user, pwd;
    
    private static void login() {
    }

    public static void main(String[] args) throws AddressException, MessagingException {
    }
    
    public static void sendEmail(String header, String to, String body) throws AddressException, MessagingException {
        // student server outlook.office365.com; JHED@jh.edu
        final String username = "";
        final String password = "";

        Properties props = new Properties();
        props.put("mail.smtp.host","smtp.johnshopkins.edu");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth","true");
        props.put("mail.smtp.starttls.enable", "true");
        
        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
          });
        session.setDebug(true);
        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(username));
        msg.setRecipients(RecipientType.TO, InternetAddress.parse(to, false));
        msg.setContent(msg, "text/html; charset=utf-8");
        msg.setSubject(header);
        msg.setText(body);

        SMTPTransport transport = (SMTPTransport) session.getTransport("smtp");
        transport.connect(username, password);
        transport.sendMessage(msg, msg.getAllRecipients());
        System.out.println("Result: " + transport.getLastServerResponse());
        transport.close();
    }
}