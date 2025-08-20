package com.qrcode.orderinglocator.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "settings")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Settings {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(name = "logo_url")
    private String logoUrl;
    
    private String address;
    
    @Column(name = "working_hours")
    private String workingHours;
    
    @Column(name = "about_image_url")
    private String aboutImageUrl;
    
    @Column(name = "about_description", columnDefinition = "TEXT")
    private String aboutDescription;
    
    @Column(name = "terms_and_conditions", columnDefinition = "TEXT")
    private String termsAndConditions;
    
    @Column(name = "facebook_url")
    private String facebookUrl;
    
    @Column(name = "whatsapp_number")
    private String whatsappNumber;
    
    @Column(name = "phone_number")
    private String phoneNumber;
    
    @Column(name = "second_phone_number")
    private String secondPhoneNumber;
}