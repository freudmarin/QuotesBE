
-- Create the 'authors' table
CREATE TABLE if not exists authors (
                         id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
                         name VARCHAR(255) NOT NULL,
                         bio  VARCHAR(2000),
                         description VARCHAR(1000),
                         link VARCHAR(255)

);

-- Create the 'tags' table
CREATE TABLE if not exists tags (
                      id VARCHAR(255) PRIMARY KEY,
                      name VARCHAR(255) NOT NULL,
                      slug VARCHAR(255) NOT NULL,
                      quote_count INT DEFAULT 0,
                      date_added TIMESTAMP,
                      date_modified TIMESTAMP
);

-- Create the 'quotes' table
CREATE TABLE if not exists quotes (
                        id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
                        text TEXT NOT NULL,
                        author_id BIGINT,
                        FOREIGN KEY (author_id) REFERENCES authors (id)
);

-- Create the join table for the many-to-many relationship between quotes and tags
CREATE TABLE if not exists quote_tag (
                           quote_id BIGINT,
                           tag_id VARCHAR(255),
                           PRIMARY KEY (quote_id, tag_id),
                           FOREIGN KEY (quote_id) REFERENCES quotes (id),
                           FOREIGN KEY (tag_id) REFERENCES tags (id)
);
