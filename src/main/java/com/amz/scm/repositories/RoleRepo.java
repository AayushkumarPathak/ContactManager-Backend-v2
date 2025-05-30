package com.amz.scm.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.amz.scm.models.Role;

public interface RoleRepo  extends JpaRepository<Role,Integer>{
    

}
