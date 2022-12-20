drop table if exists tasks;
create table tasks(
    id INT PRIMARY KEY AUTO_INCREMENT,
    description VARCHAR(100) NOT NULL,
    done bit
)