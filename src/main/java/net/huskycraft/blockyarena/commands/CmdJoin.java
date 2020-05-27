/*
 * Copyright 2017-2020 The BlockyArena Contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.huskycraft.blockyarena.commands;

import net.huskycraft.blockyarena.games.Game;
import net.huskycraft.blockyarena.games.GameManager;
import net.huskycraft.blockyarena.games.GamersManager;
import net.huskycraft.blockyarena.games.TeamMode;
import net.huskycraft.blockyarena.utils.Gamer;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class CmdJoin implements CommandExecutor{

    private static final CmdJoin INSTANCE = new CmdJoin();

    /* enforce the singleton property with a private constructor */
    private CmdJoin() {}

    public static CmdJoin getInstance() {
        return INSTANCE;
    }

    /**
     * Sends the given player to an active Game.
     */
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Player player = (Player)src;
        Gamer gamer = GamersManager.getGamer(player.getUniqueId()).get();
        try {
            TeamMode teamMode = TeamMode.valueOf(args.<String>getOne("mode").get().toUpperCase());
            if (GamersManager.isInGame(player)) {
                player.sendMessage(Text.of("You've already joined a game!"));
                return CommandResult.empty();
            }
            Game game = GameManager.getInstance().getGame(teamMode.getCapacity());
            if (game == null) {
                player.sendMessage(Text.of("There is no available arena at this time."));
                return CommandResult.empty();
            }
            game.add(gamer);
            // gamer.join(game);
        } catch (IllegalArgumentException e) {
            player.sendMessage(Text.of("You've entered an invalid team mode!"));
            return CommandResult.empty();
        }

        return CommandResult.success();
    }
}
