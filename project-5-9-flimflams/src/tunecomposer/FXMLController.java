/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tunecomposer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.*;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

/**
 *
 * @author Quinn
 */
public class FXMLController implements Initializable{

    //private final int [] dragStartPos;
    protected static Color color = Color.RED;
    private static int instrument = 0; 
    protected static Color selectedStroke = Color.WHITE;
    protected static Color unselectedStroke = Color.BLACK;  
    
    /**
     * Keeps track of the default number of ticks a created noteBox should last
     */
    private static int defaultNoteDuration = 100;
    
    /**
     * Keeps track of the default volume a created noteBox should last
     */
    private static int defaultVolume = 100;
    
    /**
     * Used to compare mouse coordinates against while dragging notes.
     */
        /**
     * If note has been grabbed for a current drag, then set to 1, else it's 0.
     */
    //private final boolean isNoteGrabbedForDrag = false;
    
    /**
     * Will manage the logic of clicking and dragging mouse.
     * Doesn't directly contain any selection functions.
     * Owns a reference back to this object.
     */
    private final DragManager dragManager;    
    
    /**
     * list containing MIDI instrument values for:
     * piano, harpsichord, marimba, church organ, 
     * accordion, guitar, violin, and French horn.
     */
    private final int [] instrumentIds = {0,6,12,19,21,24,40,60};
  
    private final Composition COMPOSITION; 
    
    private final StateManager STATEMANAGER;
    
    private final ClipBoardManager CLIPBOARDMANAGER;
    
    private final FileManager FILEMANAGER;

    private double speedFactor;
    /**
     * Reference to the instrument radio selection group in player.
     */
    @FXML 
    ToggleGroup instrumentGroup;
    @FXML
    MenuItem redo;   
    @FXML
    MenuItem undo;
    @FXML
    MenuItem selectAll;
    @FXML
    MenuItem deleteAll;
    @FXML
    MenuItem group;
    @FXML
    MenuItem ungroup;
    @FXML
    MenuItem cut; 
    @FXML
    MenuItem copy;
    @FXML
    MenuItem paste;
    @FXML
    MenuItem play;
    @FXML
    MenuItem stop;
    @FXML

    MenuItem saveButton;
    @FXML
    MenuItem saveAsButton;
    @FXML
    MenuItem newButton;
    @FXML
    MenuItem exportButton;
    @FXML 
    Slider durationSlider;
    @FXML
    Slider speedSlider;
    @FXML
    Slider volumeSlider;
    @FXML
    ToggleButton nightModeToggle;
 
    /**
     * Initialize timeline and redline to display time.
     */
    private Timeline timeline = new Timeline();
    private final Rectangle redline = new Rectangle(0,0,1,1280);
    
    /**
     * Create the pane for drawing      
     */
    @FXML
    private Pane compositionPane;
    @FXML
    private Pane backgroundPane;
    @FXML
    private Pane sideBarPane;

    
    /**
     * Initializes a new MidiPlayer for this instance.
     */
    public FXMLController() {
        this.STATEMANAGER = new StateManager();
        this.COMPOSITION = new Composition(STATEMANAGER);
        this.dragManager = new DragManager(COMPOSITION, this, STATEMANAGER);
        this.CLIPBOARDMANAGER = new ClipBoardManager(STATEMANAGER, this);
        this.FILEMANAGER = new FileManager(STATEMANAGER);
        this.speedFactor = 1;
    }
    /**
     * Handles when the Undo Button is Pressed
     * @param event 
     */
   @FXML
    protected void handleUndo(ActionEvent event) {
        stop();
        STATEMANAGER.undo();
        finalizeRectLocations();
        updateComposition();
    }
    
