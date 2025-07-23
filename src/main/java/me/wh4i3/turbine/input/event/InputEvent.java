package me.wh4i3.turbine.input.event;

import me.wh4i3.turbine.event.Event;
import me.wh4i3.turbine.input.InputKey;

public class InputEvent implements Event {
	public final InputKey key;
	public final int action;

	public InputEvent(InputKey key, int action) {
		this.key = key;
		this.action = action;
	}
}
