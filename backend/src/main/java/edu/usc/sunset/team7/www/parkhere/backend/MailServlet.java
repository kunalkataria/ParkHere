package edu.usc.sunset.team7.www.parkhere.backend;

import com.google.firebase.internal.Log;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Jonathan on 11/14/16.
 */

public class MailServlet extends HttpServlet {

    private static final String TAG = "Mail Servlet";

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        //Unpack email text
        String email = req.getParameter("email");
        String textBody = req.getParameter("textBody");
        System.out.println("************EMAIL:" + email);
        System.out.println("************TEXTBODY:" + textBody);


        sendSimpleMail(email, textBody);

    }

    private void sendSimpleMail(String email, String textBody) {
        // [START simple_example]
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress("parkhereteam@gmail.com", "Park Here Team"));
            msg.addRecipient(Message.RecipientType.TO,
                    new InternetAddress(email, "ParkHere Customer"));
            msg.setSubject("Your ParkHere Invoice");
            msg.setText(textBody);
            Transport.send(msg);
        } catch (AddressException e) {
            Log.d(TAG, e.getMessage());
        } catch (MessagingException e) {
            Log.d(TAG, e.getMessage());
        } catch (UnsupportedEncodingException e) {
            Log.d(TAG, e.getMessage());
        }
    }
}