    /**
     * Visually updates the composition based on the existing notes and gestures
     * in the current state of the state manager.
     */
    protected void updateComposition() {
        compositionPane.getChildren().clear();	
        for (NoteBox n : STATEMANAGER.getCurrentNoteBoxes()) {
            compositionPane.getChildren().add(n.getRectangle());		
        }        

        for (Gesture g : STATEMANAGER.getCurrentGestures()) {
            compositionPane.getChildren().add(g.getRectangle());		

        }       
        selectAll.setDisable(STATEMANAGER.getCurrentNoteBoxes().isEmpty());
        play.setDisable(STATEMANAGER.getCurrentNoteBoxes().isEmpty());
        deleteAll.setDisable(STATEMANAGER.getSelectedNoteBoxes().isEmpty());
        ungroup.setDisable(STATEMANAGER.getSelectedGestures().isEmpty());
        group.setDisable(STATEMANAGER.getSelectedNoteBoxes().size() < 2);
        cut.setDisable(STATEMANAGER.getSelectedNoteBoxes().isEmpty());
        copy.setDisable(STATEMANAGER.getSelectedNoteBoxes().isEmpty());
        paste.setDisable(
                //!clipBoardManager.clipboard.hasString()
                false
                
        );

        undo.setDisable(STATEMANAGER.getPastState() == null);
        redo.setDisable(STATEMANAGER.getFutureState() == null);
        saveButton.setDisable(STATEMANAGER.getCurrentNoteBoxes().isEmpty());
        saveAsButton.setDisable(STATEMANAGER.getCurrentNoteBoxes().isEmpty());
        newButton.setDisable(STATEMANAGER.getCurrentNoteBoxes().isEmpty());
        exportButton.setDisable(STATEMANAGER.getCurrentNoteBoxes().isEmpty());
    }
    
    @FXML
    protected void handleChangeInstrument(ActionEvent event) {
        STATEMANAGER.switchInstrument(instrument);
    }
    
    /*
     * Upon mouse released on the slider, default duration is updated to the value of the slider.
     * @param event 
     */
    @FXML
    protected void handleDurationSliderRelease(MouseEvent event) {
        //System.out.println(durationSlider.getValue());
        defaultNoteDuration = (int)durationSlider.getValue();
    }
    
    @FXML
    protected void handleSpeedSliderRelease(MouseEvent event) {
        speedFactor = speedSlider.getValue();
    }
    
    @FXML
    protected void handleVolumeSliderRelease(MouseEvent event) {
        defaultVolume = (int)volumeSlider.getValue();
    }
    
    @FXML
    protected void handleResetDefaults(ActionEvent event) {
        speedFactor = 1;
        speedSlider.setValue(1);
        defaultNoteDuration = 100;
        defaultVolume = 100;
        durationSlider.setValue(100);
        volumeSlider.setValue(100);
        nightModeToggle.setSelected(false);
        backgroundPane.setStyle("-fx-background-color: #ededed;");
        sideBarPane.setStyle("-fx-background-color: #f1f1f1;");        
    }
        
    @FXML
    protected void handleNightModeToggle(ActionEvent event) {
        if (nightModeToggle.isSelected()) {
            backgroundPane.setStyle("-fx-background-color: #0d0d0d;");
            sideBarPane.setStyle("-fx-background-color: #262626;");
        } else {
            backgroundPane.setStyle("-fx-background-color: #ededed;");
            sideBarPane.setStyle("-fx-background-color: #f1f1f1;");        
        }
    }
    
    
    /**
     * Handles when the Redo Button is Pressed
     * @param event 
     */
    @FXML
    protected void handleRedo(ActionEvent event) {
        stop();
        STATEMANAGER.redo();
        finalizeRectLocations();
        updateComposition();
    }
    

    /**
     * Handles when the Cut Button is Pressed
     * @param event 
     */
    @FXML
    protected void handleCut(ActionEvent event) {
        stop();
        CLIPBOARDMANAGER.cut();
        updateComposition();
    }
    
    /**
     * Handles when the Copy Button is Pressed
     * @param event 
     */
    @FXML
    protected void handleCopy(ActionEvent event) {
        stop();
        CLIPBOARDMANAGER.copy();
        updateComposition();
    }
    
