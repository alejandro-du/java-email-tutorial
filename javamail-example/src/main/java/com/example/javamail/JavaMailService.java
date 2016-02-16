package com.example.javamail;

import javax.activation.DataHandler;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * @author alejandro@vaadin.com
 **/
public class JavaMailService {

    /**
     * Sends an email message with no attachments.
     *
     * @param from       email address from which the message will be sent.
     * @param recipients the recipients of the message.
     * @param subject    subject header field.
     * @param text       content of the message.
     * @throws MessagingException
     * @throws IOException
     */
    public static void send(String from, Collection<String> recipients, String subject, String text)
            throws MessagingException, IOException {
        send(from, recipients, subject, text, null, null, null);
    }

    /**
     * Sends an email message to one recipient with one attachment.
     *
     * @param from       email address from which the message will be sent.
     * @param recipient  the recipients of the message.
     * @param subject    subject header field.
     * @param text       content of the message.
     * @param attachment attachment to be included with the message.
     * @param fileName   file name of the attachment.
     * @param mimeType   mime type of the attachment.
     * @throws MessagingException
     * @throws IOException
     */
    public static void send(String from, String recipient, String subject, String text, InputStream attachment,
                            String fileName, String mimeType)
            throws MessagingException, IOException {
        send(from, Arrays.asList(recipient), subject, text, Arrays.asList(attachment), Arrays.asList(fileName),
                Arrays.asList(mimeType));
    }

    /**
     * Sends an email message with attachments.
     *
     * @param from        email address from which the message will be sent.
     * @param recipients  array of strings containing the recipients of the message.
     * @param subject     subject header field.
     * @param text        content of the message.
     * @param attachments attachments to be included with the message.
     * @param fileNames   file names for each attachment.
     * @param mimeTypes   mime types for each attachment.
     * @throws MessagingException
     * @throws IOException
     */
    public static void send(String from, Collection<String> recipients, String subject, String text,
                            List<InputStream> attachments, List<String> fileNames, List<String> mimeTypes)
            throws MessagingException, IOException {
        // check for null references
        Objects.requireNonNull(from);
        Objects.requireNonNull(recipients);

        // a message with attachments consists of several parts in a multipart
        MimeMultipart multipart = new MimeMultipart();

        // create text part
        MimeBodyPart textPart = new MimeBodyPart();
        textPart.setText(text, "utf-8", "html");

        // add the text part to the multipart
        multipart.addBodyPart(textPart);

        // create attachment parts if required
        if (attachments != null) {
            // check that attachment and fileNames arrays sizes match
            if (attachments.size() != fileNames.size() || attachments.size() != mimeTypes.size()) {
                throw new IllegalArgumentException(
                        "Attachments, file names, and mime types array sizes must match");
            }

            // create parts and add them to the multipart
            for (int i = 0; i < attachments.size(); i++) {
                // create a data source to wrap the attachment and its mime type
                ByteArrayDataSource dataSource = new ByteArrayDataSource(attachments.get(i), mimeTypes.get(i));

                // create a dataHandler wrapping the data source
                DataHandler dataHandler = new DataHandler(dataSource);

                // create a body part for the attachment and set its data handler and file name
                MimeBodyPart attachmentPart = new MimeBodyPart();
                attachmentPart.setDataHandler(dataHandler);
                attachmentPart.setFileName(fileNames.get(i));

                // add the body part to the multipart
                multipart.addBodyPart(attachmentPart);
            }
        }

        // load email configuration from properties file
        Properties properties = new Properties();
        properties.load(JavaMailService.class.getResourceAsStream("/mail.properties"));

        // create a Session instance specifying the system properties
        Session session = Session.getInstance(properties);

        // create a message instance associated to the session
        MimeMessage message = new MimeMessage(session);

        // set the multipart as content for the message
        message.setContent(multipart);

        // configure from address, add recipients, and set the subject of the message
        message.setFrom(from);
        message.addRecipients(Message.RecipientType.TO, String.join(",", recipients));
        message.setSubject(subject);

        // send the message
        String username = properties.getProperty("mail.smtp.username");
        String password = properties.getProperty("mail.smtp.password");
        Transport.send(message, username, password);
    }

}
