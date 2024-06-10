package com.github.hokkaydo.eplbot.command;


import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;

import java.util.List;
import java.util.Optional;

public record CommandContext(String commandName, List<OptionMapping> options,
                             User user,
                             Member author, MessageChannel channel,
                             Command.Type commandType, SlashCommandInteraction interaction, InteractionHook hook,
                             ReplyCallbackAction replyCallbackAction) {

    public Optional<OptionMapping> getOption(String name) {
        return options.stream()
                      .filter(option -> option.getName().equals(name))
                      .findFirst();
    }

}