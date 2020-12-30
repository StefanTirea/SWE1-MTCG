-- Created by Vertabelo (http://vertabelo.com)
-- Last modification date: 2020-12-30 12:39:53.133

-- tables
-- Table: battle_result
CREATE TABLE battle_result (
    id bigserial  NOT NULL,
    user_id bigint  NOT NULL,
    time timestamp  NOT NULL,
    rounds int  NOT NULL,
    result int  NOT NULL,
    elo_change int  NOT NULL,
    CONSTRAINT battle_result_pk PRIMARY KEY (id)
);

-- Table: card
CREATE TABLE card (
    id bigserial  NOT NULL,
    user_id bigint  NULL,
    name varchar(100)  NOT NULL,
    monster_type varchar(50)  NOT NULL,
    element_type varchar(50)  NOT NULL,
    damage int  NOT NULL,
    locked boolean  NOT NULL,
    CONSTRAINT card_pk PRIMARY KEY (id)
);

-- Table: package
CREATE TABLE package (
    id bigserial  NOT NULL,
    description varchar(250)  NOT NULL,
    user_id int8  NOT NULL,
    CONSTRAINT package_pk PRIMARY KEY (id)
);

-- Table: packaged_items
CREATE TABLE packaged_items (
    id bigserial  NOT NULL,
    package_id bigint  NOT NULL,
    card_id bigint  NOT NULL,
    CONSTRAINT packaged_items_pk PRIMARY KEY (id)
);

-- Table: token
CREATE TABLE token (
    id bigserial  NOT NULL,
    user_id bigint  NOT NULL,
    token varchar(150)  NOT NULL,
    expires_at timestamp  NOT NULL,
    CONSTRAINT token_pk PRIMARY KEY (id)
);

-- Table: trading
CREATE TABLE trading (
    id bigserial  NOT NULL,
    user_id bigint  NOT NULL,
    card_id bigint  NOT NULL,
    min_damage int  NOT NULL,
    type varchar(150)  NOT NULL,
    CONSTRAINT trading_pk PRIMARY KEY (id)
);

-- Table: user
CREATE TABLE "user" (
    id bigserial  NOT NULL,
    username varchar(50)  NOT NULL,
    password varchar(200)  NOT NULL,
    role varchar(20)  NOT NULL,
    coins int  NOT NULL,
    deck bigint[]  NOT NULL,
    elo int  NOT NULL,
    games_played int  NOT NULL DEFAULT 0,
    games_won int  NOT NULL DEFAULT 0,
    CONSTRAINT username_unique UNIQUE (username) NOT DEFERRABLE  INITIALLY IMMEDIATE,
    CONSTRAINT user_pk PRIMARY KEY (id)
);

-- foreign keys
-- Reference: battle_result_user (table: battle_result)
ALTER TABLE battle_result ADD CONSTRAINT battle_result_user
    FOREIGN KEY (user_id)
    REFERENCES "user" (id)  
    NOT DEFERRABLE 
    INITIALLY IMMEDIATE
;

-- Reference: card_user (table: card)
ALTER TABLE card ADD CONSTRAINT card_user
    FOREIGN KEY (user_id)
    REFERENCES "user" (id)  
    NOT DEFERRABLE 
    INITIALLY IMMEDIATE
;

-- Reference: package_user (table: package)
ALTER TABLE package ADD CONSTRAINT package_user
    FOREIGN KEY (user_id)
    REFERENCES "user" (id)  
    NOT DEFERRABLE 
    INITIALLY IMMEDIATE
;

-- Reference: packaged_items_card (table: packaged_items)
ALTER TABLE packaged_items ADD CONSTRAINT packaged_items_card
    FOREIGN KEY (card_id)
    REFERENCES card (id)  
    NOT DEFERRABLE 
    INITIALLY IMMEDIATE
;

-- Reference: packaged_items_package (table: packaged_items)
ALTER TABLE packaged_items ADD CONSTRAINT packaged_items_package
    FOREIGN KEY (package_id)
    REFERENCES package (id)  
    NOT DEFERRABLE 
    INITIALLY IMMEDIATE
;

-- Reference: token_user (table: token)
ALTER TABLE token ADD CONSTRAINT token_user
    FOREIGN KEY (user_id)
    REFERENCES "user" (id)  
    NOT DEFERRABLE 
    INITIALLY IMMEDIATE
;

-- Reference: trading_card (table: trading)
ALTER TABLE trading ADD CONSTRAINT trading_card
    FOREIGN KEY (card_id)
    REFERENCES card (id)  
    NOT DEFERRABLE 
    INITIALLY IMMEDIATE
;

-- Reference: trading_user (table: trading)
ALTER TABLE trading ADD CONSTRAINT trading_user
    FOREIGN KEY (user_id)
    REFERENCES "user" (id)  
    NOT DEFERRABLE 
    INITIALLY IMMEDIATE
;

-- End of file.

