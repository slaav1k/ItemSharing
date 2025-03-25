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

SELECT add_user_return(1234567890, 'Тест Тестович', '79991234567', 'test@example.com', '123', 'Москва');
