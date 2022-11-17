package org.vivi.sekai;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.vivi.sekai.CommandProxy.CommandConnection;
import org.vivi.sekai.CommandProxy.CommandOptions;

/**
 * All in one dumpster fire library class
 * 
 * @author vivisan
 *
 */
public class Sekai
{
	private static final CommandProxy commandProxy = new CommandProxy();
	private static final char[] suffixes = new char[] { 'k', 'M', 'B', 'T', 'q', 'Q', 's', 'S' };
	private static final int version = (Bukkit.getVersion().contains("1.8")) ? 8
			: ((Bukkit.getVersion().contains("1.9")) ? 9
					: (Bukkit.getVersion().contains("1.10")) ? 10
							: (Bukkit.getVersion().contains("1.11") ? 11
									: (Bukkit.getVersion().contains("1.12") ? 12
											: (Bukkit.getVersion().contains("1.13") ? 13
													: (Bukkit.getVersion().contains("1.14") ? 14
															: (Bukkit.getVersion().contains("1.15") ? 15
																	: (Bukkit.getVersion().contains("1.16") ? 16
																			: (Bukkit.getVersion().contains("1.17") ? 17
																					: (Bukkit.getVersion()
																							.contains("1.18")
																									? 18
																									: (Bukkit
																											.getVersion()
																											.contains(
																													"1.19") ? 19
																															: 20))))))))));

	/**
	 * Saves a resource from the specified Spigot plugin JAR to the specified
	 * destination if it does not exist
	 * 
	 * @param resourcePath The path to copy from
	 * @param dest         The file you want to copy to
	 */
	public static void saveDefaultFile(Plugin plugin, String resourcePath, File dest)
	{
		if (!dest.exists())
			try
			{
				dest.createNewFile();
				copyResource(plugin, resourcePath, dest);
			} catch (IOException e)
			{
				e.printStackTrace();
			}
	}

	/**
	 * Copies a resource in the given plugin's jar from the specified internal file
	 * path to the specified file
	 * 
	 * @param internalPath The file path to copy from
	 * @param dest         The file to paste into
	 */
	public static void copyResource(Plugin plugin, String internalPath, File dest)
	{
		try
		{
			InputStream is = null;
			OutputStream os = null;
			try
			{
				is = plugin.getClass().getResourceAsStream(internalPath);
				os = new FileOutputStream(dest);
				byte[] buffer = new byte[1024];
				int length;
				while ((length = is.read(buffer)) != -1)
				{
					os.write(buffer, 0, length);
				}
			} catch (Exception e)
			{
				e.printStackTrace();
			} finally
			{
				is.close();
				os.close();
			}
		} catch (IOException e)
		{
		}
	}

	public static CommandProxy getCommandProxy()
	{
		return commandProxy;
	}

	public static void registerCommand(PluginCommand command, CommandOptions options)
	{
		getCommandProxy().commandOptionsMap.put(command, options);
		command.setExecutor(getCommandProxy());
	}

	public static void connectCommand(PluginCommand command, CommandConnection connection)
	{
		getCommandProxy().commandActivationMap.put(command, connection);
	}

	public static boolean isSameInventory(Inventory first, Inventory second)
	{
		if (first == null && second == null)
			return true;
		if ((first == null && second != null) || (first != null && second == null))
			return false;
		if (first.getType() != second.getType())
			return false;
		if (first.getHolder() == null && second.getHolder() == null)
			return true;
		if ((first.getHolder() == null && second.getHolder() != null)
				|| (first.getHolder() != null && second.getHolder() == null)
				|| !first.getHolder().equals(second.getHolder()))
			return false;
		if (first.getSize() != second.getSize())
			return false;
		if (first.getViewers().size() != second.getViewers().size())
			return false;
		ItemStack[] firstContents = first.getContents();
		ItemStack[] secondContents = second.getContents();
		if (firstContents.length != secondContents.length)
			return false;
		for (int i = 0; i < firstContents.length; i++)
		{
			if (firstContents[i] == null && secondContents[i] == null)
				continue;
			else if (firstContents[i] == null && secondContents[i] != null)
				return false;
			else if (secondContents[i] == null && firstContents[i] != null)
				return false;
			else if (!firstContents[i].equals(secondContents[i]))
				return false;
		}

		// This is good enough already.
//		for (int index = 0; index < first.getViewers().size(); index++)
//		{
//			HumanEntity a = first.getViewers().get(index);
//			HumanEntity b = second.getViewers().get(index);
//			if (!a.getOpenInventory().getTitle().equals(b.getOpenInventory().getTitle()))
//				return false;
//			if (!((a == null && b == null) || a.equals(b)))
//				return false;
//		}
		return true;
	}

