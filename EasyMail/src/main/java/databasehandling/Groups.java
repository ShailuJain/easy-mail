package databasehandling;

import ui.Group;

import javax.xml.transform.TransformerException;
import java.util.ArrayList;
import java.util.List;

public class Groups {
    /**
     * This class acts as the intermediate class for adding the groups to the database which is maintained in the groups.xml
     * file. This class handles all the exceptions thrown by the GroupsXML class.
     */

    /**
     *
     * @param groupName: Checks if the group is already added in the groups present.
     * @return true - if the groupName provided is already added false otherwise.
     */
    public boolean isGroupPresent(String groupName){
        return GroupsXML.hasGroup(groupName);
    }

    /**
     *
     * @param group: Add the specified group to the database (groups.xml)
     * @return true- if the group was successfully added.
     */

    public boolean add(Group group) {
        try {
            if(group!=null && group.getName()!=null && group.getEmails()!=null) {
                return GroupsXML.insert(group.getName(), convertToUserIds(group.getEmails()));
            }
        } catch (TransformerException e) {
            e.printStackTrace();
        }
        return false;
    }

//    public void addEmailToGroup(Group group, String email){
//        if(group!=null && email!=null &&!email.equals("")){
//            List<String> updatedList = group.getEmails();
//            updatedList.add(email);
//            try {
//                GroupsTable.update(group.getName(), convertToUserIds(updatedList));
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//    public void deleteEmailFromGroup(Group group, String email){
//        try {
//            if(group!=null && email!=null &&!email.equals("") && GroupsTable.isEmailPresentInGroup(group.getName(),email)){
//                List<String> updatedList = group.getEmails();
//                updatedList.remove(email);
//                GroupsTable.update(group.getName(), convertToUserIds(updatedList));
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }

    /**
     *
     * @param prevGroupName: The group name that was already present in the database.
     * @param group: The new group that should be added in place of the previous group
     * @return true- if the update was successfully performed.
     */
    public boolean update(String prevGroupName, Group group){
        if(group!=null && group.getName()!=null && group.getEmails()!=null){
            try {
                return GroupsXML.update(prevGroupName,group.getName(),convertToUserIds(group.getEmails()));
            } catch (TransformerException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     *
     * @param groupName: represents the group that want to be deleted.
     * @return true - if the delete operation was completed.
     */
    public boolean deleteGroup(String groupName){
        try {
            if(groupName!=null && GroupsXML.hasGroup(groupName)){
                return GroupsXML.delete(groupName);
            }
        } catch (TransformerException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     *
     * @return returns all the groups present in the database (groups.xml)
     */
    public List<Group> getAllGroups(){
        return GroupsXML.getAllGroups();
    }

    /**
     *
     * @param groupName: The name of the group from which the emails has to be retrieved
     * @return List of Strings where each string will contain an email in it.
     */
    public List<String> getAllEmailsInGroup(String groupName){
        return GroupsXML.getAllEmailsInGroup(groupName);
    }

    /**
     *
     * @param selectedEmails: the emails of which the id is to be retrieved.
     * @return The list of integers representing the respective id's of the emails in selectedEmails.
     */
    private List<Integer> convertToUserIds(List<String> selectedEmails) {
        List<Integer> userIds = new ArrayList<>();
        for (String email: selectedEmails) {
            userIds.add(UsersXML.getIdForEmail(email));
        }
        return userIds;
    }
}
