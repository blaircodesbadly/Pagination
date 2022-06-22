package io.github.blaircodesbadly.pagination.data;

import com.google.gson.annotations.Expose;

import java.util.List;

public class ConfigPage {

    @Expose
    private String header;
    @Expose
    private String padding;

    @Expose
    private Integer linesPerPage;

    @Expose
    private List<String> contents;

    @Expose
    private String pageAlias;


    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getPadding() {
        return padding;
    }

    public void setPadding(String padding) {
        this.padding = padding;
    }

    public Integer getLinesPerPage() {
        return linesPerPage;
    }

    public void setLinesPerPage(Integer linesPerPage) {
        this.linesPerPage = linesPerPage;
    }

    public List<String> getContents() {
        return contents;
    }

    public void setContents(List<String> contents) {
        this.contents = contents;
    }

    public String getPageAlias() {
        return pageAlias;
    }

    public void setPageAlias(String pageAlias) {
        this.pageAlias = pageAlias;
    }
}
