-- begin DDCRD_DIAGNOSE_EXECUTION_LOG
create table DDCRD_DIAGNOSE_EXECUTION_LOG (
    ID varchar(32),
    VERSION integer not null,
    CREATE_TS datetime(3),
    CREATED_BY varchar(50),
    UPDATE_TS datetime(3),
    UPDATED_BY varchar(50),
    DELETE_TS datetime(3),
    DELETED_BY varchar(50),
    --
    EXECUTION_SUCCESSFUL boolean not null,
    EXECUTION_TIMESTAMP datetime(3) not null,
    EXECUTION_USER varchar(255),
    EXECUTION_RESULT_FILE_ID varchar(32),
    DIAGNOSE_TYPE varchar(255) not null,
    EXECUTION_TYPE varchar(255) not null,
    --
    primary key (ID)
)^
-- end DDCRD_DIAGNOSE_EXECUTION_LOG
-- begin CUBAAT_SSH_CREDENTIALS
create table CUBAAT_SSH_CREDENTIALS (
    ID varchar(32) not null,
    VERSION integer not null,
    CREATE_TS datetime(3),
    CREATED_BY varchar(50),
    UPDATE_TS datetime(3),
    UPDATED_BY varchar(50),
    DELETE_TS datetime(3),
    DELETED_BY varchar(50),
    --
    HOSTNAME varchar(255) not null,
    SESSION_NAME varchar(255) not null,
    IS_FOR_EVERYONE boolean,
    PORT integer not null,
    LOGIN varchar(255) not null,
    PRIVATE_KEY_ID varchar(32),
    --
    primary key (ID)
)^
-- end CUBAAT_SSH_CREDENTIALS