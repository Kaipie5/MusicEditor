/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tunecomposer;

import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author lazarcl
 */
public class DragManager {
    
    private boolean mouseHeldDown;
    FXMLController controller;
    StateManager stateManager;

    double dragStartX;
    double dragStartY;
    
    /**
     * The type of drag that has been started.
     * Type 0 is a movement of the selected notes. 
     * Type 1 is dragging the width of the selected notes. 
     * Type 2 is dragging a box to select containing notes.
     */
    private int dragType;
    
    /**
     * Minimum width that a note can be scaled to.
     */
    private final double minNoteWidth = 5;
    
    
    /** 
     * Reference to the currently held note. Null if no note 
     * was clicked.
     */
    private NoteBox clickedNote = null;
    
    /**
     * Selection area rectangle that user can drag over notes to select.
     */
    private Rectangle area;
    private final Composition composition;
    
    /**
     * Sets up the fields of DragManager object.
     * @param list List of all rectangle notes in the composition panel.
     * @param c FXMLController object reference to refer back to.
     */
    public DragManager(
            Composition c, FXMLController f, StateManager s
    ) {
        mouseHeldDown = false;
        dragType = 0;
        dragStartX = 0;
        dragStartY = 0;
        stateManager = s;
        controller = f;
        area = null;
        composition = c;
        
    }
    
    /**
     * Set dragStartPos field to compare mouse movement during upcoming drag.
     * @param event Mouse being clicked on a Note
     */
    public void handleDragStart(MouseEvent event) {
        composition.stopSequence();
        double mouseX = event.getX();
        double mouseY = event.getY();
        dragStartX = mouseX;
        dragStartY = mouseY;
        clickedNote = controller.getRectClicked(mouseX, mouseY);
        
        if ( clickedNote == null ) {
            dragType = 2; // type: selection area
            area = controller.addAreaSelectionRect(mouseX, mouseY);
            if (!event.isShortcutDown()) {
                controller.unselectAll();
            }
            mouseHeldDown = true;
            return;
        } else if ( isEndDrag(mouseX, mouseY) ) {
            dragType = 1; // type: scaling width of boxes
        } else {
            controller.handleClickInPlace(event);
            dragType = 0; // type: moving notes
        }
        mouseHeldDown = true;
    }
    
    /**
     * Sets isNoteGrabbedForDrag back to 0 for false.
     * @param event Mouse being let go
     */
    public void handleDragEnd(MouseEvent event) {
        
        //if started outside a note and mouse didn't move then add note
        if ( dragType == 2 ) { 
            if ( dragStartX == event.getX() && dragStartY == event.getY() ) {
                controller.handleClickInPlace(event);
            }
        }
        
        if ( area != null ) {
            controller.removeCompositionPaneShape(area);
            area = null;
        }
        
        controller.finalizeRectLocations();
        updateAllNoteMidiProperties();
        mouseHeldDown = false;
        clickedNote = null;
    }
    
    /**
     * Handle any movement of pressed mouse the composition pane.
     * @param event Dragging a pressed mouse
     */
    public void handleDragging(MouseEvent event) {
        double mouseX = event.getX();
        double mouseY = event.getY();
        
        //do nothing if a note wasn't grabbed
        if ( mouseHeldDown == false ) {
            return;
        } else if ( dragType == 0 ) { 
            if ( clickedNote == null) {
                return;
            } else if ( clickedNote.isSelected == false ) {
                return; // leave if not clicking a selected note
            }
            dragSelectedBoxes(mouseX, mouseY);
        } else if ( dragType == 1 ) {
            dragSelectedEnds(mouseX);
        } else if ( dragType == 2 && area != null) {
            updateSelectionArea(mouseX, mouseY, event.isShortcutDown());
        }
    }
    
    /** 
     * Transform all selected rectangles to change given by inputs.
     * The movement is the distance between inputs and startDrag coordinates.
     * @param x The x coordinate of the mouse
     * @param y The y coordinate of the mouse
     */
    public void dragSelectedBoxes(double x, double y ) {
        double translateX = x - dragStartX;
        double translateY = y - dragStartY;
        
        for ( NoteBox note : stateManager.getCurrentNoteBoxes() ) {
            if ( note.isSelected ) {
                note.getRectangle().setTranslateX(translateX);
                note.getRectangle().setTranslateY(translateY);
            }
        }
        for ( Gesture gesture : stateManager.getCurrentGestures() ) {
            if ( gesture.isSelected() ) {
                gesture.getRectangle().setTranslateX(translateX);
                gesture.getRectangle().setTranslateY(translateY);
            }
        }        
    }
    
    /**
     * Moves the ends of all selected notes to scale with the mouse.
     * @param x The x value of where the mouse is
     */
    public void dragSelectedEnds(double x) {
        if ( clickedNote.isSelected == false) {
            return;
        }
        for ( NoteBox note : stateManager.getCurrentNoteBoxes() ) {
            if ( note.isSelected) {
                double mouseMovement = x - dragStartX;
                double newWidth = note.getDragStartWidth() + mouseMovement;
                if ( newWidth >= minNoteWidth ) {
                    note.getRectangle().setWidth(newWidth);
                } else {
                    note.getRectangle().setWidth(minNoteWidth);
                }
            }
        }
        for ( Gesture gesture : stateManager.getCurrentGestures() ) {
            if ( gesture.isSelected() ) {
                int newMaxX = gesture.getMaxXCoordinate();
                int minX = gesture.getMinXCoordinate();
                gesture.getRectangle().setWidth(newMaxX - minX);
            }
        }
    }
    
