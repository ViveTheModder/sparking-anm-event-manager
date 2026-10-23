package gui;
//
import java.awt.Color;

public class Theme {
	private Color topColor, medColor, btmColor, txtColor;
	private String name;
	
	public Theme(String name, Color top, Color med, Color btm, Color txt) {
		this.name = name;
		topColor = top;
		medColor = med;
		btmColor = btm;
		txtColor = txt;
	}
	public Color getTopColor() {
		return topColor;
	}
	public Color getMedColor() {
		return medColor;
	}
	public Color getBtmColor() {
		return btmColor;
	}
	public Color getTxtColor() {
		return txtColor;
	}
	public String getName() {
		return name;
	}
}
