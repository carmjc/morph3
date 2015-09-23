package net.carmgate.morph.ui.actions.selection;

import javax.inject.Inject;
import javax.inject.Singleton;

import net.carmgate.morph.ui.actions.selection.Select.PickingResult;
import net.carmgate.morph.ui.inputs.DragContext;
import net.carmgate.morph.ui.inputs.GameMouse;
import net.carmgate.morph.ui.inputs.InputHistory;
import net.carmgate.morph.ui.inputs.MouseListener;
import net.carmgate.morph.ui.inputs.UIEvent.EventType;
import net.carmgate.morph.ui.renderers.SelectRenderer.TargetType;
import net.carmgate.morph.ui.widgets.Widget;

@Singleton
public class WidgetMouseActions implements MouseListener {

	@Inject private InputHistory inputHistory;
	@Inject private DragContext dragContext;
	@Inject private GameMouse gameMouse;

	private Widget widgetDown;

	@Override
	public void onMouseEvent() {
		if (inputHistory.getLastMouseEvent().getButton() == 0 && inputHistory.getLastMouseEvent().getEventType() == EventType.MOUSE_BUTTON_DOWN
				&& !dragContext.dragInProgress()) {
			PickingResult pickingResult = gameMouse.pick();
			if (pickingResult != null && pickingResult.getTargetType() == TargetType.WIDGET) {
				Widget widget = (Widget) pickingResult.getTarget();
				widget.onMouseDown();
				widgetDown = widget;
			}
		}

		if (inputHistory.getLastMouseEvent().getButton() == 0 && inputHistory.getLastMouseEvent().getEventType() == EventType.MOUSE_BUTTON_UP
				&& widgetDown != null) {
			widgetDown.onMouseUp();
			widgetDown = null;
		}
	}

}
