/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tunecomposer;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import javafx.scene.control.Dialog;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;

/**
 *
 * @author Quinn
 */
public class FileManager {
    
    StateManager stateManager;
    FileInputStream fileInput;
    FileOutputStream fileOutput;
    String fileName = "";
    
    public FileManager(StateManager stateManager) {
        this.stateManager = stateManager;
    }

    

    /**
     * Determines if it should ask the user if they want to save, then goes on to
     * clear the fileStream and stateManager, effectively reverting the program to
     * the state it was in when first opened.
     * @throws IOException
     * @throws ClassNotFoundException 
     */
    public void newFile() throws IOException, ClassNotFoundException {
        if (hasChanged() || fileInput == null) {
            String answer = promptUserWithQuestion("Would you like to save first?");
            if (answer == "Yes") {
                save();
            } else if (answer == "Cancel") {
                return;
            }
        }
        fileInput = null;
        fileOutput = null;
        fileName = "";
        stateManager.getPastStack().clear();
        stateManager.getFutureStack().clear();
        stateManager.getCurrentState().getAllGestures().clear();
        stateManager.getCurrentState().getAllNoteBoxes().clear();        
    }
    
    
    /**
     * Determines if it should ask the user if they want to save, then goes on to bring
     * up a file browsing window; only *.comp files can be opened, and once one is selected
     * the state object is read in and added to the stateManager.
     * @throws FileNotFoundException
     * @throws IOException
     * @throws ClassNotFoundException 
     */
    public void openFile() throws FileNotFoundException, IOException, ClassNotFoundException {
        if ((hasChanged() || fileInput == null) && stateManager.getCurrentState().getAllNoteBoxes().size() > 0) {
            String userAnswer = promptUserWithQuestion("Would you first like to save?");
            if (userAnswer == "Yes") {
                save();
            } else if (userAnswer == "Cancel") {
                return;
            }
        }
        System.out.println("I got past the first if statement");
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
            new ExtensionFilter("Composition", "*.comp")
        );
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            FileInputStream fin = new FileInputStream(selectedFile);
            fileInput = fin;
            ObjectInputStream ois = new ObjectInputStream(fin);
            stateManager.updateState((State) ois.readObject());
            stateManager.getPastStack().clear();
            stateManager.getFutureStack().clear();
            System.out.println(stateManager.getCurrentState());
            System.out.println(stateManager.getCurrentState());
        }
    }
    
    /**
     * Asks the user to input a file name, checks to make sure the input is valid.
     * If not, it will ask again, otherwise it creates a new file, saving the current
     * state object in the state manager to a *.comp file.
     * @throws FileNotFoundException
     * @throws IOException 
     */
    public void saveAs() throws FileNotFoundException, IOException {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Save As");
        dialog.setHeaderText("Write file to directory");
        dialog.setContentText("Saves the current state!");
        while(true) {
            Optional<String> result = dialog.showAndWait();
            if (result.isPresent()){
                fileName = result.get();
                if (isFileNameValid(fileName)) {
                    fileName = fileName + ".comp";
                    FileOutputStream fout = new FileOutputStream(fileName);
                    fileInput = new FileInputStream(fileName);
                    fileOutput = fout;
                    ObjectOutputStream oos = new ObjectOutputStream(fout);
                    oos.writeObject(stateManager.getCurrentState());
                    oos.close();
                    break;
                }
            } else {
                break;
            }
        }
    }    
    
    public void export(Sequence sequence) throws FileNotFoundException, IOException {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Export file");
        dialog.setHeaderText("Export current composition as MIDI file");
        dialog.setContentText("Give your composition a name!");
        while(true) {
            Optional<String> result = dialog.showAndWait();
            if (result.isPresent()){
                String midiFilename = result.get();
                if (isFileNameValid(midiFilename)) {
                    File f = new File(midiFilename + ".mid");
                    MidiSystem.write(sequence,1,f);                   
                    break;
                }
            } else {
                break;
            }
        }
    }    
    

    /**
     * Saves the currentState to the file to which the composition belongs.
     * @throws FileNotFoundException
     * @throws IOException
     * @throws ClassNotFoundException 
     */
    public void save() throws FileNotFoundException, IOException, ClassNotFoundException {
        if (fileInput == null) {
            saveAs();
        } else {
            FileOutputStream fout = new FileOutputStream(fileName);
            fileOutput = fout;
            ObjectOutputStream oos = new ObjectOutputStream(fout);
            oos.writeObject(stateManager.getCurrentState());
            oos.close();
        }
    }    
    /**
     *Returns the state object saved to the open file.
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public State getFileState() throws FileNotFoundException, IOException, ClassNotFoundException {
        FileInputStream fin = new FileInputStream(fileName);
        ObjectInputStream ois = new ObjectInputStream(fin);
        State readInState = (State) ois.readObject();
        return readInState;
    }
    

    public ObjectOutputStream getObjectOutputStream() throws IOException {
        return new ObjectOutputStream(fileOutput);
    }
    
    public ObjectInputStream getObjectInputStream() throws IOException {
        return new ObjectInputStream(fileInput);
    }
    

    /**
     * Determines if the state saved in the file and the current state in the state
     * manager are the same.
     * @return true if they are the same, false if they are not.
     * @throws IOException
     * @throws FileNotFoundException
     * @throws ClassNotFoundException 
     */
    public boolean hasChanged() throws IOException, FileNotFoundException, ClassNotFoundException {
        State fileState = null;
        State compState = stateManager.getCurrentState();
        try {
        fileState = getFileState();
        } catch (FileNotFoundException e) {
            if (!compState.getAllNoteBoxes().isEmpty()) {
                return true;
            } else {
                return false;
            }
        }
        if (fileState.equals(compState)) {
            return false;
        } else {
            return true;
        }
        
    }
    

    /**
     * Takes a string, checks to make sure there are no illegal characters in it
     * @param fileName the string to check for illegal characters.
     * @return true if the string does not contain illegal characters
     */
    private boolean isFileNameValid(String fileName) {
        ArrayList<String> illegalCharacters = new ArrayList<>(Arrays.asList("#","%","&","*","{","}"
        ,"\\",":","<",">","?","/","+","|","/"));
       for (String c : illegalCharacters) {
           if (fileName.contains(c)) {
               return false;
           }
       }
       return true;  
    }
    
    /**
     * Prompts the user to answer a "Yes" "No" or "Cancel" question that is passed
     * in as a string.
     * @param question The question to ask the user.
     * @return The string indicating their choice ('Yes','No', or 'cancel'). 
     */
        private String promptUserWithQuestion(String question) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Please confirm before continuing");
        alert.setContentText(question);

        ButtonType buttonTypeOne = new ButtonType("Yes");
        ButtonType buttonTypeTwo = new ButtonType("No");
        ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo, buttonTypeCancel);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == buttonTypeOne){
            return "Yes";
        } else if (result.get() == buttonTypeTwo) {
            return "No";
        } else {
            return "Cancel";
        }
    }

}
