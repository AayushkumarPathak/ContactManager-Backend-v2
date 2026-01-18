package com.techmagnet.scm;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.techmagnet.scm.utils.AppConstants;
import com.techmagnet.scm.models.Role;
import com.techmagnet.scm.repositories.RoleRepo;

@SpringBootApplication
@Slf4j
public class SmartContactManagerV2Service implements CommandLineRunner {

    private final PasswordEncoder passwordEncoder;
    private final RoleRepo roleRepo;

    public SmartContactManagerV2Service(PasswordEncoder passwordEncoder, RoleRepo roleRepo) {
        this.passwordEncoder = passwordEncoder;
        this.roleRepo = roleRepo;
    }

    public static void main(String[] args) {

        SpringApplication.run(SmartContactManagerV2Service.class, args);
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
           log.error("Error Starting Service: {}", e.getMessage(), e);
        }
    }
    
}
