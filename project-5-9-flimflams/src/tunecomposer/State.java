
package tunecomposer;
import java.util.*;

/**
 *
 * @author Quinn
 */
public class State implements java.io.Serializable {
    
    private ArrayList<NoteBox> allNoteBoxes = new ArrayList<>();
    private ArrayList<Gesture> allGestures = new ArrayList<>();
    
    public State(ArrayList<NoteBox> noteBoxes, ArrayList<Gesture> gestures) {
        //copy everything in arraylists too deepcopy.
        for (NoteBox n : noteBoxes) {
            allNoteBoxes.add(n.getClone());
        }
        for (Gesture g: gestures) {
            allGestures.add(g.getClone());
        }
    }
    
    /**
     * Gets all noteboxes of the state
     * @return All NoteBoxes that exist in this state.
     */
    public ArrayList<NoteBox> getAllNoteBoxes() {
        return allNoteBoxes;
    }
    
    /**
     * Gets all gestures of the state
     * @return All gestures that exist in this state
     */
    public ArrayList<Gesture> getAllGestures() {
        return allGestures;
    }
    
    
    /**
     * Turns the state to a string
     * @return The string representing this state
     */
    @Override
    public String toString() {
        String result = "";
        //result+= "[" + allNoteBoxes.size() + "," + allGestures.size() + "]";
        result+= "[" + allNoteBoxes.size() + "," + allGestures.size() + "," + this.hashCode()+ "]";
        return result;
    }
    

}