package actions;

import dataprocessors.AppData;
import dataprocessors.TSDProcessor;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.*;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ui.AppUI;
import vilij.components.ActionComponent;
import vilij.components.Dialog;
import vilij.components.ErrorDialog;
//import vilij.components.UIComponent;
import vilij.propertymanager.PropertyManager;
import vilij.templates.ApplicationTemplate;

import javax.imageio.ImageIO;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

import static settings.AppPropertyTypes.*;

/**
 * This is the concrete implementation of the action handlers required by the application.
 *
 * @author Ritwik Banerjee
 */
public final class AppActions implements ActionComponent {

    /**
     * The application to which this class of actions belongs.
     */
    private ApplicationTemplate applicationTemplate;
    private boolean saveSuccess;
    private File file =null;
    /**
     * Path to the data file currently active.
     */
     private Path dataFilePath;

     public Path getDataFilePath(){
         return  dataFilePath;
     }

     public File getfile(){
         return file;
     }

    public AppActions(ApplicationTemplate applicationTemplate) {
        this.applicationTemplate = applicationTemplate;
    }

    @Override
    public void handleNewRequest() {
        if(((AppUI)applicationTemplate.getUIComponent()).getTextArea().getLength()>0) {
            try {
                promptToSave();
            } catch (Exception e) {

            }
        }
    }
    //Omar

    @Override
    public void handleSaveRequest() {
        if (dataFilePath != null) {
            fileExist();
        } else {
            saveTheFile();
        }
        if (saveSuccess == true) {
            ((AppUI) applicationTemplate.getUIComponent()).setHasNewText(false);
            disableSave();
        }

    }

    public void disableSave() {
        ((AppUI) applicationTemplate.getUIComponent()).disableSaveButton();

    }

    @Override
    public void handleLoadRequest() {
        // TODO: NOT A PART OF HW 1
        try {
            Stage primaryStage = new Stage();
            FileChooser fileChooser = new FileChooser();
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Tab Separated Data files (*.tsd)", "*.tsd");
            fileChooser.getExtensionFilters().add(extFilter);
            file = fileChooser.showOpenDialog(primaryStage);
            applicationTemplate.getDataComponent().loadData(Paths.get(file.getAbsolutePath()));
        }
        catch(Exception e){
            //System.out.println(e);
            ErrorDialog.getDialog().show("Error","To open a file you must select a file");
            return;
//            applicationTemplate.getActionComponent().handleNewRequest();
        }


    }


    @Override
    public void handleExitRequest() {
         if(((AppUI)applicationTemplate.getUIComponent()).getthreadAlive()){
             Stage stage = new Stage();
             stage.setTitle("Caution");
             Text text = new Text("Algorithm is running do you want to leave?");
             Button yes = new Button("I want to exit");
             Button no = new Button("Take me back");
             HBox hbox = new HBox();
             VBox vbox = new VBox();
             hbox.getChildren().addAll(yes,no);
             hbox.setSpacing(25);
             vbox.getChildren().add(text);
             vbox.setSpacing(25);
             vbox.getChildren().add(hbox);
             Scene scene= new Scene(vbox,300,150);
             stage.setScene(scene);
             stage.show();

             yes.setOnAction(e->{
                 stage.close();
                 applicationTemplate.getUIComponent().getPrimaryWindow().close();         //System.exit(0);
                 System.exit(0);

             });
             no.setOnAction(e->{
                 stage.close();
                 return;
             });
             return;
         }
         if(dataFilePath==null&&(!((AppUI)(applicationTemplate.getUIComponent())).getTextArea().getText().isEmpty())){
             Stage stage = new Stage();
             stage.setTitle("Caution");
             Text text = new Text("Are you sure you want to leave");
             Button yes = new Button("Yes");
             Button no = new Button("No");
             Button save = new Button("Save and Leave");
             HBox hbox = new HBox();
             VBox vbox = new VBox();
             hbox.getChildren().addAll(yes,no,save);
             hbox.setSpacing(25);
             vbox.getChildren().add(text);
             vbox.setSpacing(50);
             vbox.getChildren().add(hbox);
             Scene scene= new Scene(vbox,400,200);
             stage.setScene(scene);
             stage.show();


             yes.setOnAction(e->{
                 stage.close();
                 applicationTemplate.getUIComponent().getPrimaryWindow().close();         //System.exit(0);

             });
             no.setOnAction(event -> {
                 stage.close();
             });
             save.setOnAction(event -> {
                 stage.close();
                 handleSaveRequest();
             });
             }
             else{
             applicationTemplate.getUIComponent().getPrimaryWindow().close();         //System.exit(0);

         }

    }

    @Override
    public void handlePrintRequest() {
        // TODO: NOT A PART OF HW 1
    }

