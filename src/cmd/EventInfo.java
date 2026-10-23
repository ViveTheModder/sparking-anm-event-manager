package cmd;
//Sparking Event Information class by ViveTheJoestar

public class EventInfo {
	private int byteIdx;
	private int bitIdx;
	private String desc;
	
	public EventInfo(String csvRow) {
		String[] csvCols = csvRow.split(",");
		if (csvCols.length == 3) {
			byteIdx = Integer.parseUnsignedInt(csvCols[0]);
			bitIdx = Integer.parseUnsignedInt(csvCols[1]);
			desc = csvCols[2];
		}
	}
	public EventInfo(int byteIdx, int bitIdx) {
		byteIdx = this.byteIdx;
		bitIdx = this.bitIdx;
	}

	public int getByteIdx() {
		return byteIdx;
	}
	public int getBitIdx() {
		return bitIdx;
	}
	public String getDesc() {
		return desc;
	}
}