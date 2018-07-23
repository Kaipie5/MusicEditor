/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tunecomposer;

import java.io.Serializable;
import java.util.ArrayList;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author limpicbc
 */
public class NoteBox implements Serializable {
    
    private transient Rectangle rectangle;
    private String id;
    private int pitch;
    private int duration;
    private int tick;
    int yCoord;
    private int instrument;
    public boolean inGesture;
    private double dragStartWidth;
    public boolean isSelected;
    //The groups that this note is a part of with the last group being the top group
    public ArrayList<Gesture> gestures = new ArrayList<>();
    protected static Color selectedStroke = Color.WHITE;
    protected static Color unselectedStroke = Color.BLACK;    
    protected static Color color = Color.RED;
        
    /**
     * Constructor for a noteBox
     * @param x tick start, and x coordinate
     * @param y pitch and y coordinate
     * @param width width of the rectangle
     * @param height height of the rectangle
     * @param i instrument represented by an integer
     * @param uniqueId unique id for each notebox
     */
    public NoteBox(
        int x, 
        int y, 
        int width, 
        int height, 
        int i, 
        String uniqueId
    ) {
        
        selectColor(i);
        this.rectangle = new Rectangle(x, y, width, height);
        this.rectangle.setFill(color);
        this.rectangle.setStrokeWidth(2);
        pitch = 127 - (y/10);
        duration = width;
        tick = x;
        instrument = i;
        id = uniqueId;
        yCoord = y;
        
        dragStartWidth = width;
        
        inGesture = false;
        
        select();
    }
    
    /**
     * Selects the color of the notebox based on the instrument it uses
     * @param i instrument symbolized by an integer
     */
    private void selectColor(int i){
        switch (i) {
            case 0:
                color = Color.RED;
                break;
            case 1:
                color = Color.ORANGE;
                break;
            case 2:
                color = Color.GOLD;
                break;
            case 3:
                color = Color.GREEN;
                break;
            case 4:
                color = Color.BLUE;
                break;
            case 5:
                color = Color.INDIGO;
                break;
            case 6:
                color = Color.VIOLET;
                break;
            case 7:
                color = Color.BROWN;
                break;
            default:
                color = Color.RED;
                break;
        }
    }
    
    /**
     * selectes the notebox and the biggest gesture it is a part of
     */
    public void select() {
        isSelected = true;
        rectangle.setStroke(selectedStroke);      
        if (gestures.size() > 0) {
            Gesture root = gestures.get(gestures.size()-1);
            root.selectAll();
        }
    }
    
    public void switchInstrumentID(int instrumentID) {
        this.instrument = instrumentID;
        selectColor(instrumentID);
        this.rectangle.setFill(color);
    }
    
    /**
     * unselectes the notebox and the biggest gesture it is a part of
     */
    public void unselect(){
        isSelected = false;
        rectangle.setStroke(unselectedStroke);
        if (gestures.size() > 0) {
            Gesture root = gestures.get(gestures.size()-1);
            root.unselectAll();
        }
    }
    
    /**
     * Gets the unique id for this notebox
     * @return The unique id for this NoteBox
     */
    public String getId(){
        return this.id;
    }
    
    /**
     * Sets the unique id for this notebox
     * @param newId The new unique id for this notebox
     */
    public void setId(String newId){
        this.id = newId;
    }
    
    /**
     * Gets the color of the notebox
     * @return The color of the notebox
     */
    public Color getColor(){
        return NoteBox.color;
    }
    
    /**
     * gets a clone of this notebox
     * @return The notebox that was created
     */
    public NoteBox getClone() {
        try {
            return (NoteBox)super.clone();
        }
        catch (CloneNotSupportedException e) {
            return this; //should never happen
        }
    }
    
    /**
     * Gets The largest gesture of this notebox
     * @return the largest gesture of this notebox
     */
    public Gesture curGesture(){
        if(gestures.size() > 0){
            return gestures.get(gestures.size()-1);
        }else{
            return null;
        }
    }
    
    /**
     * Ungroups the notebox from the largest gesture it is a part of
     * @param compositionPane the pane on which the notebox is drawn
     * @param stateManager the stateManager which handles state
     */
    public void ungroup(Pane compositionPane, StateManager stateManager){
        if(gestures.size() > 0){
            Gesture gesture = gestures.get(gestures.size()-1);
            stateManager.deleteGesture(gesture);
            gesture.gestureOutline.erase(compositionPane);
            gestures.remove(gesture);
        } 
    }
    
