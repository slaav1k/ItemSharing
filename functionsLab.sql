CREATE OR REPLACE FUNCTION add_user_return(
    p_passport_num BIGINT,
    p_full_name VARCHAR(255),
    p_phone CHAR(11),
    p_email VARCHAR(50),
    p_password VARCHAR(255),
    p_address VARCHAR(255)
) RETURNS BOOLEAN AS $$
BEGIN
    INSERT INTO users (passport_num, full_name, phone, email, password, address)
    VALUES (p_passport_num, p_full_name, p_phone, p_email, p_password, p_address);
    
    RETURN TRUE;
EXCEPTION
    WHEN OTHERS THEN
        RETURN FALSE;
END;
$$ LANGUAGE plpgsql;

SELECT * 
FROM pg_proc 
WHERE proname = 'add_user_return';

DROP FUNCTION get_user_by_passport(bigint)

CREATE OR REPLACE FUNCTION get_user_by_passport(
    p_passport_num BIGINT
) RETURNS TABLE (
    passport_num BIGINT,
    full_name VARCHAR(255),
    phone VARCHAR(11),
    email VARCHAR(50),
	password VARCHAR(255),
    address VARCHAR(255)
) AS $$
BEGIN
    RETURN QUERY
    SELECT u.passport_num, u.full_name, u.phone, u.email, u.password, u.address
    FROM users u
    WHERE u.passport_num = p_passport_num;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE PROCEDURE update_user(
    IN p_passport_num BIGINT,
    IN p_full_name VARCHAR(255),
    IN p_phone VARCHAR(11),
    IN p_email VARCHAR(50),
    IN p_password VARCHAR(255),  
    IN p_address VARCHAR(255)
) LANGUAGE plpgsql AS $$
BEGIN
    UPDATE users
    SET full_name = p_full_name,
        phone = p_phone,
        email = p_email,
        password = CASE 
                      WHEN p_password IS NOT NULL AND p_password != '' 
                      THEN p_password 
                      ELSE password 
                   END,
        address = p_address
    WHERE passport_num = p_passport_num;
END;
$$;

CREATE OR REPLACE PROCEDURE delete_user(
    IN p_passport_num BIGINT
) LANGUAGE plpgsql AS $$
BEGIN
    DELETE FROM users WHERE passport_num = p_passport_num;
END;
$$;



