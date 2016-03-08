package com.example.javamail;

import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.EnableVaadin;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.server.SpringVaadinServlet;
import com.vaadin.ui.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.ContextLoaderListener;

import javax.servlet.annotation.WebListener;
import javax.servlet.annotation.WebServlet;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author alejandro@vaadin.com
 **/
@SpringUI
@Theme("valo")
public class ExampleUI extends UI implements Upload.Receiver {

    @Autowired
    private SpringEmailService springEmailService;

    private ByteArrayOutputStream outputStream;
    private String mimeType;
    private String fileName;

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        // the text field where users will specify their email address
        TextField textField = new TextField("Your email:");

        // an upload component to select the file to be attached
        Upload upload = new Upload("Attachment", this);
        upload.setImmediate(true);

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

            springEmailService.send(from, to, subject, text, inputStream, fileName, mimeType);

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

    @WebServlet(value = "/*", asyncSupported = true)
    public static class Servlet extends SpringVaadinServlet {
    }

    @WebListener
    public static class MyContextLoaderListener extends ContextLoaderListener {
    }

    @Configuration
    @EnableVaadin
    public static class MyConfiguration {
    }

}
