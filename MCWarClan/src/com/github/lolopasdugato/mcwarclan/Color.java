package com.github.lolopasdugato.mcwarclan;

import org.bukkit.DyeColor;

import java.io.Serializable;

public class Color implements Serializable {

    public static final String TEXTCOLORWHITE = "§f";    // Default colorMark for white(default...)
    static private final long serialVersionUID = 3;
	private String _colorName;							// Name of the color which is used
	private String _colorMark;							// Chat mark of this color for minecraft
	private boolean _validColor = true;					// If this color is not used by the server, it will stay as true
    private DyeColor _dye;                              // Dye color of a wool

    //////////////////////////////////////////////////////////////////////////////
    //------------------------------- Constructors -------------------------------
    //////////////////////////////////////////////////////////////////////////////

    /**
     *  Classic color constructor. Automatically initialize values for a team color.
     * @param colorName
     */
    public Color(String colorName) {
        _colorName = colorName.toUpperCase();
        if (_colorName.equals("RED")) {
            _dye = DyeColor.RED;
            _colorMark = "§c";

        } else if (_colorName.equals("BLUE")) {
            _dye = DyeColor.BLUE;
            _colorMark = "§1";

        } else if (_colorName.equals("GREEN")) {
            _dye = DyeColor.GREEN;
            _colorMark = "§2";

        } else if (_colorName.equals("YELLOW")) {
            _dye = DyeColor.YELLOW;
            _colorMark = "§e";

        } else if (_colorName.equals("BLACK")) {
            _dye = DyeColor.BLACK;
            _colorMark = "§0";

        } else if (_colorName.equals("WHITE")) {
            _dye = DyeColor.WHITE;
            _colorMark = "§f";
            _validColor = false;

        } else if (_colorName.equals("MAGENTA")) {
            _dye = DyeColor.MAGENTA;
            _colorMark = "§d";

        } else if (_colorName.equals("GRAY")) {
            _dye = DyeColor.GRAY;
            _colorMark = "§8";
            _validColor = false;

        } else if (_colorName.equals("ORANGE")) {
            _dye = DyeColor.ORANGE;
            _colorMark = "§6";
            _validColor = false;

        } else if (_colorName.equals("PURPLE")) {
            _dye = DyeColor.PURPLE;
            _colorMark = "§5";

        } else if (_colorName.equals("LIGHTGREY")) {
            _dye = DyeColor.SILVER;
            _colorMark = "§7";

        } else if (_colorName.equals("LIGHTGREEN")) {
            _dye = DyeColor.LIME;
            _colorMark = "§a";
            _validColor = false;

        } else if (_colorName.equals("CYAN")) {
            _dye = DyeColor.CYAN;
            _colorMark = "§3";

        } else if (_colorName.equals("LIGHTBLUE")) {
            _dye = DyeColor.LIGHT_BLUE;
            _colorMark = "§b";

        } else {
            _dye = DyeColor.WHITE;
            _colorMark = "§f";
            _validColor = false;

        }
    }

    //////////////////////////////////////////////////////////////////////////////
    //--------------------------------- Getters ----------------------------------
    //////////////////////////////////////////////////////////////////////////////

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

    //////////////////////////////////////////////////////////////////////////////
    //--------------------------------- Setters ----------------------------------
    //////////////////////////////////////////////////////////////////////////////

    public String get_colorMark() {
        return _colorMark;
    }

    public void set_colorMark(String _colorMark) {
        this._colorMark = _colorMark;
    }

    public DyeColor get_dye() {
        return _dye;
    }

    public void set_dye(DyeColor _dye) { this._dye = _dye; }

    //////////////////////////////////////////////////////////////////////////////
    //--------------------------------- Functions --------------------------------
    //////////////////////////////////////////////////////////////////////////////

    /**
     *  refresh settings that should be reloaded if config.yml has been changed.
     */
    public void refresh(){
        // No Settings
    }
}