    /**
     * Finds if the input coords fall within 5 pixels of the end of a note.
     * @param inX The x coordinate of the mouse
     * @param inY The y coordinate of the mouse
     * @return returns true if grabbing the drag region of any note.
     */        
    public boolean isEndDrag(double inX, double inY) {          
        for ( NoteBox note : stateManager.getCurrentNoteBoxes() ) {            
            double rWidth = note.getRectangle().getWidth();
            double rHeight = note.getRectangle().getHeight();
            double rX = note.getRectangle().getX();
            double rY = note.getRectangle().getY();
            
            if ( inY >= rY) {
                if ( inY <= (rY + rHeight) ) {
                    if ( inX >= (rX + rWidth - 5) ) {
                        if ( inX <= (rX + rWidth) ) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    } // end isEndDrag
    
    /**
     *Resizes the selection rectangle and selects or unselects notes.
     * @param mouseX The x coordinate of the mouse
     * @param mouseY The y coordinate of the mouse
     * @param ctrlClick If the right mouse button is clicked then true.
     */
    public void updateSelectionArea(
            double mouseX, double mouseY, boolean ctrlClick
    ) {
        if (controller.nightModeToggle.isSelected()) {
            area.setStroke(Color.ORANGE);
        } else {
            area.setStroke(Color.BLUE);
        }
        resizeSelectionArea(mouseX, mouseY);
        for(NoteBox note : stateManager.getCurrentNoteBoxes()){
            if (!ctrlClick && !note.getRectangle().intersects(area.getBoundsInLocal())) {
                controller.unselect(note);
            }
        }
        for ( NoteBox note : stateManager.getCurrentNoteBoxes()) {
            if (note.getRectangle().intersects(area.getBoundsInLocal()) ) {
                controller.select(note);
            } 
        }
        
    }
    
    /**
     * Decides which quadrant the inputs are in with relation to the start
     * clicking coordinates. 
     * @param mouseX The x coordinate of the mouse
     * @param mouseY The y coordinate of the mouse
     */
    public void resizeSelectionArea(double mouseX, double mouseY) {
        if ( ( mouseX > dragStartX ) && ( mouseY < dragStartY ) ) {
            anchorRectQuadrant1(mouseX, mouseY, area);
        } else if ( ( mouseX < dragStartX ) && ( mouseY < dragStartY ) ) {
            anchorRectQuadrant2(mouseX, mouseY, area);
        } else if ( ( mouseX < dragStartX ) && ( mouseY > dragStartY ) ) {
            anchorRectQuadrant3(mouseX, mouseY, area);
        } else if ( ( mouseX > dragStartX ) && ( mouseY > dragStartY ) ) {
            anchorRectQuadrant4(mouseX, mouseY, area);
        }
    }
                             
    /**
     * Used to scale selection rectangle if its up and right of startCoords.
     * @param mouseX The x coordinate of the Mouse
     * @param mouseY The y coordinate of the Mouse
     * @param rect The rectangle to scale and move. 
     */
    public void anchorRectQuadrant1(double mouseX, double mouseY, Rectangle rect) { 
        double delWidth = Math.abs(mouseX - dragStartX);
        double delHeight = Math.abs(dragStartY - mouseY);
        rect.setWidth(delWidth);
        rect.setHeight(delHeight);
        rect.setX(dragStartX);
        rect.setY(mouseY);
    }
    
    /**
     * Used to scale selection rectangle if its up and left of startCoords.
     * @param mouseX The x coordinate of the Mouse
     * @param mouseY The y coordinate of the Mouse
     * @param rect The rectangle to scale and move. 
     */    
    public void anchorRectQuadrant2(double mouseX, double mouseY, Rectangle rect) { 
        double delWidth = Math.abs(dragStartX - mouseX);
        double delHeight = Math.abs(dragStartY - mouseY);
        rect.setWidth(delWidth);
        rect.setHeight(delHeight);
        rect.setX(mouseX);
        rect.setY(mouseY);
    }
    
    /**
     * Used to scale selection rectangle if its down and left of startCoords.
     * @param mouseX The x coordinate of the Mouse
     * @param mouseY The y coordinate of the Mouse
     * @param rect The rectangle to scale and move. 
     */    
    public void anchorRectQuadrant3(double mouseX, double mouseY, Rectangle rect) { 
        double delWidth = Math.abs(dragStartX - mouseX);
        double delHeight = Math.abs(mouseY - dragStartY);
        rect.setWidth(delWidth);
        rect.setHeight(delHeight);
        rect.setX(mouseX);
        rect.setY(dragStartY);
    }
    
    /**
     * Used to scale selection rectangle if its down and right of startCoords.
     * @param mouseX the x coordinate of the Mouse
     * @param mouseY The y coordinate of the Mouse
     * @param rect The rectangle to scale and move. 
     */    
    public void anchorRectQuadrant4(double mouseX, double mouseY, Rectangle rect) { 
        double delWidth = Math.abs(mouseX - dragStartX);
        double delHeight = Math.abs(mouseY - dragStartY);
        rect.setWidth(delWidth);
        rect.setHeight(delHeight);
        rect.setX(dragStartX);
        rect.setY(dragStartY);
    }
    
    /**
     * Updates the notes midi properties to match the rectangles location
     * and length.
     */
    public void updateAllNoteMidiProperties() {
        for ( NoteBox note : stateManager.getCurrentNoteBoxes() ) {
            note.updateDragWidth();
            note.updateNoteDuration();
            note.updateNoteStartTick();
            note.updateNotePitch();
        }
    }
}

