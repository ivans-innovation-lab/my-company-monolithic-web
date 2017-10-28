
CREATE SEQUENCE SYSTEM_SEQUENCE_AFE1D6B0_4F71_4073_9CFA_5DB97AD3DF70 START WITH 1 BELONGS_TO_TABLE;
CREATE SEQUENCE SYSTEM_SEQUENCE_3B0FFA08_4E47_402E_A303_1D7EAEB60E94 START WITH 1 BELONGS_TO_TABLE;
DROP TABLE IF EXISTS `association_value_entry`;
CREATE TABLE ASSOCIATION_VALUE_ENTRY(
    ID BIGINT DEFAULT (NEXT VALUE FOR SYSTEM_SEQUENCE_AFE1D6B0_4F71_4073_9CFA_5DB97AD3DF70) NOT NULL NULL_TO_DEFAULT SEQUENCE SYSTEM_SEQUENCE_AFE1D6B0_4F71_4073_9CFA_5DB97AD3DF70,
    ASSOCIATION_KEY VARCHAR(255) NOT NULL,
    ASSOCIATION_VALUE VARCHAR(255),
    SAGA_ID VARCHAR(255) NOT NULL,
    SAGA_TYPE VARCHAR(255)
);
ALTER TABLE ASSOCIATION_VALUE_ENTRY ADD CONSTRAINT CONSTRAINT_B PRIMARY KEY(ID);
-- 0 +/- SELECT COUNT(*) FROM ASSOCIATION_VALUE_ENTRY;
CREATE INDEX IDXS2YI8BOBX8DD4EE6T63DUFS6D ON ASSOCIATION_VALUE_ENTRY(SAGA_ID, ASSOCIATION_KEY);
DROP TABLE IF EXISTS `blog_post`;
CREATE TABLE BLOG_POST(
    ID VARCHAR(255) NOT NULL,
    AGGREGATE_VERSION BIGINT,
    AUTHOR_ID VARCHAR(255),
    BROADCAST BOOLEAN,
    CATEGORY VARCHAR(255),
    DRAFT BOOLEAN,
    PUBLIC_SLUG VARCHAR(255),
    PUBLISH_AT TIMESTAMP,
    RAW_CONTENT VARCHAR(10255),
    RENDER_CONTENT VARCHAR(10255),
    TITLE VARCHAR(255),
    VERSION BIGINT
);
ALTER TABLE BLOG_POST ADD CONSTRAINT CONSTRAINT_E PRIMARY KEY(ID);
-- 0 +/- SELECT COUNT(*) FROM BLOG_POST;
DROP TABLE IF EXISTS `domain_event_entry`;
CREATE TABLE DOMAIN_EVENT_ENTRY(
    GLOBAL_INDEX BIGINT DEFAULT (NEXT VALUE FOR SYSTEM_SEQUENCE_3B0FFA08_4E47_402E_A303_1D7EAEB60E94) NOT NULL NULL_TO_DEFAULT SEQUENCE SYSTEM_SEQUENCE_3B0FFA08_4E47_402E_A303_1D7EAEB60E94,
    EVENT_IDENTIFIER VARCHAR(255) NOT NULL,
    META_DATA BLOB,
    PAYLOAD BLOB NOT NULL,
    PAYLOAD_REVISION VARCHAR(255),
    PAYLOAD_TYPE VARCHAR(255) NOT NULL,
    TIME_STAMP VARCHAR(255) NOT NULL,
    AGGREGATE_IDENTIFIER VARCHAR(255) NOT NULL,
    SEQUENCE_NUMBER BIGINT NOT NULL,
    TYPE VARCHAR(255)
);
ALTER TABLE DOMAIN_EVENT_ENTRY ADD CONSTRAINT CONSTRAINT_8 PRIMARY KEY(GLOBAL_INDEX);
-- 0 +/- SELECT COUNT(*) FROM DOMAIN_EVENT_ENTRY;
DROP TABLE IF EXISTS `project`;
CREATE TABLE PROJECT(
    ID VARCHAR(255) NOT NULL,
    AGGREGATE_VERSION BIGINT,
    CATEGORY VARCHAR(255),
    DESCRIPTION VARCHAR(255),
    NAME VARCHAR(255),
    REPO_URL VARCHAR(255),
    SITE_URL VARCHAR(255),
    VERSION BIGINT
);
ALTER TABLE PROJECT ADD CONSTRAINT CONSTRAINT_1 PRIMARY KEY(ID);

