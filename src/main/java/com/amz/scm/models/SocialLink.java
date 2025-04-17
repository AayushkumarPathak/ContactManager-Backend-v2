package com.amz.scm.models;


import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class SocialLink {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String link;
    private String title;


    @ManyToOne
    @JoinColumn(name = "contact_id")
    @JsonBackReference
    private Contact contact;
}
