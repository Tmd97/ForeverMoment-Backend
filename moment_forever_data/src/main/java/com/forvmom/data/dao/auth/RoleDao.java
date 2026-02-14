package com.forvmom.data.dao.auth;

import com.forvmom.data.dao.GenericDao;
import com.forvmom.data.entities.ApplicationUser;
import com.forvmom.data.entities.auth.AuthUserRole;
import com.forvmom.data.entities.auth.Role;

import java.util.List;
import java.util.Optional;

public interface RoleDao extends GenericDao<Role, Long> {

    Optional<Role> findByNameIgnoreCase(String roleName);

    boolean existsByNameIgnoreCase(String roleName);

    List<Role> findByActiveTrue();

    List<Role> findBySystemRoleTrue();
    List<Role> getAllActiveRoles();

    List<Role> findByIds(List<Long> ids);
//    List<AuthUserRole> findAuthUserByRole(Long id);



}