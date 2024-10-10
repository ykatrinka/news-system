TRUNCATE TABLE comment RESTART IDENTITY;
TRUNCATE TABLE news RESTART IDENTITY;

INSERT INTO news (time, title, text)
VALUES (NOW(), 'News 1', 'Content news 1'),
       (NOW(), 'News 2', 'Content news 2'),
       (NOW(), 'News 3', 'Content news 3');



INSERT INTO comment (time, text, username, news_id)
VALUES (NOW(), 'Comment 1', 'Patrik', 1),
       (NOW(), 'Comment 2', 'Charly', 1),
       (NOW(), 'Comment 1 Sarah', 'Polly', 2),
       (NOW(), 'Comment 2', 'Sarah', 2);