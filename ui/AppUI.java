package ui;

import Algorithms.RandomClassifier;
import Algorithms.RandomClustering;
import actions.AppActions;
import dataprocessors.AppData;
import dataprocessors.DataSet;
import dataprocessors.TSDProcessor;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
//import javafx.scene.chart.ScatterChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
//import javafx.scene.text.Font;
//import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import vilij.components.ErrorDialog;
import vilij.propertymanager.PropertyManager;
import vilij.templates.ApplicationTemplate;
import vilij.templates.UITemplate;


//import static com.sun.tools.doclets.formats.html.markup.HtmlStyle.bar;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

import static settings.AppPropertyTypes.*;
import static vilij.settings.PropertyTypes.*;

/**
 * This is the application's user interface implementation.
 *
 * @author Ritwik Banerjee
 */
public final class AppUI extends UITemplate {

    /** The application to which this class of actions belongs. */
    ApplicationTemplate applicationTemplate;

    @SuppressWarnings("FieldCanBeLocal")
    private Button                       scrnshotButton; // toolbar button to take a screenshot of the data
    private LineChart<Number, Number> chart;          // the chart where data will be displayed
    private Button                       displayButton;  // workspace button to display data on the chart
    private TextArea                     textArea;       // text area for new data input
    private boolean                      hasNewText;     // whether or not the text area has any new data since last display
    private String scrnPath;
    private String settingsPath;
    private CheckBox checkBox = new CheckBox("Edit");
   // private ToggleButton edit = new ToggleButton("edit");
   // private ToggleButton done = new ToggleButton("done");
    AnchorPane aPane = new AnchorPane();
    Button classification = new Button("Classification");
    Button clustering = new Button("Clustering");
    Text text = new Text("");
    Text algoText = new Text("Algorithms");
    TSDProcessor processor = new TSDProcessor();
    VBox algoAndSummary = new VBox();
    RadioButton algoA = new RadioButton("Algorithms A");
    RadioButton algoB = new RadioButton("Algorithms B");
    RadioButton algoC = new RadioButton("Algorithms C");
  //  RadioButton radioClustering =  new RadioButton("Random Clustering");
  //  RadioButton radioClassification =  new RadioButton("Random Classification");
    // ToggleGroup clusteringOne = new ToggleGroup();
    // ToggleGroup classificationOne = new ToggleGroup();
     HBox classAbox = new HBox();
     HBox classBbox = new HBox();
     HBox classCbox = new HBox();
     VBox totalBox = new VBox();
    Image image;
    Button a=new Button();
    Button b=new Button();
    Button c =new Button();
    ImageView imageviewA;
    ImageView imageviewB;
    ImageView imageviewC;
    //HBox gray = new HBox();
    Button done ;
    TextField iterationText = new TextField();
    TextField intervalText = new TextField();
   // String A;
    //String B;
    //String C;
    Button RUN = new Button( "RUN");

    RandomClustering clusteringAlgo;
    RandomClassifier classificationAlgo;

    Boolean cluster;
    CheckBox continousRun;
    Thread thread;
    boolean threadAlive=false;








    public LineChart<Number, Number> getChart() { return chart; }

    public AppUI(Stage primaryStage, ApplicationTemplate applicationTemplate) {
        super(primaryStage, applicationTemplate);
        this.applicationTemplate = applicationTemplate;
    }

    @Override
    protected void setResourcePaths(ApplicationTemplate applicationTemplate) {
        super.setResourcePaths(applicationTemplate);
        PropertyManager manager = applicationTemplate.manager;
        String iconsPath = SEPARATOR + String.join(SEPARATOR,
                manager.getPropertyValue(GUI_RESOURCE_PATH.name()),
                manager.getPropertyValue(ICONS_RESOURCE_PATH.name()));
        scrnPath  = String.join(SEPARATOR, iconsPath, manager.getPropertyValue(SCREENSHOT_ICON.name()));
        settingsPath =  String.join(SEPARATOR, iconsPath, manager.getPropertyValue(SETTINGS_ICON.name()));

    }

    @Override
    protected void setToolBar(ApplicationTemplate applicationTemplate) {
        super.setToolBar(applicationTemplate);
        PropertyManager manager = applicationTemplate.manager;
        //System.out.print(scrnPath);
        scrnshotButton = setToolbarButton(scrnPath, manager.getPropertyValue(SCREENSHOT_TOOLTIP.name()), true);
        toolBar.getItems().add(scrnshotButton);
        toolBar.getItems().remove(printButton);
    }

