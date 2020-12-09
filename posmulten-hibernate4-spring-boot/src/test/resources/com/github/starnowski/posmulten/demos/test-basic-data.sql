--- tenant xds1
SELECT set_pos_demo_tenant('xds');
INSERT INTO user_info (user_id, username, tenant_id) VALUES ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'starnowski', 'xds');
INSERT INTO user_info (user_id, username, tenant_id) VALUES ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12', 'mkyc', 'xds');

--- tenant xds1
SELECT set_pos_demo_tenant('xds1');
INSERT INTO user_info (user_id, username, tenant_id) VALUES ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a13', 'kglenny', 'xds1');