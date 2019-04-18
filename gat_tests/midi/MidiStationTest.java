package midi;

import javax.sound.midi.MidiEvent;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

import config.Themes;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import algorithm.composers.kaida.KaidaComposer;
import bols.BolBase;
import bols.tals.Teental;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.fail;

public class MidiStationTest {
	MidiStation ms;

	@Test
	public void testMidiStationInit() throws Exception {
		BolBase bolBase = new BolBase();
		KaidaComposer al = new KaidaComposer(bolBase, new Teental(), Themes.getTheme01(bolBase));
		ms = new MidiStation(al);
		try {
			ms.initMidi();
			ms.shutDown();
		} catch (Exception e) {
			fail ("Initialisation of Midi failed: " + e.getMessage());
		}
		
	}
	
	@Test
	public void testMidiStationInitAndPlayABit() throws Exception {
		BolBase bolBase = new BolBase();
		KaidaComposer al = new KaidaComposer(bolBase, new Teental(), Themes.getTheme01(bolBase));
		ms = new MidiStation(al);
		ms.initMidi();
		
		assertTrue((ms.getSequence().getTickLength() > 0), "ticklength should be greater than 0");
		System.out.println("ticklength of sequence: " + ms.getSequence().getTickLength() + " in microsecs: " + ms.getSequence().getMicrosecondLength());
		
		assertEquals(1, ms.getSequence().getTracks().length, "there should be 1 Track exactly");
		
		Track track = ms.getSequence().getTracks()[0];
		assertTrue(track.ticks() > 0, "Track.ticks() should be set greater 0");
		System.out.println("ticklength of track: " + track.ticks());
		
		String strTrack = "";
		for (int i = 0; i < track.size(); i++) {
			strTrack += toString(track.get(i));
		}
		System.out.println("track: \n" + strTrack);	
		ms.play();
		ms.shutDown(800);
	}
	public String toString(MidiEvent e) {
		String s = "(";
		s += "t:" + e.getTick() + ",";
		String c = e.getMessage().getClass().getName();
		String cs = c.replace("javax.sound.midi.","");
		s += "" + cs;//e.getMessage().getClass().getName();//classy[(classy.length-1)];
		if (cs.equals("ShortMessage")) {
		s += ",c:" + ((ShortMessage)e.getMessage()).getCommand() +
			",d1:" + ((ShortMessage)e.getMessage()).getData1() +
		     ",d2:" + ((ShortMessage)e.getMessage()).getData2() ;
		}
	
		s+=")\n";
		return s;
	}
	
}
