DROP TABLE IF EXISTS "item_review_photo_links";
DROP TABLE IF EXISTS "item_photo_links";
DROP TABLE IF EXISTS "photo_links";
DROP TABLE IF EXISTS "item_attributes";
DROP TABLE IF EXISTS "attribute_enum_value";
DROP TABLE IF EXISTS "category_attribute";
DROP TABLE IF EXISTS "attribute";
DROP TABLE IF EXISTS "item_review";
DROP TABLE IF EXISTS "blacklist";
DROP TABLE IF EXISTS "review";
DROP TABLE IF EXISTS "request";
DROP TABLE IF EXISTS "request_status";
DROP TABLE IF EXISTS "item";
DROP TABLE IF EXISTS "category";
DROP TABLE IF EXISTS "users";

CREATE TABLE "users" (
    "passport_num" BIGINT NOT NULL,
    "full_name" VARCHAR(255) NOT NULL,
    "phone" CHAR(11) NOT NULL,
    "email" VARCHAR(50) NOT NULL,
	"password" VARCHAR(255) NOT NULL,
    "address" VARCHAR(255) NOT NULL,
    PRIMARY KEY("passport_num")
);

CREATE TABLE "category" (
    "category_id" BIGINT NOT NULL,
    "name" VARCHAR(100) NOT NULL,
    "description" VARCHAR(255) NOT NULL,
    PRIMARY KEY("category_id")
);

CREATE TABLE "item" (
    "item_id" VARCHAR(100) NOT NULL,
    "owner" BIGINT NOT NULL,
    "name" VARCHAR(50) NOT NULL,
    "category" BIGINT NOT NULL,
    "description" VARCHAR(255) NOT NULL,
    "address" VARCHAR(255) NOT NULL,
    "is_available" BOOLEAN NOT NULL,
    "sizes" VARCHAR(255) NOT NULL,
    "weight" DOUBLE PRECISION NOT NULL,
    "color" VARCHAR(255) NOT NULL,
    "material" VARCHAR(255) NOT NULL,
    "maker" VARCHAR(255) NOT NULL,
    "model" VARCHAR(255) NOT NULL,
    "release_year" BIGINT NOT NULL,
    PRIMARY KEY("item_id"),
    FOREIGN KEY("owner") REFERENCES "users"("passport_num"),
    FOREIGN KEY("category") REFERENCES "category"("category_id")
);

CREATE TABLE "request_status" (
    "status_id" BIGINT NOT NULL,
    "name" VARCHAR(50) NOT NULL,
    "description" VARCHAR(255) NOT NULL,
    PRIMARY KEY("status_id")
);

CREATE TABLE "request" (
    "request_id" BIGSERIAL NOT NULL,
    "holder" BIGINT NOT NULL,
    "item" VARCHAR(100) NOT NULL,
    "start_date" DATE NOT NULL,
    "end_date" DATE NOT NULL,
    "status" BIGINT NOT NULL,
    PRIMARY KEY("request_id"),
    FOREIGN KEY("item") REFERENCES "item"("item_id"),
    FOREIGN KEY("status") REFERENCES "request_status"("status_id"),
    FOREIGN KEY("holder") REFERENCES "users"("passport_num")
);

CREATE TABLE "review" (
    "reviewed" BIGINT NOT NULL,
    "reviewer" BIGINT NOT NULL,
    "comment" VARCHAR(255) NOT NULL,
    "score" INTEGER NOT NULL,
    "date" DATE NOT NULL,
    PRIMARY KEY("reviewed", "reviewer", "date"),
    FOREIGN KEY("reviewed") REFERENCES "users"("passport_num"),
    FOREIGN KEY("reviewer") REFERENCES "users"("passport_num")
);

