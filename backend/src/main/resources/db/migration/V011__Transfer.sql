CREATE SEQUENCE TRANSFER_NUMBER_SEQ INCREMENT BY 1 MINVALUE 1 MAXVALUE 999999999999 CACHE 1 NO CYCLE;

CREATE TABLE TRANSFER (
	ID BIGSERIAL NOT NULL,
	UUID UUID NOT NULL,
	STATUS VARCHAR(20) NULL,
	DELIVER_DATE TIMESTAMP NULL,
	REQUEST_DATE TIMESTAMP NULL,
	TRANSFER_DATE TIMESTAMP NULL,
	TRANSFER_NUMBER VARCHAR(50) NULL,
	SOURCE_OWNER_ID INT8 NOT NULL,
	TARGET_OWNER_ID INT8 NOT NULL,
	SOURCE_SITE_ID INT8 NOT NULL,
	TARGET_SITE_ID INT8 NOT NULL,
	REJECTION_REASON VARCHAR(500) NULL,
	ACTIVE BOOL NOT NULL,
	CREATED_BY INT8 NOT NULL,
	CREATED_AT TIMESTAMP NOT NULL,
	MODIFIED_BY INT8 NULL,
	MODIFIED_AT TIMESTAMP NULL,
	CONSTRAINT TRANSFER_PKEY PRIMARY KEY (ID),
	CONSTRAINT UX_TRANSFER__UUID UNIQUE (UUID),
	CONSTRAINT FK_TRANSFER__SOURCE_SITE_ID FOREIGN KEY (SOURCE_SITE_ID) REFERENCES SITE(ID),
	CONSTRAINT FK_TRANSFER__TARGET_SITE_ID FOREIGN KEY (TARGET_SITE_ID) REFERENCES SITE(ID),
	CONSTRAINT FK_TRANSFER__SOURCE_OWNER_ID FOREIGN KEY (SOURCE_OWNER_ID) REFERENCES PUBLIC.USER (ID),
	CONSTRAINT FK_TRANSFER__TARGET_OWNER_ID FOREIGN KEY (TARGET_OWNER_ID) REFERENCES PUBLIC.USER (ID)
);
CREATE INDEX IX_TRANSFER__SOURCE_SITE_ID ON TRANSFER USING BTREE (SOURCE_SITE_ID);
CREATE INDEX IX_TRANSFER__TARGET_SITE_ID ON TRANSFER USING BTREE (TARGET_SITE_ID);
CREATE INDEX IX_TRANSFER__SOURCE_OWNER_ID ON TRANSFER USING BTREE (SOURCE_OWNER_ID);
CREATE INDEX IX_TRANSFER__TARGET_OWNER_ID ON TRANSFER USING BTREE (TARGET_OWNER_ID);


CREATE TABLE TRANSFER_HISTORY (
	ID BIGSERIAL NOT NULL,
	UUID UUID NOT NULL,
	DESCRIPTION VARCHAR(500) NULL,
	STATUS VARCHAR(20) NULL,
	TRANSFER_ID INT8 NOT NULL,
	USER_ID INT8 NOT NULL,
	CREATED_BY INT8 NOT NULL,
	CREATED_AT TIMESTAMP NOT NULL,
	MODIFIED_BY INT8 NULL,
	MODIFIED_AT TIMESTAMP NULL,
	CONSTRAINT TRANSFER_HISTORY_PKEY PRIMARY KEY (ID),
	CONSTRAINT UX_TRANSFER_HISTORY__UUID UNIQUE (UUID),
	CONSTRAINT FK_TRANSFER_HISTORY__TRANSFER_ID FOREIGN KEY (TRANSFER_ID) REFERENCES TRANSFER(ID),
	CONSTRAINT FK_TRANSFER_HISTORY__USER_ID FOREIGN KEY (USER_ID) REFERENCES PUBLIC.USER(ID)
);
CREATE INDEX IX_TRANSFER_HISTORY__TRANSFER_ID ON TRANSFER_HISTORY USING BTREE (TRANSFER_ID);
CREATE INDEX IX_TRANSFER_HISTORY__USER_ID ON TRANSFER_HISTORY USING BTREE (USER_ID);

CREATE TABLE TRANSFER_ITEM (
	ID BIGSERIAL NOT NULL,
	UUID UUID NOT NULL,
	QUANTITY NUMERIC(19,4) NULL,
	APPROVE_QUANTITY NUMERIC(19,4) NULL,
	APPROVE BOOL NOT NULL,
	TRANSFER_ID INT8 NOT NULL,
	ITEM_ID INT8 NOT NULL,
	DESCRIPTION VARCHAR(500) NULL,
	ACTIVE BOOL NOT NULL,
	CREATED_BY INT8 NOT NULL,
	CREATED_AT TIMESTAMP NOT NULL,
	MODIFIED_BY INT8 NULL,
	MODIFIED_AT TIMESTAMP NULL,
	CONSTRAINT TRANSFER_ITEM_PKEY PRIMARY KEY (ID),
	CONSTRAINT UX_TRANSFER_ITEM__UUID UNIQUE (UUID),
	CONSTRAINT FK_TRANSFER_ITEM__ITEM_ID FOREIGN KEY (ITEM_ID) REFERENCES ITEM(ID),
	CONSTRAINT FK_TRANSFER_ITEM__TRANSFER_ID FOREIGN KEY (TRANSFER_ID) REFERENCES TRANSFER(ID)
);
CREATE INDEX IX_TRANSFER_ITEM__ITEM_ID ON TRANSFER_ITEM USING BTREE (ITEM_ID);
CREATE INDEX IX_TRANSFER_ITEM__TRANSFER_ID ON TRANSFER_ITEM USING BTREE (TRANSFER_ID);