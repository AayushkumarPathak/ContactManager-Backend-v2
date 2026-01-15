package com.techmagnet.scm.services.impl;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.techmagnet.scm.exceptions.ApiException;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Page;
import com.techmagnet.scm.exceptions.ResourceNotFoundException;
import com.techmagnet.scm.models.Contact;
import com.techmagnet.scm.models.SocialLink;
import com.techmagnet.scm.models.User;
import com.techmagnet.scm.payloads.ContactDto;
import com.techmagnet.scm.payloads.ContactResponse;
import com.techmagnet.scm.repositories.ContactRepo;
import com.techmagnet.scm.repositories.UserRepo;
import com.techmagnet.scm.services.ContactService;
import com.techmagnet.scm.services.ImageUploader;


@Service
@Slf4j
public class ContactServiceImpl implements ContactService {

    @Autowired
    private ContactRepo contactRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ImageUploader imageUploader;

    @Override
    public ContactDto createContact(ContactDto contactDto, Long user_id, MultipartFile imageFile) {

        User user = this.userRepo.findById(user_id).orElseThrow(() -> new ResourceNotFoundException("user", "user_id", String.valueOf(user_id)));

        Contact currContact = this.modelMapper.map(contactDto, Contact.class);


        // currContact.setPicture("default.png");
        currContact.setCreatedAt(new Date());
        currContact.setUser(user);

        if (imageFile != null && !imageFile.isEmpty()) {
            String supabaseImageUrl = imageUploader.uploadImage(imageFile);
            currContact.setPicture(supabaseImageUrl);
        } else {
            String defaultUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTL0ZPaTrhUTirOwz7dEn4sxkCE-wZQsZljqg&s";

            currContact.setPicture(defaultUrl);
        }


        List<SocialLink> updatedLinks = contactDto.getLinks();
        updatedLinks.forEach(link -> link.setContact(currContact));

        currContact.setLinks(updatedLinks);

        Contact savedContact = this.contactRepo.save(currContact);

        return this.modelMapper.map(savedContact, ContactDto.class);


    }

    @Override
    public ContactDto getContactById(Long contactId) {

        Contact contact = this.contactRepo.findById(contactId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Contact", "contactId",
                                String.valueOf(contactId)
                        ));

        return this.modelMapper.map(contact, ContactDto.class);

    }

    @Override
    public ContactDto updateContact(Long contactId, ContactDto contactDto) {

        Contact oldContact = this.contactRepo.findById(contactId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Contact", "contactId",
                                String.valueOf(contactId)
                        ));
        try {
            oldContact.setFullName(contactDto.getFullName());
            oldContact.setEmail(contactDto.getEmail());
            oldContact.setPhoneNumber(contactDto.getPhoneNumber());
            oldContact.setAddress(contactDto.getAddress());
            oldContact.setDescription(contactDto.getDescription());
            oldContact.setPicture(contactDto.getPicture());
            oldContact.setFavorite(contactDto.isFavorite());
            oldContact.setWebsiteLink(contactDto.getWebsiteLink());
            oldContact.setLinkedInLink(contactDto.getLinkedInLink());

            oldContact.setLinks(contactDto.getLinks());

            // Don't forget to save the updated contact to the database
            // and return the updated contact as a ContactDto object
            Contact updatedContact = this.contactRepo.save(oldContact);
            return this.modelMapper.map(updatedContact, ContactDto.class);
        } catch (Exception e) {
            log.error("Exception occurred while updating contact: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void deleteContact(Long contactId) {
        try {
            this.contactRepo.deleteById(contactId);
        } catch (Exception e) {
            throw new ApiException(
                    String.format("Unable to delete the contact with %s \n 'Error: ' %s",
                            contactId, e.getMessage()
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

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        User userIsExists = this.userRepo.findById(uid).orElseThrow(() -> new ResourceNotFoundException("User not found with  ", "user id", String.valueOf(uid)));


        Page<Contact> pageContacts = this.contactRepo.findByUserId(uid, pageable);

        List<Contact> allContacts = pageContacts.getContent();

        List<ContactDto> collectedContacts = allContacts.stream().map((contact) -> {
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

    @Override
    public boolean checkContactExistsWithFullname(String fullname, long user_id) {
        boolean isExistByFullname = this.contactRepo.findByFullNameAndUser_Id(fullname, user_id).isPresent();

        return isExistByFullname;
    }

    // public ContactResponse getFavoriteContactsByUserId(Long uid, Integer pageNumber, Integer pageSize, String sortBy, String sortDir){
    //     Sort sort = (sortDir.equalsIgnoreCase("asc")) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

    //     Pageable pageable = PageRequest.of(pageNumber, pageSize,sort);

    //     FavoriteBasedContactSearcher fbcs = new FavoriteBasedContactSearcher(uid);

    //     return fbcs.search();
    // }
}
