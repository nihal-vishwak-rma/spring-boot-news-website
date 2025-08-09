package com.news.SpringBootNews_wb.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import com.news.SpringBootNews_wb.model.NewsResponse;
import com.news.SpringBootNews_wb.services.NewsService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequestMapping("/top-news")
public class NewsController {

    @Autowired
    private NewsService newsService;


    @Value("${news.api.key}")
    private String apikey;

    @Value("${news.api.url1}")
    private String apiurl1;

    @GetMapping("/topnews/{category}")
    public String getCategoryNews(@PathVariable String category, @RequestParam(defaultValue = "1") int page,
            Model model) {

        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        String url = apiurl1 + apikey;

        url = url.replace("TODAYDATE",today);
        url = url.replace("CONTENT", category);
        url = url.replace("PAGE", String.valueOf(page));

        int pagesize = 12;

        System.out.println(url);

        NewsResponse newsResponse = newsService.allNews(url);

        if (newsResponse == null) {
            System.out.println("NewsResponse is NULL");
            model.addAttribute("newsNotFound", "News Not Found");
        } else {

            System.out.println("Articles count: " + newsResponse.getArticles().size());
            
            model.addAttribute("articles", newsResponse.getArticles());
            model.addAttribute("currentPage", page);
            model.addAttribute("pageSize", pagesize);
            model.addAttribute("category", category);
            model.addAttribute("hasMore", newsResponse.getArticles().size() == pagesize);

        }

        return "topnews";
    }


    

}
