DROP PROCEDURE IF EXISTS getAvailableRolePermissions;
CREATE PROCEDURE `getAvailableRolePermissions`(IN role_name varchar(100))
BEGIN
	SELECT p.id AS permissionId, p.description AS permissionName, pg.description AS groupName, pg.id AS groupId
	FROM permissions p
	JOIN permission_groups pg ON pg.name = p.`group`
	WHERE p.system_permission = FALSE and p.name NOT IN (
		select p1.name
		from permissions p1
		JOIN role_permissions rp ON rp.permission = p1.name
		WHERE rp.`role` = role_name
	);
END