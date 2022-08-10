package org.vivi.eps.libs;

public class NumberUtils {
	public static char[] suffixes = new char[]{'k', 'M', 'B', 'T', 'q', 'Q', 's', 'S'};

	/** Logarithm implementation.
	 * 
	 * @param value Value to abbreviate
	 * @return Abbreviated number in k, M, B, T and Q
	 */
	public static String abbreviate(double value)
	{
		if (value < 1000)
			return Short.toString((short)value);
		else
		{
			int exp = (int)(Math.log10(value) * 0.33);
			char suffix = suffixes[exp-1];
			double norm = Math.floor(value * (100 / (Math.pow(1000, exp)))) * 0.01;
			return String.format("%." + 2 + "f%s", norm, suffix);
		}
	}
}
