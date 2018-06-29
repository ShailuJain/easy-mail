package ui;

import com.google.api.services.gmail.GmailScopes;
import constants.FileConstants;
import constants.RegexConstants;
import databasehandling.Groups;
import databasehandling.Users;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import mail.EasyMailClient;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static ui.EmailLabel.createAndInitEmailLabel;

public class Controller {
    private boolean isEmailValid = false;
    static EasyMailClient easyMailClient = new EasyMailClient();
    private Users users = null;
    private Groups groups = null;
    private GroupAlert groupAlert = null;
    private EmailLabel selectedEmailLabel = null;
    private GroupLabel selectedGroupLabel = null;
    @FXML
    private Button btnDeleteUser;

    @FXML
    private Button btnUpdateUser;

    @FXML
    private Button btnDeleteGroup;

    @FXML
    private Button btnUpdateGroup;

    @FXML
    public void initialize(){
        users = new Users();
        groups = new Groups();
        btnDeleteUser.setDisable(true);
        btnUpdateUser.setDisable(true);
        btnDeleteGroup.setDisable(true);
        btnUpdateGroup.setDisable(true);
        easyMailClient.authorizeAccount(FileConstants.CLIENT_SECRET_DIR,Collections.singletonList(GmailScopes.GMAIL_COMPOSE));
        updateFlowPaneLabels();
    }

    @FXML
    private FlowPane userFlowPane;

    @FXML
    private FlowPane groupFlowPane;

    @FXML
    private void handleAddUser(ActionEvent ae){
        String email = takeEmailInput(null);
        if(email != null && !users.isEmailPresent(email)){
            EmailLabel emailLabel = createAndInitEmailLabel(email);
            emailLabel.setOnMouseClicked(this::emailLabelClicked);
            if(users.add(email)) {
                userFlowPane.getChildren().add(emailLabel);
                new Alert(Alert.AlertType.INFORMATION,"Successful!",ButtonType.OK).show();
            }
            else{
                new Alert(Alert.AlertType.ERROR,"Some Error Occurred",ButtonType.OK).show();
            }
        }else{
            new Alert(Alert.AlertType.ERROR,"Not valid or Email already present!",ButtonType.OK).show();
        }
    }

    @FXML
    private void handleDeleteUser(ActionEvent ae){
        if(users.delete(selectedEmailLabel.getText())) {
            userFlowPane.getChildren().remove(selectedEmailLabel);
            new Alert(Alert.AlertType.INFORMATION,"Successful!",ButtonType.OK).show();
        }
        else
            new Alert(Alert.AlertType.ERROR,"Some Error Occurred",ButtonType.OK).show();
    }

    @FXML
    private void handleUpdateUser(ActionEvent ae){
        String oldEmail = selectedEmailLabel.getText();
        String updatedEmail = takeEmailInput(oldEmail);
        if(updatedEmail!=null && !updatedEmail.isEmpty()){
            if(users.update(oldEmail,updatedEmail)){
                userFlowPane.getChildren().remove(selectedEmailLabel);
                EmailLabel emailLabel = createAndInitEmailLabel(updatedEmail);
                emailLabel.setOnMouseClicked(this::emailLabelClicked);
                userFlowPane.getChildren().add(emailLabel);
                new Alert(Alert.AlertType.INFORMATION,"Successful!",ButtonType.OK).show();
            }else{
                new Alert(Alert.AlertType.ERROR,"Some Error Occurred",ButtonType.OK).show();
            }
        }
    }


    private void emailLabelClicked(MouseEvent mouseEvent) {
        EmailLabel emailLabel = (EmailLabel)mouseEvent.getSource();
        if(selectedEmailLabel!=null){
            selectedEmailLabel.setBorder(null);
        }
        if(selectedEmailLabel!=emailLabel){
            emailLabel.setBorder(new Border(new BorderStroke(Color.BLACK,BorderStrokeStyle.SOLID,new CornerRadii(5),BorderStroke.THIN)));
            selectedEmailLabel = emailLabel;
            btnDeleteUser.setDisable(false);
            btnUpdateUser.setDisable(false);

        }else{
            emailLabel.setBorder(null);
            selectedEmailLabel = null;
            btnDeleteUser.setDisable(true);
            btnUpdateUser.setDisable(true);
        }
    }

