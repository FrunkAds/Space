package com.example.space.models;

import com.example.space.service.ParsingService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class Parsing {

    private final ParsingService parsingService;

    public Parsing(ParsingService parsingService) {
        this.parsingService = parsingService;
    }

    @Scheduled(fixedDelay = 1000000)
    public void parseArticle() throws IOException {
        Document document = Jsoup.connect("https://zaxid.net/kosmos_kosmichni_doslidzhennya_poloti_tag53065/").get();
        Elements elements = document.getElementsByAttributeValue("class", "default-news-list");
        for (Element element : elements) {
            String url = element.children().attr("href");
            String text = element.children().text();

            if (!parsingService.isExist(text)) {
                Article article = new Article();
                article.setText(text);
                article.setUrl(url);
                parsingService.save(article);
            }
        }
    }
}
