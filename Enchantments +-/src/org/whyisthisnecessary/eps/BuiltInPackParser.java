package org.whyisthisnecessary.eps;

public class BuiltInPackParser {

	public BuiltInPackParser(Main plugin)
	{
		new org.eps.pickaxepack.PackMain(plugin);
		new org.eps.pvppack.PackMain(plugin);
	}
}
