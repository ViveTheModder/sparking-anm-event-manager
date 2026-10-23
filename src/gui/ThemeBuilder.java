package gui;

import java.awt.Color;
import java.io.File;
import java.io.IOException;

import cmd.Main;

public class ThemeBuilder {
	private static Color getColorFromHexCode(String code) {
		char[] codeChars = code.toCharArray();
		if (codeChars.length >= 7) {
			if (codeChars[0] == '#') {
				int[] colorVals = new int[3];
				for (int charCnt = 1; charCnt <= 6; charCnt += 2) {
					char[] colorValBytes = new char[2];
					System.arraycopy(codeChars, charCnt, colorValBytes, 0, 2);
					colorVals[charCnt / 2] = Integer.parseInt(new String(colorValBytes), 16);
				}
				return new Color(colorVals[0], colorVals[1], colorVals[2]);
			}
			return null;
		}
		return null;
	}
	
	public static Theme[] getAllThemes() throws IOException {
		File csv = new File("./csv/themes.csv");
		if (csv.isFile()) {
			String[] rows = Main.readCsvRows(csv);
			if (rows.length > 0) {
				Theme[] themes = new Theme[rows.length];
				for (int rowCnt = 0; rowCnt < rows.length; rowCnt++) {
					String[] cols = rows[rowCnt].split(",");
					if (cols.length == 5) {
						Color[] colors = new Color[4];
						for (int colorCnt = 1; colorCnt <= 4; colorCnt++) {
							colors[colorCnt - 1] = getColorFromHexCode(cols[colorCnt]);
						}
						themes[rowCnt] = new Theme(cols[0], colors[0], colors[1], colors[2], colors[3]);
					}
					else return new Theme[0];
				}
				return themes;
			}
		}
		return new Theme[0];
	}
}