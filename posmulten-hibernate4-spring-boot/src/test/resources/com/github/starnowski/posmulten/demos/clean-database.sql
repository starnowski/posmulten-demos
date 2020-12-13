DELETE FROM user_role CASCADE;
DELETE FROM user_info CASCADE;
DELETE FROM tenant_info CASCADE;
DELETE FROM posts CASCADE;
DELETE FROM comments CASCADE;

--TODO Add option in builder component so that the correct access for grantee could be added.
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO "posmhib4sb-user";
--TODO Add option in builder component so that the correct access for grantee could be added.
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO "posmhib4sb-user";



