package com.mcmiddleearth.entities.command.argument;

import com.mcmiddleearth.entities.api.McmeEntityType;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import org.bukkit.attribute.Attribute;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class AttributeTypeArgument extends HelpfulEntitiesArgumentType implements ArgumentType<String> {

    @Override
    public String parse(StringReader reader) throws CommandSyntaxException {
        return reader.readUnquotedString();
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return addSuggestions(builder, Arrays.stream(Attribute.values()).map(attribute -> attribute.name().toLowerCase())
                                             .sorted().collect(Collectors.toList()));
    }

}
