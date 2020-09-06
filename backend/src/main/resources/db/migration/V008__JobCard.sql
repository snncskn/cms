ALTER TABLE PUBLIC.USER ADD POSITION VARCHAR(50) NULL;

CREATE TABLE JOB_CARD (
	ID BIGSERIAL NOT NULL,
	UUID UUID NOT NULL,
	ACTIVE BOOL NOT NULL,
	REPORT_TYPE VARCHAR(20) NULL,
	BREAKDOWN_END_DATE TIMESTAMP NULL,
	BREAKDOWN_START_DATE TIMESTAMP NULL,
	REPORT_NUMBER VARCHAR(20) NULL,
	STATUS VARCHAR(255) NULL,
	START_DATE TIMESTAMP NULL,
	END_DATE TIMESTAMP NULL,
	VEHICLE_ID INT8 NOT NULL,
    CURRENT_KM_HOUR VARCHAR(20) NOT NULL,
    RISK_ASSESSMENT BOOL NULL,
    LOCK_OUT_PROCEDURE BOOL NULL,
    WHEEL_NUTS BOOL NULL,
    OIL_LEVEL BOOL  NULL,
    MACHINE_GREASE BOOL NULL,
	FOREMAN_USER_ID INT8 NULL,
	MECHANIC_USER_ID INT8 NULL,
	OPERATOR_USER_ID INT8 NULL,
	SUPERVISOR_USER_ID INT8 NULL,
	DESCRIPTION VARCHAR(2000) NULL,
	CREATED_BY      INT8         NOT NULL,
    CREATED_AT      TIMESTAMP    NOT NULL,
    MODIFIED_BY     INT8         NULL,
    MODIFIED_AT     TIMESTAMP    NULL,
	CONSTRAINT JOB_CARD_PKEY PRIMARY KEY (ID),
	CONSTRAINT UX_JOB_CARD__UUID UNIQUE (UUID),
	CONSTRAINT FK_JOB_CARD__FOREMAN_USER_ID FOREIGN KEY (FOREMAN_USER_ID) REFERENCES PUBLIC.USER(ID),
	CONSTRAINT FK_JOB_CARD__MECHANIC_USER_ID FOREIGN KEY (MECHANIC_USER_ID) REFERENCES PUBLIC.USER(ID),
	CONSTRAINT FK_JOB_CARD__OPERATOR_USER_ID FOREIGN KEY (OPERATOR_USER_ID) REFERENCES PUBLIC.USER(ID),
	CONSTRAINT FK_JOB_CARD__SUPERVISOR_USER_ID FOREIGN KEY (SUPERVISOR_USER_ID) REFERENCES PUBLIC.USER(ID),
	CONSTRAINT FK_JOB_CARD__VEHICLE_ID FOREIGN KEY (VEHICLE_ID) REFERENCES VEHICLE(ID)
);
CREATE INDEX IX_JOB_CARD__FOREMAN_USER_ID ON JOB_CARD USING BTREE (FOREMAN_USER_ID);
CREATE INDEX IX_JOB_CARD__MECHANIC_USER_ID ON JOB_CARD USING BTREE (MECHANIC_USER_ID);
CREATE INDEX IX_JOB_CARD__OPERATOR_USER_ID ON JOB_CARD USING BTREE (OPERATOR_USER_ID);
CREATE INDEX IX_JOB_CARD__SUPERVISOR_USER_ID ON JOB_CARD USING BTREE (SUPERVISOR_USER_ID);
CREATE INDEX IX_JOB_CARD__VEHICLE_ID ON JOB_CARD USING BTREE (VEHICLE_ID);


CREATE TABLE JOB_CARD_HISTORY (
	ID BIGSERIAL NOT NULL,
	UUID UUID NOT NULL,
	DESCRIPTION VARCHAR(500) NOT NULL,
	JOB_CARD_ID INT8 NOT NULL,
    CREATED_BY      INT8         NOT NULL,
    CREATED_AT      TIMESTAMP    NOT NULL,
    MODIFIED_BY     INT8         NULL,
    MODIFIED_AT     TIMESTAMP    NULL,
    CONSTRAINT JOB_CARD_HISTORY_PKEY PRIMARY KEY (ID),
	CONSTRAINT UX_JOB_CARD_HISTORY__UUID UNIQUE (UUID),
	CONSTRAINT FK_JOB_CARD_ITEM__JOB_CARD_ID FOREIGN KEY (JOB_CARD_ID) REFERENCES JOB_CARD(ID)
);
CREATE INDEX IX_JOB_CARD_HISTORY__JOB_CARD_ID ON JOB_CARD_HISTORY USING BTREE (JOB_CARD_ID);


