-- begin CUBAAT_SSH_CREDENTIALS
create table CUBAAT_SSH_CREDENTIALS (
    ID varchar(36) not null,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    HOSTNAME varchar(255) not null,
    SESSION_NAME varchar(255) not null,
    IS_FOR_EVERYONE boolean,
    PORT integer not null,
    LOGIN varchar(255) not null,
    PRIVATE_KEY_ID varchar(36),
    --
    primary key (ID)
)^
-- end CUBAAT_SSH_CREDENTIALS
