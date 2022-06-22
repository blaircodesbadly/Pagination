package io.github.blaircodesbadly.pagination.data;

import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

public class PaginationPage {
    public UUID pageUUID;
    public TextComponent contents;
    public ServerPlayer receiver;

    public PaginationPage(UUID pageUUID, TextComponent contents, ServerPlayer receiver) {
        this.pageUUID = pageUUID;
        this.contents = contents;
        this.receiver = receiver;
    }

}