    @Override
    protected void setToolbarHandlers(ApplicationTemplate applicationTemplate) {
        applicationTemplate.setActionComponent(new AppActions(applicationTemplate));
        AppActions forScreenShot = new AppActions(applicationTemplate);
        newButton.setOnAction(e ->
        {
            aPane.getChildren().clear();
            applicationTemplate.getActionComponent().handleNewRequest();
            newButton.setDisable(true);
            newButton.setDisable(true);
            newPaneAdder();
        }


);
        saveButton.setOnAction(e -> applicationTemplate.getActionComponent().handleSaveRequest());
        loadButton.setOnAction(e -> {
            try {
                applicationTemplate.getActionComponent().handleLoadRequest();
                newPaneAdder();
                textMaker();
                aPane.getChildren().remove(checkBox);
                scrnshotButton.setDisable(true);
                textArea.setDisable(true);
               // aPane.getChildren().remove(displayButton);
               // displayButton.setDisable(false);
                //text.setLayoutX(25);
                //text.setLayoutY(280);
                checkBox.setSelected(true);
                if(!algoAndSummary.getChildren().contains(algoText)){
                    algoAndSummary.setLayoutX(25);
                    algoAndSummary.setLayoutY(360);
                    algoAndSummary.getChildren().add(algoText);
                }
                if (processor.getNumberOfLabels() == 2) {
                    algoAndSummary.getChildren().add(classification);
                    algoAndSummary.getChildren().add(clustering);
                } else {
                    algoAndSummary.getChildren().remove(clustering);
                    algoAndSummary.getChildren().add(clustering);
                }
               // algoAndSummary.setLayoutX(25);
               // algoAndSummary.setLayoutY(380);
                aPane.getChildren().remove(algoAndSummary);
                aPane.getChildren().add(algoAndSummary);
               // aPane.getChildren().remove(checkBox);
                handleDisplayRequest();


            }
            catch (ArrayIndexOutOfBoundsException e2) {
                ErrorDialog.getDialog().show("Error","Incorrect Format");
               // System.out.print("here");


            } catch (Exception e1) {
                //System.out.print("here");
            }
        });
        exitButton.setOnAction(e -> applicationTemplate.getActionComponent().handleExitRequest());
        printButton.setOnAction(e -> applicationTemplate.getActionComponent().handlePrintRequest());
        scrnshotButton.setOnAction(event -> {
            try {
                forScreenShot.handleScreenshotRequest();
            } catch (Exception e) {

            }
        });

    }



    @Override
    public void initialize() {
        layout();
        setWorkspaceActions();
    }

    @Override
    public void clear() {
        textArea.clear();
        hasNewText= true;
        ((applicationTemplate.getDataComponent())).clear();
        newButton.setDisable(hasNewText);
        saveButton.setDisable(hasNewText);
       // displayButton.setDisable(hasNewText);
        scrnshotButton.setDisable(true);
        textArea.clear();
        if(aPane.getChildren().contains(RUN)){
            aPane.getChildren().remove(RUN);
        }
    }

    public TextArea getTextArea(){
        return textArea;
    }




