package com.forvmom.core.controller.admin;

import com.forvmom.common.dto.request.AuthUserResponseDto;
import com.forvmom.common.response.ApiResponse;
import com.forvmom.common.response.ResponseUtil;
import com.forvmom.common.utils.AppConstants;
import com.forvmom.common.dto.request.RoleRequestDto;
import com.forvmom.common.dto.response.RoleResponseDto;
import com.forvmom.core.services.AdminRoleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@RestController
@RequestMapping("/admin/roles")
@Tag(name = "Admin Role API", description = "Endpoints for managing user roles (Admin only)")
public class AdminRoleController {

    @Autowired
    private AdminRoleService roleService;

    @PostMapping
    @Operation(summary = "Create Role", description = "Create a new user role")
    public ResponseEntity<ApiResponse<?>> createRole(@Valid @RequestBody RoleRequestDto requestDto) {
        RoleResponseDto response = roleService.createRole(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseUtil.buildCreatedResponse(response, "Role created successfully"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Role by ID", description = "Fetch a role by its unique ID")
    public ResponseEntity<ApiResponse<?>> getRoleById(@PathVariable Long id) {
        RoleResponseDto response = roleService.getRoleById(id);
        return ResponseEntity.ok(
                ResponseUtil.buildOkResponse(response, AppConstants.MSG_FETCHED));
    }

    @GetMapping("/name/{name}")
    @Operation(summary = "Get Role by Name", description = "Fetch a role by its name")
    public ResponseEntity<ApiResponse<?>> getRoleByName(@PathVariable String name) {
        RoleResponseDto response = roleService.getRoleByName(name);
        return ResponseEntity.ok(
                ResponseUtil.buildOkResponse(response, AppConstants.MSG_FETCHED));
    }

    @GetMapping
    @Operation(summary = "Get All Roles", description = "Fetch a list of all roles")
    public ResponseEntity<ApiResponse<?>> getAllRoles() {
        List<RoleResponseDto> responses = roleService.getAllRoles();
        return ResponseEntity.ok(
                ResponseUtil.buildOkResponse(responses, AppConstants.MSG_FETCHED));
    }

    @GetMapping("/active")
    @Operation(summary = "Get Active Roles", description = "Fetch a list of all active roles")
    public ResponseEntity<ApiResponse<?>> getAllActiveRoles() {
        List<RoleResponseDto> responses = roleService.getAllActiveRoles();
        return ResponseEntity.ok(
                ResponseUtil.buildOkResponse(responses, "Active roles fetched successfully"));
    }

    @GetMapping("/system")
    public ResponseEntity<ApiResponse<?>> getSystemRoles() {
        List<RoleResponseDto> responses = roleService.getSystemRoles();
        return ResponseEntity.ok(
                ResponseUtil.buildOkResponse(responses, "System roles fetched successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> updateRole(
            @PathVariable Long id,
            @Valid @RequestBody RoleRequestDto requestDto) {
        RoleResponseDto response = roleService.updateRole(id, requestDto);
        return ResponseEntity.ok(
                ResponseUtil.buildOkResponse(response, "Role updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return ResponseEntity.ok(
                ResponseUtil.buildOkResponse(null, "Role deleted (deactivated) successfully"));
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<?>> activateRole(@PathVariable Long id) {
        RoleResponseDto response = roleService.activateRole(id);
        return ResponseEntity.ok(
                ResponseUtil.buildOkResponse(response, "Role activated successfully"));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<?>> deactivateRole(@PathVariable Long id) {
        RoleResponseDto response = roleService.deactivateRole(id);
        return ResponseEntity.ok(
                ResponseUtil.buildOkResponse(response, "Role deactivated successfully"));
    }

    @GetMapping("/{roleId}/users")
    public ResponseEntity<ApiResponse<?>> getAllUserByRole(@PathVariable Long roleId) {
        List<AuthUserResponseDto> responses = roleService.getAllUserByRole(roleId);
        return ResponseEntity.ok(
                ResponseUtil.buildOkResponse(responses, "All account details of User fetched successfully"));
    }

}