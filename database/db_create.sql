CREATE TABLE Building (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    address VARCHAR(255) NOT NULL
);

CREATE TABLE Event (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    building_id INT,
    date TIMESTAMP NOT NULL,
    description TEXT,
    category VARCHAR(100),
    FOREIGN KEY (building_id) REFERENCES Building(id)
);
