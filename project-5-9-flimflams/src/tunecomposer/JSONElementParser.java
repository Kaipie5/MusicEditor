/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tunecomposer;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject; 
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class JSONElementParser {
    
    ArrayList<NoteBox> addedNotes;
    ArrayList<Gesture> arrayOfDecodedGestures;
    ArrayList<NoteBox> arrayOfDecodedNoteBoxes;
    
    /**
     * Constructor of JSONelementParser
     * Creates the addedNotes array list to be filled when 
     * the parser decodes the notes
     */
    public JSONElementParser() {        
        addedNotes = new ArrayList<>();
        arrayOfDecodedGestures = new ArrayList<>();
        arrayOfDecodedNoteBoxes = new ArrayList<>();
    }
    
    /**
     * Encodes the noteboxes into a JSON array string
     * @param noteBoxes selected noteboxes to be encoded
     * @return string of the JSON array that contains the encoded notes
     */
    public String encodeNoteBoxes(ArrayList<NoteBox> noteBoxes){
        arrayOfDecodedNoteBoxes = new ArrayList<>();
        JSONArray jsonArray = new JSONArray();
        //Goes through all selected noteboxes encodes them and adds them to the JSONArray to be returned at the end
        for(NoteBox note: noteBoxes){
            JSONObject noteJson = encodeNoteBox(note);
            jsonArray.add(noteJson);
        }
        return jsonArray.toJSONString();
    }
    
    /**
     * Encodes an individual NoteBox
     * @param note the NoteBox to be encoded
     * @return returns an JSONObject to be turned into a string that 
     *  can be decoded by the encodeNoteBoxes method
     */
    private static JSONObject encodeNoteBox(NoteBox note){
        JSONObject jsonNote = new JSONObject(); 
        jsonNote.put("isNote", true);
        jsonNote.put("xCoord", note.getTick());
        jsonNote.put("yCoord", note.getYCoord() + 10);
        jsonNote.put("width", note.getDuration());
        jsonNote.put("height", 10);
        jsonNote.put("instrument", note.getInstrument());
        jsonNote.put("id", note.getId());
        return jsonNote;
    }
    
    /**
     * Creates a notebox from the decoded JSONOBject
     * @param jsonNote encoded JSONObject
     * @return created NoteBox
     */
    private static NoteBox decodeNoteBox(JSONObject jsonNote) {
        String id = (String)jsonNote.get("id");
        int x = (int)(long)jsonNote.get("xCoord");
        int y = (int)(long)jsonNote.get("yCoord");
        int width = (int)(long)jsonNote.get("width");
        int height = (int)(long)jsonNote.get("height");
        int instrument = (int)(long)jsonNote.get("instrument");       
        NoteBox note = new NoteBox(x, y, width, height, instrument, id);
        return note;
    }
    
    
    /**
     * Figures out if the decoded object is a note or a gesture
     * @param jsonOb object being decoded
     * @return true if note, false if gesture
     */
    public boolean decodeIsNote(JSONObject jsonOb){
        return (boolean)jsonOb.get("isNote");
    }
    
    /**
     * Decodes all notes and gestures
     * @param jsonArrayString JSON string holding all parsed noteboxes and gestures
     */
    public void decodeAll(String jsonArrayString){
        JSONParser jsonParser = new JSONParser();
        JSONArray jsonArray;      
        
        JSONObject jsonObject = null;
        try {
            jsonObject = (JSONObject) jsonParser.parse(jsonArrayString);
        } catch (ParseException ex) {
            Logger.getLogger(JSONElementParser.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        jsonArray = (JSONArray) jsonObject.get("all");
        for (Object object : jsonArray) {
            JSONObject element = (JSONObject) object;
            if(decodeIsNote(element)){
                NoteBox decodedNoteBox = decodeNoteBox(element);
                arrayOfDecodedNoteBoxes.add(decodedNoteBox);
                addedNotes.add(decodedNoteBox);
            }else{
                Gesture decodedGesture = decodeGesture(element);
                arrayOfDecodedGestures.add(decodedGesture);
            }
        }
    }
    
    /**
     * Encodes selected Gestures
     * @param gestures Arraylist of selected gestures
     * @return returns the arraylist of encoded gestures in string form
     */
    public String encodeGestures(ArrayList<Gesture> gestures){
        arrayOfDecodedGestures = new ArrayList<>();
        JSONArray jsonArray = new JSONArray();
        for(Gesture gest: gestures){
            JSONObject gestureJson = encodeGesture(gest);
            jsonArray.add(gestureJson);
        }
        return jsonArray.toJSONString();
    }
    
    /**
     * Encodes a singular Gesture
     * @param gest Gesture to be encoded
     * @return returns jsonObject which holds the encoded Gesture
     */
    public JSONObject encodeGesture(Gesture gest) {
        JSONObject jsonGesture = new JSONObject();
        jsonGesture.put("isNote", false);
        int i = 0;
        for(NoteBox note : gest.getNoteBoxes()){
            jsonGesture.put("id" + i, note.getId());
            i++;
        }
        return jsonGesture;
    }
    
    /**
     * decodes the gesture and also creates the new gesture
     * @param jsonString The Object that holds the encoded Gesture to be Decoded
     * @return The gesture that has been decoded from the jsonString
     */
    public Gesture decodeGesture(JSONObject jsonString) {
        ArrayList<String> idList = new ArrayList<>();
        ArrayList<NoteBox> newGestureNoteBoxes = new ArrayList<>();

        //Creates list of ids in the gesture 
        int i =0;
        for (Object ob : jsonString.values()) {
            String id = (String)jsonString.get("id" + i);
            idList.add(id);
            i++;
        }
        
        //Goes through the decoded notes that have been added and figures out if their id was part of one of the Encoded gestures
        //If they were it adds them to an arraylist to be used to create that gesture
        for(NoteBox note : addedNotes){
            if(idList.contains(note.getId())){
                newGestureNoteBoxes.add(note);
            }
        }
        
        Gesture gesture = new Gesture(newGestureNoteBoxes);
        return gesture;
    }    
}
