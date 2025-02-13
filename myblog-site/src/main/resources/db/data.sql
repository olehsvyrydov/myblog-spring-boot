INSERT INTO posts (title, description, content, image_url, created_at)VALUES ( 'My simple post',
                   'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Curabitur
                    convallis nisl sit amet nunc vestibulum, non finibus lectus venenatis.',
    '<p>
                    Lorem ipsum dolor sit amet, consectetur adipiscing elit. Curabitur
                    convallis nisl sit amet nunc vestibulum, non finibus lectus venenatis.
                </p>
                <p>
                    Quisque a libero eget lacus tincidunt auctor. Nam vitae augue
                    dapibus, vestibulum odio id, volutpat felis.
                </p>', '/images/pexels-366671-991831.jpg', '2025-01-12T13:35:52');

INSERT INTO posts (title, description, content, image_url, created_at)VALUES ('My second simple post',
                   'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Curabitur
                    convallis nisl sit amet nunc vestibulum, non finibus lectus venenatis.',
                '<p>
                    Lorem ipsum dolor sit amet, consectetur adipiscing elit. Curabitur
                    convallis nisl sit amet nunc vestibulum, non finibus lectus venenatis.
                </p>
                <p>
                    Quisque a libero eget lacus tincidunt auctor. Nam vitae augue
                    dapibus, vestibulum odio id, volutpat felis.
                </p>', '/images/pexels-chevonrossouw-2558605.jpg', '2025-01-12T15:35:52');

INSERT INTO posts (title, description, content, image_url, created_at)VALUES ('My third simple post',
                  'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Curabitur
                  convallis nisl sit amet nunc vestibulum, non finibus lectus venenatis.', '<p>
                    Lorem ipsum dolor sit amet, consectetur adipiscing elit. Curabitur
                    convallis nisl sit amet nunc vestibulum, non finibus lectus venenatis.
                </p>
                <p>
                    Quisque a libero eget lacus tincidunt auctor. Nam vitae augue
                    dapibus, vestibulum odio id, volutpat felis.
                </p>', '/images/pexels-francesco-ungaro-96938.jpg', '2025-01-13T13:35:52');

INSERT INTO posts (title, description, content, image_url, created_at)VALUES ( 'My third simple post',
                 'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Curabitur
                 convallis nisl sit amet nunc vestibulum, non finibus lectus venenatis.', '<p>
                    Lorem ipsum dolor sit amet, consectetur adipiscing elit. Curabitur
                    convallis nisl sit amet nunc vestibulum, non finibus lectus venenatis.
                </p>
                <p>
                    Quisque a libero eget lacus tincidunt auctor. Nam vitae augue
                    dapibus, vestibulum odio id, volutpat felis.
                </p>', '/images/default_image.jpg', '2025-01-14T14:35:35');
// comments
INSERT INTO comments (content, post_id, created_at)VALUES ('You are the best!', 1, '2025-01-12T14:35:35');
INSERT INTO comments (content, post_id, created_at)VALUES ('Good job!', 1, '2025-01-13T14:35:35');
INSERT INTO comments (content, post_id, created_at)VALUES ('Great image!', 2, '2025-01-14T14:35:35');
INSERT INTO comments (content, post_id, created_at)VALUES ('I want the same', 3, '2025-01-14T15:35:35');
INSERT INTO comments (content, post_id, created_at)VALUES ( 'You are the best!', 3, '2025-01-14T16:35:35');
// tags_posts
INSERT INTO tags (tag_name, post_id) VALUES ( '#cats', 1 );
INSERT INTO tags (tag_name, post_id) VALUES ( '#fluffies', 1 );
INSERT INTO tags (tag_name, post_id) VALUES ( '#cats', 2 );
INSERT INTO tags (tag_name, post_id) VALUES ( '#groups', 2 );
INSERT INTO tags (tag_name, post_id) VALUES ( '#cats', 3 );
INSERT INTO tags (tag_name, post_id) VALUES ( '#cats', 4 );
INSERT INTO tags (tag_name, post_id) VALUES ( '#groups', 4 );
// likes
INSERT INTO likes (post_id, likes_count) VALUES ( 1, 10 );
INSERT INTO likes (post_id, likes_count) VALUES ( 2, 15 );
INSERT INTO likes (post_id, likes_count) VALUES ( 3, 5 );
