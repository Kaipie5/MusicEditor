/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tunecomposer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;

/**
 *
 * @author farmanrl
 */
public class Composition {
    
    protected static int length = 0;
    
    /**
     * list containing MIDI instrument values for:
     * piano, harpsichord, marimba, church organ, 
     * accordion, guitar, violin, and French horn.
     */
    private final int [] instrumentIds = {0,6,12,19,21,24,40,60};

    /**
     * One midi player is used throughout, so we can stop a scale that is
     * currently playing.
     */
    private final MidiPlayer player;
    
    private StateManager stateManager;
    
    /**
     * Initializes a new MidiPlayer for this instance.
     */
    public Composition(StateManager s) {
        this.stateManager = s;
        this.player = new MidiPlayer(100,60);
    }
    
    /**
     * Sets the instruments for player object.
     * All channels instruments are reset to piano by MidiPlayer's clear().
     */
    protected void setPlayerInstruments() {
        for ( int i=0; i < instrumentIds.length; i++ ){
            player.addMidiEvent(ShortMessage.PROGRAM_CHANGE + i, 
                instrumentIds[i], 0, 0, 0);
        }
    }
    
    /**
     * Clear player and add notes from noteRectangleList.
     */
    protected void generateSequence(double speedFactor, int volume) {
        player.clear();
        length = 0;
        for ( NoteBox note : stateManager.getCurrentNoteBoxes() ) {           
            int pitch = note.getPitch();
            int tick = note.getTick();
            int duration = note.getDuration();
            int instrument = note.getInstrument();
            player.addNote(pitch, volume, (int)(tick/speedFactor), duration, instrument, 0);
            int end = tick + duration;
            if ( end > length ) {
                length = end;
            }
        }
    }
    
    /**
     * Plays the sequencer from the beginning
     */
    protected void playSequence() {
        player.stop();
        player.restart();
        player.play();
    }
    
    private List<List<Integer>> getEdges() {
        List<Integer> c0 = Arrays.asList(1, 2, 4, 6, 7);
	List<Integer> c1 = Arrays.asList(2, 4);
	List<Integer> c2 = Arrays.asList(3, 4);
	List<Integer> c3 = Arrays.asList(4);
	List<Integer> c4 = Arrays.asList(6, 7);
	List<Integer> c5 = Arrays.asList(6);
	List<Integer> c6 = Arrays.asList(4, 5, 7);
	List<Integer> c7 = Arrays.asList(4, 6, 7, 8, 9, 11);
	List<Integer> c8 = Arrays.asList(7, 9);
	List<Integer> c9 = Arrays.asList(7, 8, 9, 10, 11);
	List<Integer> c10 = Arrays.asList(9, 11);
	List<Integer> c11 = Arrays.asList(7, 9, 10, 11, 12);
	List<Integer> c12 = Arrays.asList(11, 13);
	List<Integer> c13 = Arrays.asList(14);
	List<Integer> c14 = Arrays.asList(13, 11);

	List<List<Integer>> edges = Arrays.asList(c0, c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12, c13, c14);

        List<List<Integer>> adjustedEdges = new ArrayList<>();
	for (int i=0; i<edges.size(); i++) {
            List<Integer> newArray = new ArrayList<>();
            for (Integer elem : edges.get(i)) {
		newArray.add(elem-i);
            }
            adjustedEdges.add(newArray);
        }
	return adjustedEdges;
    }
    
    public List<String> centeredScale(char letter, int startOctave) {
	List<String> scale = new ArrayList<>();
	String A = "CDEFGAB";
	char start = A.charAt(A.indexOf(letter));
	for (int i=0; i<15; i++) {
		char new_letter = A.charAt((start+i) % A.length());
		int octave = startOctave + (start+i)/A.length();
		scale.add("midi." + new_letter + "_" + Integer.toString(octave));
        }
	return scale;
    }
    
    public int step(int currNoteID, List<List<Integer>> edges) {
	List<Integer> nextSteps = edges.get(currNoteID);
        Random rand = new Random();
	return currNoteID + nextSteps.get(rand.nextInt(nextSteps.size()));
    }
    
    public List<String> generateNotes(String chords, List<Boolean> rhythm, int startOctave) {
	List<Integer> startNotes = Arrays.asList(4, 7, 9);
	List<String> midi_sequence = new ArrayList<>();
	for (char key : chords.toCharArray()) {
            Random rand = new Random();
            Integer currNote = startNotes.get(rand.nextInt(startNotes.size()));
            List<Integer> path = new ArrayList<>();
            for (int i=0; i<8; i++) {
		if (rhythm.get(i)) {
                    path.add(currNote);
                    currNote = step(currNote, getEdges());
                } else {
		    path.add(0);
                }
            }
            List<String> scale = centeredScale(key, startOctave);
            for (int n : path) {
                if (n != 0) {
                    midi_sequence.add(scale.get(n));
                } else {
                    midi_sequence.add("");
                }
            }
        }
	return midi_sequence;
    }

    public List<String> getBass(String chords) {
	List<String> midivalues = new ArrayList<>();
	for (char c : chords.toCharArray()) {
            midivalues.add("midi." + c + "_4");
        }
	return midivalues;
    }

    public List<Boolean> generateRhythmByNotes(int numnotes) {
        List<Boolean> rhythm = new ArrayList<>();
	for (int i=0; i<numnotes; i++) {       
            rhythm.add(true);
        } for (int i=0; i<8 - numnotes; i++) {       
            rhythm.add(false);
        }
	Collections.shuffle(rhythm);
	return rhythm;
    }

               
    /**
     * Stops the timeline and player.
     */
    protected void stopSequence() {
        player.stop();
    }
    
    protected Sequence getSequence() {
        return player.getSequence();
    }
}