DROP TABLE IF EXISTS `team`;
CREATE TABLE TEAM(
    ID VARCHAR(255) NOT NULL,
    AGGREGATE_VERSION BIGINT,
    DESCRIPTION VARCHAR(255),
    NAME VARCHAR(255),
    STATUS INTEGER,
    VERSION BIGINT,
    PROJECT_ID VARCHAR(255)
);
ALTER TABLE TEAM ADD CONSTRAINT CONSTRAINT_2 PRIMARY KEY(ID);

ALTER TABLE TEAM ADD CONSTRAINT FKP6OVPC4SOFLFCJBAFCH33W2KY FOREIGN KEY(PROJECT_ID) REFERENCES PROJECT(ID) NOCHECK;

-- 0 +/- SELECT COUNT(*) FROM PROJECT;
DROP TABLE IF EXISTS `saga_entry`;
CREATE TABLE SAGA_ENTRY(
    SAGA_ID VARCHAR(255) NOT NULL,
    REVISION VARCHAR(255),
    SAGA_TYPE VARCHAR(255),
    SERIALIZED_SAGA BLOB
);
ALTER TABLE SAGA_ENTRY ADD CONSTRAINT CONSTRAINT_BD PRIMARY KEY(SAGA_ID);
-- 0 +/- SELECT COUNT(*) FROM SAGA_ENTRY;
DROP TABLE IF EXISTS `snapshot_event_entry`;
CREATE TABLE SNAPSHOT_EVENT_ENTRY(
    AGGREGATE_IDENTIFIER VARCHAR(255) NOT NULL,
    SEQUENCE_NUMBER BIGINT NOT NULL,
    TYPE VARCHAR(255) NOT NULL,
    EVENT_IDENTIFIER VARCHAR(255) NOT NULL,
    META_DATA BLOB,
    PAYLOAD BLOB NOT NULL,
    PAYLOAD_REVISION VARCHAR(255),
    PAYLOAD_TYPE VARCHAR(255) NOT NULL,
    TIME_STAMP VARCHAR(255) NOT NULL
);
ALTER TABLE SNAPSHOT_EVENT_ENTRY ADD CONSTRAINT CONSTRAINT_EB PRIMARY KEY(AGGREGATE_IDENTIFIER, SEQUENCE_NUMBER, TYPE);
-- 0 +/- SELECT COUNT(*) FROM SNAPSHOT_EVENT_ENTRY;
DROP TABLE IF EXISTS `token_entry`;
CREATE TABLE TOKEN_ENTRY(
    PROCESSOR_NAME VARCHAR(255) NOT NULL,
    SEGMENT INTEGER NOT NULL,
    OWNER VARCHAR(255),
    TIMESTAMP VARCHAR(255) NOT NULL,
    TOKEN BLOB,
    TOKEN_TYPE VARCHAR(255)
);
ALTER TABLE TOKEN_ENTRY ADD CONSTRAINT CONSTRAINT_7 PRIMARY KEY(PROCESSOR_NAME, SEGMENT);
-- 0 +/- SELECT COUNT(*) FROM TOKEN_ENTRY;
ALTER TABLE DOMAIN_EVENT_ENTRY ADD CONSTRAINT UK8S1F994P4LA2IPB13ME2XQM1W UNIQUE(AGGREGATE_IDENTIFIER, SEQUENCE_NUMBER);
ALTER TABLE DOMAIN_EVENT_ENTRY ADD CONSTRAINT UK_FWE6LSA8BFO6HYAS6UD3M8C7X UNIQUE(EVENT_IDENTIFIER);
ALTER TABLE SNAPSHOT_EVENT_ENTRY ADD CONSTRAINT UK_E1UUCJSEO68GOPMND0VGDL44H UNIQUE(EVENT_IDENTIFIER);
