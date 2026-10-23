package cmd;
//Sparking ANM Event Manager by ViveTheJoestar
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import gui.Program;

public class Main {
	//NOTE: This method assumes that the CSV ends with a new line (or empty row). It will not work as intended otherwise.
	public static String[] readCsvRows(File csv) throws IOException {
		int endOfHeader = 0, rowCnt = 0;
		RandomAccessFile raf = new RandomAccessFile(csv, "r");
		byte[] csvBytes = new byte[(int) raf.length()];
		raf.readFully(csvBytes);
		for (int pos = 0; pos < csvBytes.length; pos++) {
			if (csvBytes[pos] == 0x0A) {
				if (rowCnt == 0) endOfHeader = pos;
				rowCnt++;
			}
		}
		String[] rows = new String[rowCnt - 1];
		rowCnt = 0;
		int next = endOfHeader + 1, prev = endOfHeader + 1;
		for (int pos = endOfHeader + 1; pos < csvBytes.length; pos++) {
			if (csvBytes[pos] == 0x0A) {
				prev = next;
				next = pos;
				byte[] rowBytes = new byte[next - prev];
				next++;
				System.arraycopy(csvBytes, prev, rowBytes, 0, rowBytes.length);
				rows[rowCnt] = new String(rowBytes);
				rowCnt++;
			}
		}
		raf.close();
		return rows;
	}
	public static void main(String[] args) {
		Program.start();
	}
}