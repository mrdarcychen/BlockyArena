package io.github.mrdarcychen.commands;

import io.github.mrdarcychen.games.PlayerManager;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.chat.ChatTypes;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextFormat;
import org.spongepowered.api.text.format.TextStyles;

import java.util.Optional;

public class CmdChallenge {
    private static final ChallengeService challengeService = new ChallengeService();

    static final CommandExecutor requestExec = (src, args) -> {
        if (!(src instanceof Player)) {
            src.sendMessage(Text.of("You must be a player to execute this command."));
            return CommandResult.empty();
        }
        Player initiator = (Player) src;
        Optional<Player> optRival = args.getOne(Text.of("player"));
        if (!optRival.isPresent()) {
            initiator.sendMessage(ChatTypes.ACTION_BAR, Messages.INVALID_PLAYER);
            return CommandResult.empty();
        }
        Player rival = optRival.get();
        if (rival == src) {
            initiator.sendMessage(ChatTypes.ACTION_BAR, Messages.CHALLENGE_SELF);
            return CommandResult.empty();
        }
        if (PlayerManager.isPlaying(rival.getUniqueId())) {
            initiator.sendMessage(ChatTypes.ACTION_BAR, Messages.PLAYER_BUSY);
            return CommandResult.empty();
        }
        ChallengeService.ChallengeData data = new ChallengeService.ChallengeData(initiator, rival);
        challengeService.request(data, 15);
        return CommandResult.success();
    };

    static final CommandExecutor respondExec = (src, args) -> {
        if (!(src instanceof Player)) {
            src.sendMessage(Text.of("You must be a player to execute this command."));
            return CommandResult.empty();
        }
        Player you = (Player) src;
        Optional<Player> optChallenger = args.getOne(Text.of("player"));
        if (!optChallenger.isPresent()) {
            you.sendMessage(ChatTypes.ACTION_BAR, Messages.INVALID_PLAYER);
            return CommandResult.empty();
        }
        Player challenger = optChallenger.get();
        ChallengeService.ChallengeData data = new ChallengeService.ChallengeData(challenger, you);
        if (challengeService.remove(data)) {
            data.notifySuccessfulRespond();
            return CommandResult.success();
        }
        you.sendMessage(ChatTypes.ACTION_BAR, Messages.NO_CHALLENGE_RECEIVED);
        return CommandResult.empty();
    };

    static final CommandSpec respondSpec = CommandSpec.builder()
            .arguments(GenericArguments.onlyOne(GenericArguments.player(Text.of("player"))))
            .executor(respondExec)
            .permission("blockyarena.play")
            .build();

    public static final CommandSpec SPEC = CommandSpec.builder()
            .child(respondSpec, "accept")
            .arguments(GenericArguments.onlyOne(GenericArguments.player(Text.of("player"))))
            .executor(requestExec)
            .permission("blockyarena.play")
            .build();

    public CmdChallenge() {

    }

    static final class Messages {
        public static final Text PLAYER_BUSY = Text
                .builder("That player is busy at the moment. Try again later.")
                .color(TextColors.RED).build();

        static final Text INVALID_PLAYER = Text
                .builder("The player you specified is not a valid server player.")
                .color(TextColors.RED).build();

        static final Text CHALLENGE_SELF = Text
                .builder("Nice try, but good spirit! Challenge someone else instead!")
                .color(TextColors.RED).build();

        static final Text NO_CHALLENGE_RECEIVED = Text
                .builder("You did not receive a challenge from that player.")
                .color(TextColors.RED).build();

        static final Text BE_PATIENT = Text
                .builder("Please wait for your rival's acceptance.")
                .color(TextColors.RED).build();

        static final Text invite(String name) {
            Text click = Text.builder("Click here")
                    .onClick(TextActions.runCommand("/ba challenge accept " + name))
                    .format(TextFormat.of(TextStyles.UNDERLINE))
                    .color(TextColors.GOLD).build();
            Text post = Text.of(" to accept in the next 15 seconds.");
            return MessageBroker.wrap(Text.builder()
                    .append(Text.of("\n" + name + " wants to challenge you to a duel. \n"))
                    .append(click).append(post).build());
        }
    }
}
