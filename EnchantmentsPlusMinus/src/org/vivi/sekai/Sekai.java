package org.vivi.sekai;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
	private static final Random RANDOM = new Random();
	private static final int MC_VERSION = (Bukkit.getVersion().contains("1.8")) ? 8
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

	public static boolean isSameInventory(Inventory a, Inventory b)
	{
		if (a == b)
			return true;
		if ((a == null && b != null) || (a != null && b == null))
			return false;
		if (a.getType() != b.getType())
			return false;
		if (a.getHolder() == null && b.getHolder() == null)
			return true;
		if ((a.getHolder() == null && b.getHolder() != null)
				|| (a.getHolder() != null && b.getHolder() == null)
				|| !a.getHolder().equals(b.getHolder()))
			return false;
		if (a.getSize() != b.getSize())
			return false;
		if (a.getViewers().size() != b.getViewers().size())
			return false;
		ItemStack[] firstContents = a.getContents();
		ItemStack[] secondContents = b.getContents();
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
	
	public static double randomDouble(double origin, double bound)
	{
		double difference = bound - origin;
		return difference > 0 ? RANDOM.nextDouble() * difference + origin : origin;
	}
	
	/**
	 * Converts a {@code List} of {@code String} into a multi-line {@code String}.
	 * 
	 * @param list To convert
	 * @return Requested {@code String}
	 */
	public static String convertListToString(List<String> list)
	{
		StringBuilder epsUsageStringBuilder = new StringBuilder();
		for (String line : list)
			epsUsageStringBuilder.append(ChatColor.translateAlternateColorCodes('&', line)).append("\n");
		return epsUsageStringBuilder.toString();
	}
	
	/**
     * Translates a {@code List} of string using an alternate color code character into a
     * string that uses the internal ChatColor.COLOR_CODE color code
     * character. The alternate color code character will only be replaced if
     * it is immediately followed by 0-9, A-F, a-f, K-O, k-o, R or r.
     *
     * @param altColorChar The alternate color code character to replace. Ex: {@literal &}
     * @param linesToTranslate Text containing the alternate color code character.
     * @return Text containing the ChatColor.COLOR_CODE color code character.
     */
	public static List<String> translateAlternateColorCodes(char altColorChar, List<String> linesToTranslate)
	{
		List<String> translatedLines = new ArrayList<String>();
		for (String line : linesToTranslate)
			translatedLines.add(ChatColor.translateAlternateColorCodes(altColorChar, line));
		return translatedLines;
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
	
	/**
	 * Returns the version of the Minecraft server in numerical form. e.g. 1.8.6,
	 * 1.8.2 and 1.8.8 will all return 8. 1.16, 1.14.2 and 1.19.84 will return 16,
	 * 14 and 19 respectively.
	 * 
	 * @return Returns the version of the Minecraft server in numerical form.
	 */
	public static int getMCVersion()
	{
		return MC_VERSION;
	}
}
