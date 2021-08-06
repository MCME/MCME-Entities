package com.mcmiddleearth.entities.command.argument;

import com.mcmiddleearth.command.argument.HelpfulArgumentType;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

public abstract class HelpfulEntitiesArgumentType implements HelpfulArgumentType {

    private String toolTip;

    @Override
    public void setTooltip(String toolTip) {
        this.toolTip = toolTip;
    }

    @Override
    public String getTooltip() {
        return toolTip;
    }

    public <S> CompletableFuture<Suggestions> addSuggestions(final SuggestionsBuilder builder, Collection<String> suggestions) {
        for (String option : suggestions) {
            if (option.toLowerCase().startsWith(builder.getRemaining().toLowerCase())) {
                if(toolTip == null) {
                    builder.suggest(option);
                } else {
                    builder.suggest(option, new LiteralMessage(toolTip));
                }
            }
        }
        if(suggestions.isEmpty() && toolTip != null) {
            builder.suggest(toolTip);
        }
        return builder.buildFuture();
    }

}
