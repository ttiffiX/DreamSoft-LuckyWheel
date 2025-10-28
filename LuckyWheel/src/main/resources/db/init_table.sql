drop table rewards;
CREATE TABLE rewards
(
    id   SERIAL PRIMARY KEY,
    name VARCHAR(255),
    type varchar(100)
);

drop table users;
CREATE TABLE users
(
    id       SERIAL PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    normal_tickets INT DEFAULT 0,
    premium_tickets INT DEFAULT 0
);

drop table rewards_history;
CREATE TABLE rewards_history
(
    id        SERIAL PRIMARY KEY,
    user_id   BIGINT NOT NULL,
    wheel_id   BIGINT NOT NULL,
    reward_id BIGINT NOT NULL,
    spin_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (reward_id) REFERENCES rewards (id) ON DELETE CASCADE
);

drop table wheel_milestones;
CREATE TABLE wheel_milestones
(
    id          SERIAL PRIMARY KEY,
    user_id     BIGINT NOT NULL,
    wheel_id    BIGINT NOT NULL,
    spin_count  INT DEFAULT 0,
    last_milestone_claim_time TIMESTAMP,

    UNIQUE (user_id, wheel_id),
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);