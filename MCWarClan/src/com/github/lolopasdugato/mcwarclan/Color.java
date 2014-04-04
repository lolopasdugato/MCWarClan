package com.github.lolopasdugato.mcwarclan;

import java.io.Serializable;

public class Color implements Serializable {
	
	static private final long serialVersionUID = 003;
	
	private String _colorName;							// Name of the color which is used
	private String _colorMark;							// Chat mark of this color for minecraft
	private boolean _validColor = true;					// If this color is not used by the server, it will stay as true
	public static final String TEXTCOLORWHITE = "§f";	// Default colorMark for white(default...)
	 
	public boolean is_validColor() {
		return _validColor;
	}

	public void set_validColor(boolean _validColor) {
		this._validColor = _validColor;
	}

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
			_validColor = false;
			break;
		case "MAGENTA":
			_colorMark = "§d";
			break;
		case "GREY":
			_colorMark = "§8";
			_validColor = false;
			break;
		case "ORANGE":
			_colorMark = "§6";
			_validColor = false;
			break;
		case "PURPLE":
			_colorMark = "§5";
			break;
		case "LIGHTGREY":
			_colorMark = "§7";
			break;
		case "LIGHTGREEN":
			_colorMark = "§a";
			_validColor = false;
			break;
		case "CYAN":
			_colorMark = "§3";
			break;
		case "LIGHTBLUE":
			_colorMark = "§b";
			break;
		default:
			_colorMark = "§f";
			_validColor = false;
			break;
		}
	}

}
