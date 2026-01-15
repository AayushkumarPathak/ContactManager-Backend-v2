package com.techmagnet.scm.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.techmagnet.scm.models.Role;

public interface RoleRepo  extends JpaRepository<Role,Integer>{
    

}
