package net.huskycraft.blockyarena.utils;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.chat.ChatTypes;
import org.spongepowered.api.text.format.TextColor;

import net.huskycraft.blockyarena.BlockyArena;

public class Utils {
	
	public static void broadcastToEveryone(String message, TextColor color) {
		Text coloredText = Text.builder("["+BlockyArena.getInstance().getLogger().getName() + "] " + message).color(color).build();
		MessageChannel.TO_ALL.send(coloredText, ChatTypes.CHAT);
	}
}
