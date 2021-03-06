package pl.com.turski.ah.view.directoryChoose;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.DirectoryChooser;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import pl.com.turski.ah.view.ViewController;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.List;

/**
 * User: Adam
 */
@Component
public class DirectoryChooseController implements ViewController {

    private static final Logger LOG = LoggerFactory.getLogger(DirectoryChooseController.class);

    @FXML
    Button directoryChooseButton;
    @FXML
    Label directoryPathLabel;
    @FXML
    ListView<String> imagesFileList;
    @FXML
    Node view;

    private List<File> images;
    private File imagesDirectory;

    public void folderChooseButtonAction(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File directory = directoryChooser.showDialog(view.getScene().getWindow());
        if (directory != null) {
            LOG.info("Użytkownik wybrał katalog {}", directory);
            imagesDirectory = directory;
            images = Arrays.asList(directory.listFiles((FileFilter) new SuffixFileFilter(Arrays.asList(".jpg", ".jpeg",".JPG",".JPEG"))));
            directoryPathLabel.setText(directory.getAbsolutePath());
            ObservableList<String> fileList = FXCollections.observableArrayList();
            for (File image : images) {
                fileList.add(image.getName());
            }
            imagesFileList.setItems(fileList);
        }
    }

    public void resetView() {
        images = null;
        imagesDirectory = null;
        imagesFileList.setItems(FXCollections.<String>emptyObservableList());
        directoryPathLabel.setText("");
    }

    public Node getView() {
        return view;
    }

    public List<File> getImages() {
        return images;
    }

    public File getImagesDirectory() {
        return imagesDirectory;
    }
}
