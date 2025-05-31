package com.amz.scm;

import java.util.List;


import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.amz.scm.helpers.AppConstants;
import com.amz.scm.models.Role;
import com.amz.scm.repositories.RoleRepo;

@SpringBootApplication
public class Scm2apiApplication implements CommandLineRunner {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepo roleRepo;

    public static void main(String[] args) {

        SpringApplication.run(Scm2apiApplication.class, args);
    }

    @Bean
    public ModelMapper modelMapper(){
        return new ModelMapper();
    }

    @Override
    public void run(String... args) throws Exception {
       
        String encodedPasswordUserWithId2 = this.passwordEncoder.encode("adminaayush122");
        System.out.println(encodedPasswordUserWithId2);


        try {

            boolean isAdminRoleAlreadyExists = this.roleRepo.findById(AppConstants.ADMIN_USER).isPresent();
             boolean isUserRoleAlreadyExists = this.roleRepo.findById(AppConstants.NORMAL_USER).isPresent();

            if(!isAdminRoleAlreadyExists && !isUserRoleAlreadyExists){
                Role roleAdmin = new Role();
                roleAdmin.setId(AppConstants.ADMIN_USER);
                roleAdmin.setName(AppConstants.ROLE_ADMIN);
                
                
                Role roleNormal = new Role();
                roleNormal.setId(AppConstants.NORMAL_USER);
                roleNormal.setName(AppConstants.ROLE_USER);

                List<Role> roles = List.of(roleAdmin,roleNormal);

                List<Role> savedRoles = this.roleRepo.saveAll(roles);

                savedRoles.forEach(r->{
                    System.out.println(
                        r.getId()+ " "+
                        r.getName()
                    );
                });
            }


        } catch (Exception e) {
            
        }
    }

    


}