    private void layout() {
        PropertyManager manager = applicationTemplate.manager;
        displayButton = new Button(manager.getPropertyValue(DISPLAY_VALUE.name()));
        displayButton.setDisable(true);
        chart = new LineChart<Number, Number>(new NumberAxis(), new NumberAxis());
        chart.setTitle(manager.getPropertyValue(DATA_VISUALIZATION.name()));
        chart.setPrefWidth(700);
        chart.setHorizontalZeroLineVisible(false);
        chart.setVerticalZeroLineVisible(false);
        HBox Hbox = new HBox();
        VBox vBox = new VBox();
        textArea = new TextArea();
        textArea.setPrefWidth(250);
        textArea.setPrefHeight(175);
        textArea.textProperty().addListener((obs, old, niu) -> {
            String s = textArea.getText().replaceAll("\\s", "");
            if (!s.isEmpty() || !s.equals("")) {
                hasNewText= false;
                newButton.setDisable(hasNewText);
                saveButton.setDisable(hasNewText);
               // displayButton.setDisable(hasNewText);

            }
            else{
                hasNewText= true;
                newButton.setDisable(hasNewText);
                saveButton.setDisable(hasNewText);
               // displayButton.setDisable(hasNewText);

            }
        });
        String csspath = SEPARATOR + String.join(SEPARATOR,
                manager.getPropertyValue(GUI_RESOURCE_PATHS.name()),
                manager.getPropertyValue(CSS_RESOURCE_PATHS.name()),
                manager.getPropertyValue(CSS_RESOURCE_FILENAMES.name()));
        chart.getStylesheets().add(csspath);
        textArea.getStylesheets().add(csspath);
        vBox.setPadding(new Insets(0, 0, 0, 10));
        vBox.setSpacing(10);
        Label label = new Label(manager.getPropertyValue(DATA_FILE.name()));
        label.setPadding(new Insets(5, 5, 0, 25));


        //edit.setToggleGroup(group);
      //  done.setToggleGroup(group);
       // vBox.getChildren().addAll(label, textArea, displayButton,checkBox,text,edit,done);
        vBox.getChildren().addAll(label, textArea, displayButton,checkBox,text);

        Hbox.setSpacing(50);
        Hbox.getChildren().addAll(vBox, chart);
        Hbox.setAlignment(Pos.TOP_LEFT);
        //appPane.getChildren().add(Hbox);
        chart.setHorizontalGridLinesVisible(false);
        chart.setVerticalGridLinesVisible(false);
        textArea.applyCss();
        textArea.layout();
        textArea.setLayoutY(50);
        textArea.setLayoutX(25);
        chart.setLayoutX(275);
        chart.setLayoutY(10);
        displayButton.setLayoutX(25);
        displayButton.setLayoutY(280);
        checkBox.setLayoutX(25);
        checkBox.setLayoutY(240);

        //edit.setLayoutX(150);
        //edit.setLayoutY(240);
//        done.setLayoutY(240);
//        done.setLayoutX(193);
       // text.setLayoutY(325);
       // text.setLayoutX(25);


        //algoText.setLayoutY(360);
        //algoText.setLayoutX(120);


        //classification.setLayoutX(25);
        //clustering.setLayoutX(25);

        //classification.setLayoutY(370);
        //clustering.setLayoutY(400);


        newButton.setDisable(false);
        appPane.getChildren().add(aPane);
//        ScrollPane sp = new ScrollPane();
//        sp.setContent(textArea);
//        sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
//        sp.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        //radioClustering.setToggleGroup(clusteringOne);
        //radioClassification.setToggleGroup(classificationOne);
        //clustHbox.getChildren().add(radioClustering);
        //classHbox.getChildren().add(radioClassification);




}

