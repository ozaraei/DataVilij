package dataprocessors;

import ui.AppUI;
import vilij.components.DataComponent;
import vilij.templates.ApplicationTemplate;


//import javax.print.DocFlavor;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Scanner;

//import static com.sun.tools.doclets.formats.html.markup.HtmlStyle.bar;

/**
 * This is the concrete application-specific implementation of the data component defined by the Vilij framework.
 *
 * @author Ritwik Banerjee
 * @see DataComponent
 */
public class AppData implements DataComponent {

    private TSDProcessor        processor;
    private ApplicationTemplate applicationTemplate;

    public ArrayList<String> getArray() {
        return array;
    }
    /*public void clearArray() {
        array.clear();
    }*/




    public AppData(ApplicationTemplate applicationTemplate) {
        this.processor = new TSDProcessor();
        this.applicationTemplate = applicationTemplate;
    }
    ArrayList<String> array= new ArrayList<>();
    @Override
    public void loadData(Path dataFilePath) {
        clear();
        Scanner input = null;
        try {
            input = new Scanner(dataFilePath);
        } catch (Exception e) {
        }
        String dataFull = "";
        int i=1;
        String data10="";
        while(input.hasNextLine()){
            dataFull+=input.nextLine()+"\n";
            if(i<=10){
                data10= new String(dataFull);
            }

            i++;
        }
        String[] k = dataFull.split("\n");
        for (int j = 10; j < k.length; j++) {
            array.add(k[j]+"\n");
        }
        try {
            processor.processString(dataFull);
            ((AppUI)applicationTemplate.getUIComponent()).getTextArea().setText(data10);
        } catch (Exception e) {
            return;
        }
        processor.lineNumberGreaterThan10Checker();
        displayData();
    }

    public void saveData(String dataString) throws Exception {
            processor.processString(dataString);
    }

    @Override
    public void saveData(Path dataFilePath) {
        // TODO: NOT A PART OF HW 1

    }

    @Override
    public void clear() {
        processor.clear(((AppUI)applicationTemplate.getUIComponent()).getChart());
    }

    public void displayData() {
        processor.toChartData(((AppUI) applicationTemplate.getUIComponent()).getChart());
    }

    public TSDProcessor getProcessor() {
        return processor;
    }
}

