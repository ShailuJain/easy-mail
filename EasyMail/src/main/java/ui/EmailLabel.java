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
import java.util.Collections;
import java.util.List;

public class EmailLabel extends Label {
    /**
     * This class represents the label of an email to be shown on the screen which will allow the user to drag files on the label
     */
    private static Alert alert = null;

    /**
     *
     * @param email:  email to be setted on the label
     */
    private EmailLabel(String email){
        super(email,new ImageView(new Image(EmailLabel.class.getResourceAsStream("/images/user1.png"))));
        setTextAlignment(TextAlignment.CENTER);
        setContentDisplay(ContentDisplay.TOP);
        setPadding(new Insets(10,10,10,10));
    }

    /**
     *
     * @param files : The list of files to be sent to the user email that is associated with this label
     * @return true if the files were sent successfully to the user email
     */
    public boolean sendFilesToMyEmail(List<File> files){
        StringBuilder fileNames = new StringBuilder();
        for (File file: files) {
            fileNames.append(file.getName()+"\n");
        }
        return Controller.easyMailClient.sendMail(Collections.singletonList(getText()),"Files",fileNames.toString(),files);
    }

    /**
     * Factory method to create and initialize the EmailLabel object with the required listeners. For enabling the drag and drop feature.
     * @param email : EmailLabel object is created with this email.
     * @return EmailLabel object which is created and initialied with the required listeners.
     */
    public static EmailLabel createAndInitEmailLabel(String email) {
        EmailLabel emailLabel = new EmailLabel(email);

        emailLabel.setOnDragOver(event ->  {
            if(event.getGestureSource() != emailLabel && event.getDragboard().hasFiles()){
                event.acceptTransferModes(TransferMode.COPY);
            }
            event.consume();
        });

        emailLabel.setOnDragEntered(event -> {
            EmailLabel dropEmailLabel = (EmailLabel)event.getSource();
            dropEmailLabel.setGraphic(new ImageView(new Image(EmailLabel.class.getResourceAsStream("/images/share.png"))));
        });


        emailLabel.setOnDragExited(event -> {
            EmailLabel dropEmailLabel = (EmailLabel)event.getSource();
            dropEmailLabel.setGraphic(new ImageView(new Image(EmailLabel.class.getResourceAsStream("/images/user1.png"))));
        });

        emailLabel.setOnDragDropped(event -> {
            Dragboard dragboard = event.getDragboard();
            if(dragboard.hasFiles()){
                List<File> files = dragboard.getFiles();
                if(emailLabel.sendFilesToMyEmail(files)){
                    alert = new Alert(Alert.AlertType.INFORMATION,"Files Sent!",ButtonType.OK);
                    alert.setHeaderText(null);
                    alert.show();
                    return;
                }
                new Alert(Alert.AlertType.ERROR,"Error in sending files!",ButtonType.CLOSE);
            }
        });

        return emailLabel;

    }

}
