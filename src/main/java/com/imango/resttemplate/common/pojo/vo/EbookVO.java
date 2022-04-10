package com.imango.resttemplate.common.pojo.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.xml.bind.annotation.XmlRootElement;

@Data
@XmlRootElement(name = "Ebook")
public class EbookVO {
    @JsonProperty("Id")
    private String id;

    @JsonProperty("Name")
    private String name;

    @JsonProperty("Author")
    private String author;

    public EbookVO() {
        this.id = "_id";
        this.name = "_name";
        this.author = "_author";
    }

    public EbookVO(String id, String name, String author) {
        this.id = id;
        this.name = name;
        this.author = author;
    }
}
