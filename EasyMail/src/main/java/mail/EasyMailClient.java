package mail;

import com.google.api.client.util.Base64;
import com.google.api.services.gmail.Gmail;
import util.Zipper;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

public class EasyMailClient {
    private String CLIENT_SECRET_DIR = null;
    private List<String> SCOPES = null;
    private Gmail account = null;
    private String mailFrom = "me";


    /**
     * @param CLIENT_SECRET_DIR : the path to the client secret json file.
     * @param SCOPES : specfying a list of access types for the gmail account.
     */
    public boolean authorizeAccount(String CLIENT_SECRET_DIR, List<String> SCOPES) throws IllegalArgumentException{
        init(CLIENT_SECRET_DIR,SCOPES);
        account = GmailAuthorization.getGmailInstance(CLIENT_SECRET_DIR,SCOPES);
        if(account == null)
            return false;
        return true;
    }

    private void init(String CLIENT_SECRET_DIR, List<String> SCOPES) {
        this.CLIENT_SECRET_DIR = CLIENT_SECRET_DIR;
        this.SCOPES = SCOPES;
    }

    /**
     * @param to : list representing the receivers of the mail
     * @param subject : the subject of the mail
     * @param bodyText : The Description of the mail to be written
     * @param files : Array of files to be emailed.
     *                NOTE: If the files is null no file will be transfered and only the message will be transferred i.e bodyText
     */
    public boolean sendMail(List<String> to, String subject, String bodyText, List<File> files){
        try {
            com.google.api.services.gmail.model.Message message = sendMessage(account,createEmailWithAttachment(to,subject,bodyText,files));
            Zipper.delete();
            if(message!=null && message.toPrettyString().contains("SENT"))
                return true;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return false;
    }
    private MimeMessage createEmailWithAttachment(List<String> to, String subject, String bodyText, List<File> files) throws MessagingException {
        if(to!=null && !to.isEmpty() && !files.isEmpty()){
            Properties properties = new Properties();
            Session session = Session.getDefaultInstance(properties,null);

            MimeMessage mimeMessage = new MimeMessage(session);

            mimeMessage.setFrom(new InternetAddress(mailFrom));
            InternetAddress[] addresses = new InternetAddress[to.size()];
            for (int i = 0; i < to.size(); i++) {
                addresses[i] = new InternetAddress(to.get(i));
            }
            mimeMessage.addRecipients(Message.RecipientType.TO,addresses);
            mimeMessage.setSubject(subject);


            MimeBodyPart mimeBodyPart = new MimeBodyPart();
            mimeBodyPart.setContent(bodyText,"text/plain");

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(mimeBodyPart);

            if(files!=null) {
                try {
                    mimeBodyPart = new MimeBodyPart();
                    mimeBodyPart.setDataHandler(new DataHandler(new FileDataSource(Zipper.zip(files))));
                    mimeBodyPart.setFileName("files.zip");

                    multipart.addBodyPart(mimeBodyPart);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            mimeMessage.setContent(multipart);
            return mimeMessage;
        }
        return null;
    }

    /**
     *
     * @param service the authorized the gmail object to send the mail.
     * @param mimeMessage  the converted message
     * @return the message object as a response after sending the email.
     * @throws IOException
     * @throws MessagingException
     */
    private com.google.api.services.gmail.model.Message sendMessage(Gmail service, MimeMessage mimeMessage) throws IOException, MessagingException {
        if(service!=null && mimeMessage!=null){
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            mimeMessage.writeTo(baos);
            byte[] bytes = baos.toByteArray();
            com.google.api.services.gmail.model.Message message = new com.google.api.services.gmail.model.Message();
            message.setRaw(Base64.encodeBase64URLSafeString(bytes));
            message = service.users().messages().send(mailFrom,message).execute();
            return message;
        }
        return null;
    }
}