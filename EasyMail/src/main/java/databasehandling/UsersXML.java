package databasehandling;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UsersXML {
    /**
     * This class uses the library for xml parsing and creating xml elements to store the emails.
     * Library : DOM.
     */
    private static DocumentBuilderFactory documentBuilderFactory = null;
    private static DocumentBuilder documentBuilder = null;
    private static Document document = null;
    private static File file = null;
    private static TransformerFactory transformerFactory = null;
    private static Transformer transformer = null;
    static {
        try {
            /**
             * this static block initializes all the required elements for xml creation and parsing, files, etc.
             */
            file = new File("users.xml");
            documentBuilderFactory = DocumentBuilderFactory.newInstance();

            documentBuilder = documentBuilderFactory.newDocumentBuilder();
            if(file.length() <= 10L){
                document = documentBuilder.newDocument();
                Element element = document.createElement("emails");
                Element dummy = document.createElement("email");
                element.appendChild(dummy);
                document.appendChild(element);
                element.removeChild(dummy);
                saveXML();
            }else{
                document = documentBuilder.parse(file);
            }
            init();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }
    /**
     * this method initializes the xml and sets the id attribute as XML's id attribute for all the elements present in the
     * xml file.
     */
    private static void init() {
        NodeList emails = document.getElementsByTagName("email");
        for (int i = 0; i < emails.getLength(); i++) {
            Element email = (Element) emails.item(i);
            email.setIdAttribute("id",true);
        }
    }

    /**
     * Saves the changes into the associated xml file.
     * @throws TransformerException
     */
    private static void saveXML() throws TransformerException{
        transformerFactory = TransformerFactory.newInstance();
        transformer = transformerFactory.newTransformer();
        DOMSource domSource = new DOMSource(document);
        StreamResult streamResult = new StreamResult(file);
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING,"utf-8");
        transformer.setOutputProperty(OutputKeys.STANDALONE,"no");
        transformer.transform(domSource,streamResult);
    }

    /**
     *
     * @param email the email want to be added to the database (users.xml)
     * @return true - if the insertion is successful
     * @throws TransformerException
     */
    public static boolean insert(String email) throws TransformerException{
        if(!isEmailPresent(email)){
            Element element = document.getDocumentElement();
            System.out.println(element.getNodeName());
            if(element!=null && element.getNodeName().equals("emails")){
                Element emailInXML = document.createElement("email");
                emailInXML.setAttribute("id",getAllEmails().size()+1+"");
                emailInXML.setIdAttribute("id",true);
                emailInXML.setTextContent(email);
                element.appendChild(emailInXML);
                saveXML();
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @param email : email to be deleted.
     * @return true if this operation in successful
     * @throws TransformerException
     */
    public static boolean delete(String email) throws TransformerException{
        if(isEmailPresent(email)){
            document.getDocumentElement().removeChild(document.getElementById(getIdForEmail(email)+ ""));
            saveXML();
            return true;
        }
        return false;
    }

    /**
     *
     * @param email : the email address for which the associated id is to be fetched
     * @return the associated id with the provided email address. -1 if the email is not present
     */
    public static int getIdForEmail(String email){
        int id = -1;
        if(email!=null){
            NodeList nodeList = document.getElementsByTagName("email");
            for (int i = 0; i < nodeList.getLength(); i++) {
                if(nodeList.item(i).getNodeType() == Element.ELEMENT_NODE){
                    Element emailInXML = (Element) nodeList.item(i);
                    if(emailInXML!=null && email.equals(emailInXML.getTextContent())){
                        id =  Integer.parseInt(emailInXML.getAttribute("id"));
                    }
                }
            }
        }
        return id;
    }

    /**
     *
     * @param preEmail the previous email
     * @param newEmail new email
     * @return true if the update operation was successful
     * @throws TransformerException
     */
    public static boolean update(String preEmail, String newEmail) throws TransformerException {
        if(isEmailPresent(preEmail) && !isEmailPresent(newEmail)){
            Element element = document.getElementById(getIdForEmail(preEmail) + "");
            if(element!=null){
                element.setTextContent(newEmail);
            }
            saveXML();
            return true;
        }
        return false;
    }

    /**
     *
     * @param email the email that should be checked if present or not
     * @return true if the provided email is present in the database
     */
    public static boolean isEmailPresent(String email){
        if(document.getElementById(getIdForEmail(email) + "")!=null){
            return true;
        }
        return false;
    }

    /**
     *
     * @return all the emails present in the database
     */
    public static List<String> getAllEmails(){
        List<String> emails = new ArrayList<>();
        NodeList nodeList = document.getElementsByTagName("email");
        for (int i = 0; i < nodeList.getLength(); i++) {
            if(nodeList.item(i).getNodeType() == Element.ELEMENT_NODE){
                Element emailInXML = (Element) nodeList.item(i);
                if(emailInXML!=null){
                    emails.add(emailInXML.getTextContent());
                }
            }
        }
        return emails;
    }

    /**
     *
     * @param id the id of the email to be retrieved
     * @return the associated email address with this email.
     */
    public static String getEmail(int id){
        if(id>-1){
            Element element = document.getElementById(id+"");
            System.out.println(element + "get email");
            if(element!=null)
                return element.getTextContent();
        }
        return null;
    }
}
