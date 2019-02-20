import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.sed.ifl.control.monitor.ActivityMonitorControl;
import org.eclipse.sed.ifl.model.monitor.ActivityMonitorModel;

public class TESTHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ActivityMonitorControl control = new ActivityMonitorControl(new ActivityMonitorModel());
		control.init();
		return null;
	}

}
