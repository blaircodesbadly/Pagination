package io.github.blaircodesbadly.pagination.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import io.github.blaircodesbadly.pagination.data.ConfigPage;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class PaginationConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().enableComplexMapKeySerialization().excludeFieldsWithoutExposeAnnotation().create();
    @Expose
    private List<ConfigPage> paginations = new ArrayList<>();

    public static File getSaveFile() {
        return new File(ServerLifecycleHooks.getCurrentServer().getServerDirectory(), "config/PaginationMod.json");
    }

    public static PaginationConfig sync() {
        PaginationConfig cfg;
        if (getSaveFile().exists()) {
            try (Reader r = new FileReader(getSaveFile())) {
                cfg = GSON.fromJson(r, PaginationConfig.class);
            } catch (Exception e) {
                cfg = new PaginationConfig();
            }
        } else {
            cfg = new PaginationConfig();

            ConfigPage defaultPage = new ConfigPage();
            defaultPage.setHeader("HEADER");
            defaultPage.setLinesPerPage(3);
            defaultPage.setPageAlias("default");
            defaultPage.setPadding("=");
            List<String> strings = new ArrayList<>();
            strings.add("A default Pagination with 3 lines per page.");
            for (int i = 0; i < 24; i++) {
                strings.add("hello");
            }
            defaultPage.setContents(strings);
            List<ConfigPage> list = cfg.getPaginations();
            list.add(defaultPage);
            cfg.setPaginations(list);
        }

        cfg.save();
        return cfg;
    }

    public void save() {
        try (Writer w = new FileWriter(getSaveFile())) {
            GSON.toJson(this, w);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<ConfigPage> getPaginations() {
        return paginations;
    }

    public void setPaginations(List<ConfigPage> paginations) {
        this.paginations = paginations;
    }
}
