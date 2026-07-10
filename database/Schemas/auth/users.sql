CREATE TABLE IF NOT EXISTS users(
    usr_uid UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    usr_eml VARCHAR(225) UNIQUE NOT NULL,
    usr_pwd_hsh VARCHAR(225),
    usr_fst_name VARCHAR(100),
    usr_lst_name VARCHAR(100),

    usr_actv_flg BOOLEAN DEFAULT TRUE NOT NULL,
    usr_eml_vrf_flg BOOLEAN DEFAULT FALSE NOT NULL,
    usr_lck_flg BOOLEAN DEFAULT FALSE NOT NULL,
    sft_del BOOLEAN DEFAULT FALSE NOT NULL,

    crte_usr_uid UUID REFERENCES users(usr_uid), -- Self-referencing FK
    crte_time TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    upd_usr_uid UUID REFERENCES users(usr_uid),
    upd_time TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);