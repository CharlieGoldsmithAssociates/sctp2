DROP PROCEDURE IF EXISTS `getRolePermissionsInfo`;
CREATE PROCEDURE `getRolePermissionsInfo`(IN role_name varchar(100))
begin
    SELECT p.description AS permission, pg.description AS `group`
    FROM role_permissions rp
    JOIN permissions p ON p.name = rp.permission
    JOIN permission_groups pg ON pg.name  = p.`group`
    WHERE rp .`role` = role_name
    ORDER BY pg.description;
END;

DROP PROCEDURE IF EXISTS getAvailableRolePermissions;
CREATE PROCEDURE `getAvailableRolePermissions`(IN role_name varchar(100))
BEGIN
	SELECT p.* FROM permissions p WHERE p.name NOT IN (
		select p1.name
		from permissions p1
		JOIN role_permissions rp ON rp.permission = p1.name
		WHERE rp.`role` = role_name
	);
END