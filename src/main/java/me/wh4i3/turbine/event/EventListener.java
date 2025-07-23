package me.wh4i3.turbine.event;

public interface EventListener<T extends Event>{
	public void evoke(T event);
}
