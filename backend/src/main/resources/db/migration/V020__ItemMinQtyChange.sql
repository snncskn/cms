ALTER TABLE public.item ALTER COLUMN min_stock_quantity TYPE numeric(19,4) USING min_stock_quantity::numeric;
