

package edu.uchicago.akorsos;

import edu.uchicago.akorsos.entities.Booking;
import edu.uchicago.akorsos.jsf.BookingController;
import java.util.Properties;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@Stateless
public class EmailSession {
    
@Resource(name= "MailSession")
private Session mailSession;

 public void sendEmail(BookingController bc) {
        String to = "akorsos@uchicago.edu";
        String recip = bc.getSelected().getEmail();
                   
        MimeMessage message = new MimeMessage(mailSession);

        try {
            InternetAddress address = new InternetAddress(recip);
            message.addRecipient(Message.RecipientType.TO, address);
            message.setSubject("Your Confirmation");
            message.setText("This is your confirmation message.");

            Transport.send(message);
            System.out.println("Sent message successfully.");
        } catch (MessagingException mex) {
            mex.printStackTrace();
            System.out.println("Message not sent.");

        }
    }
}