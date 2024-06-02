package com.jetoff.gui;

import javax.swing.*;

/**
 * Created by Alain on 25/09/2017.
 */
class JetSetter extends JSlider
{
	private static int MIN_LEVEL    = 17;
	private static int MAX_LEVEL    = 35;
	private static int INIT_LEVEL   = 17;
	private static int TICK_SPACING = 1;

	JetSetter()
	{
		super( JSlider.HORIZONTAL, MIN_LEVEL, MAX_LEVEL, INIT_LEVEL );
		this.setMajorTickSpacing( TICK_SPACING );
		this.setMinorTickSpacing( TICK_SPACING );
		this.setPaintTicks( true );
		this.setPaintLabels( true );
	}
}
