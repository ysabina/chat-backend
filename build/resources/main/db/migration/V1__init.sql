
-- users table
CREATE TABLE users (
  id          BIGSERIAL PRIMARY KEY,
  username    VARCHAR(50) UNIQUE NOT NULL,
  password    VARCHAR(100) NOT NULL,
  online      BOOLEAN     NOT NULL DEFAULT FALSE,
  created_at  TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- chat_rooms table
CREATE TABLE chat_rooms (
  id          BIGSERIAL PRIMARY KEY,
  name        VARCHAR(100) UNIQUE NOT NULL,
  created_at  TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- join table for participants
CREATE TABLE room_participants (
  room_id   BIGINT NOT NULL REFERENCES chat_rooms(id) ON DELETE CASCADE,
  user_id   BIGINT NOT NULL REFERENCES users(id)      ON DELETE CASCADE,
  PRIMARY KEY (room_id, user_id)
);

-- messages table
CREATE TABLE messages (
  id          BIGSERIAL PRIMARY KEY,
  room_id     BIGINT    NOT NULL REFERENCES chat_rooms(id) ON DELETE CASCADE,
  sender_id   BIGINT             REFERENCES users(id)      ON DELETE SET NULL,
  content     TEXT      NOT NULL,
  sent_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  delivered   BOOLEAN   NOT NULL DEFAULT FALSE
);
