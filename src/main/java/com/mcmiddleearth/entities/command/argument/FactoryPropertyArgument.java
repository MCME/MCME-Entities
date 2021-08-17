package com.mcmiddleearth.entities.command.argument;

import com.mcmiddleearth.entities.api.McmeEntityType;
import com.mcmiddleearth.entities.api.VirtualEntityFactory;
import com.mcmiddleearth.entities.api.VirtualEntityGoalFactory;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

public class FactoryPropertyArgument extends HelpfulEntitiesArgumentType implements ArgumentType<String> {

    @Override
    public String parse(StringReader reader) throws CommandSyntaxException {
        return reader.readUnquotedString();
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        Collection<String> suggestions = new ArrayList<>();
        suggestions.add("clear");
        suggestions.addAll(VirtualEntityFactory.availableProperties());
        suggestions.addAll(VirtualEntityGoalFactory.availableProperties());
        return addSuggestions(builder, suggestions);
    }

}
