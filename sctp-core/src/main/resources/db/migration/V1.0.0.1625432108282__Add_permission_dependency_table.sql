CREATE TABLE IF NOT EXISTS permission_dependency (
    id bigint primary key auto_increment,
    parent varchar(100) not null,
    child varchar(100) not null,
    foreign key (parent) references permissions (name),
    foreign key (child) references permissions (name)
);

-- Give the system administrator some initial permissions

INSERT INTO permission_groups(name, description) VALUES ('administration', 'Administration');

INSERT INTO permissions (name, description, `group`, system_permission, active)
VALUES
    ('ACCESS_ADMINISTRATION', 'Grants access to the administration module', 'administration', 1, 0) -- Implicit permission
   ,('READ_LOGS', 'Access audit event logs', 'administration', 0, 1)
   ,('ARCHIVE_LOGS', 'Archive logs', 'administration', 0, 1)
   ,('READ_PERMISSIONS', 'View system permissions', 'administration', 0, 1)
;

INSERT INTO role_permissions(role, permission)
VALUES
    ('ROLE_SYSTEM_ADMIN', 'ACCESS_ADMINISTRATION')
   ,('ROLE_SYSTEM_ADMIN', 'READ_LOGS')
   ,('ROLE_SYSTEM_ADMIN', 'ARCHIVE_LOGS')
   ,('ROLE_SYSTEM_ADMIN', 'READ_PERMISSIONS')
;

INSERT INTO permission_dependency(parent, child)
VALUES
    ('ACCESS_ADMINISTRATION', 'READ_USERS')
   ,('ACCESS_ADMINISTRATION', 'WRITE_USERS')
   ,('ACCESS_ADMINISTRATION', 'READ_LOGS')
   ,('ACCESS_ADMINISTRATION', 'ARCHIVE_LOGS')
   ,('ACCESS_ADMINISTRATION', 'READ_PERMISSIONS')
;