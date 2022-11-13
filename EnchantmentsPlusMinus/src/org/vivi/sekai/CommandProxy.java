package org.vivi.sekai;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

public class CommandProxy implements CommandExecutor
{
	protected Map<Command, CommandOptions> commandOptionsMap = new HashMap<Command, CommandOptions>();
	protected Map<Command, CommandConnection> commandActivationMap = new HashMap<Command, CommandConnection>();

	private boolean validateCommand(CommandSender sender, String[] collapsingArgs, CommandOptions commandOptions)
	{
		boolean isPlayer = sender instanceof Player;

		if (isPlayer && commandOptions.consoleOnly)
		{
			if (commandOptions.playerOnlyMessage != null)
				sender.sendMessage(commandOptions.playerOnlyMessage);
			return false;
		} else if (!isPlayer && commandOptions.playerOnly)
		{
			if (commandOptions.consoleOnlyMessage != null)
				sender.sendMessage(commandOptions.consoleOnlyMessage);
			return false;
		} else if ((isPlayer && commandOptions.requiredArgumentsConsole > collapsingArgs.length)
				|| (!isPlayer && commandOptions.requiredArgumentsPlayer > collapsingArgs.length))
		{
			if (commandOptions.insufficientArgumentsMessage != null)
				sender.sendMessage(commandOptions.insufficientArgumentsMessage);
			return false;
		} else if (!sender.hasPermission(commandOptions.executionPermission))
		{
			if (commandOptions.insufficientPermissionsMessage != null)
				sender.sendMessage(commandOptions.insufficientPermissionsMessage);
			return false;
		}

		for (Map.Entry<String, CommandOptions> entry : commandOptions.argumentOptionsMap.entrySet())
			if (entry.getKey().equalsIgnoreCase(collapsingArgs[0]))
				return validateCommand(sender, Arrays.copyOfRange(collapsingArgs, 1, collapsingArgs.length),
						entry.getValue());

		return true;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		CommandOptions commandOptions = commandOptionsMap.get(command);
		if (commandOptions == null)
			return false;

		if (!validateCommand(sender, args, commandOptions))
			return false;
		
		CommandConnection connection = commandActivationMap.get(command);
		if (connection == null)
			return false;
		else
		{
			connection.onCommand(sender, command, label, args);
			return true;
		}
	}

	public static class CommandOptions
	{
		protected boolean playerOnly = false;
		protected boolean consoleOnly = false;
		protected short requiredArgumentsConsole = 0;
		protected short requiredArgumentsPlayer = 0;
		protected Permission executionPermission = null;
		protected String playerOnlyMessage = null;
		protected String consoleOnlyMessage = null;
		protected String insufficientArgumentsMessage = null;
		protected String insufficientPermissionsMessage = null;
		protected Map<String, CommandOptions> argumentOptionsMap = new HashMap<String, CommandOptions>();

		/**
		 * Now, I know this is a little alarming at first, but trust me. It is as bad as
		 * you think.
		 * 
		 * @param playerOnly                     Whether the {@code Command} can only be
		 *                                       run by a {@code Player}
		 * @param consoleOnly                    Whether the {@code Command} can only be
		 *                                       run by the console (Not a
		 *                                       {@code Player})
		 * @param requiredArgumentsConsole       The number of arguments required to
		 *                                       execute the command in the console
		 * @param requiredArgumentsPlayer        The number of arguments required to
		 *                                       execute the command as a {@code Player}
		 * @param executionPermission            The {@code Permission} required to
		 *                                       execute this command (If multiple
		 *                                       permissions are required, the general
		 *                                       {@code Permission} to run it
		 * @param playerOnlyMessage              The message displayed when the
		 *                                       player-only command is run by the
		 *                                       console
		 * @param consoleOnlyMessage             The message displayed when the
		 *                                       console-only command is run by a
		 *                                       {@code Player}
		 * @param insufficientArgumentsMessage   The message displayed when there are
		 *                                       not enough arguments specified to run
		 *                                       the command
		 * @param insufficientPermissionsMessage The message displayed when the sender
		 *                                       does not have permission to run the
		 *                                       command
		 */
		public CommandOptions(boolean playerOnly, boolean consoleOnly, short requiredArgumentsConsole,
				short requiredArgumentsPlayer, Permission executionPermission, String playerOnlyMessage,
				String consoleOnlyMessage, String insufficientArgumentsMessage, String insufficientPermissionsMessage)
		{
			this.playerOnly = playerOnly;
			this.consoleOnly = consoleOnly;
			this.requiredArgumentsConsole = requiredArgumentsConsole;
			this.requiredArgumentsPlayer = requiredArgumentsPlayer;
			this.executionPermission = executionPermission;
			this.playerOnlyMessage = playerOnlyMessage;
			this.consoleOnlyMessage = consoleOnlyMessage;
			this.insufficientArgumentsMessage = insufficientArgumentsMessage;
			this.insufficientPermissionsMessage = insufficientPermissionsMessage;
		}

		/**
		 * Adds further options to arguments in the command. If needed, argument options
		 * inside argument options are allowed.
		 * 
		 * Note that for {@code argumentOptions.requiredArgumentsConsole} and
		 * {@code argumentOptions.requiredArgumentsConsole}, required arguments refer to
		 * the number of arguments REMAINING required to fulfill the command.
		 * 
		 * @param argument        The argument label
		 * @param argumentOptions The {@code CommandOptions} of the argument
		 * @return Existing CommandOptions instance (to build on top of)
		 */
		public CommandOptions argument(String argument, CommandOptions argumentOptions)
		{
			argumentOptionsMap.put(argument, argumentOptions);
			return this;
		}
	}

	public static interface CommandConnection
	{
		public void onCommand(CommandSender sender, Command command, String label, String[] args);
	}
}
