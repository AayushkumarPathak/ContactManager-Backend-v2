package com.amz.scm.services.impl;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.amz.scm.exceptions.ResourceNotFoundException;
import com.amz.scm.helpers.AppConstants;
import com.amz.scm.models.Providers;
import com.amz.scm.models.Role;
import com.amz.scm.models.User;
import com.amz.scm.payloads.UserDto;
import com.amz.scm.repositories.RoleRepo;
import com.amz.scm.repositories.UserRepo;
import com.amz.scm.services.UserService;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepo roleRepo;

    @Override
    public UserDto saveUser(UserDto user) {
        User currUser = this.modelMapper.map(user, User.class);

        currUser.setEnabled(true);
        currUser.setPassword(user.getPassword());
        currUser.setProvider(Providers.SELF);
        currUser.setCreatedAt(new Date());
        
        if(currUser.getUsername().startsWith("scm#admin")){

            Set<Role> myrole = new HashSet<>();
            myrole.add(new Role(201, "ROLE_ADMIN"));

            currUser.setRoles(myrole);
        }
        else{
            Set<Role> myrole = new HashSet<>();
            myrole.add(new Role(202, "NORMAL_USER"));
            currUser.setRoles(myrole);
        }


        

        User savedUser = this.userRepo.save(currUser);

        return this.modelMapper.map(savedUser, UserDto.class);
    }

    @Override
    public UserDto getUserById(long id) {
        User user = this.userRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", String.valueOf(id)));
        return this.modelMapper.map(user, UserDto.class);
    }

    @Override
    public UserDto updateUser(UserDto user,Long userId) {
        User user1 = this.userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", String.valueOf(userId)));

        UserDto oldUser = this.modelMapper.map(user1,UserDto.class);
        oldUser.setFullName(user.getFullName());
        oldUser.setEmail(user.getEmail());
        oldUser.setPhoneNumber(user.getPhoneNumber());
        oldUser.setUsername(user.getUsername());
        oldUser.setPassword(user.getPassword());
        oldUser.setAddress(user.getAddress());
        oldUser.setAbout(user.getAbout());
        oldUser.setPassword(user.getPassword());

        User updatedUser = this.modelMapper.map(oldUser, User.class);
        
        //user is now updated save to db
        updatedUser = this.userRepo.save(updatedUser);

        return this.modelMapper.map(updatedUser, UserDto.class);
    }

    @Override
    public void deleteUser(long id) {
       try{
           this.userRepo.deleteById(id);
       }
       catch (Exception e){
           throw new ResourceNotFoundException("Unable to delete User not found with: ", "id", String.valueOf(id));
       }
    }

    @Override
    public boolean isUserExist(long userId) {
        return this.userRepo.existsById(userId);
    }

    @Override
    public boolean isUserExistByEmail(String email) {
        User user = this.userRepo.findByEmail(email).orElseThrow(() ->
                new ResourceNotFoundException("User", "email", email));

        return user != null;

    }

    @Override
    public List<UserDto> getAllUsers() {
        List<User> allUser = this.userRepo.findAll();
        List<UserDto> userDtos = allUser.stream()
                .map((user) -> this.modelMapper.map(user, UserDto.class))
                .collect(Collectors.toList());
        return userDtos;
    }

    @Override
    public UserDto getUserByEmail(String email) {
        User user = this.userRepo.findByEmail(email).orElseThrow(() ->
                new ResourceNotFoundException("User", "email", email));
        return this.modelMapper.map(user, UserDto.class);
    }

    @Override
    public UserDto registerUser(UserDto userDto) {
       
        User user = this.modelMapper.map(userDto, User.class);

        //handle password encryption
        user.setPassword(this.passwordEncoder.encode(user.getPassword()));

        user.setCreatedAt(new Date());
        user.setProvider(Providers.SELF);
        user.setEnabled(true);
        
        //handle role
        Role role = this.roleRepo.findById(AppConstants.NORMAL_USER).get();
        
        user.getRoles().add(role);


        User savedUser = this.userRepo.save(user);

        return this.modelMapper.map(savedUser, UserDto.class);

    }

    @Override
    public boolean checkUserAlreadyExists(String username, String email) {
        boolean emailExists  = this.userRepo.findByEmail(email).isPresent();
        boolean usernameExists  = this.userRepo.findByUsername(username).isPresent();

        return emailExists  || usernameExists ;
    }
}