	/**
	 * Abbreviates a given number using suffixes, rounded to 2 decimal places. e.g.
	 * Converts 1,581,195 to 1.58M
	 * 
	 * @param value Value to abbreviate
	 * @return Abbreviated number in k, M, B, T and Q
	 */
	public static String abbreviate(double value)
	{
		if (value < 1000)
			return Double.toString(value);
		else
		{
			int exp = (int) (Math.log10(value) / 3);
			double norm = Math.floor(value * (100 / (Math.pow(1000, exp)))) * 0.01;
			return String.format("%." + 2 + "f%s", norm, suffixes[exp - 1]);
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
		for (int i = 0; i < suffixes.length; i++)
		{
			if (suffixes[i] == suffix)
			{
				Double parsedValue = Double.parseDouble(abbreviated.substring(0, abbreviated.length() - 1));
				parsedValue *= Math.pow(1000, i + 1);
				return parsedValue;
			}
		}
		return Double.parseDouble(abbreviated);
	}

	/**
	 * Gets the Roman numeral of the specified value.
	 * 
	 * @param value The value
	 * @return The Roman numeral of the value
	 */
	public static String getRomanNumeral(int value)
	{
		switch (value)
		{
		case 1:
			return "I";
		case 2:
			return "II";
		case 3:
			return "III";
		case 4:
			return "IV";
		case 5:
			return "V";
		case 6:
			return "VI";
		case 7:
			return "VII";
		case 8:
			return "VIII";
		case 9:
			return "IX";
		case 10:
			return "X";
		default:
			return Integer.toString(value);
		}
	}

	/**
	 * Checks if the specified {@code String} is a Roman numeral.
	 * 
	 * @param romanNumeral The Roman numeral
	 * @return If the String is a Roman numeral
	 */
	public static boolean isRomanNumeral(String romanNumeral)
	{
		switch (romanNumeral)
		{
		case "I":
		case "II":
		case "III":
		case "IV":
		case "V":
		case "VI":
		case "VII":
		case "VIII":
		case "IX":
		case "X":
			return true;
		default:
			return false;
		}
	}

	/**
	 * Returns the version of the Minecraft server in numerical form. e.g. 1.8.6,
	 * 1.8.2 and 1.8.8 will all return 8. 1.16, 1.14.2 and 1.19.84 will return 16,
	 * 14 and 19 respectively.
	 * 
	 * @return Returns the version of the Minecraft server in numerical form.
	 */
	public static int getMCVersion()
	{
		return version;
	}

	/**
	 * Evaluates a String class that contains a mathematical expression.
	 * <p>
	 * Available operations:
	 * <p>
	 * +, -, *, /, ^, sqrt, sin, cos, tan, log, ln, parenthesis ()
	 * 
	 * @param expression Expression to solve
	 * @return Answer to the expression
	 */
	public static double evaluateExpression(final String expression)
	{
		return new Object() {
			int pos = -1, ch;

			void nextChar()
			{
				ch = (++pos < expression.length()) ? expression.charAt(pos) : -1;
			}

			boolean eat(int charToEat)
			{
				while (ch == ' ')
					nextChar();
				if (ch == charToEat)
				{
					nextChar();
					return true;
				}
				return false;
			}

			double parse()
			{
				nextChar();
				double x = parseExpression();
				if (pos < expression.length())
					throw new RuntimeException("Unexpected: " + (char) ch);
				return x;
			}

			// Grammar:
			// expression = term | expression `+` term | expression `-` term
			// term = factor | term `*` factor | term `/` factor
			// factor = `+` factor | `-` factor | `(` expression `)`
			// | number | functionName factor | factor `^` factor

			double parseExpression()
			{
				double x = parseTerm();
				for (;;)
				{
					if (eat('+'))
						x += parseTerm(); // addition
					else if (eat('-'))
						x -= parseTerm(); // subtraction
					else
						return x;
				}
			}

			double parseTerm()
			{
				double x = parseFactor();
				for (;;)
				{
					if (eat('*'))
						x *= parseFactor(); // multiplication
					else if (eat('/'))
						x /= parseFactor(); // division
					else
						return x;
				}
			}

			double parseFactor()
			{
				if (eat('+'))
					return parseFactor(); // unary plus
				if (eat('-'))
					return -parseFactor(); // unary minus

				double x;
				int startPos = this.pos;
				if (eat('('))
				{ // parentheses
					x = parseExpression();
					eat(')');
				} else if ((ch >= '0' && ch <= '9') || ch == '.')
				{ // numbers
					while ((ch >= '0' && ch <= '9') || ch == '.')
						nextChar();
					x = Double.parseDouble(expression.substring(startPos, this.pos));
				} else if (ch >= 'a' && ch <= 'z')
				{ // functions
					while (ch >= 'a' && ch <= 'z')
						nextChar();
					String func = expression.substring(startPos, this.pos);
					x = parseFactor();
					if (func.equals("sqrt"))
						x = Math.sqrt(x);
					else if (func.equals("sin"))
						x = Math.sin(Math.toRadians(x));
					else if (func.equals("cos"))
						x = Math.cos(Math.toRadians(x));
					else if (func.equals("tan"))
						x = Math.tan(Math.toRadians(x));
					else if (func.equals("log"))
						x = Math.log10(x);
					else if (func.equals("ln"))
						x = Math.log(x);
					else
						throw new RuntimeException("Unknown function: " + func);
				} else
				{
					throw new RuntimeException("Unexpected: " + (char) ch);
				}

				if (eat('^'))
					x = Math.pow(x, parseFactor()); // exponentiation

				return x;
			}
		}.parse();
	}
}
