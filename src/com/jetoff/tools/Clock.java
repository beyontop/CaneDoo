package com.jetoff.tools;

import javax.swing.*;
import java.awt.event.ActionListener;

/**
 * Created by Alain on 04/04/2018.
 */
public class Clock extends JLabel
{
	static int delay = 1000;
	Integer t = 0;

	public Clock()
	{
		this.setBorder( BorderFactory.createLoweredBevelBorder() );
		ActionListener sec = e -> {
			t++;
			Clock.this.setText( t.toString() );
		};
		new Timer( delay, sec ).start();
	}
}
