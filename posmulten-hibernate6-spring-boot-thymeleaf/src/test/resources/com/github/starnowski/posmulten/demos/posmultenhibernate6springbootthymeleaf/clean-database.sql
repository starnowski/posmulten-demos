SELECT set_pos_demo_tenant('no_such_tenant');
TRUNCATE user_role CASCADE;
TRUNCATE posts CASCADE;
TRUNCATE user_info CASCADE;

-- ADMIN
TRUNCATE tenant_info CASCADE;
