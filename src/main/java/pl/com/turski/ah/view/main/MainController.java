package pl.com.turski.ah.view.main;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.controlsfx.dialog.Dialogs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.oxm.XmlMappingException;
import pl.com.turski.ah.core.setting.SettingManager;
import pl.com.turski.ah.model.view.Step;
import pl.com.turski.ah.view.ViewController;
import pl.com.turski.ah.view.about.AboutController;
import pl.com.turski.ah.view.folderChoose.FolderChooseController;
import pl.com.turski.ah.view.galleryCreate.GalleryCreateController;
import pl.com.turski.ah.view.setting.SettingController;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * User: Adam
 */
public class MainController implements ViewController {

    private static final Logger LOG = LoggerFactory.getLogger(MainController.class);

    @FXML
    Node view;
    @FXML
    MenuItem menuAbout;
    @FXML
    Label stepTitle;
    @FXML
    GridPane contentGrid;
    @FXML
    FlowPane actionPanel;
    @FXML
    Button previousButton;
    @FXML
    Button nextButton;
    @FXML
    Button finishButton;

    @Autowired
    private SettingManager settingManager;
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private FolderChooseController folderChooseController;
    @Autowired
    private GalleryCreateController galleryCreateController;

    private Step step;

    public void init() {
        LOG.info("Inicjalizacja głównego kontrolera");
        actionPanel.getChildren().clear();
        contentGrid.getChildren().clear();
        boolean settingInitialized = initSettings();
        if (settingInitialized) {
            actionPanel.getChildren().add(nextButton);
            step = Step.FOLDER_CHOOSE;
            stepTitle.setText(step.getStepTitle());
            contentGrid.add(folderChooseController.getView(), 0, 0);
        }
    }

    private boolean initSettings() {
        try {
            settingManager.loadSettings();
            return true;
        } catch (XmlMappingException e) {
            LOG.error("Wystąpił błąd mapowania pliku ustawień", e);
            Dialogs.create().title("Błąd").message("Wystąpił błąd mapowania pliku ustawień").lightweight().showError();
        } catch (IOException e) {
            LOG.error("Wystąpił błąd IO podczas wczytywania pliku ustawień");
            Dialogs.create().title("Błąd").message("Wystąpił błąd IO podczas wczytywania pliku ustawień").lightweight().showError();
        }
        return false;
    }

    public void menuSettingAction(ActionEvent event) {
        SettingController settingController = (SettingController) applicationContext.getBean("settingController");
        Stage stage = new Stage();
        stage.setTitle("Ustawienia");
        stage.setScene(new Scene((Parent) settingController.getView()));
        stage.initModality(Modality.APPLICATION_MODAL);
        settingController.initSetting();
        stage.showAndWait();
    }

    public void menuCloseAction(ActionEvent event) {
        Platform.exit();
    }

    public void menuAboutAction(ActionEvent event) {
        AboutController aboutController = (AboutController) applicationContext.getBean("aboutController");
        Stage stage = new Stage();
        stage.setTitle("O programie");
        stage.setScene(new Scene((Parent) aboutController.getView()));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

    @Override
    public Node getView() {
        return view;
    }

    public void previousButtonAction(ActionEvent event) {
        if (step == Step.GALLERY_CREATE) {
            step = Step.FOLDER_CHOOSE;
            contentGrid.getChildren().clear();
            contentGrid.add(folderChooseController.getView(), 0, 0);
            actionPanel.getChildren().clear();
            actionPanel.getChildren().add(nextButton);
            stepTitle.setText(step.getStepTitle());
        } else if (step == Step.SEND_TO_FTP) {
            step = Step.GALLERY_CREATE;
            contentGrid.getChildren().clear();
            //contentGrid.add(folderChooseController.getView(), 0, 0);
            actionPanel.getChildren().clear();
            actionPanel.getChildren().add(previousButton);
            actionPanel.getChildren().add(nextButton);
            stepTitle.setText(step.getStepTitle());
        }

    }

    public void nextButtonAction(ActionEvent event) {
        if (step == Step.FOLDER_CHOOSE) {
            List<File> images = folderChooseController.getImages();
            File rootFolder = folderChooseController.getRootDirectory();
            if (images == null) {
                Dialogs.create().title("Błąd").message("Nie wybrałeś żadnego folderu").lightweight().showError();
            } else if (images.isEmpty()) {
                Dialogs.create().title("Błąd").message("Wybrany folder nie zawiera żadnych zdjęć").lightweight().showError();
            } else {
                step = Step.GALLERY_CREATE;
                stepTitle.setText(step.getStepTitle());
                galleryCreateController.initController(rootFolder, images);
                contentGrid.getChildren().clear();
                contentGrid.add(galleryCreateController.getView(), 0, 0);
                actionPanel.getChildren().clear();
                actionPanel.getChildren().add(previousButton);
                actionPanel.getChildren().add(nextButton);
            }
        }

    }

    public void finishButtonAction(ActionEvent event) {
        folderChooseController.resetView();
        galleryCreateController.resetView();
    }


}
