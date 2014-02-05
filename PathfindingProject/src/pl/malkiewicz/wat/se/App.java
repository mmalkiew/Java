package pl.malkiewicz.wat.se;

import pl.malkiewicz.wat.se.controller.AppController;
import pl.malkiewicz.wat.se.model.AppModel;
import pl.malkiewicz.wat.se.view.AppFrame;

public class App
{

	
	public static void main( String[] args )
	{
		AppModel model = new AppModel();
		AppFrame frame = new AppFrame( model );
		AppController controller = new AppController( model, frame );
	}
}
