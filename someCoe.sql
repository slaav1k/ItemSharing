SELECT * FROM public.attribute

INSERT INTO photo_links (photo_id, url) VALUES
(19, 'items/ITM001_2.jpeg');

INSERT INTO item_photo_links (item_id, photo_id) VALUES
('ITM001', 19);

select * from category_attribute

DELETE FROM item_review_photo_links;

DELETE FROM users where passport_num = '1234123456';

UPDATE users 
SET 
    passport_num = '1234123456',
    full_name = 'Архипкин Вячеслав Михайлович',
    phone = '79106114058',
    email = 'sl',
    password = '$2a$10$mte4KZiaYGJolQIzZAHUi.rZYq.2Xdy47Fbuph/5.QD/Ufw9brdhG',
    address = 'Рязань, ул. Гагарина, д. 95'
WHERE passport_num = '1234123456';

UPDATE users 
SET 
    password = '$2a$10$mte4KZiaYGJolQIzZAHUi.rZYq.2Xdy47Fbuph/5.QD/Ufw9brdhG'
WHERE passport_num = '1234123457';

select * from photo_links
select * from item_review
select * from item_review_photo_links
select * from request
select * from item
	DELETE FROM item where owner = '1234123456';

-- Сначала удаляем атрибуты
DELETE FROM item_attributes WHERE item_id IN (
    SELECT item_id FROM item WHERE owner = '1234123456'
);

-- Затем удаляем товары
DELETE FROM item WHERE owner = '1234123456';

select * from users

select * from photo_links
select * from item_photo_links
	
INSERT INTO request (holder, item, start_date, end_date, status) VALUES
(4723918271, 'ITM001', '2025-03-27', '2025-03-29', 1);

UPDATE request
SET status = 1
WHERE request_id = 2;
