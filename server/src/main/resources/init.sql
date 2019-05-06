create table IF NOT EXISTS users (
  id_user serial primary key,
  user_name varchar(32) not null unique,
  password_hash varchar(32) not null
);