package com.amz.scm.services;



import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.amz.scm.payloads.ContactDto;
import com.amz.scm.payloads.ContactResponse;

public interface ContactService {

    ContactDto createContact(ContactDto contactDto,Long user_id, MultipartFile imageFile);

    ContactDto getContactById(Long contactId);

    ContactDto updateContact(Long contactId, ContactDto contactDto);

    void deleteContact(Long contactId);

    ContactDto getContactByEmail(Long uid ,String email);

    boolean checkContactExistsWithFullname(String fullname, long user_id);


    /* This is very important method to show contacts that user created for him/her */
    ContactResponse getAllContactsByUser(Long uid, Integer pageNumber, Integer pageSize, String sortBy, String sortDir);

}
