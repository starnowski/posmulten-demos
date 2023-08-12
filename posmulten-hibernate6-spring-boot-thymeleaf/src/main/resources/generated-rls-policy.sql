CREATE OR REPLACE FUNCTION public.get_ten_id() RETURNS VARCHAR(255) AS $$
SELECT current_setting('pos.c.ten')
$$ LANGUAGE sql
STABLE
PARALLEL SAFE;
CREATE OR REPLACE FUNCTION public.set_pos_demo_tenant(VARCHAR(255)) RETURNS VOID AS $$
BEGIN
PERFORM set_config('pos.c.ten', $1, false);
END
$$ LANGUAGE plpgsql
VOLATILE;
CREATE OR REPLACE FUNCTION public.equals_cur_tenant(VARCHAR(255)) RETURNS BOOLEAN AS $$
SELECT $1 = public.get_ten_id()
$$ LANGUAGE sql
STABLE
PARALLEL SAFE;
CREATE OR REPLACE FUNCTION public._tenant_hast_auth(VARCHAR(255), VARCHAR(255), VARCHAR(255), VARCHAR(255), VARCHAR(255)) RETURNS BOOLEAN AS $$
SELECT public.equals_cur_tenant($1)
$$ LANGUAGE sql
STABLE
PARALLEL SAFE;
CREATE OR REPLACE FUNCTION public.is_t_valid(VARCHAR(255)) RETURNS BOOLEAN AS $$
SELECT $1 <> CAST ('Some strange tenant ID' AS VARCHAR(255)) AND $1 <> CAST ('invalid_tenant' AS VARCHAR(255))
$$ LANGUAGE sql
IMMUTABLE
PARALLEL SAFE;
ALTER TABLE public.user_role ADD COLUMN tenant_id VARCHAR(255);
ALTER TABLE public.user_role ALTER COLUMN tenant_id SET NOT NULL;
ALTER TABLE public.posts ADD COLUMN tenant_id VARCHAR(255);
ALTER TABLE public.posts ALTER COLUMN tenant_id SET NOT NULL;
ALTER TABLE public."user_role" ENABLE ROW LEVEL SECURITY;
ALTER TABLE public."user_info" ENABLE ROW LEVEL SECURITY;
ALTER TABLE public."posts" ENABLE ROW LEVEL SECURITY;
CREATE POLICY user_role_table_rls_policy ON public.user_role
FOR ALL
TO "posmhib4sb-user"
USING (public._tenant_hast_auth(tenant_id, 'ALL', 'USING', 'user_role', 'public'))
WITH CHECK (public._tenant_hast_auth(tenant_id, 'ALL', 'WITH_CHECK', 'user_role', 'public'));
CREATE POLICY users_table_rls_policy ON public.user_info
FOR ALL
TO "posmhib4sb-user"
USING (public._tenant_hast_auth(tenant_id, 'ALL', 'USING', 'user_info', 'public'))
WITH CHECK (public._tenant_hast_auth(tenant_id, 'ALL', 'WITH_CHECK', 'user_info', 'public'));
CREATE POLICY posts_table_rls_policy ON public.posts
FOR ALL
TO "posmhib4sb-user"
USING (public._tenant_hast_auth(tenant_id, 'ALL', 'USING', 'posts', 'public'))
WITH CHECK (public._tenant_hast_auth(tenant_id, 'ALL', 'WITH_CHECK', 'posts', 'public'));
CREATE OR REPLACE FUNCTION public.is_user_exists(uuid) RETURNS BOOLEAN AS $$
SELECT EXISTS (
	SELECT 1 FROM public.user_info rt WHERE rt.user_id = $1 AND rt.tenant_id = public.get_ten_id()
)
$$ LANGUAGE sql
STABLE
PARALLEL SAFE;
ALTER TABLE "public"."posts" ADD CONSTRAINT user_info_tenant_constraint CHECK ((userId IS NULL) OR (public.is_user_exists(userId)));
ALTER TABLE "public"."user_role" ADD CONSTRAINT is_tenant_valid_constraint_sdfa CHECK (tenant_id IS NULL OR public.is_t_valid(tenant_id));
ALTER TABLE "public"."user_info" ADD CONSTRAINT is_tenant_valid_constraint_sdfa CHECK (tenant_id IS NULL OR public.is_t_valid(tenant_id));
ALTER TABLE "public"."posts" ADD CONSTRAINT is_tenant_valid_constraint_sdfa CHECK (tenant_id IS NULL OR public.is_t_valid(tenant_id));
ALTER TABLE public.user_role ALTER COLUMN tenant_id SET DEFAULT public.get_ten_id();
ALTER TABLE public.user_info ALTER COLUMN tenant_id SET DEFAULT public.get_ten_id();
ALTER TABLE public.posts ALTER COLUMN tenant_id SET DEFAULT public.get_ten_id();
