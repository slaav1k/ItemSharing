CREATE OR REPLACE FUNCTION update_item_availability()
RETURNS TRIGGER AS $$
BEGIN
    -- Обновляем статус вещи в зависимости от существующих одобренных заявок
    UPDATE item
    SET is_available = NOT EXISTS (
        SELECT 1 FROM request
        WHERE request.item = NEW.item
        AND request.status = 2  -- Approved
        AND CURRENT_DATE BETWEEN request.start_date AND request.end_date
    )
    WHERE item.item_id = NEW.item;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Создаем триггер, который срабатывает при вставке, обновлении или удалении заявки
CREATE TRIGGER check_item_availability
AFTER INSERT OR UPDATE OR DELETE ON request
FOR EACH ROW
EXECUTE FUNCTION update_item_availability();


CREATE EXTENSION IF NOT EXISTS pg_cron;
SELECT * FROM pg_extension WHERE extname = 'pg_cron';


CREATE OR REPLACE FUNCTION update_all_items_availability()
RETURNS VOID AS $$
BEGIN
    UPDATE item
    SET is_available = NOT EXISTS (
        SELECT 1 FROM request
        WHERE request.item = item.item_id
        AND request.status = 2  -- Approved
        AND CURRENT_DATE BETWEEN request.start_date AND request.end_date
    );
END;
$$ LANGUAGE plpgsql;


SELECT cron.schedule('0 */12 * * *', $$CALL update_all_items_availability();$$);

