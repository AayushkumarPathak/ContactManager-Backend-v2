package com.amz.scm.repositories;

import com.amz.scm.models.Contact;
import com.amz.scm.payloads.ContactDto;
import com.amz.scm.payloads.UserDto;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactRepo extends JpaRepository<Contact, Long> {
    Page<Contact> findByUser(UserDto user, Pageable pageable);

    // Main method to fetch and show all contacts that user stored 
    Page<Contact> findByUserId(Long contact_id, Pageable pageable);

    ContactDto findByUserIdAndEmail(Long uid, String email);

    Page<Contact> findByUserIdAndFullNameContaining(Long uid, String namekeyword, Pageable pageable);

    Page<Contact> findByUserIdAndEmailContaining(Long uid, String emailkeyword, Pageable pageable);

    Page<Contact> findByUserIdAndPhoneNumberContaining(Long uid, String phonekeyword, Pageable pageable);
}
