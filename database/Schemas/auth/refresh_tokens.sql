-- ==============================================================================
-- Refresh Tokens table
-- ==============================================================================

CREATE TABLE IF NOT EXISTS auth.refresh_tokens (
    rtk_uid UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    usr_uid UUID NOT NULL REFERENCES auth.users(usr_uid) ON DELETE CASCADE,
    rtk_tkn VARCHAR(255) UNIQUE NOT NULL,
    rtk_exp_time TIMESTAMP WITH TIME ZONE NOT NULL,
    rtk_rvk_flg BOOLEAN DEFAULT FALSE NOT NULL,
    crte_usr_uid UUID REFERENCES auth.users(usr_uid),
    crte_time TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    upd_usr_uid UUID REFERENCES auth.users(usr_uid),
    upd_time TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for performance
CREATE INDEX IF NOT EXISTS idx_refresh_tokens_usr_uid ON auth.refresh_tokens(usr_uid);
CREATE INDEX IF NOT EXISTS idx_refresh_tokens_rtk_tkn ON auth.refresh_tokens(rtk_tkn);