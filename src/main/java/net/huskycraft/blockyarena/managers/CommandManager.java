package net.huskycraft.blockyarena.managers;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

import net.huskycraft.blockyarena.BlockyArena;
import net.huskycraft.blockyarena.commands.CmdCreate;
import net.huskycraft.blockyarena.commands.CmdEdit;
import net.huskycraft.blockyarena.commands.CmdJoin;
import net.huskycraft.blockyarena.commands.CmdKit;
import net.huskycraft.blockyarena.commands.CmdQuit;
import net.huskycraft.blockyarena.commands.CmdRemove;

public class CommandManager {

		private static final CommandManager INSTANCE = new CommandManager();

		private CommandManager() {}

		public static CommandManager getInstance() {
			return INSTANCE;
		}
		
	    /*
	    registers user commands to CommandManager
	     */
	    public void registerCommands() {
	        CommandSpec cmdCreate = CommandSpec.builder()
	                .arguments(
	                        GenericArguments.onlyOne(GenericArguments.string(Text.of("type"))),
	                        GenericArguments.onlyOne(GenericArguments.string(Text.of("id"))))
	                .executor(CmdCreate.getInstance())
	                .permission("blockyarena.create")
	                .build();

	        CommandSpec cmdRemove = CommandSpec.builder()
	                .arguments(
	                        GenericArguments.onlyOne(GenericArguments.string(Text.of("type"))),
	                        GenericArguments.onlyOne(GenericArguments.string(Text.of("id"))))
	                .executor(CmdRemove.getInstance())
	                .permission("blockyarena.remove")
	                .build();

	        CommandSpec cmdJoin = CommandSpec.builder()
	                .arguments(
	                        GenericArguments.onlyOne(GenericArguments.string(Text.of("mode")))
	                )
	                .executor(CmdJoin.getInstance())
	                .build();

	        CommandSpec cmdQuit = CommandSpec.builder()
	                .executor(CmdQuit.getInstance())
	                .build();

	        CommandSpec cmdEdit = CommandSpec.builder()
	                .arguments(
	                        GenericArguments.onlyOne(GenericArguments.string(Text.of("id"))),
	                        GenericArguments.onlyOne(GenericArguments.string(Text.of("type"))),
	                        GenericArguments.onlyOne(GenericArguments.string(Text.of("param")))
	                )
	                .executor(CmdEdit.getInstance())
	                .permission("blockyarena.edit")
	                .build();

	        CommandSpec cmdKit = CommandSpec.builder()
	                .arguments(GenericArguments.onlyOne(GenericArguments.string(Text.of("id"))))
	                .executor(CmdKit.getInstance())
	                .build();

	        CommandSpec arenaCommandSpec = CommandSpec.builder()
	                .child(cmdEdit, "edit")
	                .child(cmdCreate, "create")
	                .child(cmdRemove, "remove")
	                .child(cmdJoin, "join")
	                .child(cmdQuit, "quit")
	                .child(cmdKit, "kit")
	                .build();

	        Sponge.getCommandManager()
	                .register(BlockyArena.getInstance(), arenaCommandSpec, "blockyarena", "arena", "ba");
	    }
}