    private void setWorkspaceActions() {
        if(!aPane.getChildren().contains(chart)){
            aPane.getChildren().add(chart);

        }
        displayButton.setOnAction(actionEvent -> {
            if(textArea.getText().isEmpty()){
                ErrorDialog.getDialog().show("Error","Text Area is empty");
                return;
            }

            try {
                textMaker();
                //aPane.getChildren().remove(gray);
               // System.out.print("hERE");
                chart.setVisible(true);
                handleDisplayRequest();
                //aPane.getChildren().add(algoText);
                //aPane.getChildren().add(classification);
                //aPane.getChildren().add(clustering);
                if(algoAndSummary.getChildren().contains(algoText)){
                    algoAndSummary.getChildren().remove(algoText);
                }
                algoAndSummary.setLayoutX(25);
                algoAndSummary.setLayoutY(360);
                algoAndSummary.getChildren().add(algoText);
                if(algoAndSummary.getChildren().contains(clustering)){
                    algoAndSummary.getChildren().remove(clustering);
                }
                if(algoAndSummary.getChildren().contains(classification)){
                    algoAndSummary.getChildren().remove(classification);

                }
                if (processor.getNumberOfLabels() == 2) {
                    algoAndSummary.getChildren().add(classification);
                    algoAndSummary.getChildren().add(clustering);
                } else {
                    algoAndSummary.getChildren().remove(clustering);
                    algoAndSummary.getChildren().add(clustering);
                }
                algoAndSummary.setLayoutX(25);
                algoAndSummary.setLayoutY(360);
                if(aPane.getChildren().contains(algoAndSummary)){
                    aPane.getChildren().remove(algoAndSummary);
                }
                aPane.getChildren().add(algoAndSummary);
               // text.setLayoutX(130);
                // text.setLayoutY(280);
              //  textMaker();
              //  aPane.getChildren().add(text);
               // System.out.print("here1");


            }
            catch (ArrayIndexOutOfBoundsException e) {
                ErrorDialog.getDialog().show("Error","Incorrect Format");
               // System.out.print("here2");


            } catch (Exception e) {
                e.printStackTrace();
                //System.out.print("here3");

            }

        });
        checkBox.setOnAction( e -> {
    if(checkBox.isSelected()){
        textArea.setDisable(true);
        displayButton.setDisable(false);
        aPane.getChildren().remove(text);
        }
    else{
        textArea.setDisable(false);
        displayButton.setDisable(true);
        try {
            aPane.getChildren().remove(text);
            aPane.getChildren().add(displayButton);
            aPane.getChildren().remove(algoAndSummary);
            algoAndSummary.setLayoutY(360);
            algoAndSummary.setLayoutX(150);
            aPane.getChildren().remove(totalBox);
        }
        catch (Exception ex){

        }
    }

    });
        textArea.textProperty().addListener(e->{
            String array[]= textArea.getText().split("\n");
            if(array.length<10){
                if((((AppData)applicationTemplate.getDataComponent()).getArray().size()>0)){
                    textArea.appendText(((AppData)applicationTemplate.getDataComponent()).getArray().remove(0));
                }
            }
        });
//        done.setOnAction(event -> {
//            textArea.setDisable(true);
//        });
//        edit.setOnAction(event->{
//            textArea.setDisable(false);
//        });


        clustering.setOnAction(event -> {
            Text clusteringName = new Text("Clustering");
            clusteringAlgo = new RandomClustering(new DataSet(),0,0,true,applicationTemplate);
            cluster =true;
            try {
                //System.out.print(settingsPath);
                image = new Image(getClass().getResourceAsStream(settingsPath));
            } catch (Exception e) {
                // System.out.print("Didnt work");
            }
            classAbox = new HBox();
            classBbox = new HBox();
            classCbox = new HBox();
            totalBox = new VBox();
            imageviewA = new ImageView(image);
            imageviewB = new ImageView(image);
            imageviewC = new ImageView(image);
            a = new Button("", imageviewA);
            b= new Button("",imageviewB);
            c= new Button("",imageviewC);

            classAbox.getChildren().add(algoA);
            classAbox.getChildren().add(a);
            classBbox.getChildren().add(algoB);
            classBbox.getChildren().add(b);
            classCbox.getChildren().add(algoC);
            classCbox.getChildren().add(c);
            totalBox.getChildren().add(clusteringName);


            totalBox.getChildren().add(classAbox);
            totalBox.getChildren().add(classBbox);
            totalBox.getChildren().add(classCbox);
            totalBox.setLayoutX(25);
            totalBox.setLayoutY(390);
            aPane.getChildren().add(totalBox);
            aPane.getChildren().remove(algoAndSummary);

            algoA.setOnAction(e->{
                algoB.setSelected(false);
                algoC.setSelected(false);
                //Clustering
                });
            algoB.setOnAction(e->{
                algoA.setSelected(false);
                algoC.setSelected(false);
            });
            algoC.setOnAction(e->{
                algoB.setSelected(false);
                algoA.setSelected(false);
            });

            a.setOnAction(e -> {
                if(algoA.isSelected()==false){
                    return;
                }
                iterationText.setText(String.valueOf(clusteringAlgo.getMaxIterations()));
                intervalText.setText(String.valueOf(clusteringAlgo.getUpdateInterval()));
                defaultMethod();
            });

            b.setOnAction(e -> {
                if(algoB.isSelected()==false){
                    return;
                }
                iterationText.setText(String.valueOf(clusteringAlgo.getMaxIterations()));
                intervalText.setText(String.valueOf(clusteringAlgo.getUpdateInterval()));
                defaultMethod();
            });

            c.setOnAction(e -> {
                if(algoC.isSelected()==false){
                    return;
                }
                iterationText.setText(String.valueOf(clusteringAlgo.getMaxIterations()));
                intervalText.setText(String.valueOf(clusteringAlgo.getUpdateInterval()));
                defaultMethod();
            });



        });
        classification.setOnAction(event -> {
            Text classificationName = new Text("Classification");
            cluster= false;
            classificationAlgo = new RandomClassifier(new DataSet(),0,0,true,applicationTemplate);
            try {
                //System.out.print(settingsPath);
                image = new Image(getClass().getResourceAsStream(settingsPath));
            } catch (Exception e) {
               // System.out.print("Didnt work");
            }
            classAbox = new HBox();
            classBbox = new HBox();
            classCbox = new HBox();
            totalBox = new VBox();
            imageviewA = new ImageView(image);
            imageviewB = new ImageView(image);
            imageviewC = new ImageView(image);
            a.setGraphic( imageviewA);
            b.setGraphic(imageviewB);
            c.setGraphic(imageviewC);

            classAbox.getChildren().add(algoA);
            classAbox.getChildren().add(a);
            classBbox.getChildren().add(algoB);
            classBbox.getChildren().add(b);
            classCbox.getChildren().add(algoC);
            classCbox.getChildren().add(c);
            totalBox.getChildren().add(classificationName);


            totalBox.getChildren().add(classAbox);
            totalBox.getChildren().add(classBbox);
            totalBox.getChildren().add(classCbox);
            totalBox.setLayoutX(25);
            totalBox.setLayoutY(390);
            aPane.getChildren().add(totalBox);
            aPane.getChildren().remove(algoAndSummary);
        });
        algoA.setOnAction(e->{
            algoB.setSelected(false);
            algoC.setSelected(false);
            //Classification

        });
        algoB.setOnAction(e->{
            algoA.setSelected(false);
            algoC.setSelected(false);
        });
        algoC.setOnAction(e->{
            algoB.setSelected(false);
            algoA.setSelected(false);
        });

        a.setOnAction(e -> {
            if(algoA.isSelected()==false){
                return;
            }
            iterationText.setText(String.valueOf(classificationAlgo.getMaxIterations()));
            intervalText.setText(String.valueOf(classificationAlgo.getUpdateInterval()));
            defaultMethod();
        });

        b.setOnAction(e -> {
            if(algoB.isSelected()==false){
                return;
            }

            iterationText.setText(String.valueOf(classificationAlgo.getMaxIterations()));
            intervalText.setText(String.valueOf(classificationAlgo.getUpdateInterval()));
            defaultMethod();
        });

        c.setOnAction(e -> {
            if(algoC.isSelected()==false){
                return;
            }
            iterationText.setText(String.valueOf(classificationAlgo.getMaxIterations()));
            intervalText.setText(String.valueOf(classificationAlgo.getUpdateInterval()));
            defaultMethod();
        });
        RUN.setOnAction(e->{
            scrnshotButton.setDisable(true);
            RUN.setDisable(true);
            if(cluster){
                if(!continousRun.isSelected()){
                    clusteringAlgo.setTocontinue(new AtomicBoolean(false));
                }
                clusteringAlgo.run();
            }
             else if(cluster==false){
                thread = new Thread(classificationAlgo);
                threadAlive=true;
                if(!continousRun.isSelected()){
                    classificationAlgo.setTocontinue(new AtomicBoolean(false));
                }
                else{
                    scrnshotButton.setDisable(true);
                    RUN.setDisable(true);
                }
                try {
                    thread.start();
                }
                catch(NullPointerException e1){

                }
            }
            });




    }

