# Sending email using JavaMail

1. Checkout the project form Git, and build and run with Maven:

```
git clone https://github.com/alejandro-du/java-email-tutorial

cd java-email-tutorial

cd spring-mail-example

mvn clean install

mvn jetty:run
```

2. Download and run [FakeSMTP](https://nilhcem.github.io/FakeSMTP).

3. Configure FakeSMTP listening port to `9090` and start the server.

4. Open [http://localhost:8080](http://localhost:8080) in your browser.
