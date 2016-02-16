package com.example.javamail;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.*;

import javax.servlet.annotation.WebServlet;
import java.io.InputStream;

/**
 * @author alejandro@vaadin.com
 **/
@Theme("valo")
public class ExampleUI extends UI {

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        // the text field where users will specify their email address
        TextField textField = new TextField("Your email:");
        textField.addValidator(new EmailValidator("Invalid email address"));

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

            // call the email service to send the message
            CommonsEmailService.send(from, to, subject, text, inputStream, fileName, mimeType);

            inputStream.close();

            Notification.show("Email sent");

        } catch (Exception e) {
            e.printStackTrace();
            Notification.show("Error sending the email", Notification.Type.ERROR_MESSAGE);
        }
    }

    @WebServlet(urlPatterns = "/*", name = "ExampleUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = ExampleUI.class, productionMode = false)
    public static class ExampleUIServlet extends VaadinServlet {
    }

}
