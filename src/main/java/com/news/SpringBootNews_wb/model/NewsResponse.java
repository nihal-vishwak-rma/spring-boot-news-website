package com.news.SpringBootNews_wb.model;

import java.util.List;

import lombok.Data;

@Data
public class NewsResponse {

    private String status;
    private int totalResult;
    private List<Article> articles;

    @Data
    public static class Article {

        private String title;
        private String description;
        private String url;
        private String urlToImage;

    }

   

}
