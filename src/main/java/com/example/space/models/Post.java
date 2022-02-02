package com.example.space.models;

import lombok.Data;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Data
@Entity
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String title;

    @Column(columnDefinition = "text", length = 65535)
    private String anons;

    @Column(columnDefinition = "text", length = 65535)
    private String fullText;

    private String date;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_post")
    private User user;

    private String filename;

    public Post(String title, String anons, String fullText) {
        this.title = title;
        this.anons = anons;
        this.fullText = fullText;
    }

    public Post() {
    }

    public void setDate() {
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        this.date = dateFormat.format(new Date());
    }
}
