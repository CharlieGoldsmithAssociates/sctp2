INSERT INTO permissions (name, description, `group`, system_permission, active)
VALUES
    ('READ_ROLES', 'Read system user roles', 'user_management', 0, 1)
   ,('WRITE_ROLES', 'Add and modify system user roles', 'user_management', 0, 1)
;

INSERT INTO role_permissions(role, permission)
VALUES
    ('ROLE_SYSTEM_ADMIN', 'READ_ROLES')
   ,('ROLE_SYSTEM_ADMIN', 'WRITE_ROLES')
;

INSERT INTO permission_dependency(parent, child)
VALUES
    ('ACCESS_ADMINISTRATION', 'READ_ROLES')
   ,('ACCESS_ADMINISTRATION', 'WRITE_ROLES')
;