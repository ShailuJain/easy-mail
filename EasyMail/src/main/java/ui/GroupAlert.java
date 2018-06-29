package ui;

import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GroupAlert extends Alert {
    /**
     * This class is a Alert specifically made for the creation of the group.
     * It helps the user in forming the Group and send mails.
     */
    private ListView<CheckBox> listView = null;
    private BorderPane borderPane = null;
    private TextField groupName =  null;
    private ButtonType add = null;
    private CheckBox selectAll = null;
    private boolean flag = false;

    /**
     *
     * @param groupNameText : The name to be set on the alert textbox.
     */
    public void setGroupNameText(String groupNameText){
        groupName.setText(groupNameText);
    }

    /**
     * Initializes the object with the emails
     * @param emails: The list of all available emails in the database which can form a group.
     */
    public GroupAlert(List<String> emails){
        super(AlertType.NONE);
        setTitle("Select Users");
        init(emails);
    }

    /**
     *
     * @param emails : The list if emails available
     * @param selectedEmails : The default selected emails in the alert box.
     */
    public GroupAlert(List<String> emails, List<String> selectedEmails){
        this(emails);
        for (CheckBox email:listView.getItems()) {
            if(selectedEmails.contains(email.getText())){
                email.setSelected(true);
            }
        }
    }

    private void init(List<String> emails) {
        listView = new ListView<>();
        borderPane = new BorderPane(listView);
        groupName = new TextField();
        add = new ButtonType("Add");
        selectAll = new CheckBox("Select All");
        borderPane.setTop(new HBox(new Label("Enter group name: "),groupName));
        listView.getItems().add(selectAll);
        for (String email : emails) {
            listView.getItems().add(new CheckBox(email));
        }
        this.getDialogPane().setContent(borderPane);
        this.getDialogPane().getButtonTypes().add(add);
        this.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        initListeners();
    }

    private void initListeners() {
        selectAll.setOnAction(event -> {
            flag = !flag;
            for (CheckBox email : this.listView.getItems()) {
                email.setSelected(flag);
            }
        });

    }

    public String getGroupName(){
        return groupName.getText();
    }

    /**
     * SHows tha Alert box and waits for the user to complete the input.
     * @return the list of emails that were selected by the user.
     */
    public List<String> showAndGetEmails() {
        List<String> selectedEmails = null;
        Optional<ButtonType> result = this.showAndWait();
        if(result!=null && result.isPresent()){
            if(result.get() == add){
                selectedEmails = new ArrayList<>();
                listView.getItems().remove(selectAll);
                for (CheckBox emailCheckBox: listView.getItems()) {
                    if(emailCheckBox.isSelected()){
                        selectedEmails.add(emailCheckBox.getText());
                    }
                }
            }
        }
        return selectedEmails;
    }
}