    /**
     * adds a gesture to the arraylist keeping track of the noteboxes gestures 
     * it is a part of
     * @param gest The gesture being added
     */
    public void addGesture(Gesture gest){
        gestures.add(gest);
    }

    /**
     * Return the old width of the rectangle before it was scaled.
     * @return 
    */
    public double getDragStartWidth() {
        return dragStartWidth;
    }
    
    /**
     * Store the width of the rectangle before scaling it.
     */
    public void updateDragWidth() {
        dragStartWidth = rectangle.getWidth();
    }
    
    /**
     * Updates duration of note to match the width of rectangle.
     */
    public void updateNoteDuration() {
        this.setDuration((int) rectangle.getWidth());
    }
    
    /**
     * Updates pitch of note to match the location of rectangle.
     */
    public void updateNotePitch() {
        double yValue;
        yValue = rectangle.getY();
        int newPitch = (int) (127 - (yValue/10));
        this.setPitch( newPitch );
    }
    
    /**
     * Updates start tick of note to match the location of rectangle. 
     */
    public void updateNoteStartTick() {
        this.setTick((int) rectangle.getX());
    }
    
    /**
     * Snaps the rectangle's height to the nearest 10s place.
     */
    public void snapY() {
        double curY =  rectangle.getY();
        int snapY = (int) Math.round(curY/10)*10;
        rectangle.setY(snapY);
    }
    
    
    /**
     * Set rectangle's coordinates to where the translations have moved it.
     * Translations move the rectangle relative to the objects x and y coord.
     * This moves the x and y cord to match where the rectangle appears to be.
     * Resets the layout and tranlations to 0.
     */
    public void updateCoordsFromTranslation() {
       double translateX = rectangle.getTranslateX();
       double translateY = rectangle.getTranslateY();
       double curX = rectangle.getX();
       double curY = rectangle.getY();
       
       rectangle.setX(curX + translateX);
       rectangle.setY(curY + translateY);

       rectangle.setTranslateX(0);
       rectangle.setTranslateY(0);
       rectangle.setLayoutX(0);
       rectangle.setLayoutY(0);
    }
    
    /**
     * Checks if this notebox is in a gesture
     * @return true if its in a gesture false if it is not
     */
    public boolean isInGesture() {
        return inGesture;
    }
    
    /**
     * Changes the noteboxes boolean indicating it is in a gesture to false
     */
    public void removeFromGesture() {
        inGesture = false;
    }
    
    /**
     * Changes the noteboxes boolean indicating it is in a gesture to true
     */
    public void addToGesture() {
        inGesture = true;
    }
    
    /**
     * Sets the pitch of the noteBox
     * @param p the new pitch to be set as this noteboxes pitch
     */
    public void setPitch(int p) {
        pitch = p;
    }
    
    /**
     * sets the duration of the notebox
     * @param d the new duration to be set as this noteboxes duration
     */
    public void setDuration(int d) {
        duration = d;
    }
    
    /**
     * sets the startTick of the notebox
     * @param t the new startTick to be set as this noteboxes startTick
     */
    public void setTick(int t) {
        tick = t;
    }
    
    /**
     * Sets the instrument of the NoteBox
     * @param i The new instrument to be set as this noteboxes instrument
     */
    public void setInstrument(int i) {
        instrument = i;
    }
    
    /**
     * Gets the pitch of the Notebox
     * @return the pitch of this Notebox
     */
    public int getPitch() {
        return pitch;
    }
    
    /**
     * Gets the duration of the Notebox
     * @return the duration of this notebox
     */
    public int getDuration() {
        return duration;
    }
    
    /**
     * Gets the startTick of the notebox
     * @return The startTick of this notebox
     */
    public int getTick() {
        return tick;
    }
    
    /**
     * Gets the instrument of the notebox
     * @return the instrument of this notebox
     */
    public int getInstrument() {
        return instrument;
    }
    
    /**
     * gets the rectangle of the notebox
     * @return the rectangle of this notebox
     */
    public Rectangle getRectangle(){
        if (rectangle != null) {
            return rectangle;
        } else {
            rectangle = new Rectangle(tick, yCoord, duration, 10);
            selectColor(instrument);
            this.rectangle.setFill(color);
            this.rectangle.setStrokeWidth(2);
            if (this.isSelected) {
                rectangle.setStroke(selectedStroke);      
            } else {
                rectangle.setStroke(unselectedStroke);                  
            }
            return rectangle;
        }
    }
    
    /**
     * gets the y coordinate of the notebox
     * @return the y coordinate of this notebox
     */
    public int getYCoord() {
        return (int) this.rectangle.getY();
    }
    
    
}
