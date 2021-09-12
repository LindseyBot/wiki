package net.notfab.lindsey.worker.services;

import net.lindseybot.controller.registry.CommandRegistry;
import net.lindseybot.entities.discord.Label;
import net.lindseybot.entities.interaction.commands.CommandMeta;
import net.lindseybot.entities.interaction.commands.OptType;
import net.lindseybot.entities.interaction.commands.builder.CommandBuilder;
import net.lindseybot.enums.Modules;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SlashManager {

    public SlashManager(CommandRegistry registry, @Value("${app.beta}") boolean beta) {
        this.register(registry, beta, this.describePokedex());
        //this.register(registry, beta, this.describeFlip());
        //this.register(registry, beta, this.describeRoll());
    }

    private void register(CommandRegistry registry, boolean beta, CommandMeta meta) {
        if (meta.getGuilds().isEmpty() && beta) {
            return;
        }
        registry.register(meta);
    }

    private CommandMeta describePokedex() {
        return new CommandBuilder("pokedex", Label.of("commands.pokedex.description"), 1.0)
                .module(Modules.FUN)
                .addOption(OptType.STRING, "search", Label.of("commands.pokedex.search"), true)
                .build();
    }

    private CommandMeta describeFlip() {
        return new CommandBuilder("flip", Label.of("commands.flip.description"), 1.0)
                .module(Modules.FUN)
                .build();
    }

    private CommandMeta describeRoll() {
        return new CommandBuilder("roll", Label.of("commands.roll.description"), 1.0)
                .module(Modules.FUN)
                .addOption(OptType.INT, "sides", Label.of("commands.roll.sides"), false)
                .build();
    }

}
