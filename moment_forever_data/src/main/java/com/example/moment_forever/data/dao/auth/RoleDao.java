package com.example.moment_forever.data.dao.auth;

import com.example.moment_forever.data.dao.GenericDao;
import com.example.moment_forever.data.entities.ApplicationUser;
import com.example.moment_forever.data.entities.auth.AuthUserRole;
import com.example.moment_forever.data.entities.auth.Role;

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