    private void groupLabelClicked(MouseEvent mouseEvent) {
        GroupLabel groupLabel = (GroupLabel) mouseEvent.getSource();
        if(selectedGroupLabel!=null){
            selectedGroupLabel.setBorder(null);
        }
        if(selectedGroupLabel!=groupLabel){
            groupLabel.setBorder(new Border(new BorderStroke(Color.BLACK,BorderStrokeStyle.SOLID,new CornerRadii(5),BorderStroke.THIN)));
            selectedGroupLabel = groupLabel;
            btnDeleteGroup.setDisable(false);
            btnUpdateGroup.setDisable(false);

        }else{
            groupLabel.setBorder(null);
            selectedGroupLabel = null;
            btnDeleteGroup.setDisable(true);
            btnUpdateGroup.setDisable(true);
        }
    }

    @FXML
    private void handleAddGroup(ActionEvent ae){
        groupAlert = new GroupAlert(users.getAllEmails());
        List<String> selectedEmails = groupAlert.showAndGetEmails();
        if(selectedEmails!=null && !selectedEmails.isEmpty() && !groups.isGroupPresent(groupAlert.getGroupName())){
            GroupLabel groupLabel = GroupLabel.createAndInitGroupLabel(new Group( groupAlert.getGroupName(),selectedEmails));
            groupLabel.setOnMouseClicked(this::groupLabelClicked);
            if(groups.add(new Group(groupAlert.getGroupName(),selectedEmails))){
                groupFlowPane.getChildren().add(groupLabel);
                new Alert(Alert.AlertType.INFORMATION,"Successful",ButtonType.OK).show();
            }else{
                new Alert(Alert.AlertType.ERROR,"Some Error Occurred",ButtonType.OK).show();
            }
        }else{
            new Alert(Alert.AlertType.ERROR,"Not valid or Group already present!",ButtonType.OK).show();
        }
    }

    @FXML
    private void handleDeleteGroup(ActionEvent ae){
        if(groups.deleteGroup(selectedGroupLabel.getText())){
            groupFlowPane.getChildren().remove(selectedGroupLabel);
            new Alert(Alert.AlertType.INFORMATION,"Successful",ButtonType.OK).show();
        }else{
            new Alert(Alert.AlertType.ERROR,"Some Error Occurred",ButtonType.OK).show();
        }
    }

    @FXML
    private void handleUpdateGroup(ActionEvent ae){
        GroupAlert groupAlert = new GroupAlert(users.getAllEmails(), groups.getAllEmailsInGroup(selectedGroupLabel.getText()));
        groupAlert.setGroupNameText(selectedGroupLabel.getText());
        List<String> updatedEmails = groupAlert.showAndGetEmails();
        if(updatedEmails!=null && !updatedEmails.isEmpty()){
            if(groups.update(selectedGroupLabel.getText(),new Group(groupAlert.getGroupName(),updatedEmails))){
                selectedGroupLabel.setText(groupAlert.getGroupName());
                new Alert(Alert.AlertType.INFORMATION,"Updation successful",ButtonType.OK).show();
            }else{
                new Alert(Alert.AlertType.ERROR,"Some Error Occurred",ButtonType.OK).show();
            }
        }else{
            new Alert(Alert.AlertType.ERROR,"Some Error Occurred",ButtonType.OK).show();
        }
    }


    private String takeEmailInput(String defaultValue){
        TextInputDialog emailDialog = null;
        if(defaultValue!=null)
            emailDialog = new TextInputDialog(defaultValue);
        else
            emailDialog = new TextInputDialog();
        emailDialog.setHeaderText("Enter a email : ");
        emailDialog.getEditor().setOnKeyPressed(event -> {
            TextField textField = (TextField) event.getSource();
            if(!textField.getText().matches(RegexConstants.EMAIL)){
                textField.setStyle("-fx-text-fill: red");
                isEmailValid = false;
            }else{
                textField.setStyle("-fx-text-fill: green");
                isEmailValid = true;
            }
        });
        Optional<String> data = emailDialog.showAndWait();
        if(data!=null && data.isPresent() && isEmailValid){
            return data.get();
        }
        return null;
    }


    public void updateFlowPaneLabels(){
        for (String email : users.getAllEmails()) {
            EmailLabel emailLabel = createAndInitEmailLabel(email);
            emailLabel.setOnMouseClicked(this::emailLabelClicked);
            userFlowPane.getChildren().add(emailLabel);
        }
        for (Group group:groups.getAllGroups()) {
            GroupLabel groupLabel = GroupLabel.createAndInitGroupLabel(group);
            groupLabel.setOnMouseClicked(this::groupLabelClicked);
            groupFlowPane.getChildren().add(groupLabel);
        }
    }
}