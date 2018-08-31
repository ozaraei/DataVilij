package dataprocessors;

import javafx.geometry.Point2D;
import javafx.scene.Cursor;
//import javafx.scene.chart.Chart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Tooltip;
import javafx.scene.shape.Rectangle;
//import javafx.scene.text.Text;
import vilij.components.ErrorDialog;

//import javax.print.DocFlavor;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;


/**
 * The data files used by this data visualization applications follow a tab-separated format, where each data point is
 * named, labeled, and has a specific location in the 2-dimensional X-Y plane. This class handles the parsing and
 * processing of such data. It also handles exporting the data to a 2-D plot.
 * <p>
 * A sample file in this format has been provided in the application's <code>resources/data</code> folder.
 *
 * @author Ritwik Banerjee
 * @see XYChart
 */
public final class TSDProcessor {

    public static int lineNumber;
    private String address= new String();
    private ArrayList<String> array = new ArrayList<>();
   // private double yCount=0;
   // private double totalCount=0;
    public double maxXvalue=0;
    public double lowestXvalue=0;
    private int instance =0;
    private String text = "";
    private int numLabels;
    private int four=1;
    static boolean  exception= false;



    public static class InvalidDataNameException extends Exception {

       // private static final String NAME_ERROR_MSG = "All data instance names must start with the @ character.";


        public InvalidDataNameException(String name) {
            Alert errorStage = new Alert(Alert.AlertType.INFORMATION);
            errorStage.setContentText((String.format("Invalid name "+name+" at "+ lineNumber )));
            errorStage.showAndWait();
            exception =true;
        }

    }

    private Map<String, String>  dataLabels;
    private Map<String, Point2D> dataPoints;

    public TSDProcessor() {
        dataLabels = new LinkedHashMap<>();
        dataPoints = new LinkedHashMap<>();
    }

    /**
     * Processes the data and populated two {@link Map} objects with the data.
     *
     * @param tsdString the input data provided as a single {@link String}
     * @throws Exception if the input string does not follow the <code>.tsd</code> data format
     */
    public void processString(String tsdString) throws Exception {
        lineNumber=0;
        AtomicBoolean hadAnError   = new AtomicBoolean(false);
        StringBuilder errorMessage = new StringBuilder();
        Stream.of(tsdString.split("\n"))
              .map(line -> Arrays.asList(line.split("\t")))
              .forEach(list -> {
                  try {
                      lineNumber++;
                      String name = checkedname(list.get(0));
                      String label = list.get(1);
                      String[] pair = list.get(2).split(",");
                      Point2D point = new Point2D(Double.parseDouble(pair[0]), Double.parseDouble(pair[1]));
                      dataLabels.put(name, label);
                      if(!text.contains(label)&&(!label.equals("null"))){
                          if(four==5){
                              label+="\n";
                              four=0;
                          }
                          text += label+", ";
                          numLabels++;
                          four++;
                      }
                      dataPoints.put(name, point);
                     // totalCount++;

                  } catch (Exception e) {
                      errorMessage.setLength(0);
                      errorMessage.append(e.getClass().getSimpleName()).append(": ").append(e.getMessage()).append(lineNumber);
                      hadAnError.set(true);
                  }
              });
        if (errorMessage.length() > 0)
            return;
    }

    /**
     * Exports the data to the specified 2-D chart.
     *
     * @param chart the specified chart
     */
    void toChartData(XYChart<Number, Number> chart) {
        Set<String> labels = new LinkedHashSet<>(dataLabels.values());
        for (String label : labels) {
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName(label);
            dataLabels.entrySet().stream().filter(entry -> entry.getValue().equals(label)).forEach(entry -> {
                Point2D point = dataPoints.get(entry.getKey());
                series.getData().add(new XYChart.Data<>(point.getX(), point.getY()));
               // yCount+=point.getY();

                if(lowestXvalue==0){
                    lowestXvalue=point.getX();
                }
                else if(point.getX()<lowestXvalue){
                    lowestXvalue=point.getX();
                }
                if(point.getY()>maxXvalue){
                    maxXvalue=point.getX();
                }
            });
            chart.getData().add(series);
            int i=0;
            for ( XYChart.Series<Number, Number> series2 : chart.getData()) {
                for ( XYChart.Data<Number, Number> data2 : series.getData()) {
                    Tooltip tooltip = new Tooltip();
                    tooltip.setText(array.get(i));
                    Tooltip.install(data2.getNode(), tooltip);
                    data2.getNode().setCursor(Cursor.CLOSED_HAND);
                }
                i++;
            }
        }

//        XYChart.Series<Number, Number> series2 = new XYChart.Series<>();
//        if(totalCount==1){
//            return;
//        }
//        double average = yCount/totalCount;
//        series2.setName("Average");
//        XYChart.Data<Number, Number> xL = new XYChart.Data<>(lowestXvalue, average);
//        XYChart.Data<Number, Number> xM = new XYChart.Data<>(maxXvalue, average);
//        series2.getData().add(xL);
//        series2.getData().add(xM);
//        chart.getData().add(series2);
//        Rectangle removePoint1 = new Rectangle(0, 0);
//        Rectangle removePoint2 = new Rectangle(0,0);
//
//        xL.setNode(removePoint1);
//        xM.setNode(removePoint2);


    }
    //REMOVES POINTS FROM MEMORY AND CHART
//    public void removeAllPoints(XYChart chart){
//        chart.getData().clear();
//        clear();
//        address="";
//        array= new ArrayList<>();
//        yCount=0;
//        totalCount=0;
//        maxXvalue=0;
//        lowestXvalue=0;
//    }

    void clear(XYChart<Number,Number> chart) {
        dataPoints.clear();
        dataLabels.clear();
        chart.getData().clear();
        address= new String();
        array= new ArrayList<>();
       // yCount=0;
        //totalCount=0;
        maxXvalue=0;
        lowestXvalue=0;


    }
/*   public void clear() {
        dataPoints.clear();
        dataLabels.clear();
        address=  new String();
        array= new ArrayList<>();
        yCount=0;
        totalCount=0;
        maxXvalue=0;
        lowestXvalue=0;

    }*/

    private String checkedname(String name) throws InvalidDataNameException, DuplicateException {
        if(address.contains(name)){
            throw new DuplicateException(name);
        }
        if (!name.startsWith("@")) {
            throw new InvalidDataNameException(name);
        }
        address+=name+"\n";
        array.add(name);
        instance++;
        return name;
    }
    public static class DuplicateException extends Exception {

        public DuplicateException(String name) {
            Alert errorStage = new Alert(Alert.AlertType.INFORMATION);
            errorStage.setContentText((String.format("Duplicate Name "+name+" at "+ lineNumber )));
            errorStage.showAndWait();


        }

    }
    public void lineNumberGreaterThan10Checker(){
        if(lineNumber>10){
            ErrorDialog.getDialog().show("Too many lines","You have "+lineNumber+" lines. Only showing top ten lines in text box");
        }
    }
    public int getNumberOfinstances(){
        return instance;
    }
    public int getNumberOfLabels(){
        return dataLabels.size();
    }

    public String getLabelNames(){
        if(exception){
            exception =false;
            return "";
        }
       int comma =  text.lastIndexOf(",");
       text = text.substring(0,comma);
     return text;
    }

    public ArrayList<String> getArray() {
        return array;
    }
}