    /**
     * Handles when the Paste Button is Pressed
     * @param event 
     */
    @FXML
    protected void handlePaste(ActionEvent event) {
        stop();
        CLIPBOARDMANAGER.paste();
        updateComposition();
    }

    
    /**
     * Handles the play button from the menu
     * @param event the menu selection event
     */
    @FXML 
    protected void handlePlayScaleButtonAction(ActionEvent event) {
        play();
    }  
    
    /**
     * Plays on both the composition and the timeline.
     */
    protected void play() {
        COMPOSITION.generateSequence(speedFactor, defaultVolume);
        COMPOSITION.setPlayerInstruments();
        COMPOSITION.playSequence();
        playTimeline();
        stop.setDisable(false);
    }
    
    /**
     * Stops playing action.
     */
    protected void stop() {
        timeline.stop();
        redline.setVisible(false);
        redline.setX(0);
        COMPOSITION.stopSequence();
        stop.setDisable(true);
    }
    
    /**
     * Plays the redline animation on the timeline
     * TODO: Animation will not finish when adding more notes after playing.
     */
    protected void playTimeline() {
        timeline.stop();
        redline.setX(0);
        redline.setVisible(true);
        KeyValue start = new KeyValue(redline.translateXProperty(), 0);
        KeyValue end = new KeyValue(
                redline.translateXProperty(), Composition.length
        );
        KeyFrame startFrame = new KeyFrame(Duration.ZERO, start);
        double factor = 10 * (1/speedFactor);
        KeyFrame endFrame = new KeyFrame(
                Duration.millis(factor*Composition.length), end
        );
        timeline = new Timeline(startFrame, endFrame);
        EventHandler onFinished = e -> stop();
        timeline.setOnFinished(onFinished);
        timeline.play();
    }

    /**
     * Creates and adds a NoteBox to the composition.
     * @param x
     * @param y
     */
    public void addNote(int x, int y) {
        stop();
        int yy = (int)Math.round(y/10)*10;
        //System.out.println(yy);
        String uniqueId = CLIPBOARDMANAGER.createUniqueId(STATEMANAGER.getCurrentNoteBoxes());        
        NoteBox noteBox = new NoteBox(x, yy, defaultNoteDuration, 10, instrument, uniqueId);
        compositionPane.getChildren().add(noteBox.getRectangle());
        STATEMANAGER.addNoteBox(noteBox);
        updateComposition();
    }
    
    /**
     * Returns an unfilled rectangle with zero dimension.
     * @param x
     * @param y
     * @return 
     */
    public Rectangle addAreaSelectionRect(double x, double y) {
        Rectangle rect = new Rectangle(x, y, 0, 0);
        rect.getStrokeDashArray().addAll(5d, 10d);
        rect.setFill(Color.TRANSPARENT);
        rect.setStrokeWidth(2);
        rect.setStroke(Color.BLUE);
        compositionPane.getChildren().add(rect);
        return rect;
    }
    	
    /**
     * Removes a shape from the composition pane. If object not found, then
     * nothing happens.
     * @param removeThis 
     */
    public void removeCompositionPaneShape(Shape removeThis) {
        compositionPane.getChildren().remove(removeThis);
    }
    
    /**
     * If not executing a drag, then manage creating or select notes.
     * @param event 
     */
    @FXML
    public void handleClickInPlace(MouseEvent event) {
        int mouseX = (int) event.getX();
        int mouseY = (int) event.getY();
        boolean rightClick = event.isShortcutDown();
        NoteBox rect = getRectClicked(mouseX, mouseY);
        if (rect == null) {
            clickBlankSpace(mouseX, mouseY, rightClick);
        } else {
            clickANote(mouseX, mouseY, rect, rightClick);
        }
    }
    
