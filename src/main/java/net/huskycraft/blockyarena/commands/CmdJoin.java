/*
 * This file is part of BlockyArena, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2018 HuskyCraft <https://www.huskycraft.net>
 * Copyright (c) 2018 Darcy-Chen
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package net.huskycraft.blockyarena.commands;

import net.huskycraft.blockyarena.BlockyArena;
import net.huskycraft.blockyarena.games.Game;
import net.huskycraft.blockyarena.games.GamersManager;
import net.huskycraft.blockyarena.games.TeamMode;
import net.huskycraft.blockyarena.utils.Gamer;
import net.huskycraft.blockyarena.utils.GamerStatus;
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
            if (GamersManager.getGamer(player.getUniqueId()).get().getStatus() == GamerStatus.PLAYING) {
                player.sendMessage(Text.of("You've already joined a game!"));
                return CommandResult.empty();
            } else {
                Game game = BlockyArena.getGameManager().getGame(teamMode);
                if (game == null) {
                    player.sendMessage(Text.of("There is no available arena at this time."));
                    return CommandResult.empty();
                }
                gamer.join(game);
            }
        } catch (IllegalArgumentException e) {
            player.sendMessage(Text.of("You've entered an invalid team mode!"));
            return CommandResult.empty();
        }

        return CommandResult.success();
    }
}
