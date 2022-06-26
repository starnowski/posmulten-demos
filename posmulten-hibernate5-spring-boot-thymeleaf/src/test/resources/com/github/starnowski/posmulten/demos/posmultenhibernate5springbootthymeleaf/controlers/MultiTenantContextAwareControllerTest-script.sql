SELECT set_pos_demo_tenant('no_such_tenant');-- TODO Set correct tenant
--- tenant xds1

INSERT INTO user_info (user_id, username, tenant_id, password) VALUES ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'starnowski', 'xds', '$2a$10$xsaKSi2sqp9DnWlgG8Ah9.DNwxCA9zblyGAAbYub4AAs1LBN6CUnO');
INSERT INTO user_info (user_id, username, tenant_id, password) VALUES ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12', 'dude', 'xds', '$2a$10$xsaKSi2sqp9DnWlgG8Ah9.DNwxCA9zblyGAAbYub4AAs1LBN6CUnO');

--- tenant xds1
INSERT INTO user_info (user_id, username, tenant_id, password) VALUES ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a13', 'kglenny', 'xds1', '$2a$10$xsaKSi2sqp9DnWlgG8Ah9.DNwxCA9zblyGAAbYub4AAs1LBN6CUnO');
INSERT INTO user_info (user_id, username, tenant_id, password) VALUES ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14', 'starnowski', 'xds1', '$2a$10$xsaKSi2sqp9DnWlgG8Ah9.DNwxCA9zblyGAAbYub4AAs1LBN6CUnO');
INSERT INTO user_info (user_id, username, tenant_id, password) VALUES ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a15', 'dude', 'xds1', '$2a$10$xsaKSi2sqp9DnWlgG8Ah9.DNwxCA9zblyGAAbYub4AAs1LBN6CUnO');

INSERT INTO user_role (id, role, user_id, tenant_id) VALUES (nextval( 'hibernate_sequence' ), 'ADMIN', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14', 'xds1'); -- starnowski as ADMIN
INSERT INTO user_role (id, role, user_id, tenant_id) VALUES (nextval( 'hibernate_sequence' ), 'AUDITOR', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a15', 'xds1'); -- dude as AUDITOR

--- tenant_info
INSERT INTO tenant_info (tenant_id, domain) VALUES ('xds', 'my.doc.com');
INSERT INTO tenant_info (tenant_id, domain) VALUES ('xds1', 'polish.dude.eu');

INSERT INTO company (company_id, tenant_id, name, status) VALUES (1, 'xds', 'my_doc_com', 'ACTIVE');
INSERT INTO company (company_id, tenant_id, name, status) VALUES (1, 'xds1', 'polish_dude_eu', 'ACTIVE');
