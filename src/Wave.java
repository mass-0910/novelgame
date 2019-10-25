import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Wave {
	private Clip wav[] = new Clip[10];
	private int ws = 0;
	private int wav_max = 0;

	public void load(String fname, int max) {
		if (max > 10)
			max = 10;
		wav_max = max;
		try {
			for (int i = 0; i < max; i++) {
				if (wav[i] != null)
					wav[i].close();
				File file = new File("music/" + fname);
				System.out.println(file);
				AudioInputStream aistream = AudioSystem.getAudioInputStream(file);
				DataLine.Info info = new DataLine.Info(Clip.class, aistream.getFormat());
				wav[i] = (Clip) AudioSystem.getLine(info);
				wav[i].open(aistream);
				aistream.close();
			}
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
		System.gc();
	}

	public boolean isPlaying(int t) {
		return wav[t].isRunning();
	}

	public int getFrame(int t) {
		int out = 0;
		try {
			out = wav[t].getFramePosition();
		} catch (Exception e) {
		}
		return out;
	}

	public int getFrameLength(int t) {
		int out = 0;
		try {
			out = wav[t].getFrameLength();
		} catch (Exception e) {
		}
		return out;
	}

	public void volume(double vol) {
		for (int i = 0; i < wav_max; i++) {
			System.out.println(wav_max);
			FloatControl ctrl = (FloatControl) wav[i].getControl(FloatControl.Type.MASTER_GAIN);
			ctrl.setValue((float) Math.log10(vol) * 20);
		}
	}

	public void play2(int t) {
		wav[t].start();
	}

	public void play(boolean loop) {
		wav[ws].setFramePosition(0);
		if (loop)
			wav[ws].loop(Clip.LOOP_CONTINUOUSLY);
		wav[ws].start();
		ws++;
		if (ws > wav_max - 1)
			ws = 0;
	}

	public void pause(int i) {
		wav[i].stop();
	}

	public void setFrame(int i, int t) {
		wav[i].setFramePosition(t);
	}

	public void setMS(int i, int t) {
		wav[i].setMicrosecondPosition(t);
	}

	public void stop() {
		for (int ii = 0; ii < wav_max; ii++) {
			try {
				wav[ii].setFramePosition(0);
				wav[ii].stop();
			} catch (NullPointerException e) {
			}
		}
	}
}
