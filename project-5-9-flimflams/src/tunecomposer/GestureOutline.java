/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tunecomposer;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author limpicbc
 */
public class GestureOutline implements Serializable {

    protected Rectangle rectangle;    
    HashMap hashMap;
        
    /**
     * Constructs a gesture outline based of off all the noteBoxes is is to contain.
     * @param NoteBoxesInGesture 
     */
    public GestureOutline(ArrayList<NoteBox> NoteBoxesInGesture) {                
        HashMap<String, Integer> hashmapMaxMin;
        hashmapMaxMin = findMaxMinXYCoordinates(NoteBoxesInGesture);
        hashMap = hashmapMaxMin;
        setDimensions(hashmapMaxMin);
        styleBox();
    }
 
    /**
     * initializes the dimensions of the gesture outline rectangle
     * @param hashmapMaxMin 
     */
    protected void setDimensions(HashMap<String, Integer> hashmapMaxMin){
     int gestureOutlineWidth = hashmapMaxMin.get("xMax") - hashmapMaxMin.get("xMin");
     int gestureOutlineHeight = hashmapMaxMin.get("yMax") - hashmapMaxMin.get("yMin") + 10;
     this.rectangle = new Rectangle(
        hashmapMaxMin.get("xMin"),
        hashmapMaxMin.get("yMin"),
        gestureOutlineWidth,
        gestureOutlineHeight);  
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
     * Updates the coordinates of the rectangle (for drag events).
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
     * adds the styling to the gesture outline rectangle.
     */
    private void styleBox(){
        rectangle.setFill(Color.TRANSPARENT);
        rectangle.setStrokeWidth(2);
        rectangle.setStroke(Color.BLUE);
        rectangle.getStrokeDashArray().addAll(5d, 10d);
    }
    
    protected HashMap<String, Integer> findMaxMinXYCoordinates(ArrayList<NoteBox> selectedNotes) {        
        // 1. use a four element HashMap to store the mins and maxes
        HashMap<String, Integer> xyMaxesAndMins = new HashMap<>();        
        // 2. intialize local variables
        int maxXCoordinate = 0;
        int minXCoordinate = 2000;
        int maxYCoordinate = 0;
        int minYCoordinate = 1280;        
        // 3. look in the selected notes and find the max and min XY values
        for ( NoteBox note : selectedNotes ) {
            int leftXCoordinate = (int) note.getRectangle().getX();
            int rightXCoordinate = (int) (note.getRectangle().getWidth() + note.getRectangle().getX());
            int topYCoordinate = (int) note.getRectangle().getY();
            int bottomYCoordinate = (int) note.getRectangle().getY();
            if (rightXCoordinate > maxXCoordinate) {maxXCoordinate = rightXCoordinate;}
            if (leftXCoordinate < minXCoordinate) {minXCoordinate = leftXCoordinate;}
            if (topYCoordinate > maxYCoordinate) {maxYCoordinate = topYCoordinate;}
            if (bottomYCoordinate < minYCoordinate) {minYCoordinate = bottomYCoordinate;}                        
        }
        
        // 4. store the max and min values in the hashmap
        xyMaxesAndMins.put("xMax", maxXCoordinate);
        xyMaxesAndMins.put("xMin", minXCoordinate);        
        xyMaxesAndMins.put("yMax", maxYCoordinate);
        xyMaxesAndMins.put("yMin", minYCoordinate);
        
        // 5. return the hashmap
        return xyMaxesAndMins;
    }   
    
    /**
     * adds the gestureOutline to the program's overall pane
     * @param compositionPane 
     */
    public void draw(Pane compositionPane){
        compositionPane.getChildren().add(this.rectangle);
    }
    
    /**
     * removes the gestureOutline from the overall pane
     * @param compositionPane 
     */
    public void erase(Pane compositionPane){
        compositionPane.getChildren().remove(this.rectangle);
    }
    
    public Rectangle getRectangle() {
        return rectangle;
    }
    
}
