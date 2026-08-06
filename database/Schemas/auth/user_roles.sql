-- ==============================================================================
-- User-Role mapping table (Many-to-Many)
-- ==============================================================================

CREATE TABLE IF NOT EXISTS auth.user_roles (
    usr_uid UUID NOT NULL REFERENCES auth.users(usr_uid) ON DELETE CASCADE,
    rol_uid UUID NOT NULL REFERENCES auth.roles(rol_uid) ON DELETE CASCADE,
    crte_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (usr_uid, rol_uid)
);

-- Indexes for performance
CREATE INDEX IF NOT EXISTS idx_user_roles_usr_uid ON auth.user_roles(usr_uid);
CREATE INDEX IF NOT EXISTS idx_user_roles_rol_uid ON auth.user_roles(rol_uid);