    /**
     * Manages selection of notes if any note is clicked
     * @param x x mouse coordinate
     * @param y y mouse coordinate
     * @param note note that was clicked
     * @param rightClick if right mouse button clicked, then true.
     */
    public void clickANote(int x, int y, NoteBox note, boolean rightClick) {
        if (rightClick) {
            select(note);
        } else {
            if (note.isSelected == false) {
                unselectAll(); 
                select(note);
            }
        }
    }
    
    /**
     * Create a selected note where clicked. If left click, then unselect all
     * others. If right click, then keep selected notes selected.
     * @param x mouse click x coordinate.
     * @param y mouse click y coordinate.
     * @param rightClick if right mouse button clicked, then true.
     */
    @FXML
    public void clickBlankSpace(int x, int y, boolean rightClick) {
        if (rightClick == false) {
            unselectAll();
        }
        addNote(x, y);    
    }    
    
    /**
     * Selects the rectangle, changing the isSelected field and stroke.
     * @param noteBox notebox to be selected
     */
    public void select(NoteBox noteBox) {
        STATEMANAGER.select(noteBox);
    }

    
    /**
     * Unselects the rectangle, changing the isSelected field and stroke.
     * @param noteBox notebox to be unselected
     */
    public void unselect(NoteBox noteBox) {
        STATEMANAGER.unselect(noteBox);
    }
    
    /**
     * Unselects all notes in noteRectangleList.
     */
    public void unselectAll() {
        STATEMANAGER.unselectAll();
    }
    
    /**
     * Selects all notes in noteRectangleList.
     */
    public void selectAll() {
        STATEMANAGER.selectAll();
    }

    /**
     * Returns either reference to rectangle note that mouse clicks in, or null.
     * @param x X coordinate to check.
     * @param y Y coordinate to check.
     * @return Reference to a NoteBox if contains inputs. Otherwise null.
     */
    public NoteBox getRectClicked(double x, double y) {
        for (NoteBox noteBox : STATEMANAGER.getCurrentNoteBoxes()) {
            if (noteBox.getRectangle().contains(x, y)) {
                return noteBox;
            }
        }
        return null;
    }   
    
    /**
     * Pass the handling of starting to click to the dragManager.
     * @param event
     */
    @FXML
    public void handleClickStart(MouseEvent event) {
        dragManager.handleDragStart(event);
    }
    
    /**
     * Pass the handling of stopping clicking to the dragManager.
     * @param event
     */
    @FXML
    public void handleClickEnd(MouseEvent event) {
        dragManager.handleDragEnd(event);
        updateComposition();
    }
    
    /**
     * Handle any movement of pressed mouse the composition pane.
     * @param event 
     */
    @FXML
    public void handleDrag(MouseEvent event) {
        dragManager.handleDragging(event);
    }

    /**
     * Updates locations of all dragged NoteBoxs, and snaps Y coordinates.
     * For use when mouse is let go.
     * Rectangles x and y coordinates must be set equal to the translations
     * that have been done on them.
     */
    @FXML
    protected void finalizeRectLocations() {
        STATEMANAGER.modifyState();
        updateComposition();
    }   

     /**
     * Initialized with our FXML, draws initial setup of composition pane.
     * @param location
     * @param resources
     */
    @FXML
    @Override
    public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
        //draw all horizontal grey lines on the composition panel.
        for (int i = 0; i < 128; i++) {
            Line line = new Line(0, i*10, 6000, i*10);
            line.setStroke(Color.web("#333333"));
            backgroundPane.getChildren().add(line);
        }
        //for (int i = 0; i < 20; i++) {
        //    Line line = new Line(i*100, 0, i*100, 1280);
        //    line.setStroke(Color.LIGHTGREY);
        //    backgroundPane.getChildren().add(line);
        //}        
        //draw the red line on the composition panel.
        timeline.setCycleCount(1);
        redline.setFill(Color.RED);
        redline.setVisible(false);
        backgroundPane.getChildren().add(redline);
        ungroup.setDisable(true);
        group.setDisable(true);
        undo.setDisable(true);
        redo.setDisable(true);
        selectAll.setDisable(true);
        deleteAll.setDisable(true);
        play.setDisable(true);
        stop.setDisable(true);
        paste.setDisable(true);

