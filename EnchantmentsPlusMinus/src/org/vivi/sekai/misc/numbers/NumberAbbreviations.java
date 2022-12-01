package org.vivi.sekai.misc.numbers;

public class NumberAbbreviations
{
	public static final char[] SUFFIXES = new char[] { 'k', 'M', 'B', 'T', 'q', 'Q', 's', 'S' };
	
	/**
	 * Abbreviates a given number using suffixes, rounded to 2 decimal places. e.g.
	 * Converts 1,581,195 to 1.58M
	 * 
	 * @param value Value to abbreviate
	 * @return Abbreviated number in k, M, B, T and Q
	 */
	public static String abbreviate(double value)
	{
		return abbreviate(value, false);
	}

	/**
	 * Abbreviates a given number using suffixes, rounded to 2 decimal places. e.g.
	 * Converts 1,581,195 to 1.58M
	 * 
	 * @param value Value to abbreviate
	 * @param isRoundTo2f For values below {@code 1000}, whether to round to 2 decimal places
	 * @return Abbreviated number in k, M, B, T and Q
	 */
	public static String abbreviate(double value, boolean isRoundTo2f)
	{
		if (value < 1000)
			return Double.toString(isRoundTo2f ? Math.floor(value*100)/100 : value);
		else
		{
			int exp = (int) (Math.log10(value) / 3);
			double norm = Math.floor(value * (100 / (Math.pow(1000, exp)))) * 0.01;
			return String.format("%." + 2 + "f%s", norm, SUFFIXES[exp - 1]);
		}
	}

	/**
	 * Parses an abbreviated value such as 1.58M into a {@code double}.
	 * 
	 * @param abbreviated Abbreviated value stored in a {@code String}
	 * @return Parsed value
	 */
	public static double parseAbbreviated(String abbreviated)
	{
		char suffix = abbreviated.charAt(abbreviated.length() - 1);
		for (int i = 0; i < SUFFIXES.length; i++)
		{
			if (SUFFIXES[i] == suffix)
			{
				Double parsedValue = Double.parseDouble(abbreviated.substring(0, abbreviated.length() - 1));
				parsedValue *= Math.pow(1000, i + 1);
				return parsedValue;
			}
		}
		return Double.parseDouble(abbreviated);
	}
}
