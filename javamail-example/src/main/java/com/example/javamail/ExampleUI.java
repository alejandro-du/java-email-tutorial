package com.example.javamail;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.*;

import javax.servlet.annotation.WebServlet;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author alejandro@vaadin.com
 **/
@Theme("valo")
public class ExampleUI extends UI implements Upload.Receiver {

    private ByteArrayOutputStream outputStream;
    private String mimeType;
    private String fileName;

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        // the text field where users will specify their email address
        TextField textField = new TextField("Your email:");

        // an upload component to select the file to be attached
        Upload upload = new Upload("Attachment", this);

        // a button with a click listener that sends the email
        Button button = new Button("Send me the file", e -> sendEmail(textField.getValue()));

        // a layout containing the previous components
        VerticalLayout layout = new VerticalLayout(textField, upload, button);
        layout.setMargin(true);
        layout.setSpacing(true);
        setContent(layout); // sets the content for this UI
    }

    private void sendEmail(String to) {
        try {
            // all values as variables to clarify its usage
            InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
            String from = "sender@test.com";
            String subject = "Your file";
            String text = "Here there is your <b>file</b>!";

            // call the mail service to send the message
            JavaMailService.send(from, to, subject, text, inputStream, fileName, mimeType);

            inputStream.close();

            Notification.show("Email sent");

        } catch (Exception e) {
            e.printStackTrace();
            Notification.show("Error sending the email", Notification.Type.ERROR_MESSAGE);
        }
    }

    @Override
    public OutputStream receiveUpload(String filename, String mimeType) {
        this.fileName = filename;
        this.mimeType = mimeType;
        return outputStream = new ByteArrayOutputStream();
    }

    @WebServlet(urlPatterns = "/*", name = "ExampleUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = ExampleUI.class, productionMode = false)
    public static class ExampleUIServlet extends VaadinServlet {
    }

}