    public void defaultMethod() {

        Text configuration = new Text("Algorithm Run Configuration\n\n\n" );
        continousRun = new CheckBox();
        GridPane pane = new GridPane();
        Text iteration = new Text("Max Iterations");
        Text interval = new Text("Update Interval");
        Text continous = new Text("Continous RUN?");

        pane.add(configuration,0,0);
        pane.add(iteration,0,1);
        pane.add(interval,0,3);
        pane.add(continous,0,5);
        pane.add(iterationText,2,1);
        pane.add(intervalText,2,3);
        pane.add(continousRun,2,5);

        Scene scene = new Scene(pane,600,600);
        Stage stage = new Stage();
        stage.setTitle("Configuration");

        done = new Button("Done");
        pane.add(done,7,7);
        stage.setScene(scene);
        stage.show();

        done.setOnAction(e->{
            if(intervalText.getText().matches(".*[a-z].*")||intervalText.getText().contains("-")){
                intervalText.setText("1");
            }
            if(iterationText.getText().matches(".*[a-z].*")||iterationText.getText().contains("-")){
                iterationText.setText("1");
            }
            if(iterationText.getText().isEmpty()||intervalText.getText().isEmpty()||!continousRun.isSelected()){

            }
            if(cluster){
                clusteringAlgo.setMaxIterations(Integer.parseInt(iterationText.getText()));
                clusteringAlgo.setUpdateInterval(Integer.parseInt(intervalText.getText()));
            }
            else{
                classificationAlgo.setMaxIterations(Integer.parseInt(iterationText.getText()));
                classificationAlgo.setUpdateInterval(Integer.parseInt(intervalText.getText()));
            }

            stage.close();
            RUN.setLayoutX(25);
            RUN.setLayoutY(500);
            if(!aPane.getChildren().contains(RUN)) {
                aPane.getChildren().add(RUN);
            }
            if(intervalText.getText().equals("0")||iterationText.getText().equals("0")){
                if(aPane.getChildren().contains(RUN)) {
                    aPane.getChildren().remove(RUN);
                }
            }
            continousRun.setOnAction(event -> {
                if(cluster){
                    clusteringAlgo.setTocontinue(new AtomicBoolean(clusteringAlgo.tocontinue()));
                }
                else if(cluster==false){
                    classificationAlgo.setTocontinue(new AtomicBoolean(classificationAlgo.tocontinue()));
                }
            });
        });


    }

