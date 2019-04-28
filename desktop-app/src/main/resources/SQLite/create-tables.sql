CREATE TABLE IF NOT EXISTS users (
    id_user INTEGER PRIMARY KEY AUTOINCREMENT,
    user_name VARCHAR(32) NOT NULL UNIQUE,
    password_hash VARCHAR(32) NOT NULL
);

CREATE TABLE IF NOT EXISTS user_images (
    id_image INTEGER PRIMARY KEY AUTOINCREMENT,
    image_name VARCHAR(32) NOT NULL,

    id_user INTEGER NOT NULL REFERENCES users(id_user),
    image_date TIMESTAMP NOT NULL DEFAULT current_timestamp,
    isDownloaded INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS style_feature_vectors (
	id_vector INTEGER PRIMARY KEY AUTOINCREMENT,
	style_name VARCHAR(32) NOT NULL,	
	vector_value VARCHAR(256)
);