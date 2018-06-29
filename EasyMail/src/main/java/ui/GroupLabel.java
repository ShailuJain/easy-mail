package ui;

import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.text.TextAlignment;

import java.io.File;
import java.util.List;

public class GroupLabel extends Label {
    /**
     * This class represents the label of a group to be shown on the screen which allows the user to drag files on the label
     */
    private Group group = null;
    private static Alert alert = null;
    private GroupLabel(Group group){
        super(group.getName());
        this.group = group;
        this.alert = new Alert(Alert.AlertType.INFORMATION);
        this.setGraphic(new ImageView(new Image(GroupLabel.class.getResourceAsStream("/images/group.png"))));
        this.setTextAlignment(TextAlignment.CENTER);
        this.setContentDisplay(ContentDisplay.TOP);
        this.setPadding(new Insets(10,10,10,10));
    }
    /**
     *
     * @param files : The list of files to be sent to the user email that is associated with this label
     * @return true if the files were sent successfully to the users present in the group
     */
    public boolean sendFilesToMyEmails(List<File> files){
        StringBuilder fileNames = new StringBuilder();
        for (File file: files) {
            fileNames.append(file.getName()+"\n");
        }
        return Controller.easyMailClient.sendMail(group.getEmails(),"Files",fileNames.toString(),files);
    }

    /**
     * Factory method to create and initialize the EmailLabel object with the required listeners. For enabling the drag and drop feature.
     * @param group : GroupLabel object is created with this group.
     * @return GroupLabel object which is created and initialized with the required listeners.
     */
    public static GroupLabel createAndInitGroupLabel(Group group) {
        GroupLabel groupLabel = new GroupLabel(group);
        groupLabel.setOnDragOver(event -> {
            if (event.getGestureSource() != groupLabel && event.getDragboard().hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY);
            }
            event.consume();
        });

        groupLabel.setOnDragEntered(event -> {
            GroupLabel dropGroupLabel = (GroupLabel)event.getSource();
            dropGroupLabel.setGraphic(new ImageView(new Image(EmailLabel.class.getResourceAsStream("/images/share.png"))));
        });


        groupLabel.setOnDragExited(event -> {
            GroupLabel dropGroupLabel = (GroupLabel)event.getSource();
            dropGroupLabel.setGraphic(new ImageView(new Image(EmailLabel.class.getResourceAsStream("/images/group.png"))));
        });

        groupLabel.setOnDragDropped(event -> {
            Dragboard dragboard = event.getDragboard();
            if(dragboard.hasFiles()){
                List<File> files = dragboard.getFiles();
                if(groupLabel.sendFilesToMyEmails(files)){
                    alert = new Alert(Alert.AlertType.INFORMATION,"Files Sent!",ButtonType.OK);
                    alert.setHeaderText(null);
                    alert.show();
                    return;
                }
                new Alert(Alert.AlertType.ERROR,"Error in sending files!",ButtonType.CLOSE);
            }
        });

        return groupLabel;

    }
    public List<String> getEmails(){
        return group.getEmails();
    }
}
