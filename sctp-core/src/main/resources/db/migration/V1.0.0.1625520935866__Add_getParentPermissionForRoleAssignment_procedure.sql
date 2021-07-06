DROP PROCEDURE IF EXISTS `getParentPermissionForRoleAssignment`;
CREATE PROCEDURE `getParentPermissionForRoleAssignment`(IN roleName varchar(100), IN permissionName varchar(100))
BEGIN
	SELECT DISTINCT p.*
	FROM permission_dependency pd
	JOIN permissions p ON p.name = pd.parent
	WHERE pd.child = permissionName AND pd.parent NOT IN (
		SELECT rp.permission FROM role_permissions rp WHERE rp.`role` = roleName
	);
END