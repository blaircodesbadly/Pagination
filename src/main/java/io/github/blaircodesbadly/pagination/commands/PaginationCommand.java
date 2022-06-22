package io.github.blaircodesbadly.pagination.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.blaircodesbadly.pagination.PaginationMod;
import io.github.blaircodesbadly.pagination.data.ConfigPage;
import io.github.blaircodesbadly.pagination.data.Pagination;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.UuidArgument;
import net.minecraft.network.chat.TextComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PaginationCommand {

    public static void register(CommandDispatcher<CommandSourceStack> cmdDisp) {
        LiteralArgumentBuilder<CommandSourceStack> parentCommand = Commands.literal("pagination");

        for (ConfigPage cfgPage : PaginationMod.CONFIG.getPaginations()) {
            List<TextComponent> textComponents = new ArrayList<>();
            cfgPage.getContents().forEach(s -> textComponents.add(new TextComponent(s)));

            parentCommand.then(Commands.literal(cfgPage.getPageAlias())
                    .executes(context -> {
                        new Pagination.PaginationBuilder(context.getSource().getPlayerOrException(), textComponents)
                                .header(cfgPage.getHeader())
                                .padding(cfgPage.getPadding())
                                .linesPerPage(cfgPage.getLinesPerPage())
                                .build()
                                .send();
                        return 1;
                    })
            );
        }

        parentCommand.then(
                Commands.literal("callback").then(
                        Commands.argument("uuid", UuidArgument.uuid())
                                .executes(context -> {
                                    UUID callbackUUID = UuidArgument.getUuid(context, "uuid");
                                    PaginationMod.callbackRunnables.get(callbackUUID).run();
                                    return 1;
                                })));

        cmdDisp.register(parentCommand);
    }
}
