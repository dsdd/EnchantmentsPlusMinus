package org.vivi.sekai.misc.numbers;

public enum RomanNumeral
{
	I(1), IV(4), V(5), IX(9), X(10), XL(40), L(50), XC(90), C(100), CD(400), D(500), CM(900), M(1000);

	private final int weight;

	private RomanNumeral(int weight)
	{
		this.weight = weight;
	}

	public int getWeight()
	{
		return weight;
	}

	public static String toRomanNumeral(int value)
	{
		if (value < 1)
			return "";

		final RomanNumeral[] romanNumerals = RomanNumeral.values();
		StringBuilder builder = new StringBuilder();

		for (int i = romanNumerals.length - 1; i >= 0; i--)
		{
			while (value >= romanNumerals[i].weight)
			{
				builder.append(romanNumerals[i]);
				value -= romanNumerals[i].weight;
			}
		}
		return builder.toString();
	}

	public static boolean isRomanNumeral(String romanNumeral)
	{
		for (char character : romanNumeral.toCharArray())
			if (valueOf(Character.toString(romanNumeral.charAt(character))) == null)
				return false;
		return true;
	}

	public static int parseRomanNumeral(String romanNumeral)
	{
		int total = 0;

		for (int i = 0; i < romanNumeral.length(); i++)
		{
			int weight = valueOf(Character.toString(romanNumeral.charAt(i))).getWeight();

			if (i + 1 < romanNumeral.length())
			{
				int suffixWeight = valueOf(Character.toString(romanNumeral.charAt(i + 1))).getWeight();

				if (weight >= suffixWeight)
					total += weight;
				else
				{
					total += suffixWeight - weight;
					i++;
				}
			} else
				total += weight;
		}

		return total;
	}
}
