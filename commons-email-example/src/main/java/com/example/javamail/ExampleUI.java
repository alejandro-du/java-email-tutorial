package com.example.javamail;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Receiver;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.router.Route;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author alejandro@vaadin.com
 **/
@Route("")
public class ExampleUI extends VerticalLayout implements Receiver {

    private ByteArrayOutputStream outputStream;
    private String mimeType;
    private String fileName;

    public ExampleUI() {
        // the text field where users will specify their email address
        TextField textField = new TextField("Your email:");

        Span span = new Span("Attachment (required):");

        // an upload component to select the file to be attached
        Upload upload = new Upload(this);

        // a button with a click listener that sends the email
        Button button = new Button("Send me the file", e -> {
            ValidationResult result = new EmailValidator("Invalid email address").apply(textField.getValue(), new ValueContext());
            if (result.isError()) {
                textField.setErrorMessage(result.getErrorMessage());
                textField.setInvalid(true);
            } else {
                textField.setInvalid(false);
                sendEmail(textField.getValue());
            }

        });

        // add the previous components to the VerticalLayout
        add(textField, span, upload, button);
    }

    private void sendEmail(String to) {
        try {
            // all values as variables to clarify its usage
            InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
            String from = "sender@test.com";
            String subject = "Your file";
            String text = "Here there is your <b>file</b>!";

            // call the email service to send the message
            CommonsEmailService.send(from, to, subject, text, inputStream, fileName, mimeType);

            inputStream.close();

            Notification.show("Email sent");

        } catch (Exception e) {
            e.printStackTrace();
            Notification.show("Error sending the email");
        }
    }

    @Override
    public OutputStream receiveUpload(String filename, String mimeType) {
        this.fileName = filename;
        this.mimeType = mimeType;
        return outputStream = new ByteArrayOutputStream();
    }

}
