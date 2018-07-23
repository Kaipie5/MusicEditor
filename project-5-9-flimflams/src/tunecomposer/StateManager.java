
package tunecomposer;
import java.util.*;

/**
 *
 * @author Quinn
 */
public class StateManager {
    
    /**
     * Contains all past states, which a user could conceivably access via an "undo".
     */
    private final Stack<State> PASTSTATESTACK;
    
    /**
     * State representing the current state of the composition.
     */
    private State currentState;
    
    /**
     * Contains all future state, which a user could conceivable access via a "redo".
     */
    private final Stack<State> FUTURESTATESTACK;
    
    /**
     * Constructor of the StateManager
     */
    public StateManager() {
        PASTSTATESTACK = new Stack();
        FUTURESTATESTACK = new Stack();
        currentState = new State(new ArrayList<>(), new ArrayList<>());
    }
    
    /**
     * Gets the current state of the StateMAnager
     * @return The current state of this StateManager
     */
    public State getCurrentState() {
        return currentState;
    }
    
    /**
     *returns the immediately previous state, without changing the currentState.
     * @return Null or the most recent past state from the stack
     */
    public State getPastState() {
        if (!PASTSTATESTACK.isEmpty()) {
            return PASTSTATESTACK.peek();
        }
        return null;
    }
    
    /**
     * Returns the immediately next future state, without changing currentState
     * @return Null or the immediate future state from the stack
     */
    public State getFutureState() {
        if (!FUTURESTATESTACK.isEmpty()) {
            return FUTURESTATESTACK.peek();
        }
        return null;
    }
    
    public Stack<State> getFutureStack() {
        return FUTURESTATESTACK;
    }
    
    public Stack<State> getPastStack() {
        return PASTSTATESTACK;
    }
    
    /**
     * Returns all the noteBoxes of the currentState
     * @return an ArrayList of NoteBoxes
     */
    public ArrayList<NoteBox> getCurrentNoteBoxes() {
        State state = getCurrentState();
        return state.getAllNoteBoxes();
        
    }
    
    /**
     * Returns all gestures of the currentState.
     * @return an ArrayList of gestures
     */
    public ArrayList<Gesture> getCurrentGestures() {
        State state = getCurrentState();
        return state.getAllGestures();
    }
    
    /**
     * Adds a noteaBox by creating a new state object, and updating the StateManager.
     * @param noteBox 
     */
    public void addNoteBox(NoteBox noteBox) {
        ArrayList<NoteBox> noteBoxList = getCurrentNoteBoxes();
        ArrayList<Gesture> gestureList = getCurrentGestures();
        State newState = new State(noteBoxList, gestureList);
        newState.getAllNoteBoxes().add(noteBox);
        updateState(newState);
    }
    
    public void addNoteBoxes(ArrayList<NoteBox> newNoteBoxes) {
        ArrayList<NoteBox> noteBoxList = getCurrentNoteBoxes();
        ArrayList<Gesture> gestureList = getCurrentGestures();
        State newState = new State(noteBoxList, gestureList);
        for (NoteBox n : newNoteBoxes) {
            newState.getAllNoteBoxes().add(n);
        }
        updateState(newState);
    }
    
    /**
     * Adds a Gesture by creating a new state object, and updating the StateManager.
     * @param gesture
     */
    public void addGesture(Gesture gesture) {
        ArrayList<NoteBox> noteBoxList = getCurrentNoteBoxes();
        ArrayList<Gesture> gestureList = getCurrentGestures();
        State newState = new State(noteBoxList, gestureList);
        newState.getAllGestures().add(gesture);
        updateState(newState);
    }
    
    /**
     * Deletes a gesture by creating a new state and updating the StateController.
     * @param gesture 
     */
    public void deleteGesture(Gesture gesture) {
        ArrayList<NoteBox> noteBoxList = getCurrentNoteBoxes();
        ArrayList<Gesture> gestureList = getCurrentGestures();
        State newState = new State(noteBoxList, gestureList);
        newState.getAllGestures().remove(gesture);
        updateState(newState);
       
    }
    
    public void deleteNotesAndGestures(ArrayList<NoteBox> noteBoxes, ArrayList<Gesture> gestures) {
        ArrayList<NoteBox> noteBoxList = getCurrentNoteBoxes();
        ArrayList<Gesture> gestureList = getCurrentGestures();
        State newState = new State(noteBoxList, gestureList);
        for (NoteBox n : noteBoxes) {
            newState.getAllNoteBoxes().remove(n);
        }
        for (Gesture g : gestures) {
            newState.getAllGestures().remove(g);
        }
        updateState(newState);
    }
    
    public void AddNotesAndGestures(ArrayList<NoteBox> noteBoxes, ArrayList<Gesture> gestures) {
        ArrayList<NoteBox> noteBoxList = getCurrentNoteBoxes();
        ArrayList<Gesture> gestureList = getCurrentGestures();
        State newState = new State(noteBoxList, gestureList);
        for (NoteBox n : noteBoxes) {
            newState.getAllNoteBoxes().add(n);
        }
        for (Gesture g : gestures) {
            newState.getAllGestures().add(g);
        }
        updateState(newState);
    }
    
