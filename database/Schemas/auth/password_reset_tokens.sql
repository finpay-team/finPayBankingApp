-- Table to store single-use, expiring password-reset tokens.
-- Usr_uid ties each token to a user; expired/used tokens are rejected at use-time.
CREATE TABLE IF NOT EXISTS password_reset_tokens(
                                                    prt_uuid UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    usr_uid UUID NOT NULL REFERENCES users(usr_uid) ON DELETE CASCADE,

    prt_tkn VARCHAR(255) UNIQUE NOT NULL,
    prt_exp_time TIMESTAMP WITH TIME ZONE NOT NULL,
                                                        prt_used_flg BOOLEAN DEFAULT FALSE NOT NULL,

                                                        crte_usr_uid UUID REFERENCES users(usr_uid),
    crte_time TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                                                        upd_usr_uid UUID REFERENCES users(usr_uid),
    upd_time TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
                                                        );
