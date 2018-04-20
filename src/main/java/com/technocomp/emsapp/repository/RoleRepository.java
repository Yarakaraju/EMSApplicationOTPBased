package com.technocomp.emsapp.repository;

import com.technocomp.emsapp.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Ravi Varma Yarakaraj on 12/28/2017.
 */
@Repository("roleRepository")
public interface RoleRepository extends JpaRepository<Role, Long>{
	Role findByRoleName(String role);

}
