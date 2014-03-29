package com.github.lolopasdugato.mcwarclan;

public class Color {

	private String _colorName;
	private String _colorMark;
	public static final String TEXTCOLORWHITE = "§f";
	
	public String get_colorName() {
		return _colorName;
	}

	public void set_colorName(String _colorName) {
		this._colorName = _colorName;
	}

	public String get_colorMark() {
		return _colorMark;
	}

	public void set_colorMark(String _colorMark) {
		this._colorMark = _colorMark;
	}

	public Color(String colorName) {
		_colorName = colorName.toUpperCase();
		switch(_colorName){
		case "RED":
			_colorMark = "§c";
			break;
		case "BLUE":
			_colorMark = "§1";
			break;
		case "GREEN":
			_colorMark = "§2";
			break;
		case "YELLOW":
			_colorMark = "§e";
			break;
		case "BLACK":
			_colorMark = "§0";
			break;
		case "WHITE":
			_colorMark = "§f";
			break;
		case "MAGENTA":
			_colorMark = "§d";
			break;
		case "GREY":
			_colorMark = "§8";
			break;
		case "ORANGE":
			_colorMark = "§6";
			break;
		case "PURPLE":
			_colorMark = "§5";
			break;
		case "LIGHTGREY":
			_colorMark = "§7";
			break;
		case "LIGHTGREEN":
			_colorMark = "§a";
			break;
		case "CYAN":
			_colorMark = "§3";
			break;
		case "LIGHTBLUE":
			_colorMark = "§b";
			break;
		default:
			_colorMark = "§f";
			break;
		}
	}

}
