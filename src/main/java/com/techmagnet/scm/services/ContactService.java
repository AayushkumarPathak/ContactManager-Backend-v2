package com.techmagnet.scm.services;



import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.techmagnet.scm.payloads.ContactDto;
import com.techmagnet.scm.payloads.ContactResponse;

public interface ContactService {

    ContactDto createContact(ContactDto contactDto,Long user_id, MultipartFile imageFile) throws IOException;

    ContactDto getContactById(Long contactId);

    ContactDto updateContact(Long contactId, ContactDto contactDto);

    void deleteContact(Long contactId);

    ContactDto getContactByEmail(Long uid ,String email);

    boolean checkContactExistsWithFullname(String fullname, long user_id);


    /* This is very important method to show contacts that user created for him/her */
    ContactResponse getAllContactsByUser(Long uid, Integer pageNumber, Integer pageSize, String sortBy, String sortDir);

}