    public void handleScreenshotRequest() {
        WritableImage writableImage =
                ((AppUI)(applicationTemplate.getUIComponent())).getChart().snapshot(new SnapshotParameters(),null);

        File file = new File("screenshot.png");
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(writableImage, null), "png", file);
            //System.out.println("snapshot saved: " + file.getAbsolutePath());
        } catch (IOException ex) {

        }
    }

    /**
     * This helper method verifies that the user really wants to save their unsaved work, which they might not want to
     * do. The user will be presented with three options:
     * <ol>
     * <li><code>yes</code>, indicating that the user wants to save the work and continue with the action,</li>
     * <li><code>no</code>, indicating that the user wants to continue with the action without saving the work, and</li>
     * <li><code>cancel</code>, to indicate that the user does not want to continue with the action, but also does not
     * want to save the work at this point.</li>
     * </ol>
     *
     * @return <code>false</code> if the user presses the <i>cancel</i>, and <code>true</code> otherwise.
     */
    private boolean promptToSave() {

        PropertyManager manager = applicationTemplate.manager;
        Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION, manager.getPropertyValue(SAVE_UNSAVED_WORK.name()),
                ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
        confirmationDialog.showAndWait();

        if (confirmationDialog.getResult() == ButtonType.NO) {
            applicationTemplate.getUIComponent().clear();
            dataFilePath = null;

        } else if (confirmationDialog.getResult() == ButtonType.YES) {
            //Save file
            String text = ((AppUI) applicationTemplate.getUIComponent()).getTextArea().getText();
            TSDProcessor processor; applicationTemplate.getDataComponent().clear();
            processor = ((AppData) applicationTemplate.getDataComponent()).getProcessor();
            try {
                processor.processString(text);
            } catch (Exception e) {
                return false;
            }
            saveTheFile();
            clear();
            dataFilePath = null;

        }

        return false;
    }

    private void clear() {
        applicationTemplate.getUIComponent().clear();
        dataFilePath = null;

    }


    private void saveTheFile() {
        PropertyManager manager = applicationTemplate.manager;
        AppData appData = new AppData(applicationTemplate);
        try {
            appData.saveData(((AppUI) applicationTemplate.getUIComponent()).getTextArea().getText());
            if(appData.getProcessor().getArray().size()==0){
                return;
            }
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle(manager.getPropertyValue(SAVE_FILE.name()));
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter(manager.getPropertyValue(DATA_FILE_EXT_DESC.name()), manager.getPropertyValue(DATA_FILE_EXT.name())));

            Stage mainStage = applicationTemplate.getUIComponent().getPrimaryWindow();

            File file = fileChooser.showSaveDialog(mainStage);


            if (file == null) {
                ErrorDialog errorDialog = (ErrorDialog) applicationTemplate.getDialog(Dialog.DialogType.ERROR);
                errorDialog.show(manager.getPropertyValue(SAVE_FILE_ERROR.name()), manager.getPropertyValue(TO_SAVE_THE_FILE_YOU_MUST_CLICK_ON_SAVE.name()));

                saveSuccess = false;
            } else {
                FileWriter fileWriter = null;
                try {
                    fileWriter = new FileWriter(file);
                } catch (IOException e) {
                }
                dataFilePath = Paths.get(file.getAbsolutePath());
                file = new File(dataFilePath.toString());
                try {
                    fileWriter = new FileWriter(file);
                } catch (IOException e) {
                }

                try {
                    fileWriter.write(((AppUI) applicationTemplate.getUIComponent()).getTextArea().getText());
                } catch (IOException e) {
                }
                try {
                    fileWriter.close();
                } catch (IOException e) {
                }
                saveSuccess = true;

            }
        } catch (TSDProcessor.InvalidDataNameException e) {
        } catch (Exception e) {
            // e.printStackTrace();
        }
    }

    private void fileExist() {

        String text = ((AppUI) applicationTemplate.getUIComponent()).getTextArea().getText();
        TSDProcessor processor; applicationTemplate.getDataComponent().clear();
        processor = ((AppData) applicationTemplate.getDataComponent()).getProcessor();
        try {
            processor.processString(text);
        } catch (Exception e) {
            return;
            //e.printStackTrace();
        }
        File file = new File(dataFilePath.toString());
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(file);
        } catch (IOException e) {
        }
        try {
            fileWriter = new FileWriter(file);
        } catch (IOException e) {
        }
        try {
            fileWriter.write(((AppUI) applicationTemplate.getUIComponent()).getTextArea().getText());
        } catch (IOException e) {
        }
        try {
            fileWriter.close();
        } catch (IOException e) {
        }
        saveSuccess = true;

    }



}
