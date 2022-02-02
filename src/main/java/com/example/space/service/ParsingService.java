package com.example.space.service;

import com.example.space.models.Article;
import com.example.space.repos.ArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ParsingService {
    @Autowired
    private ArticleRepository articleRepository;

    public void save(Article article){
        articleRepository.save(article);
    }

    public boolean isExist(String text){
        List<Article> articles = articleRepository.findAll();
        for (Article a: articles) {
            if(a.getText().equals(text)){
                return true;
            }
        }
        return false;
    }

    public List<Article> getAllArticle(){
        return articleRepository.findAll();
    }
}