    /**
     * Deletes a noteaBox by creating a new state and updating the StateController
     * @param noteBox 
     */
    public void deleteNoteBox(NoteBox noteBox) {
        ArrayList<NoteBox> noteBoxList = getCurrentNoteBoxes();
        ArrayList<Gesture> gestureList = getCurrentGestures();
        State newState = new State(noteBoxList, gestureList);
        newState.getAllNoteBoxes().remove(noteBox);
        updateState(newState);
    }
    
    public void deleteNoteBoxes(ArrayList<NoteBox> noteBoxes) {
        ArrayList<NoteBox> noteBoxList = getCurrentNoteBoxes();
        ArrayList<Gesture> gestureList = getCurrentGestures();
        State newState = new State(noteBoxList, gestureList);
        for (NoteBox n : noteBoxes) {
            newState.getAllNoteBoxes().remove(n);
        }
        updateState(newState);
    }
    
    /**
     * Modifies the state by creating a new state object to represent the shifted elements.
     */
    public void modifyState() {
        ArrayList<NoteBox> noteBoxList = getCurrentNoteBoxes();
        ArrayList<Gesture> gestureList = getCurrentGestures();
        State newState = new State(noteBoxList, gestureList);
        for ( NoteBox note : newState.getAllNoteBoxes()) {
            if ( note.isSelected ) {
                note.updateCoordsFromTranslation();
                note.snapY();          
            }
        }
        for ( Gesture gesture : newState.getAllGestures() ) {
            if ( gesture.isSelected() ) {
                gesture.gestureOutline.updateCoordsFromTranslation();
                gesture.gestureOutline.snapY();
            }
        }
    }
    
    public void switchInstrument(int id) {
        ArrayList<NoteBox> noteBoxList = getCurrentNoteBoxes();
        ArrayList<Gesture> gestureList = getCurrentGestures();
        State newState = new State(noteBoxList, gestureList);
        for (NoteBox n: newState.getAllNoteBoxes()) {   
            if (n.isSelected) {
                n.switchInstrumentID(id);
            }
        }
        updateState(newState);
    }
    
    /**
     * Selects a Notebox
     * @param noteBox The notebox to be selected
     */
    protected void select(NoteBox noteBox) {
       noteBox.select();
    }
    
    /**
     * Selects all noteboxes
     */
    protected void selectAll() {
        for (NoteBox noteBox : getCurrentNoteBoxes()) {
            noteBox.select();
        }
    }
    
    /**
     * Unselects a NoteBox
     * @param noteBox The notebox to be unselected
     */
    protected void unselect(NoteBox noteBox) {
       noteBox.unselect();
    }    
    
    /**
     * Unselects all noteboxes
     */
    protected void unselectAll() {
        for (NoteBox noteBox : getCurrentNoteBoxes()) {
            noteBox.unselect();
        }
    }
    
    /**
     * Returns an arrayList of the currently selected noteBoxes.
     * @return The arraylist of all selected Noteboxes
     */
    protected ArrayList<NoteBox> getSelectedNoteBoxes() {
        ArrayList<NoteBox> selectedNoteBoxes = new ArrayList<>();
        for (NoteBox s : getCurrentNoteBoxes()) {
            if (s.isSelected) {
                selectedNoteBoxes.add(s);
            }
        }
        return selectedNoteBoxes;
    }
    
    /**
     * Returns an arrayList of the currently selected gestures.
     * @return All currently selected Gestures
     */
    protected ArrayList<Gesture> getSelectedGestures() {
        ArrayList<Gesture> selectedGestures = new ArrayList<>();
        for (NoteBox s : getCurrentNoteBoxes()) {
            if (s.isSelected) {
                int i = 0;
                for (Gesture gesture : s.gestures) {
                    if (!selectedGestures.contains(gesture)) {
                        selectedGestures.add(i, gesture);
                        i++;
                    }
                }
            }
        }
        return selectedGestures;
    }
    
    /**
     * Changes the current state to be the immediately previous state in the stack.
     */
    public void redo() {
        if (!FUTURESTATESTACK.isEmpty()) {
            PASTSTATESTACK.push(currentState);
            currentState = FUTURESTATESTACK.pop();
        }
    }
    
    /**
     * Changes the current state to be the immediately next state in the stack.
     * @return The current state of this StaeManager
     */
    public State undo() {
        if (!PASTSTATESTACK.isEmpty()) {
            FUTURESTATESTACK.push(currentState);
            currentState = PASTSTATESTACK.pop();
        }
        return currentState;
    }
    
    /**
     * Updates the StateManager's currentState and past state stack based on a new state object.
     * @param newState The state to be added to the StateManager stack
     */
    public void updateState(State newState) {
        FUTURESTATESTACK.clear(); //cannot redo if a change has been made.
        PASTSTATESTACK.push(currentState);
        currentState = newState;
    }
    
    /**
     * Turns the StageManger into a string
     * @return The string which represents this statemanager
     */
    @Override
    public String toString() {
        String result = "";
        result += "pastStateStack: " + PASTSTATESTACK + "\n";
        result += "currentState: " + currentState + "\n";
        result += "futureStateStack: " + FUTURESTATESTACK + "\n";
        return result;
    }
    
    
}