package org.vivi.eps.api;

import java.util.ArrayList;
import java.util.List;

public interface Reloadable {

	static List<Reloadable> CLASSES = new ArrayList<Reloadable>();
	
	static void addReloadable(Reloadable r)
	{
		CLASSES.add(r);
	}
	
	/**
	 *  Fires when a reload is called.
	 */
	public void reload();
}
