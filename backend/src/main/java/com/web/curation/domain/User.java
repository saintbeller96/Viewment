package com.web.curation.domain;

import com.web.curation.domain.article.Article;
import com.web.curation.domain.connection.Likes;
import com.web.curation.domain.reply.Reply;
import com.web.curation.domain.connection.Follow;
import com.web.curation.domain.reply.Reply;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;


/**
 * com.web.curation.domain
 * User.java
 * @date    2021-01-22 오후 3:42
 * @author  김종성
 *
 * @변경이력
 * 21-02-02 replies 추가
 * articles, memories CascadeType.ALL 로 변경 2021-02-01
 **/

@Entity
@Getter
@Setter
public class User {
    @Id
    @Column(name="USER_ID", insertable = false, updatable = false)
    private String id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column(name="MESSAGE")
    private String message;

    @Column(name="JOIN_DATE")
    @CreationTimestamp
    private Timestamp joinDate;

    public User(){}
    public User(String id, String email, String nickname){
        this.id = id;
        this.email = email;
        this.nickname = nickname;
    }

    /**
     * Memory
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Memory> memories = new ArrayList<>();


    /**
     * Article
     */
    @OneToMany(mappedBy = "user",  cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Article> articles = new ArrayList<>();


    /**
     * Reply
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Reply> replies = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Likes> likes = new ArrayList<>();


    @OneToOne
    @JoinColumn(name="IMAGE_ID")
    private Image profileImage;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    public void addMemory(Memory memory){
        this.memories.add(memory);
        memory.setUser(this);
    }

    public void removeMemory(Memory memory){
        this.memories.remove(memory);
        memory.setUser(null);
    }

    public void addLike(Likes like){
        this.likes.add(like);
        like.setUser(this);
    }

    public void removeLike(Likes like){
        this.likes.remove(like);
        like.setUser(null);
    }
    
    public void setProfileImage(Image image) {
        this.profileImage = image;
    }

    public void resetProfileImage() {
        profileImage = null;
    }
}