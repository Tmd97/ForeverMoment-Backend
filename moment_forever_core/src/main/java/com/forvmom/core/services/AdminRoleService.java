package com.forvmom.core.services;

import com.forvmom.common.dto.request.AuthUserResponseDto;
import com.forvmom.common.dto.request.RoleRequestDto;
import com.forvmom.common.dto.response.RoleResponseDto;

import com.forvmom.common.errorhandler.NotAllowedCustomException;
import com.forvmom.common.errorhandler.ResourceNotFoundException;
import com.forvmom.core.mapper.ApplicationUserBeanMapper;
import com.forvmom.core.mapper.RoleBeanMapper;
import com.forvmom.data.dao.auth.AuthUserDao;
import com.forvmom.data.dao.auth.RoleDao;
import com.forvmom.data.entities.auth.AuthUser;
import com.forvmom.data.entities.auth.AuthUserRole;
import com.forvmom.data.entities.auth.Role;
import com.forvmom.security.dto.AuthBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AdminRoleService {

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private AuthUserDao authUserDao;

    // TODO: (need discussion) System roles that cannot be deleted
    private static final List<String> SYSTEM_ROLES = List.of("SYSTEM", "SUPER_ADMIN");

    @Transactional
    public RoleResponseDto createRole(RoleRequestDto requestDto) {
        //Only Super Admin can create system roles
        checkRoleCreationAndModificationAllowed(requestDto);
        if (roleDao.existsByNameIgnoreCase(requestDto.getRoleName())) {
            throw new IllegalArgumentException(
                    "Role with name '" + requestDto.getRoleName() + "' already exists"
            );
        }
        Role role = RoleBeanMapper.mapDtoToEntity(requestDto);
        Role savedRole = roleDao.save(role);

        return RoleBeanMapper.mapEntityToDto(savedRole);
    }

    @Transactional(readOnly = true)
    public RoleResponseDto getRoleById(Long id) {
        Role role = getRoleByIdValidation(id);
        return RoleBeanMapper.mapEntityToDto(role);
    }


    @Transactional(readOnly = true)
    public RoleResponseDto getRoleByName(String name) {
        Optional<Role> role = roleDao.findByNameIgnoreCase(name);
        if (role.isEmpty()) {
            throw new ResourceNotFoundException("Role not found with name: " + name);
        }
        return RoleBeanMapper.mapEntityToDto(role.get());
    }

    @Transactional(readOnly = true)
    public List<RoleResponseDto> getAllRoles() {
        List<Role> roles = roleDao.findByActiveTrue();
        return RoleBeanMapper.toDtoList(roles);
    }

    @Transactional(readOnly = true)
    public List<RoleResponseDto> getAllActiveRoles() {
        List<Role> roles = roleDao.getAllActiveRoles();
        return RoleBeanMapper.toDtoList(roles);
    }

    @Transactional(readOnly = true)
    public List<AuthUserResponseDto> getAllUserByRole(Long roleId) {
        List<AuthUserRole> authUserRoles = authUserDao.findAuthUserByRole(roleId);
        List<AuthUser> authUserList = authUserRoles.stream()
                .map(AuthUserRole::getAuthUser)
                .toList();
        return authUserList.stream().map(AuthBeanMapper::mapEntityToDtoForAuth).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RoleResponseDto> getSystemRoles() {
        List<Role> roles = roleDao.findBySystemRoleTrue();
        return RoleBeanMapper.toDtoList(roles);
    }

    @Transactional
    public RoleResponseDto updateRole(Long id, RoleRequestDto requestDto) {
        Role role = getRoleByIdValidation(id);
        validIfSuperAdmin(role);
        // Check if name is being changed and already exists
        if (!role.getName().equalsIgnoreCase(requestDto.getRoleName()) &&
                roleDao.existsByNameIgnoreCase(requestDto.getRoleName())) {
            throw new IllegalArgumentException(
                    "Role with name '" + requestDto.getRoleName() + "' already exists"
            );
        }

        RoleBeanMapper.updateEntity(role, requestDto);
        Role updatedRole = roleDao.save(role);

        return RoleBeanMapper.mapEntityToDto(updatedRole);
    }

    @Transactional
    public void deleteRole(Long id) {
        Role role = getRoleByIdValidation(id);
        validIfSuperAdmin(role);
        // Soft delete - just deactivate instead of actual delete
        role.setActive(false);
        roleDao.save(role);

        // Or hard delete if you prefer:
        // roleDao.delete(role);
    }

    @Transactional
    public RoleResponseDto activateRole(Long id) {
        Role role = getRoleByIdValidation(id);
        validIfSuperAdmin(role);
        role.setActive(true);
        Role updatedRole = roleDao.save(role);
        return RoleBeanMapper.mapEntityToDto(updatedRole);
    }

    @Transactional
    public RoleResponseDto deactivateRole(Long id) {
        Role role = getRoleByIdValidation(id);
        validIfSuperAdmin(role);
        role.setActive(false);
        Role updatedRole = roleDao.save(role);
        return RoleBeanMapper.mapEntityToDto(updatedRole);
    }

    private void validIfSuperAdmin(Role role) {
        if (role.getName().equalsIgnoreCase("SUPER_ADMIN")) {
            throw new NotAllowedCustomException(
                    "Cannot modify system role: " + role.getName()
            );
        }
    }


    public void checkRoleCreationAndModificationAllowed(RoleRequestDto requestDto) {
        if (requestDto.getSystemRole() != null && requestDto.getSystemRole()) {
            throw new NotAllowedCustomException("Only Super Admin can create system roles");
        }
    }

    private Role getRoleByIdValidation(Long id) {
        Role role = roleDao.findById(id);
        if (role == null) {
            throw new ResourceNotFoundException("Role not found with id: " + id);
        }
        return role;
    }
}