CREATE TABLE "blacklist" (
    "blocked_user" BIGINT NOT NULL,
    "blocked_by_user" BIGINT NOT NULL,
    "date" DATE NOT NULL,
    PRIMARY KEY("blocked_user", "blocked_by_user"),
    FOREIGN KEY("blocked_user") REFERENCES "users"("passport_num"),
    FOREIGN KEY("blocked_by_user") REFERENCES "users"("passport_num")
);

CREATE TABLE "item_review" (
    "item_review_id" BIGSERIAL NOT NULL,
    "item" VARCHAR(100) NOT NULL,
    "reviewer" BIGINT NOT NULL,
    "comment" VARCHAR(255) NOT NULL,
    "score" INTEGER NOT NULL,
    "date" DATE NOT NULL,
    PRIMARY KEY("item_review_id"),
    FOREIGN KEY("item") REFERENCES "item"("item_id"),
    FOREIGN KEY("reviewer") REFERENCES "users"("passport_num")
);

-- CREATE TABLE "item_attributes" (
--     "item" VARCHAR(100) NOT NULL,
--     "attribute" VARCHAR(255) NOT NULL,
--     "value" VARCHAR(255) NOT NULL,
--     PRIMARY KEY("item", "attribute"),
--     FOREIGN KEY("item") REFERENCES "item"("item_id")
-- );

CREATE TABLE "attribute" (
    "attribute_id" SERIAL PRIMARY KEY,
    "name" VARCHAR(100) NOT NULL,
    "type" VARCHAR(10) CHECK ("type" IN ('ENUM', 'NUMBER'))
);

CREATE TABLE "category_attribute" (
    "category_id" BIGINT NOT NULL REFERENCES "category"("category_id"),
    "attribute_id" BIGINT NOT NULL REFERENCES "attribute"("attribute_id"),
    PRIMARY KEY ("category_id", "attribute_id")
);


CREATE TABLE "attribute_enum_value" (
    "attribute_id" BIGINT NOT NULL REFERENCES "attribute"("attribute_id"),
    "value" VARCHAR(255) NOT NULL,
    PRIMARY KEY ("attribute_id", "value")
);

-- CREATE TABLE "item_attributes" (
--     "item_id" VARCHAR(100) NOT NULL REFERENCES "item"("item_id"),
--     "attribute_id" BIGINT NOT NULL REFERENCES "attribute"("attribute_id"),
--     "value_text" VARCHAR(255),  -- Используется для ENUM
--     "value_number" DOUBLE PRECISION,  -- Используется для NUMBER
--     CHECK (
--         ("value_text" IS NOT NULL AND "value_number" IS NULL) OR 
--         ("value_text" IS NULL AND "value_number" IS NOT NULL)
--     ),
--     PRIMARY KEY ("item_id", "attribute_id")
-- );

CREATE TABLE "item_attributes" (
    "item_id" VARCHAR(100) NOT NULL REFERENCES "item"("item_id"),
    "attribute_id" BIGINT NOT NULL REFERENCES "attribute"("attribute_id"),
    "value" VARCHAR(255) NOT NULL,
    PRIMARY KEY ("item_id", "attribute_id")
);


CREATE TABLE "photo_links" (
    "photo_id" BIGSERIAL NOT NULL,
    "url" VARCHAR(255) NOT NULL,
    PRIMARY KEY("photo_id")
);

CREATE TABLE "item_photo_links" (
    "item_id" VARCHAR(100) NOT NULL,
    "photo_id" BIGINT NOT NULL,
    PRIMARY KEY("item_id", "photo_id"),
    FOREIGN KEY("item_id") REFERENCES "item"("item_id"),
    FOREIGN KEY("photo_id") REFERENCES "photo_links"("photo_id")
);

CREATE TABLE "item_review_photo_links" (
    "item_review_id" BIGINT NOT NULL,
    "photo_id" BIGINT NOT NULL,
    PRIMARY KEY("item_review_id", "photo_id"),
    FOREIGN KEY("item_review_id") REFERENCES "item_review"("item_review_id"),
    FOREIGN KEY("photo_id") REFERENCES "photo_links"("photo_id")
);
