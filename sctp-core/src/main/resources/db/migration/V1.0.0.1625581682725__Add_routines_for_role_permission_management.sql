DROP PROCEDURE IF EXISTS removeRolePermission;
CREATE PROCEDURE removeRolePermission(IN roleName varchar(30), IN permissionName varchar(30))
BEGIN
-- Delete permissions from the given role that meet the following conditions:
-- 	Permission has no dependents and is independent
-- 	Permission has dependents (All dependents will be removed as well)
--  Permission is a parent with no child
	DELETE
	FROM role_permissions rp
	WHERE rp.`role` = roleName
	AND rp.permission IN (
		SELECT permissionName
		UNION
		-- If the permission is a parent, remove its children as well --
		SELECT pd.child
		FROM permission_dependency pd
		WHERE pd.parent = permissionName
	);

	-- Remove childless parent permissions
	DELETE rp2
	FROM role_permissions rp2
	JOIN permission_dependency pd2 ON pd2.parent = rp2.permission
	WHERE rp2.`role` = roleName AND getNumberOfChildRolePermissions(roleName, permissionName) = 0;
END;


DROP FUNCTION IF EXISTS getNumberOfChildRolePermissions;
CREATE FUNCTION getNumberOfChildRolePermissions(roleName varchar(30), permissionName varchar(30))
RETURNS BIGINT
DETERMINISTIC
BEGIN
	DECLARE child_count BIGINT;

	SELECT COALESCE (count(pd.id), 0) INTO @child_count
	FROM permission_dependency pd
	WHERE pd.parent = permissionName AND pd.child IN (
		SELECT rp.permission
		FROM role_permissions rp
		WHERE rp.`role` = roleName
	);
	RETURN @child_count;
END;

DROP PROCEDURE IF EXISTS getRolePermissionsForDisplay;
CREATE PROCEDURE getRolePermissionsForDisplay(IN roleName varchar(30))
BEGIN
	SELECT
		p.id AS permissionId
		,p.description AS permissionName
		,pg.id AS groupId
		,pg.description AS groupName
	FROM permissions p
	JOIN permission_groups pg ON pg.name = p.`group`
	JOIN role_permissions rp ON rp.permission = p.name
	WHERE rp.`role` = roleName AND p.system_permission = FALSE;
END
