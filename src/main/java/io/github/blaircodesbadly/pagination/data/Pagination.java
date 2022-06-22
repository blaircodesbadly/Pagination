package io.github.blaircodesbadly.pagination.data;

import io.github.blaircodesbadly.pagination.PaginationMod;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Pagination {
    public ServerPlayer receiver;
    public List<PaginationPage> pages;

    public int currentIndex;
    public int linesPerPage;

    public String paddingString;
    public String headerString;


    public Pagination(PaginationBuilder builder) {
        this.receiver = builder.receiver;
        this.pages = builder.paginationPages;
        this.linesPerPage = builder.linesPerPage;
        this.paddingString = builder.paddingString;
        this.headerString = builder.headerString;
    }

    public void send() {
        TextComponent footer1 = new TextComponent(String.valueOf(paddingString).repeat(18));
        TextComponent footer3 = new TextComponent((currentIndex + 1) + "/" + pages.size());
        TextComponent footer5 = new TextComponent(String.valueOf(paddingString).repeat(18));

        TextComponent footer2 = sendTextWithRunnableClickAction(() -> {
            if ((currentIndex - 1) > -1) {
                prevPage(currentIndex);
                send();
            }
        }, new TextComponent("§l<"));

        TextComponent footer4 = sendTextWithRunnableClickAction(() -> {
            if (currentIndex + 1 < pages.size()) {
                nextPage(currentIndex);
                send();
            }
        }, new TextComponent("§l>"));


        TextComponent currPage = pages.get(currentIndex).contents;
        boolean found = false;
        //check if it already has a footer - i can probably fix this a less dumb way but it's 3AM
        for (Component comp : currPage.getSiblings()) {
            if (comp.getString().contains(">") || comp.getString().contains("<"))
                found = true;
        }

        if (!found) {
            currPage.append(footer1);
            currPage.append(footer2);
            currPage.append(footer3);
            currPage.append(footer4);
            currPage.append(footer5);
        }

        receiver.sendMessage(currPage, UUID.randomUUID());
    }

    public void nextPage(int currentPage) {
        if (pages.size() > currentPage && pages.get(currentPage) != null) {
            this.currentIndex++;
        }
    }

    public void prevPage(int currentPage) {
        if (pages.size() > currentPage && pages.get(currentPage) != null) {
            this.currentIndex--;
        }
    }

    public TextComponent sendTextWithRunnableClickAction(Runnable click, TextComponent text) {
        TextComponent newComp = (TextComponent) text.copy();
        UUID runanbleID = UUID.randomUUID();
        PaginationMod.callbackRunnables.put(runanbleID, click);
        return (TextComponent) newComp.setStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/pagination callback " + runanbleID)));
    }


    public static class PaginationBuilder {
        private final ServerPlayer receiver;
        private final List<TextComponent> textPages;
        private List<PaginationPage> paginationPages = new ArrayList<>();
        private int linesPerPage = 15;
        private String paddingString = "=";
        private String headerString = paddingString;


        public PaginationBuilder(ServerPlayer receiver, List<TextComponent> pages) {
            this.receiver = receiver;
            this.textPages = pages;
        }

        public PaginationBuilder linesPerPage(int linesPerPage) {
            this.linesPerPage = linesPerPage;
            return this;
        }

        public PaginationBuilder header(String headerString) {
            this.headerString = headerString;
            return this;
        }

        public PaginationBuilder padding(String paddingString) {
            this.paddingString = paddingString;
            return this;
        }

        public Pagination build() {
            List<TextComponent> pages = new ArrayList<>();
            int headerSize = headerString.length();
            int paddingSize = 21 - (headerSize / 2);
            String multipliedPadding = String.valueOf(paddingString).repeat(paddingSize);
            TextComponent header = new TextComponent(multipliedPadding + headerString + multipliedPadding); //12 on each side
            TextComponent parent = new TextComponent("");
            int counter = 0;

            parent.append(header);
            parent.append("\n");

            for (TextComponent component : textPages) {
                parent.append(component);
                parent.append("\n");
                counter++;
                if (counter >= linesPerPage) {
                    pages.add(parent);
                    parent = new TextComponent("");
                    counter = 0;
                    parent.append(new TextComponent(multipliedPadding + headerString + multipliedPadding));
                    parent.append("\n");
                }
            }

            for (TextComponent comp : pages) {
                paginationPages.add(new PaginationPage(UUID.randomUUID(), comp, receiver));
            }

            return new Pagination(this);
        }
    }
}
