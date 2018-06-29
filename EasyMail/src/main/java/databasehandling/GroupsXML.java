package databasehandling;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import ui.Group;

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

public class GroupsXML {
    /**
     * This class uses the library for xml parsing and creating xml elements to store the groups.
     * Library : DOM.
     */
    private static DocumentBuilderFactory documentBuilderFactory = null;
    private static DocumentBuilder documentBuilder = null;
    private static Document document = null;
    private static File file = null;
    private static TransformerFactory transformerFactory = null;
    private static Transformer transformer = null;
    static {
        /**
         * this static block initializes all the required elements for xml creation and parsing, files, etc.
         */
        try {
            file = new File("groups.xml");
            documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
            if(file.length() <= 10L){
                document = documentBuilder.newDocument();
                Element element = document.createElement("groups");
                Element dummy = document.createElement("group");
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
            e.printStackTrace();e.printStackTrace();
        }
    }

    /**
     * this method initializes the xml and sets the name attribute as XML's id attribute for all the elements present in the
     * xml file.
     */
    private static void init() {
        NodeList groups = document.getElementsByTagName("group");
        for (int i = 0; i < groups.getLength(); i++) {
            Element group = (Element) groups.item(i);
            group.setIdAttribute("name",true);
        }
    }

    /**
     * Saves the changes into the associated xml file.
     * @throws TransformerException
     */
    private static void saveXML() throws TransformerException {
        transformerFactory = TransformerFactory.newInstance();
        transformer = transformerFactory.newTransformer();
        transformer.transform(new DOMSource(document),new StreamResult(file));
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING,"utf-8");
        transformer.setOutputProperty(OutputKeys.STANDALONE,"no");

        transformer.transform(new DOMSource(document), new StreamResult(System.out));
    }

    /**
     *
     * @param groupName : groupName to be inserted
     * @param userIds: The id's of the emails which want to be added to this group
     * @return true - if the insertion is successful
     * @throws TransformerException
     */
    public static boolean insert(String groupName, List<Integer> userIds) throws TransformerException {
        if(groupName!= null && userIds!=null && !hasGroup(groupName)) {
            Element element = document.getDocumentElement();
            if (element!=null && element.getNodeName().equals("groups")){
                Element group = document.createElement("group");
                group.setAttribute("name",groupName);
                group.setIdAttribute("name",true);
                Element emailids = document.createElement("emailids");
                emailids.setTextContent(convertToString(userIds));
                group.appendChild(emailids);
                element.appendChild(group);
                saveXML();
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @param prevGroupName : the previous groupname thai is present in the file.
     * @param newGroupName : the groupname that which will later represent the group.
     * @param updatedList: The updated list of emails that are added or deleted from the group.
     * @return true if the update operation was successful
     * @throws TransformerException
     */
    public static boolean update(String prevGroupName, String newGroupName, List<Integer> updatedList) throws TransformerException {
        if(hasGroup(prevGroupName) && newGroupName!=null && !newGroupName.equals("") && updatedList!=null && !updatedList.isEmpty()){
            Element element = document.getElementById(prevGroupName);
            if (element!=null){
                element.setAttribute("name",newGroupName);
                Element emailids = (Element) element.getFirstChild();
                emailids.setTextContent(convertToString(updatedList));
                saveXML();
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @param groupName: the name of the group that want to be deleted.
     * @return true if this operation in successful
     * @throws TransformerException
     */
    public static boolean delete(String groupName) throws TransformerException {
        if(groupName!=null && hasGroup(groupName)){
            document.getElementById(groupName).getParentNode().removeChild(document.getElementById(groupName));
            saveXML();
            return true;
        }
        return false;
    }

    /**
     * Converts the list of id's into a single string of id's with a space in between id's
     * @param userIds: the list of id's that is to be converted to the string format
     * @return
     */
    private static String convertToString(List<Integer> userIds) {
        String uids = "";
        for (Integer id: userIds) {
            uids += id + " ";
        }
        return uids.trim();
    }

    /**
     * Checks whether the group is already present.
     * @param groupName: the name of the group which is to be checked
     * @return : true - if the group with this groupName is present
     */
    public static boolean hasGroup(String groupName){
        if(groupName!=null){
            if(document.getElementById(groupName)!=null){
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @return All the groups present in the groups.xml
     */
    public static List<Group> getAllGroups(){
        List<Group> groups = new ArrayList<>();
        Element tagGroups = (Element) document.getElementsByTagName("groups").item(0);
        NodeList nodeList = tagGroups.getElementsByTagName("group");
        for (int i = 0; i <nodeList.getLength() ; i++) {
            String groupName = ((Element)nodeList.item(i)).getAttribute("name");
            groups.add(new Group(groupName, getAllEmailsInGroup(groupName)));
        }
        return groups;
    }

    /**
     *
     * @param groupName The name of the group from which the emails has to be retrieved
     * @return List of Strings where each string will contain an email in it.
     */
    public static List<String> getAllEmailsInGroup(String groupName){
        List<String> emails = new ArrayList<>();
        if(groupName!=null){
            Element group = document.getElementById(groupName);
            if(group!=null){
                Element emailids = (Element) group.getFirstChild();
                if(emailids!=null){
                    for (String id: emailids.getTextContent().split(" ")) {
                        emails.add(UsersXML.getEmail(Integer.parseInt(id)));
                    }
                }
            }
        }
        return emails;
    }


//    public static boolean isEmailPresentInGroup(String groupName, String email){
//        if(groupName!=null && email!=null && !groupName.equals("") && !email.equals("")){
//            List<String> emails = getAllEmailsInGroup(groupName);
//            if(emails.contains(email)){
//                return true;
//            }
//            return false;
//        }
//        return true;
//    }
}