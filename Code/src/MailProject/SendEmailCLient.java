package MailProject;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Properties;

    public class SendEmailCLient extends JFrame {
        private JTextField fromField = new JTextField();
//        private JTextField toField = new JTextField();
        private JTextField subjectField = new JTextField();
        private JComboBox<String> mailSmtpHostComboBox = new JComboBox<>();
        private JTextField portField = new JTextField();
        private JTextArea contentTextArea = new JTextArea();

        private SendEmailCLient() {
            InitializeUI();
        }

        public static void main(String[] args) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    SendEmailCLient client = new SendEmailCLient();
                    client.setVisible(true);
                }
            });
        }
        static JTextField t;
        private void InitializeUI() {

            Properties prop = new Properties();
            prop.getProperty( "" );
            FileInputStream input = null;
            try {
                input = new FileInputStream( "/Users/yasserkarimo/Desktop/Email_Project/Properties/Email_Properties_config.properties" );
                prop.load( input );

                File file = new File( prop.getProperty( "DATASET" ) );
                List<String> lines = Files.readAllLines( file.toPath() );
                for (String line : lines) {

                    // Array of addresses to be sent
                    String[] emailaddress = line.split( "," );
                    String[] messagetext = line.split( "," );

                    String to[] = {emailaddress[0]};

                    setTitle( "Send Email" );
                    setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
                    setSize( new Dimension( 600, 380 ) );

                    getContentPane().setLayout( new BorderLayout() );

                    // Header Panel
                    JPanel headerPanel = new JPanel();
                    headerPanel.setLayout( new GridLayout( 6, 2 ) );

                    headerPanel.add( new JLabel( "From:" ) );
                    headerPanel.add( fromField );
                    fromField.setText( prop.getProperty( "EMAIL" ) );

                    headerPanel.add( new JLabel( "STMP Server:" ) );
                    headerPanel.add( mailSmtpHostComboBox );
                    mailSmtpHostComboBox.addItem( "smtp.gmail.com" );

                    headerPanel.add( new JLabel( "Port:" ) );
                    headerPanel.add( portField );
                    portField.setText( prop.getProperty( "PORT" ) );

                    // Body Panel
                    JPanel bodyPanel = new JPanel();
                    bodyPanel.setLayout( new BorderLayout() );
                    bodyPanel.add( new JLabel( "Message:" ), BorderLayout.NORTH );
                    bodyPanel.add( contentTextArea, BorderLayout.CENTER );
                    contentTextArea.setText( "" + messagetext[3] );

                    JPanel footerPanel = new JPanel();
                    footerPanel.setLayout( new BorderLayout() );
                    JButton sendMailButton = new JButton( "Send E-mail" );
//                sendMailButton.addActionListener( new SendEmailActionListener() );

                    footerPanel.add( sendMailButton, BorderLayout.SOUTH );

                    getContentPane().add( headerPanel, BorderLayout.NORTH );
                    getContentPane().add( bodyPanel, BorderLayout.CENTER );
                    getContentPane().add( footerPanel, BorderLayout.SOUTH );

                    class SendEmailActionListener implements ActionListener {
                        SendEmailActionListener() {
                        }

                        @Override
                        public void actionPerformed(ActionEvent e) {
                            Properties props = new Properties();
                            props.put( "mail.smtp.host", mailSmtpHostComboBox.getSelectedItem() );
                            props.put( "mail.transport.protocol", "smtp" );
                            props.put( "mail.smtp.starttls.enable", "true" );
                            props.put( "mail.smtp.auth", "true" );
                            props.put( "mail.smtp.port", "587" );
                            props.put( "mail.smtp.socketFactory.port", "587" );
                            props.put( "mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory" );


                            Session session = Session.getDefaultInstance( props );
                            try {
                                InternetAddress fromAddress = new InternetAddress( fromField.getText() );
                                InternetAddress toAddress = new InternetAddress( emailaddress[0] );

                                Message message = new MimeMessage( session );
                                message.setFrom( fromAddress );
                                message.setRecipient( Message.RecipientType.TO, toAddress );
                                message.setSubject( subjectField.getText() );
                                message.setText( contentTextArea.getText() );

//                    Transport.send(message, usernameField.getText(),
//                            new String(passwordField.getPassword()));
                            } catch (MessagingException ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