CREATE TABLE JOB_CARD_ITEM (
	ID BIGSERIAL NOT NULL,
	UUID UUID NOT NULL,
	STATUS VARCHAR(20) NOT NULL,
	ACTIVE BOOL NOT NULL,
	REQUEST_DATE TIMESTAMP NULL,
	REQUEST_USER_ID INT8 NULL,
	DELIVERED_DATE TIMESTAMP NULL,
	DELIVERED_USER_ID INT8 NULL,
	QUANTITY NUMERIC(19,4) NULL,
	APPROVE_QUANTITY NUMERIC(19,4) NULL,
	ITEM_ID INT8 NOT NULL,
	JOB_CARD_ID INT8 NOT NULL,
	RECEIVED_USER_ID INT8 NULL,
	DESCRIPTION VARCHAR(2000) NULL,
	CREATED_BY      INT8         NOT NULL,
    CREATED_AT      TIMESTAMP    NOT NULL,
    MODIFIED_BY     INT8         NULL,
    MODIFIED_AT     TIMESTAMP    NULL,
	CONSTRAINT JOB_CARD_ITEM_PKEY PRIMARY KEY (ID),
	CONSTRAINT UX_JOB_CARD_ITEM__UUID UNIQUE (UUID),
	CONSTRAINT FK_JOB_CARD_ITEM__DELIVERED_USER_ID FOREIGN KEY (DELIVERED_USER_ID) REFERENCES PUBLIC.USER(ID),
	CONSTRAINT FK_JOB_CARD_ITEM__ITEM_ID FOREIGN KEY (ITEM_ID) REFERENCES ITEM(ID),
	CONSTRAINT FK_JOB_CARD_ITEM__JOB_CARD_ID FOREIGN KEY (JOB_CARD_ID) REFERENCES JOB_CARD(ID),
	CONSTRAINT FK_JOB_CARD_ITEM__RECEIVED_USER_ID FOREIGN KEY (RECEIVED_USER_ID) REFERENCES PUBLIC.USER(ID),
	CONSTRAINT FK_JOB_CARD_ITEM__REQUEST_USER_ID FOREIGN KEY (REQUEST_USER_ID) REFERENCES PUBLIC.USER(ID)
);
CREATE INDEX IX_JOB_CARD_ITEM__DELIVERED_USER_ID ON JOB_CARD_ITEM USING BTREE (DELIVERED_USER_ID);
CREATE INDEX IX_JOB_CARD_ITEM__ITEM_ID ON JOB_CARD_ITEM USING BTREE (ITEM_ID);
CREATE INDEX IX_JOB_CARD_ITEM__JOB_CARD_ID ON JOB_CARD_ITEM USING BTREE (JOB_CARD_ID);
CREATE INDEX IX_JOB_CARD_ITEM__RECEIVED_USER_ID ON JOB_CARD_ITEM USING BTREE (RECEIVED_USER_ID);
CREATE INDEX IX_JOB_CARD_ITEM__REQUEST_USER_ID ON JOB_CARD_ITEM USING BTREE (REQUEST_USER_ID);


CREATE SEQUENCE BRK_NUMBER_SEQ INCREMENT BY 1 MINVALUE 1 MAXVALUE 9999999 CACHE 1 NO CYCLE;
CREATE SEQUENCE SRH_NUMBER_SEQ INCREMENT BY 1 MINVALUE 1 MAXVALUE 9999999 CACHE 1 NO CYCLE;

ALTER TABLE IMAGE ADD JOB_CARD_ID INT8 NULL;
CREATE INDEX IX_IMAGE__JOB_CARD_ID ON IMAGE (JOB_CARD_ID);
