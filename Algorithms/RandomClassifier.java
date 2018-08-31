
package Algorithms;

import Algorithms.Classifier;
import dataprocessors.AppData;
import dataprocessors.DataSet;
import dataprocessors.TSDProcessor;
import javafx.application.Platform;
import javafx.scene.chart.XYChart;
import javafx.scene.shape.Rectangle;
import ui.AppUI;
import vilij.templates.ApplicationTemplate;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Ritwik Banerjee
 */
public class RandomClassifier extends Classifier {

    private static final Random RAND = new Random();

    @SuppressWarnings("FieldCanBeLocal")
    // this mock classifier doesn't actually use the data, but a real classifier will
    private DataSet dataset;

    private  int maxIterations;
    private  int updateInterval;
    public static int j=1;
    int xCoefficient=0;
    int yCoefficient=0;
    int constant=0;
    ApplicationTemplate template;
    XYChart.Data<Number, Number> xL;
    XYChart.Data<Number, Number> xM;
    XYChart.Series<Number, Number> series3;
    TSDProcessor processor;
    double ymin;
    double ymax;

    // currently, this value does not change after instantiation
    private  AtomicBoolean tocontinue;

    public void setMaxIterations(int maxIterations){
        this.maxIterations=maxIterations;

    }
    public void setUpdateInterval(int updateInterval){
        this.updateInterval = updateInterval;

    }
    public void setTocontinue(AtomicBoolean tocontinue){
        this.tocontinue= tocontinue;

    }
    public int getMaxInterations(){
        return maxIterations;
    }

    public AtomicBoolean getTocontinue(){
        return tocontinue;
    }


    @Override
    public int getMaxIterations() {
        return maxIterations;
    }

    @Override
    public int getUpdateInterval() {
        return updateInterval;
    }

    @Override
    public boolean tocontinue() {
        return tocontinue.get();
    }

    public RandomClassifier(DataSet dataset,
                            int maxIterations,
                            int updateInterval,
                            boolean tocontinue,ApplicationTemplate template) {
        this.dataset = dataset;
        this.maxIterations = maxIterations;
        this.updateInterval = updateInterval;
        this.tocontinue = new AtomicBoolean(tocontinue);
        this.template= template;
    }

    @Override
    public void run() {
        ((AppUI)template.getUIComponent()).getScrnshotButton().setDisable(true);
        Platform.runLater(this::lineMaker);
        for (int i = 1; i <= maxIterations && tocontinue(); i++) {
            xCoefficient = new Double(RAND.nextDouble() * 100).intValue();
            yCoefficient = new Double(RAND.nextDouble() * 100).intValue()+1;
            constant = new Double(RAND.nextDouble() * 100).intValue();

            // this is the real output of the classifier
            output = Arrays.asList(xCoefficient, yCoefficient, constant);

            // everything below is just for internal viewing of how the output is changing
            // in the final project, such changes will be dynamically visible in the UI
            if (i % updateInterval == 0) {
                System.out.printf("Iteration number %d: ", i); //
                Platform.runLater(this::calculateYvalue);
                flush();
            }
            if (i > maxIterations * .6 && RAND.nextDouble() < 0.05) {
                System.out.printf("Iteration number %d: ", i);
                Platform.runLater(this::calculateYvalue);
                flush();
                break;
            }
            try{
                Thread.sleep(1500);
            }
            catch(InterruptedException e){
            }
        }

        if (!tocontinue() && j<maxIterations ) {
            xCoefficient = new Double(RAND.nextDouble() * 100).intValue();  //A
            yCoefficient = new Double(RAND.nextDouble() * 100).intValue()+1;  //B
            constant = new Double(RAND.nextDouble() * 100).intValue();      //C

            // this is the real output of the classifier
            output = Arrays.asList(xCoefficient, yCoefficient, constant);

            // everything below is just for internal viewing of how the output is changing
            // in the final project, such changes will be dynamically visible in the UI
            if (j % updateInterval == 0) {
                System.out.printf("Iteration number %d: ", j); //
                Platform.runLater(this::calculateYvalue);
                flush();
            }
            if (j > maxIterations * .6 && RAND.nextDouble() < 0.05) {
                System.out.printf("Iteration number %d: ", j);
                Platform.runLater(this::calculateYvalue);
                flush();
            }
            j++;
            try{
                wait();
                notifyAll();
                ((AppUI)template.getUIComponent()).getRUN().setDisable(true);
                ((AppUI)template.getUIComponent()).getScrnshotButton().setDisable(false);
            }
            catch(Exception e){

            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
            }
        }
        ((AppUI)template.getUIComponent()).getRUN().setDisable(false);
        ((AppUI)template.getUIComponent()).getScrnshotButton().setDisable(false);
        ((AppUI) template.getUIComponent()).setThreadAlive(false);

    }

    // for internal viewing only
    protected void flush() {
        System.out.printf("%d\t%d\t%d%n", output.get(0), output.get(1), output.get(2));
    }

//    /** A placeholder main method to just make sure this code runs smoothly */
//    public static void main(String... args) throws IOException {
//        DataSet          dataset    = DataSet.fromTSDFile(Paths.get("/path/to/some-data.tsd"));
//        RandomClassifier classifier = new RandomClassifier(dataset, 100, 5, true,null);
//        classifier.run(); // no multithreading yet
//    }
    public void lineMaker(){
        processor = ((AppData)template.getDataComponent()).getProcessor();
        xL = new XYChart.Data<>(processor.lowestXvalue, ymin);
        xM = new XYChart.Data<>(processor.maxXvalue, ymax);
        if( ((AppUI) template.getUIComponent()).getChart().getData().contains(series3)){
            ((AppUI) template.getUIComponent()).getChart().getData().remove(series3);
        }
        series3 = new XYChart.Series<>();
        series3.getData().add(xL);
        series3.getData().add(xM);
        ((AppUI) template.getUIComponent()).getChart().getData().add(series3);
        Rectangle removePoint1 = new Rectangle(0, 0);
        Rectangle removePoint2 = new Rectangle(0,0);

        xL.setNode(removePoint1);
        xM.setNode(removePoint2);

    }
    public void calculateYvalue(){
        ymin = -(constant + (xCoefficient*processor.lowestXvalue));
        ymin = ymin/yCoefficient;

        ymax = -(constant + (xCoefficient*processor.maxXvalue));
        ymax = ymin/yCoefficient;
        xL.setYValue(ymax);
        xM.setYValue(ymin);
    }
}