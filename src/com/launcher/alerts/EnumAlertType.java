package com.launcher.alerts;

import java.awt.Color;

public enum EnumAlertType {
	INFO(new Color(52, 148, 196)), ERROR(new Color(178, 63, 63)), WARNING(new Color(196, 148, 52)), SUCCESS(new Color(98, 164, 83));
	
	public Color color;
	
	private EnumAlertType(Color color) { this.color = color; }
	
	public String getName() { return "alert." + this.name().toLowerCase() + ".type"; }
}
