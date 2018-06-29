package mail;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.List;

public class GmailAuthorization {
    /**
     * This class uses the gmail api for transferring the mails.
     */

    private static Gmail gmail = null;
    private static boolean isGmailCreated = false;

    //    Application Name for gmail access
    private static String APPLICATION_NAME = "EasyMail";


    private static JacksonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static List<String> SCOPES = null;
    private static String CLIENT_SECRET_DIR = null;

    // Directory to store user credentials.
    private static final String CREDENTIALS_FOLDER = "credentials";

    private static NetHttpTransport HTTP_TRANSPORT = null;

    /**
     * @param SCOPES : specfying a list of access types for the gmail account.
     * @param CLIENT_SECRET_DIR : the name for the file which is client secret (json file)
     * Scopes for accessing gmail & client secret dir i.e the client secret json file provided by the gmail OAuth api.
     * if the method creates a gmail instance with thw scope and file specified it won't create new instance for sub-sequent calls to this method
     */

    public static Gmail getGmailInstance(String CLIENT_SECRET_DIR ,List<String> SCOPES){
        if(SCOPES == null && CLIENT_SECRET_DIR == null){
            return null;
        }
        if(isGmailCreated)
            return gmail;
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            init(SCOPES, CLIENT_SECRET_DIR);
            gmail = createGmail(HTTP_TRANSPORT,getCredentials(HTTP_TRANSPORT));
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return gmail;
    }

    private static void init(List<String> scopes, String client_secret_dir) {
        SCOPES = scopes;
        CLIENT_SECRET_DIR = client_secret_dir;
    }

    /**
     *
     * @param httpTransport the transport object used by the google (gmail api)
     * @return the Credential object which represents the credentials of the user. It is used by the gmail api to send mails
     *          with the help of these credentials.
     * @throws IOException
     */
    private static Credential getCredentials(final NetHttpTransport httpTransport) throws IOException {
        InputStream in = GmailAuthorization.class.getResourceAsStream("/client_secret.json");
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        //build flow and trigger  user authorization request
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport,JSON_FACTORY,clientSecrets,SCOPES).setDataStoreFactory(new FileDataStoreFactory(new File("credentials"))).setAccessType("offline").build();
        return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
    }

    /**
     * @param httpTransport : the network http transport
     * @param credentials : valid credentials to access the gmail account.
     */
    private static Gmail createGmail(HttpTransport httpTransport, Credential credentials){
        return gmail = new Gmail.Builder(httpTransport, JacksonFactory.getDefaultInstance(), credentials).setApplicationName(APPLICATION_NAME).build();
    }
}
