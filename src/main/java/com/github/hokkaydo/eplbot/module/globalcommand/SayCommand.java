package com.github.hokkaydo.eplbot.module.globalcommand;

import com.github.hokkaydo.eplbot.Strings;
import com.github.hokkaydo.eplbot.command.Command;
import com.github.hokkaydo.eplbot.command.CommandContext;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.internal.utils.Helpers;

import java.util.List;
import java.util.function.Supplier;

public class SayCommand implements Command {

    @Override
    public void executeCommand(CommandContext context) {
        String message = context.getOption("message").map(OptionMapping::getAsString).orElse("");
        Channel channel = context.getOption("channel").map(OptionMapping::getAsChannel).orElse(null);
        String replyTo = context.getOption("reply-to").map(OptionMapping::getAsString).orElse("");

        if (context.interaction().getGuild() == null) {
            context.replyCallbackAction().setEphemeral(true).setContent(Strings.getString("SAY_COMMAND_GUILD_ONLY")).queue();
            return;
        }

        if (channel == null) {
            if(!replyTo.isBlank()) {
                context.replyCallbackAction().setEphemeral(true).setContent(Strings.getString("SAY_COMMAND_SPECIFY_CHANNEL")).queue();
                return;
            }
            context.channel().sendMessage(message).queue();
            context.replyCallbackAction().setEphemeral(true).setContent(Strings.getString("SAY_COMMAND_SUCCESS")).queue();
        } else {
            if (!(channel instanceof TextChannel textChannel)) {
                context.replyCallbackAction().setEphemeral(true).setContent(Strings.getString("SAY_COMMAND_NOT_TEXT_CHANNEL")).queue();
                return;
            }
            if (!replyTo.isBlank()) {
                if (replyTo.length() > 20 || !Helpers.isNumeric(replyTo)) {
                    context.replyCallbackAction().setEphemeral(true).setContent(Strings.getString("SAY_COMMAND_MESSAGE_NOT_FOUND")).queue();
                    return;
                }
                textChannel.retrieveMessageById(replyTo).queue(msg -> {
                    msg.reply(message).queue();
                    context.replyCallbackAction().setEphemeral(true).setContent(Strings.getString("SAY_COMMAND_SUCCESS")).queue();
                }, ignored -> context.replyCallbackAction().setEphemeral(true).setContent(Strings.getString("SAY_COMMAND_MESSAGE_NOT_FOUND")).queue());
            } else {
                textChannel.sendMessage(message).queue();
                context.replyCallbackAction().setEphemeral(true).setContent(Strings.getString("SAY_COMMAND_SUCCESS")).queue();
            }
        }
    }

    @Override
    public String getName() {
        return "say";
    }

    @Override
    public Supplier<String> getDescription() {
        return () -> Strings.getString("SAY_COMMAND_DESCRIPTION");
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(
                new OptionData(OptionType.STRING, "message", Strings.getString("SAY_COMMAND_OPTION_MESSAGE_DESCRIPTION"), true),
                new OptionData(OptionType.CHANNEL, "channel", Strings.getString("SAY_COMMAND_OPTION_CHANNEL_DESCRIPTION"), false),
                new OptionData(OptionType.STRING, "reply-to", Strings.getString("SAY_COMMAND_OPTION_REPLY_TO_DESCRIPTION"), false)
        );
    }

    @Override
    public boolean ephemeralReply() {
        return true;
    }

    @Override
    public boolean validateChannel(MessageChannel channel) {
        return channel instanceof GuildChannel;
    }

    @Override
    public boolean adminOnly() {
        return true;
    }

    @Override
    public Supplier<String> help() {
        return () -> Strings.getString("SAY_COMMAND_HELP");
    }

}
