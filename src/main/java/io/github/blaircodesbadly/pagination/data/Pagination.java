package io.github.blaircodesbadly.pagination.data;

import io.github.blaircodesbadly.pagination.PaginationMod;
import net.minecraft.network.chat.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.server.ServerLifecycleHooks;

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
        MutableComponent footer1 = Component.literal(String.valueOf(paddingString).repeat(18));
        MutableComponent footer3 = Component.literal((currentIndex + 1) + "/" + pages.size());
        MutableComponent footer5 = Component.literal(String.valueOf(paddingString).repeat(18));

        MutableComponent footer2 = sendTextWithRunnableClickAction(() -> {
            if ((currentIndex - 1) > -1) {
                prevPage(currentIndex);
                send();
            }
        }, Component.literal("§l<"));

        MutableComponent footer4 = sendTextWithRunnableClickAction(() -> {
            if (currentIndex + 1 < pages.size()) {
                nextPage(currentIndex);
                send();
            }
        }, Component.literal("§l>"));


        MutableComponent currPage = pages.get(currentIndex).contents;
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

        receiver.sendSystemMessage(currPage);
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

    public MutableComponent sendTextWithRunnableClickAction(Runnable click, MutableComponent text) {
        MutableComponent newComp = (MutableComponent) text.copy();
        UUID runanbleID = UUID.randomUUID();
        PaginationMod.callbackRunnables.put(runanbleID, click);
        return (MutableComponent) newComp.setStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/pagination callback " + runanbleID)));
    }


    public static class PaginationBuilder {
        private final ServerPlayer receiver;
        private final List<Component> textPages;
        private List<PaginationPage> paginationPages = new ArrayList<>();
        private int linesPerPage = 15;
        private String paddingString = "=";
        private String headerString = paddingString;


        public PaginationBuilder(ServerPlayer receiver, List<Component> pages) {
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
            List<MutableComponent> pages = new ArrayList<>();
            int headerSize = headerString.length();
            int paddingSize = 21 - (headerSize / 2);
            String multipliedPadding = String.valueOf(paddingString).repeat(paddingSize);
            MutableComponent header = Component.literal(multipliedPadding + headerString + multipliedPadding); //12 on each side
            MutableComponent parent = Component.literal("");
            int counter = 0;

            parent.append(header);
            parent.append("\n");

            for (Component component : textPages) {
                parent.append(component);
                parent.append("\n");
                counter++;
                if (counter >= linesPerPage) {
                    pages.add(parent);
                    parent = Component.literal("");
                    counter = 0;
                    parent.append(Component.literal(multipliedPadding + headerString + multipliedPadding));
                    parent.append("\n");
                }
            }

            for (MutableComponent comp : pages) {
                paginationPages.add(new PaginationPage(UUID.randomUUID(), comp, receiver));
            }

            return new Pagination(this);
        }
    }
}
