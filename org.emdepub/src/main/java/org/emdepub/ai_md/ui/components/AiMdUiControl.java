package org.emdepub.ai_md.ui.components;

public interface AiMdUiControl {

	public void load();
	
	public void enable(boolean enabled);
	
	public void addEvent(String eventName, Runnable runnable);
}
