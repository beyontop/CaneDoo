import com.jetoff.gui.SudokuGUI;
import com.jetoff.tools.FileHandler;

public class Launcher
{
	public Launcher() {
	}

	public static void main( String[] args )
	{
		/**
		 */
		if( !FileHandler.existMappingFile() )
			FileHandler.createMappingFile();
		new SudokuGUI();
	}
}
