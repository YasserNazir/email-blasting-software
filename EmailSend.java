package MailProject;

/**
 * The program implements a script that:
 * 1. Reads the .csv Data Files and stores the data into arrays
 * 2. Sends the email and Attachment
 *
 * Using the Java Mail API, more documentation can be found here: https://javaee.github.io/javamail/
 *
 * @version 1.0
 * @date   08 Apr 2020
 */

import com.sun.mail.util.MailSSLSocketFactory;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.io.*;
import java.nio.file.Files;
import java.security.GeneralSecurityException;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import javax.activation.*;
import javax.mail.*;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.*;
import javax.mail.internet.MimeMessage;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;

public class EmailSend {

    public static void main(String[] args) throws IOException {

        Instant start = Instant.now();
        System.setProperty( "java.util.logging.SimpleFormatter.format", "%1$tF %1$tT - %4$s: %5$s%6$s%n" ); // Formats the date into simple Format
        Logger logger = Logger.getLogger( "EmailSend" ); // Declares the logger function

        // Loads the property file from: @FileInputStream: PropertyFileDirectory\FileName.properties
        Properties prop = new Properties();
        FileInputStream input = null;

        try {
            input = new FileInputStream( "/Users/yasserkarimo/Desktop/Email_Project/Properties/Email_Properties_config.properties" );
            prop.load( input );


            /**
             * Function assigns File Handler to create new log file on Directory with the name:
             * LogDirectory - yyyy.mm.dd - hhmmss.log
             *
             * @LOG_DIRECTORY path is found on the Property files
             */
            FileHandler fh;
            try {
                fh = new FileHandler( prop.getProperty( "LOG_DIRECTORY" ) + DateFormatUtils.format( new Date(), "yyyy.MM.dd - HHmmss" ) + prop.getProperty( "LOG_TYPE" ) );
                logger.addHandler( fh );
                SimpleFormatter formatter = new SimpleFormatter();
                fh.setFormatter( formatter );
            } catch (SecurityException e1) {
                e1.printStackTrace();
                logger.severe( e1.toString() );
            } catch (IOException e2) {
                e2.printStackTrace();
                logger.severe( e2.toString() );
            }

            //Email Credentials
            String from = prop.getProperty( "EMAIL" );
            String pass = prop.getProperty( "PASS" );

            logger.info( "Email sent from: " + from );
            logger.info( " " );
            logger.info( "**SENDING EMAIL**" );

            //Connect to csv file
            File file = new File( prop.getProperty( "DATASET" ) );
            List<String> lines = Files.readAllLines( file.toPath() );
            for (String line : lines) {

                // Array of addresses to be sent
                String[] emailaddress = line.split( "," );
                String[] recipientname = line.split( "," );
                String[] subjectmessage = line.split( "," );
                String[] attachmentpath = line.split( "," );
                String[] messagetext = line.split(",");
                String[] attachmentname = line.split( "," );

                String to[] = {emailaddress[0]};

                //server-host configurations
                Properties props = System.getProperties();
                MailSSLSocketFactory sf = new MailSSLSocketFactory();
                sf.setTrustAllHosts( true );
                String host = prop.getProperty( "HOST" );
                String port = prop.getProperty( "PORT" );
                props.put( "mail.smtp.starttls.enable", "true" );
                props.put( "mail.smtp.ssl.trust", "*" ); //Allows Properties to enable ssl, disable authentication, and trust any host
                props.put( "mail.smtp.starttls.enable", "true" ); //Enables SMTP connections over SSL, verifies that the security certificate presented by the server it is connecting to is "trusted" by the client.
                props.put( "mail.smtp.ssl.socketFactory", sf ); //Mail.SSL.SocketFactory avoids the need to add the certificate to your key store
                props.put( "mail.smtp.host", host );
                props.put( "mail.smtp.user", from );
                props.put( "mail.smtp.password", pass );
                props.put( "mail.smtp.port", port );
                props.put( "mail.smtp.auth", "true" );

                Session session = Session.getDefaultInstance( props );
                MimeMessage message = new MimeMessage( session );

                try {
                    message.setFrom( new InternetAddress( from ) );

                    for (int i = 0; i < to.length; i++) {
                        message.addRecipient( Message.RecipientType.TO, new InternetAddress( to[i] ));
                    }

                    message.setSubject( subjectmessage[2] );

                    //The BODY and the embedded image
                    MimeMultipart multipart = new MimeMultipart("related");

                    // The Message Body
                    BodyPart messageBodyPart = new MimeBodyPart();
                    messageBodyPart.setContent(messagetext[3], "text/html");
                    multipart.addBodyPart(messageBodyPart);

                    // Attachment part
                    messageBodyPart = new MimeBodyPart();
                    String filepath = (attachmentpath[4]);
                    String filename = ("" + attachmentname[5]);
                    DataSource source = new FileDataSource(filepath);
                    messageBodyPart.setDataHandler(new DataHandler(source));
                    messageBodyPart.setFileName(filename);
                    multipart.addBodyPart(messageBodyPart);
                    message.setContent(multipart);

                    Transport transport = session.getTransport( "smtp" );
                    transport.connect( host, from, pass );
                    transport.sendMessage( message, message.getAllRecipients() );
                    transport.close();

                    //Sending Message Command-Transport
                    logger.info( "Sending email to " + emailaddress[0] );
                    logger.info( "Recipient Name: " + recipientname[1] );
                    logger.info( "Email Subject: " + subjectmessage[2] );
                    logger.info( "Attachment: " + attachmentpath[4] );
                    logger.info( "Email Sent" );
                    logger.info( " " );

                } catch (AddressException ae) {
                    ae.printStackTrace();
                    logger.severe( ae.toString() );
                } catch (MessagingException me) {
                    me.printStackTrace();
                    logger.severe( me.toString() );
                }
            }
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
            logger.severe(e.toString());}

        //ELAPSED TIME
        TimeUnit.SECONDS.toString();
        Instant end = Instant.now();
        Duration interval = Duration.between( start, end );

        logger.info( "Elapsed Time: " + interval.getSeconds() + " Seconds " );
        logger.info( "**PROGRAM END**" );
    }
}