        updateComposition();

        saveButton.setDisable(true);
        saveAsButton.setDisable(true);
        newButton.setDisable(true);
        exportButton.setDisable(true);
    }
    
    /**
     * When the user clicks the "Stop playing" button, stop playing the scale.
     * @param event the button click event
     */
    @FXML 
    protected void handleStopPlayingButtonAction(ActionEvent event) {
        stop();
    }    
    
    /**
     * When the user clicks the "Exit" menu item, exit the program.
     * @param event the menu selection event
     * @throws java.io.IOException
     * @throws java.lang.ClassNotFoundException
     */
    @FXML
    protected void handleExitMenuItemAction(ActionEvent event) throws IOException, ClassNotFoundException {
        FILEMANAGER.newFile();
        System.exit(0);
    }
    
    /**
     * When the user clicks the "Select All" menu item, select all notes.
     * @param event
     */
    @FXML
    protected void handleSelectMenuItemAction(ActionEvent event) {
        STATEMANAGER.selectAll();
        updateComposition();
    }
    
    /**
     * When the user clicks the "Delete" menu item, delete all selected notes.
     * @param event
     */
    @FXML
    protected void handleDeleteMenuItemAction(ActionEvent event) {
        deleteSelected();
        updateComposition();
        stop();
    }
    
    /**
     * Deletes the selected notes and gestures
     */
    void deleteSelected() {
        ArrayList<NoteBox> notesToDelete = new ArrayList<>(STATEMANAGER.getSelectedNoteBoxes());
        ArrayList<Gesture> gesturesToDelete = new ArrayList<>(STATEMANAGER.getSelectedGestures());
        for (NoteBox note : notesToDelete) {
                compositionPane.getChildren().remove(note.getRectangle());
        }

        for (Gesture gesture : new ArrayList<Gesture>(STATEMANAGER.getCurrentGestures())) {
                compositionPane.getChildren().remove(gesture.gestureOutline.rectangle);
        }
        STATEMANAGER.deleteNotesAndGestures(notesToDelete, gesturesToDelete);
    }
    
    /**
     * Groups the currently selected notes and creates a gesture
     * @param event The group action being selected
     */
    @FXML    
    protected void handleGroup(ActionEvent event) {
        Gesture gesture = new Gesture(STATEMANAGER.getSelectedNoteBoxes());
        STATEMANAGER.addGesture(gesture);
        gesture.gestureOutline.draw(compositionPane);
        updateComposition();
    }

    /**
     * When the user clicks the "Ungroup" menu item, ungroup notes from gesture.
     * @param event The ungroup action being selected
     * @param event 
     */
    @FXML    
    protected void handleUngroup(ActionEvent event) {
        //for each selected group, ungroup.
        ArrayList<NoteBox> nodes = STATEMANAGER.getSelectedNoteBoxes();
        if (nodes.size() > 0) {
            Gesture gest;
            gest = nodes.get(0).curGesture();
            if (gest != null) {                
                for(NoteBox n : nodes){
                    n.ungroup(compositionPane, STATEMANAGER);
                }
            }
        }
        updateComposition();        
    }
    
    /**
     * Handles changes to the instrument
     * http://stackoverflow.com/questions/37902660/javafx-button-sending-arguments-to-actionevent-function
     * @param event the menu selection event
     */
    @FXML
    protected void handleInstrumentMenuItemAction(ActionEvent event) {
        instrument = Integer.parseInt(instrumentGroup.getSelectedToggle().getUserData().toString());        
    }  
    
    @FXML
    protected void handleAboutMenuItemAction(ActionEvent event) {
        Alert.AlertType type = Alert.AlertType.INFORMATION;
        Alert about = new Alert(type);
        about.setTitle("About");
        about.setHeaderText("This project was created by: \n Quinn Salkind \n Richie Farman"
                + "\n Kai McConnell \n Ben Limpich");
        about.show();

    }
    
    @FXML
    protected void handleNewMenuItemAction(ActionEvent event) throws IOException, ClassNotFoundException {

        FILEMANAGER.newFile();
        updateComposition();

    }

    @FXML
    protected void handleOpenMenuItemAction(ActionEvent event) 
            throws IOException, FileNotFoundException, ClassNotFoundException {
        FILEMANAGER.openFile();
        updateComposition();
    }
    
    @FXML
    protected void handleSaveMenuItemAction(ActionEvent event) throws ClassNotFoundException, IOException {
        FILEMANAGER.save();
        updateComposition();        
    }

    @FXML
    protected void handleExportMenuItemAction(ActionEvent event) throws ClassNotFoundException, IOException {
        FILEMANAGER.export(COMPOSITION.getSequence());
    }
    
    @FXML
    protected void handleSaveAsMenuItemAction(ActionEvent event) throws IOException {

        FILEMANAGER.saveAs();

        updateComposition();
    }
    
    @FXML
    protected void generateMIDI(ActionEvent event) {
        stop();
        String chords = "ACFG";
        List<Boolean> rhythm = COMPOSITION.generateRhythmByNotes(8);
        List<String> notes = COMPOSITION.generateNotes(chords, rhythm, 4);
        List<String> bass = COMPOSITION.getBass(chords);
        //System.out.println(notes);
        //System.out.println(bass);
        ArrayList<NoteBox> melody = parseMidi(notes, bass);
        for (NoteBox noteBox : melody) {
            compositionPane.getChildren().addAll(noteBox.getRectangle());
        }
        STATEMANAGER.addNoteBoxes(melody);
        updateComposition();
    }
    
    protected ArrayList<NoteBox> parseMidi(List<String> notes, List<String> bass) {
        ArrayList<NoteBox> melody = new ArrayList<>();
        int start = 0;
        for (String n : notes) {
            int octave = Integer.parseInt(n.substring(n.lastIndexOf("_") + 1));
            String note = n.substring(n.indexOf(".") + 1, n.indexOf("_"));
            int pitch = getPitch(note, octave);
            String uniqueId = CLIPBOARDMANAGER.createUniqueId(STATEMANAGER.getCurrentNoteBoxes());        
            NoteBox noteBox = new NoteBox(start, pitch, defaultNoteDuration, 10, instrument, uniqueId);  
            melody.add(noteBox);
            start += defaultNoteDuration;
        }
        start = 0;
        for (int i=0; i<2; i++) {
            for (String b : bass) {
                int octave = Integer.parseInt(b.substring(b.lastIndexOf("_") + 1));
                String note = b.substring(b.indexOf(".") + 1, b.indexOf("_"));
                int pitch = getPitch(note, octave);
                String uniqueId = CLIPBOARDMANAGER.createUniqueId(STATEMANAGER.getCurrentNoteBoxes());        
                NoteBox noteBox = new NoteBox(start, pitch, defaultNoteDuration*4, 10, instrument, uniqueId);  
                melody.add(noteBox);
                start += defaultNoteDuration*4;
            }
        }
        return melody;
    }
    
    protected int getPitch(String note, int scale) {
        int pitch = 0;
        switch(note) {
            case "C":
                pitch = 550;
                break;
            case "D":
                pitch = 530;
                break;
            case "E":
                pitch = 510;
                break;
            case "F":
                pitch = 500;
                break;
            case "G":
                pitch = 480;
                break;
            case "A":
                pitch = 460;
                break;
            case "B":
                pitch = 440;
                break;
            default:
                break;
        }
        if (scale < 10) {
            pitch += 240;
        }
        return pitch;
    }
    
}
