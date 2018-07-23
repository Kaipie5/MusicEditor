/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tunecomposer;

import java.util.ArrayList;
import java.util.Random;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

/**
 *
 * @author limpicbc
 */
public class ClipBoardManager {
    
    
    final ArrayList addedNotes;
    final Clipboard clipboard;
    protected ClipboardContent content;
    JSONElementParser jsonParser;
    final StateManager stateManager;
    final FXMLController fxmlController; 
    
    /**
     * Constructor for the ClipBoardManager
     * @param stateManager Holds all the State of the application
     * @param fxmlController Handles all the user actions
     */
    public ClipBoardManager(StateManager stateManager, FXMLController fxmlController){
        clipboard = Clipboard.getSystemClipboard();
        addedNotes = new ArrayList();
        jsonParser = new JSONElementParser();
        this.stateManager = stateManager;
        content = new ClipboardContent();
        this.fxmlController = fxmlController; 
    }
    
    /**
     * Deletes the notes and gestures and adds them to the clipboard
     */
    public void cut(){
        copy();
        fxmlController.deleteSelected();
    }
    
    /**
     * Copys the notes and gestures and adds them to the clipboard
     */
    public void copy(){
        ArrayList<NoteBox> selectedNoteBoxes = stateManager.getSelectedNoteBoxes();
        ArrayList<Gesture> selectedGestures = stateManager.getSelectedGestures();
        String jsonNoteArray = jsonParser.encodeNoteBoxes(selectedNoteBoxes);
        String jsonGestureArray = jsonParser.encodeGestures(selectedGestures);
        
        //change the ids of the old noteBoxes so they dont conflict with the new
        for(NoteBox note: selectedNoteBoxes){
            String newId = createUniqueId(stateManager.getCurrentNoteBoxes());
            note.setId(newId);
        }        
        //Formats the string being put into the clipboard to remove errors from JSON parser
        jsonGestureArray = jsonGestureArray.substring(1, jsonGestureArray.length()-1);
        if(jsonGestureArray.length() > 0){
            jsonNoteArray = jsonNoteArray.substring(0,jsonNoteArray.length()-1);
            jsonGestureArray += "]";
        }

        content.putString("{ \"all\":" + jsonNoteArray + jsonGestureArray + "}");
        clipboard.setContent(content);
    }
    
    /**
     * Pastes the notes and Gestures from the clipboard
     */
    public void paste(){
        if(clipboard.hasString()){
            
            String contents = clipboard.getString();
            
            stateManager.unselectAll();
            
            jsonParser.decodeAll(contents);
            
            stateManager.AddNotesAndGestures(jsonParser.arrayOfDecodedNoteBoxes, jsonParser.arrayOfDecodedGestures);
            

            copy();
        }
    }
    
    /**
     * Creates a unique id for each notebox
     * @param noteBoxes All existing noteBoxes
     * @return The string id for that notebox
     */
    public String createUniqueId(ArrayList noteBoxes){
        String uniqueId = generateId();
        if (idExists(uniqueId, noteBoxes)){
            uniqueId = createUniqueId(noteBoxes);
        }
        return uniqueId;
    }
    
    /**
     * Generates a random number to be used for creating a unique id
     * @return The random number as a string to be used for the unique id
     */
    private String generateId(){
        String id = "";
        int idLength = 8;
        int idNum;
        Random rand = new Random();
        for(int i = 0; i < idLength; i++){
            idNum = rand.nextInt(10);
            id += Integer.toString(idNum);
        }
        return id;
    }
    
    /**
     * Checks if a notebox id already exists
     * @param id the id being checked
     * @param noteBoxes the noteboxes being checked
     * @return true if id exists and false if it does not
     */
    private boolean idExists(String id, ArrayList<NoteBox> noteBoxes){
        for(NoteBox note : noteBoxes){
            if(note.getId() == id) {
                return true;
            }
        }
        return false;
    }
}
