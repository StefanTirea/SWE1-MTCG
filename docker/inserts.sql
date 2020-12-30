INSERT INTO public."user" (username, password, role, coins, deck, elo, games_played, games_won) VALUES ('stefan', 'stefan', 'USER', 20, '{}', 100, 0, 0);
INSERT INTO public."user" (username, password, role, coins, deck, elo, games_played, games_won) VALUES ('admin', 'admin', 'ADMIN', 20, '{}', 100, 0, 0);
INSERT INTO public.token (user_id, token, expires_at) VALUES (1, 'stefan', '2021-12-29 17:02:29.000000');