    public void handleDisplayRequest() throws Exception {
        //aPane.getChildren().remove(gray);
        applicationTemplate.getDataComponent().clear();
        if(aPane.getChildren().contains(chart)==false) {
            aPane.getChildren().add(chart);
        }
        AppData appData = ((AppData)(applicationTemplate.getDataComponent()));
        appData.saveData(textArea.getText());
        appData.displayData();
        scrnshotButton.setDisable(true);
        textMaker();

        text.setLayoutX(25);
        text.setLayoutY(280);
        aPane.getChildren().add(text);
        aPane.getChildren().remove(displayButton);


    }
    public void setHasNewText(boolean hasNewText){
        this.hasNewText=hasNewText;
    }

    public void disableSaveButton(){
        saveButton.setDisable(true);
    }

    public void newPaneAdder(){
        try{
        aPane.getChildren().contains(textArea);
        aPane.getChildren().add(textArea);
       /* gray.setPrefHeight(500);
        gray.setPrefWidth(700);
        gray.setStyle("-fx-padding: 10;" +
                    "-fx-border-style: solid inside;" +
                    "-fx-border-width: 2;" +
                    "-fx-border-insets: 5;" +
                    "-fx-border-radius: 5;" +
                    "-fx-border-color: gray;");
        gray.setLayoutX(275);
        gray.setLayoutY(10);
        aPane.getChildren().add(gray);*/
        chart.setVisible(true);
        aPane.getChildren().add(displayButton);
        aPane.getChildren().add(checkBox);

        //aPane.getChildren().add(text);
       // aPane.getChildren().add(edit);
       // aPane.getChildren().add(done);
        }
        catch (Exception e){

        }



    }


    public void textMaker(){
        processor= new TSDProcessor();
        try {
            processor.processString(textArea.getText());
        } catch (Exception e) {
        }
        if (processor.getNumberOfinstances()==0) {
            return;
        }
        if(((AppActions)(applicationTemplate.getActionComponent())).getfile()==null){
            text.setText(processor.getNumberOfinstances() +" instances with "+processor.getNumberOfLabels()+
                    " labels\n loaded from  textBox. \nThe labels are: \n"+processor.getLabelNames());
        }
        else if(((AppActions)(applicationTemplate.getActionComponent())).getfile()!=null){
            text.setText(processor.getNumberOfinstances() + " instances with " + processor.getNumberOfLabels() +
                    " labels \n loaded from \n" + ((AppActions) (applicationTemplate.getActionComponent())).getfile().getAbsolutePath()+
            ". \nThe labels are: \n"+processor.getLabelNames());
        }

    }

    public Button getRUN() {
        return RUN;
    }

    public Button getScrnshotButton() {
        return scrnshotButton;
    }
    public CheckBox getContinousRun(){
        return continousRun;
    }
    public boolean getthreadAlive(){
        return threadAlive;
    }
    public void setThreadAlive(boolean threadAlive){
        this.threadAlive=threadAlive;
    }
}

