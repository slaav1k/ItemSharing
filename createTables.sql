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
DROP TABLE IF EXISTS "color";
DROP TABLE IF EXISTS "material";
DROP TABLE IF EXISTS "maker";
DROP TABLE IF EXISTS "model";

CREATE TABLE "users" (
    "passport_num" BIGINT NOT NULL,
    "full_name" VARCHAR(255) NOT NULL,
    "phone" CHAR(11) NOT NULL UNIQUE,
    "email" VARCHAR(50) NOT NULL UNIQUE,
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

CREATE TABLE "color" (
    "color_id" BIGSERIAL NOT NULL,
    "name" VARCHAR(50) NOT NULL,
    PRIMARY KEY("color_id")
);

CREATE TABLE "material" (
    "material_id" BIGSERIAL NOT NULL,
    "name" VARCHAR(50) NOT NULL,
    PRIMARY KEY("material_id")
);

CREATE TABLE "maker" (
    "maker_id" BIGSERIAL NOT NULL,
    "name" VARCHAR(50) NOT NULL,
    "country" VARCHAR(255) NOT NULL,
    "year_of_foundation" BIGINT NOT NULL CHECK ("year_of_foundation" <= EXTRACT(YEAR FROM CURRENT_DATE)),
    PRIMARY KEY("maker_id")
);

CREATE TABLE "model" (
    "model_id" BIGSERIAL NOT NULL,
    "name" VARCHAR(50) NOT NULL,
    "maker" BIGINT NOT NULL,
    PRIMARY KEY("model_id"),
    FOREIGN KEY("maker") REFERENCES "maker"("maker_id")
);


CREATE TABLE "item" (
    "item_id" VARCHAR(100) NOT NULL,
    "owner" BIGINT NOT NULL,
    "name" VARCHAR(50) NOT NULL,
    "category" BIGINT NOT NULL,
    "description" VARCHAR(255) NOT NULL,
    "address" VARCHAR(255) NOT NULL,
    "is_available" BOOLEAN NOT NULL,
    "weight" DOUBLE PRECISION NOT NULL,
    "color" BIGINT NOT NULL,
    "material" BIGINT NOT NULL,
    "maker" BIGINT NOT NULL,
    "model" BIGINT NOT NULL,
    "release_year" BIGINT NOT NULL,
    PRIMARY KEY("item_id"),
    FOREIGN KEY("owner") REFERENCES "users"("passport_num"),
    FOREIGN KEY("category") REFERENCES "category"("category_id"),
    FOREIGN KEY("color") REFERENCES "color"("color_id"),
    FOREIGN KEY("material") REFERENCES "material"("material_id"),
    FOREIGN KEY("maker") REFERENCES "maker"("maker_id"),
    FOREIGN KEY("model") REFERENCES "model"("model_id")
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
    "current_date_time" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
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
