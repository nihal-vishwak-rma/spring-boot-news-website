package com.news.SpringBootNews_wb.services;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.news.SpringBootNews_wb.model.NewsResponse;

@Service
public class NewsService {

    public NewsResponse allNews(String url ){

       RestTemplate restTemplate = new RestTemplate();

       try {

        NewsResponse newsResponse = restTemplate.getForObject(url , NewsResponse.class);

        if (newsResponse != null && newsResponse.getArticles() != null && !newsResponse.getArticles().isEmpty()) {
            
            return newsResponse;
            
        }
        
       } catch (Exception e) {
        System.out.println(e.getMessage());
       }


        return null;

    }

}
