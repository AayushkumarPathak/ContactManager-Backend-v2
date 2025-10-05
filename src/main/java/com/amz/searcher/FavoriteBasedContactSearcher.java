// package com.amz.searcher;

// import java.util.List;
// import java.util.stream.Collectors;

// import org.springframework.beans.factory.annotation.Autowired;

// import com.amz.scm.payloads.ContactDto;
// import com.amz.scm.payloads.ContactResponse;
// import com.amz.scm.services.ContactService;

// public class FavoriteBasedContactSearcher implements ContactSearcher {

//     private final Long userId;

//     @Autowired
//     private ContactService contactService;
    

//     public FavoriteBasedContactSearcher(Long userId) {
//         this.userId = userId;
//     }

//     @Override
//     public ContactResponse search() {
//         ContactResponse allContactsByUser = this.contactService.getAllContactsByUser(userId, null, null, null, null);

//         List<ContactDto> favoriteContacts = allContactsByUser.getContacts()
//         .stream()
//         .filter((contact)->contact.isFavorite()).collect(Collectors.toList());

//         allContactsByUser.setContacts(favoriteContacts);
//         return allContactsByUser;
        
//     }

// }
