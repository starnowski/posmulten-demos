SELECT set_pos_demo_tenant('no_such_tenant');-- TODO Set correct tenant
--- tenant xds1

SELECT set_pos_demo_tenant('xds');
INSERT INTO user_info (user_id, username, tenant_id, password) VALUES ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'starnowski', 'xds', '$2a$10$xsaKSi2sqp9DnWlgG8Ah9.DNwxCA9zblyGAAbYub4AAs1LBN6CUnO');
INSERT INTO user_info (user_id, username, tenant_id, password) VALUES ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12', 'dude', 'xds', '$2a$10$xsaKSi2sqp9DnWlgG8Ah9.DNwxCA9zblyGAAbYub4AAs1LBN6CUnO');

INSERT INTO user_role (id, role, user_id, tenant_id) VALUES (nextval( 'hibernate_sequence' ), 'ADMIN', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'xds'); -- starnowski as ADMIN
INSERT INTO user_role (id, role, user_id, tenant_id) VALUES (nextval( 'hibernate_sequence' ), 'ADMIN', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12', 'xds'); -- dude as ADMIN

INSERT INTO posts (id, userId, text) VALUES (nextval( 'hibernate_sequence' ), 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'This is a text content');
INSERT INTO posts (id, userId, text) VALUES (nextval( 'hibernate_sequence' ), 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12', 'Post post and post');

--- tenant xds1
SELECT set_pos_demo_tenant('xds1');
INSERT INTO user_info (user_id, username, tenant_id, password) VALUES ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a13', 'mcaine', 'xds1', '$2a$10$xsaKSi2sqp9DnWlgG8Ah9.DNwxCA9zblyGAAbYub4AAs1LBN6CUnO');
INSERT INTO user_info (user_id, username, tenant_id, password) VALUES ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14', 'starnowski', 'xds1', '$2a$10$xsaKSi2sqp9DnWlgG8Ah9.DNwxCA9zblyGAAbYub4AAs1LBN6CUnO');
INSERT INTO user_info (user_id, username, tenant_id, password) VALUES ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a15', 'dude', 'xds1', '$2a$10$xsaKSi2sqp9DnWlgG8Ah9.DNwxCA9zblyGAAbYub4AAs1LBN6CUnO');

INSERT INTO user_role (id, role, user_id, tenant_id) VALUES (nextval( 'hibernate_sequence' ), 'ADMIN', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14', 'xds1'); -- starnowski as ADMIN
INSERT INTO user_role (id, role, user_id, tenant_id) VALUES (nextval( 'hibernate_sequence' ), 'AUTHOR', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a15', 'xds1'); -- dude as AUTHOR

INSERT INTO posts (id, userId, text) VALUES (nextval( 'hibernate_sequence' ), 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a15', 'First post in application for xds1');
INSERT INTO posts (id, userId, text) VALUES (nextval( 'hibernate_sequence' ), 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a15', 'Second post in application for xds1');

--- tenant_info
INSERT INTO tenant_info (tenant_id, domain) VALUES ('xds', 'my.doc.com');
INSERT INTO tenant_info (tenant_id, domain) VALUES ('xds1', 'polish.dude.eu');

