package cmd;
//Sparking Event class by ViveTheJoestar
public class Event {	
	private byte[] flags;
	private int keyframe1 = -1;
	private int keyframe2 = -1;
	
	Event(byte[] data) {
		flags = data;
	}
	Event(byte[] data, int kf1) {
		flags = data;
		keyframe1 = kf1;
	}
	Event(byte[] data, int kf1, int kf2) {
		flags = data;
		keyframe1 = kf1;
		keyframe2 = kf2;
	}
	
	public byte[] getFlags() {
		return flags;
	}
	public int getKeyframe() {
		return keyframe1;
	}
	public String getFlagInfo(EventInfo[] eventRowObjs) {
		String result = "";
		if (keyframe1 != -1) result = "Flags for Current Keyframe (" + keyframe1 + ") and Next Keyframe (" + keyframe2 + "):\n";
		for (int byteCnt = 0; byteCnt < flags.length; byteCnt++) {
			for (int bitCnt = 0; bitCnt < 8; bitCnt++) {
				int flag = 1 << bitCnt;
				if ((flags[byteCnt] & flag) == flag) {
					result += "Byte " + byteCnt + ", " + "Bit " + bitCnt + ", ";
					int index = 8 * byteCnt + bitCnt;
					if (eventRowObjs != null) result += eventRowObjs[index].getDesc() + "\n";
				}
			}
		}
		return result;
	}
	public void setKeyframe(int kf) {
		keyframe1 = kf;
	}
}