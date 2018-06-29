package databasehandling;

import javax.xml.transform.TransformerException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Users {
    /**
     * This class acts as the intermediate class for adding the groups to the database which is maintained in the users.xml
     * file. This class handles all the exceptions thrown by the UsersXML class.
     */

    private List<String> emails = null;

    public Users(){
        emails = new ArrayList<>();
        updateEmailList();
    }

    /**
     *
     * @return all the emails present in the database.
     */
    public List<String> getAllEmails() {
        return Collections.unmodifiableList(emails);
    }

    /**
     * This method updates (syncs) the emails with the database (users.xml)
     */
    private void updateEmailList() {
        this.emails = UsersXML.getAllEmails();
    }

    /**
     *
     * @param email : email to be added to the database
     * @return true - if the email was added successfully
     */
    public boolean add(String email){
        if(email!=null && !isEmailPresent(email)){
            emails.add(email);
            try {
                return UsersXML.insert(email);
            } catch (TransformerException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     *
     * @param email : the email to be deleted.
     * @return true if the email was deleted.
     */
    public boolean delete(String email){
        if(email!=null && isEmailPresent(email)){
            try {
                emails.remove(email);
                return UsersXML.delete(email);
            } catch (TransformerException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     *
     * @param preEmail: the email which is already present in the database
     * @param newEmail: the new email to be placed in place of the preEmail
     * @return true if the uodation was successful
     */
    public boolean update(String preEmail, String newEmail){
        if(preEmail!=null && newEmail!=null && isEmailPresent(preEmail) && !isEmailPresent(newEmail)){
            try {
                return UsersXML.update(preEmail, newEmail);
            }catch (TransformerException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     *
     * @param email the email that should be checked if present or not
     * @return true if the provided email is present in the database
     */
    public boolean isEmailPresent(String email) {
        return UsersXML.isEmailPresent(email);
    }


}
