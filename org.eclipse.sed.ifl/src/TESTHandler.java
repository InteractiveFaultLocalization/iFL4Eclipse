import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.sed.ifl.control.monitor.ActivityMonitorControl;
import org.eclipse.sed.ifl.model.monitor.ActivityMonitorModel;
import org.eclipse.sed.ifl.model.monitor.event.NavigationEvent;

public class TESTHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ActivityMonitorModel model = new ActivityMonitorModel();
		ActivityMonitorControl control = new ActivityMonitorControl(model);
		control.init();
		
		model.insertEvent(new NavigationEvent());
		
		control.teardown();
		return null;
	}

}
