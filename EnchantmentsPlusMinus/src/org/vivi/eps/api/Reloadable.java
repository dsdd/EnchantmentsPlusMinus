package org.vivi.eps.api;

import java.util.ArrayList;
import java.util.List;

public interface Reloadable {

	static List<Reloadable> INSTANCES = new ArrayList<Reloadable>();
	
	static void addReloadable(Reloadable r)
	{
		INSTANCES.add(r);
	}
	
	/**
	 *  Fires when a reload is called.
	 */
	public void reload();
}
