package cmd;
//Sparking Animation class by ViveTheJoestar
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class Animation {	
	private boolean isClosed, isValid, wii;
	private int numEvents;
	private Event effEvent;
	private Event[] allEvents;
	private File anmFile;
	private RandomAccessFile anm;
	
	public Animation(File anmFile, boolean bigEndian) throws IOException {
		this.anmFile = anmFile;
		anm = new RandomAccessFile(anmFile, "rw");
		wii = bigEndian;
		anm.seek(0);
		byte headerVal = anm.readByte();
		numEvents = anm.read();
		byte[] offsetBytes = new byte[2];
		if (headerVal == 97) {
			anm.seek(4);
			anm.read(offsetBytes);
			int offsetAtPos4 = ValueHandler.getVal(offsetBytes, wii);
			anm.seek(8);
			anm.read(offsetBytes);
			int offsetAtPos8 = ValueHandler.getVal(offsetBytes, wii);
			if (offsetAtPos4 == 0 && offsetAtPos8 == 0) isValid = true;
		}
		if (isValid) {
			allEvents = new Event[numEvents];
			for (int eventCnt = 0; eventCnt < numEvents; eventCnt++) {
				/* 144 is the start of the first animation command/event; however,
				 * the first 4 bytes go unused, so the program has to skip them.
				 * An animation command/event (with padding) takes up 16 bytes. */
				byte[] eventBytes = new byte[8], keyframeBytes = new byte[2];
				anm.seek(148 + eventCnt * 16);
				anm.read(eventBytes);
				anm.read(keyframeBytes);
				int keyframe1 = ValueHandler.getVal(keyframeBytes, wii);
				anm.read(keyframeBytes);
				int keyframe2 = ValueHandler.getVal(keyframeBytes, wii);
				allEvents[eventCnt] = new Event(eventBytes, keyframe1, keyframe2);
			}
			byte[] eventBytes = new byte[4];
			anm.seek(144 + numEvents * 16);
			anm.read(eventBytes);
			effEvent = new Event(eventBytes);
		}
	}
	
	public boolean isClosed() {
		return isClosed;
	}
	public boolean isValid() {
		return isValid;
	}
	public int getDuration() throws IOException {
		byte[] shortBytes = new byte[2];
		anm.seek(2);
		anm.read(shortBytes);
		return ValueHandler.getVal(shortBytes, wii);
	}
	public int getNumEvents() throws IOException {
		return numEvents;
	}
	public Event getEffEvent() throws IOException {
		byte[] eventBytes = new byte[4];
		anm.seek(144 + numEvents * 16);
		anm.read(eventBytes);
		return new Event(eventBytes);
	}
	public Event[] getAllEvents() throws IOException {
		return allEvents;
	}
	public void addOrRemoveEvent(int eventIndex, int keyframe, boolean add, boolean reopen) throws IOException {
		RandomAccessFile newAnm = reopen ? new RandomAccessFile(anmFile, "rw") : anm;
		newAnm.seek(1);
		numEvents = add ? numEvents + 1 : numEvents - 1;
		//Handle underflow and overflow (since the number of events is only stored as one byte)
		if (numEvents < 0) {
			numEvents = 0;
			return;
		}
		if (numEvents > 255) {
			numEvents = 255;
			return;
		}
		if (numEvents < 0 || numEvents > 255) return;
		newAnm.writeByte(numEvents);
		int eventPos = 144 + eventIndex * 16;
		newAnm.seek(eventPos);
		byte[] remnant = new byte[(int) (newAnm.length() - eventPos - 16)];
		newAnm.seek(eventPos + 16);
		newAnm.read(remnant);
		newAnm.seek(eventPos);
		if (add) {
			newAnm.seek(eventPos + 16);
			byte[] newEventBytes = new byte[16];
			byte[] keyframeBytes = ValueHandler.getValBytes(keyframe, true, wii);
			System.arraycopy(keyframeBytes, 0, newEventBytes, 12, 2);
			newAnm.write(newEventBytes);
		}
		else newAnm.setLength((int) newAnm.length() - 16);
		newAnm.write(remnant);
		//Fix bone offsets
		for (int bonePos = 6; bonePos < 142; bonePos += 2) {
			newAnm.seek(bonePos);
			byte[] boneBytes = new byte[2];
			newAnm.read(boneBytes);
			int boneOffset = ValueHandler.getVal(boneBytes, wii);
			if (boneOffset != 0) {
				boneOffset = add ? boneOffset + 4 : boneOffset - 4;
				boneBytes = ValueHandler.getValBytes(boneOffset, true, wii);
				newAnm.seek(bonePos);
				newAnm.write(boneBytes);
			}
		}
		if (reopen) newAnm.close();
		//Reset all events (obviously, only if there is more than one)
		if (numEvents > 1) {
			int[] arraySizes = new int[2];
			arraySizes[0] = add ? eventIndex + 1 : eventIndex;
			arraySizes[1] = add ? numEvents - eventIndex - 2 : numEvents - eventIndex;
			Event[] allEventsCopy = new Event[numEvents];
			Event[] firstEvents = new Event[arraySizes[0]], lastEvents = new Event[arraySizes[1]];
			System.arraycopy(allEvents, 0, firstEvents, 0, firstEvents.length);
			System.arraycopy(allEvents, eventIndex + 1, lastEvents, 0, lastEvents.length);
			System.arraycopy(firstEvents, 0, allEventsCopy, 0, firstEvents.length);
			if (add) {
				allEventsCopy[eventIndex + 1] = new Event(new byte[8], keyframe);
				System.arraycopy(lastEvents, 0, allEventsCopy, eventIndex + 2, lastEvents.length);
			}
			else System.arraycopy(lastEvents, 0, allEventsCopy, eventIndex, lastEvents.length);
			allEvents = allEventsCopy;
		}
		else {
			if (add) {
				allEvents = new Event[1];
				allEvents[0] = new Event(new byte[8], keyframe);
			}
		}
	}
	/* This method is just to inform the program that the animation was modified for the first time.
	 * That is why no other method is able to close the RandomAccessFile stream. */
	public void closeStream() throws IOException {
		isClosed = true;
		anm.close();
	}
	/* This method on the other hand is to make sure the animation can be modified more than once.
	 * Normally, once a RandomAccessFile stream is closed, it cannot be reopened.
	 * Rather than constructing a new Animation object each time, the data fields are preserved, but a new stream is made. */
	public void reopenStream(File newFile, int duration, int keyframe, int keyframeIndex) throws IOException {
		RandomAccessFile newAnm = (newFile == null) ? new RandomAccessFile(anmFile, "rw") : new RandomAccessFile(newFile, "rw");
		//Copy the old ANM contents to a new one if specified
		if (newFile != null) {
			byte[] oldAnmBytes = new byte[(int) anm.length()];
			anm.seek(0);
			anm.readFully(oldAnmBytes);
			newAnm.seek(0);
			newAnm.write(oldAnmBytes);
			newAnm.setLength(oldAnmBytes.length);
		}
		for (int eventCnt = 0; eventCnt < numEvents; eventCnt++) {
			newAnm.seek(148 + eventCnt * 16);
			newAnm.write(allEvents[eventCnt].getFlags());
			newAnm.write(ValueHandler.getValBytes(allEvents[eventCnt].getKeyframe(), true, wii));
		}
		newAnm.seek(144 + numEvents * 16);
		newAnm.write(effEvent.getFlags());
		newAnm.seek(2);
		newAnm.write(ValueHandler.getValBytes(duration, true, wii));
		newAnm.seek(156 + keyframeIndex * 16);
		newAnm.write(ValueHandler.getValBytes(keyframe, true, wii));
		newAnm.close();
	}
	public void setAllEvents() throws IOException {
		for (int eventCnt = 0; eventCnt < numEvents; eventCnt++) {
			anm.seek(148 + eventCnt * 16);
			anm.write(allEvents[eventCnt].getFlags());
			anm.write(ValueHandler.getValBytes(allEvents[eventCnt].getKeyframe(), true, wii));
		}
	}
	public void setDuration(int duration) throws IOException {
		anm.seek(2);
		anm.write(ValueHandler.getValBytes(duration, true, wii));
	}
	public void setEffEvent() throws IOException {
		anm.seek(144 + numEvents * 16);
		anm.write(effEvent.getFlags());
	}
	public void setEventByte(int newKeyframe, int checkIndex, int keyframeIndex, boolean isAnmEvent, boolean enabled) {
		byte[] newEventBytes = null;
		int byteIdx = checkIndex / 8, bitIdx = checkIndex % 8;
		if (isAnmEvent) {
			newEventBytes = allEvents[keyframeIndex].getFlags();
			if (enabled) newEventBytes[byteIdx] += (1 << bitIdx);
			else newEventBytes[byteIdx] -= (1 << bitIdx);
			allEvents[keyframeIndex] = new Event(newEventBytes, newKeyframe);
		}
		else {
			newEventBytes = effEvent.getFlags();
			if (enabled) newEventBytes[byteIdx] += (1 << bitIdx);
			else newEventBytes[byteIdx] -= (1 << bitIdx);
			effEvent = new Event(newEventBytes);
		}
	}
	public void setEventKeyframe(int newKeyframe, int keyframeIndex) throws IOException {
		allEvents[keyframeIndex].setKeyframe(newKeyframe);
		anm.seek(156 + keyframeIndex * 16);
		anm.write(ValueHandler.getValBytes(newKeyframe, true, wii));
	}
}