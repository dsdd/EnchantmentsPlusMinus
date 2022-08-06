package org.vivi.eps;

import java.io.IOException;

public class Updater {

	/** Makes updates between versions compatible.
	 */
	public static void makeCompatible()
	{
		// Cleared.
		// If you want to migrate from 1.6r and below to the latest version, use a 1.9r release as an intermediate.
	}
	
	public static void setDefault(String path, Object value)
	{
		if (!EPS.configData.isSet(path))
		{
			EPS.configData.set(path, value);
			try {
				EPS.configData.save(EPS.configFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
