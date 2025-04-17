package com.amz.scm.services.impl;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.amz.scm.exceptions.ApiException;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import com.amz.scm.exceptions.ResourceNotFoundException;
import com.amz.scm.models.Contact;
import com.amz.scm.models.SocialLink;
import com.amz.scm.models.User;
import com.amz.scm.payloads.ContactDto;
import com.amz.scm.payloads.ContactResponse;
import com.amz.scm.payloads.UserDto;
import com.amz.scm.repositories.ContactRepo;
import com.amz.scm.repositories.UserRepo;
import com.amz.scm.services.ContactService;

import jakarta.persistence.PostRemove;


@Service
public class ContactServiceImpl implements ContactService {

    @Autowired
    private ContactRepo contactRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserRepo userRepo;

    @Override
    public ContactDto createContact(ContactDto contactDto, Long user_id) {

        User user = this.userRepo.findById(user_id).orElseThrow(()-> new ResourceNotFoundException("user", "user_id", String.valueOf(user_id)));

        Contact currContact = this.modelMapper.map(contactDto, Contact.class);

        currContact.setPicture("default.png");
        currContact.setCreatedAt(new Date());
        currContact.setUser(user);

        List<SocialLink> updatedLinks = contactDto.getLinks();
        updatedLinks.forEach(link -> link.setContact(currContact));

        currContact.setLinks(updatedLinks);

        Contact savedContact = this.contactRepo.save(currContact);

        return this.modelMapper.map(savedContact, ContactDto.class);

        
    }

    @Override
    public ContactDto getContactById(Long contactId) {

        Contact contact = this.contactRepo.findById(contactId)
        .orElseThrow(()-> 
            new ResourceNotFoundException("Contact", "contactId",
            String.valueOf(contactId)
        ));  

        return this.modelMapper.map(contact, ContactDto.class);
        
    }

    @Override
    public ContactDto updateContact(Long contactId, ContactDto contactDto) {

        Contact oldContact = this.contactRepo.findById(contactId)
        .orElseThrow(()-> 
            new ResourceNotFoundException("Contact", "contactId",
            String.valueOf(contactId)
        )); 

        oldContact.setFullName(contactDto.getFullName());
        oldContact.setEmail(contactDto.getEmail());
        oldContact.setPhoneNumber(contactDto.getPhoneNumber());
        oldContact.setAddress(contactDto.getAddress());
        oldContact.setDescription(contactDto.getDescription());
        oldContact.setPicture(contactDto.getPicture());
        oldContact.setFavorite(contactDto.isFavorite());
        oldContact.setWebsiteLink(contactDto.getWebsiteLink());
        oldContact.setLinkedInLink(contactDto.getLinkedInLink());
        oldContact.setCloudinaryPublicId(contactDto.getCloudinaryPublicId());
        oldContact.setLinks(contactDto.getLinks());


        // Dont forget to save the updated contact to the database
        // and return the updated contact as a ContactDto object
        Contact updatedContact = this.contactRepo.save(oldContact);
        return this.modelMapper.map(updatedContact, ContactDto.class);
    }

    @Override
    public void deleteContact(Long contactId) {
        try {
            this.contactRepo.deleteById(contactId);
        } catch (Exception e) {
            throw new ApiException(
                String.format("Unable to delete the contact with %s \n 'Error: ' %s", 
                    contactId,e.getMessage()
                )
            );
        }
    }

    @Override
    public ContactDto getContactByEmail(Long uid, String email) {
        try {
            ContactDto contact = this.contactRepo.findByUserIdAndEmail(uid, email);

            return this.modelMapper.map(contact, ContactDto.class);

        } catch (Exception e) {
            throw new ResourceNotFoundException("Contact", "email", email);
        }

    }

    @Override
    public ContactResponse getAllContactsByUser(Long uid, Integer pageNumber, Integer pageSize, String sortBy, String sortDir) {

        Sort sort = (sortDir.equalsIgnoreCase("asc")) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        
        Pageable pageable = PageRequest.of(pageNumber, pageSize,sort);

        User userIsExists = this.userRepo.findById(uid).orElseThrow(()-> new ResourceNotFoundException("User not found with  ", "user id", String.valueOf(uid)));


        
        Page<Contact> pageContacts = this.contactRepo.findByUserId(uid, pageable);
        
        List<Contact> allContacts = pageContacts.getContent();

        List<ContactDto> collectedContacts = allContacts.stream().map((contact)->{
            ContactDto contactDto = this.modelMapper.map(contact, ContactDto.class);
            return contactDto;
        }).collect(Collectors.toList());

        // here need of ContactResponse to send pageable data along with list of contacts 
        ContactResponse contactResponse = new ContactResponse();

        contactResponse.setContacts(collectedContacts);
        contactResponse.setPageNumber(pageContacts.getNumber());
        contactResponse.setPageSize(pageContacts.getSize());
        contactResponse.setTotalElements(pageContacts.getTotalElements());
        contactResponse.setLastPage(pageContacts.isLast());
        contactResponse.setTotalPages(pageContacts.getTotalPages());

        return contactResponse;
    }
    
}
