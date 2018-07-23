/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tunecomposer;

import java.util.ArrayList;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author Kai
 */
public class Gesture implements java.io.Serializable {
      
  private ArrayList<NoteBox> noteBoxes = new ArrayList<>();
  protected transient GestureOutline gestureOutline;
  
  /**
   * Constructs a Gesture
   * @param selectedNoteBoxes Noteboxes currently selected
   */
  public Gesture(ArrayList<NoteBox> selectedNoteBoxes){
      noteBoxes = selectedNoteBoxes;
      for(NoteBox curNoteBox : selectedNoteBoxes){
          curNoteBox.gestures.add(this);
      }
      gestureOutline = new GestureOutline(noteBoxes);
  }
    
  /**
   * Checks all the notes in a gesture to see if they are all selected.
   * Needed because the field isSelected is broken and doesn't accurately
   * keep track of whether or not a gesture is selected
   * @return True if all noteboxes in the gesture are selected, false otherwise
   */
  public boolean isSelected(){
      for (NoteBox note: noteBoxes){
          if(note.isSelected == false){
              return false;
          }
      }
      return true;
  } 
  
  /**
   * Selects all noteBoxes within the gesture.
   */
    public void selectAll(){
        for(NoteBox curNoteBox : noteBoxes){
            if (!curNoteBox.isSelected) {
                curNoteBox.select();
            }
        }
    }

    /**
     * deselects all noteBoxes within the gesture.
     */
    public void unselectAll(){
        for (NoteBox curNoteBox : noteBoxes) {
            if (curNoteBox.isSelected) {
                curNoteBox.unselect();
            }
        }
    }
  
    /**
     * Gets a clone of this gesture
     * @return A new instance of an identical gesture.
     */
  public Gesture getClone() {
        try {
            return (Gesture)super.clone();
        }
        catch (CloneNotSupportedException e) {
            return this; //should never happen
        }
    }
  
    /**
     * finds the max x value of the note boxes in the gesture
     * @return maxXCoordinate The maximum x value of the noteboxes of this gesture
     */
    protected int getMaxXCoordinate(){
        int maxXCoordinate = 0;
        for ( NoteBox note : noteBoxes ) {
            int rightXCoordinate = (int) (note.getRectangle().getX() + note.getRectangle().getWidth());
            if (rightXCoordinate > maxXCoordinate) {
                maxXCoordinate = rightXCoordinate;
            }          
        }
        return maxXCoordinate;
    }
    
    /**
     * finds the min x value of the note boxes in the gesture
     * @return minXCoordinate
     */
    protected int getMinXCoordinate(){
        int minXCoordinate = 2000;
        for ( NoteBox note : noteBoxes ) {
            int xCoordinate = (int) note.getRectangle().getX();         
            if (xCoordinate < minXCoordinate) {
                minXCoordinate = (int) (xCoordinate);                
            }          
        }
        return minXCoordinate;
    }
  /**
   * Gets the noteboxes of the gesture
   * @return An arraylist of all noteBoxes in this gesture.
   */
  public ArrayList<NoteBox> getNoteBoxes(){
      return noteBoxes;
  }
  
  public Rectangle getRectangle() {
      if (gestureOutline == null) {
        gestureOutline = new GestureOutline(noteBoxes);
      }
      return gestureOutline.getRectangle();
  }
  
}
