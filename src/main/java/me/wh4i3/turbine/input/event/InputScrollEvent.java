package me.wh4i3.turbine.input.event;

import me.wh4i3.turbine.event.Event;

public class InputScrollEvent implements Event {
	public final double x;
	public final double y;

	public InputScrollEvent(double x, double y) {
		this.x = x;
		this.y = y;
	}
}
