package com.example.javamail;

import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.EnableVaadin;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.server.SpringVaadinServlet;
import com.vaadin.ui.*;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.ContextLoaderListener;

import javax.servlet.annotation.WebListener;
import javax.servlet.annotation.WebServlet;
import java.io.InputStream;

/**
 * @author alejandro@vaadin.com
 **/
@SpringUI
@Theme("valo")
public class ExampleUI extends UI {

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        // the text field where users will specify their email address
        TextField textField = new TextField("Your email:");

        // a button with a click listener that sends the email
        Button button = new Button("Send me the PDF", e -> sendEmail(textField.getValue()));

        // a layout containing the previous components
        VerticalLayout layout = new VerticalLayout(textField, button);
        layout.setMargin(true);
        layout.setSpacing(true);
        setContent(layout); // sets the content for this UI
    }

    private void sendEmail(String to) {
        try {
            // all values as variables to clarify its usage
            InputStream inputStream = getClass().getResourceAsStream("/file.pdf");
            String from = "sender@test.com";
            String subject = "Your PDF";
            String text = "Here there is your <b>PDF</b> file!";
            String fileName = "file.pdf";
            String mimeType = "application/pdf";

            SpringEmailService.send(from, to, subject, text, inputStream, fileName, mimeType);

            inputStream.close();

            Notification.show("Email sent");

        } catch (Exception e) {
            e.printStackTrace();
            Notification.show("Error sending the email", Notification.Type.ERROR_MESSAGE);
        }
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
